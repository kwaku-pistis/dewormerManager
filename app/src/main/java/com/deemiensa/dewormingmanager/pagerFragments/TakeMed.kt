package com.deemiensa.dewormingmanager.pagerFragments


import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.deemiensa.dewormingmanager.R
import com.deemiensa.dewormingmanager.activities.AlarmReceiver
import com.deemiensa.dewormingmanager.offline.AppDatabase
import com.deemiensa.dewormingmanager.offline.DatabaseInfo
import com.deemiensa.dewormingmanager.offline.SharedPref
import com.deemiensa.dewormingmanager.services.NotificationScheduler
import com.deemiensa.dewormingmanager.viewpager.ui.main.PageViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_take_med.*
import org.jetbrains.anko.doAsync
import org.joda.time.LocalDate
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.math.absoluteValue


/**
 * A simple [Fragment] subclass.
 */
class TakeMed : Fragment() {

    private lateinit var lastDate: TextView
    private lateinit var nextDewormDate: TextView
    private lateinit var sharedPref: SharedPref
    private lateinit var pieChart: PieChart
    private lateinit var textView: TextView

    private lateinit var dewormDate: EditText
    private var localeDate: LocalDate? = null
    private var defaultDate: Date? = null
    private var dialog_view: View? = null

    var formattedDate: String? = null
    var notificationId = 0
    private lateinit var db: AppDatabase
    private lateinit var model: PageViewModel

    private lateinit var mScheduler: JobScheduler
    private val JOB_ID: Int = 0
    private val ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L // 1 Day

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_take_med, container, false)

        model = ViewModelProvider(requireActivity())[PageViewModel::class.java]
        mScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        // accessing the history database
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "history-list.db"
        ).build()

        val fab: FloatingActionButton = root!!.findViewById(R.id.fab)

        fab.setOnClickListener {
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            // show a dialog for user to select date
            val dialog = MaterialDialog(requireContext()).title(text = "TAKE DEWORMER")
                .customView(R.layout.dialog_layout)
                .positiveButton(text = "DONE") { saveToSharedPrefAndDB(localeDate) }
                .negativeButton(text = "CANCEL") {dialog -> dialog.dismiss()}

            dialog_view = dialog.getCustomView()

            dewormDate = dialog_view!!.findViewById(R.id.deworm_date)
            // set the current date
            val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.UK)
            defaultDate = Calendar.getInstance().time
            dewormDate.setText(dateFormat.format(defaultDate!!))
            dewormDate.setOnClickListener {
                localeDate = openCalenderDialog()
            }

            dialog.show()
        }

        sharedPref = SharedPref(requireContext())

        lastDate = root.findViewById(R.id.last_taken_tv_2)
        lastDate.text = sharedPref.datetaken

        nextDewormDate = root.findViewById(R.id.next_taken_tv_2)
        nextDewormDate.text = sharedPref.nextDate

        textView = root.findViewById(R.id.tv1)

        pieChart = root.findViewById(R.id.pie_chart)
        loadPieChart()

        return root
    }

    override fun onStart() {
        super.onStart()

        lastDate.text = sharedPref.datetaken
        nextDewormDate.text = sharedPref.nextDate
    }

    override fun onResume() {
        super.onResume()

        lastDate.text = sharedPref.datetaken
        nextDewormDate.text = sharedPref.nextDate
    }

    companion object {
        @JvmStatic
        fun newInstance() = TakeMed()
    }

    private fun loadPieChart(){
        val daysLeft = calculateDays()
        val daysPast = 90 - daysLeft

        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart.dragDecelerationFrictionCoef = 0.99f
        pieChart.isDrawHoleEnabled = false
        pieChart.transparentCircleRadius = 61f
        pieChart.animateY(1000, Easing.EaseInOutCirc)

        if (daysLeft >= 0){
            pieChart.isVisible = true
            textView.isVisible = false

            val pieValues = arrayListOf<PieEntry>()
            pieValues.add(PieEntry(daysLeft.toFloat(),  "days more"))
            pieValues.add(PieEntry(daysPast.toFloat(), "days past"))

            val pieDataSet = PieDataSet(pieValues, "Days")
            pieDataSet.sliceSpace = 3f
            pieDataSet.selectionShift = 5f
            pieDataSet.colors = COLORFUL_COLORS.toMutableList()

            val pieData = PieData(pieDataSet)
            pieData.setValueTextSize(10f)
            pieData.setValueTextColor(Color.YELLOW)

            pieChart.data = pieData
        }
    }

    private fun calculateDays(): Long {
        val currentDate = Date()
//        val dateTaken = sharedPref.lastdatetaken

        val parseDate = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        val date = sharedPref.unFormattedNextDate

        if (!date.isNullOrEmpty()) {
            val nextDate = parseDate.parse(date)
            val diff: Long = nextDate!!.time - currentDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            Log.d("DAYS LEFT", days.toString())

            if (days.toInt() < 0){
                textView.isVisible = true
                textView.text = "Please take your next dewormer drug"
                pieChart.isVisible = false
            }

            //return (days.toDouble() / total_days.toDouble()) * 100.00
            return days
        }

        return 0
    }

    private fun saveToSharedPrefAndDB(date: LocalDate?) {
        if (date != null) {
            val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.UK)
            val parseDate = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
//            val nextDate = date.plusDays(90)
            val nextDate = date.plusDays(2)
            val parsedDate = parseDate.parse(nextDate.toString())

            formattedDate = dateFormat.format(parsedDate!!)
            Log.d("NEXT DATE", nextDate.toString())

            sharedPref.nextDate = formattedDate
            sharedPref.unFormattedNextDate = nextDate.toString()
            sharedPref.lastdatetaken = date.toString()
            sharedPref.datetaken = dewormDate.text.toString()
        } else {
            val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.UK)
            val parseDate = SimpleDateFormat("yyyy-MM-dd", Locale.UK)

            // convert date text to local date
            val localDate = LocalDate(defaultDate)
            val nextDate = localDate.plusDays(2)
            val parsedDate = parseDate.parse(nextDate.toString())

            formattedDate = dateFormat.format(parsedDate!!)

            sharedPref.nextDate = formattedDate
            sharedPref.unFormattedNextDate = nextDate.toString()
            sharedPref.lastdatetaken = localDate.toString()
            sharedPref.datetaken = dewormDate.text.toString()
        }

        // show values in views
        last_taken_tv_2.text = dewormDate.text.toString()
        next_taken_tv_2.text = formattedDate

        loadPieChart()
        scheduleJob()

        doAsync {
            model.insert(DatabaseInfo(0, dewormDate.text.toString(), formattedDate.toString()))
        }
    }

    private fun openCalenderDialog(): LocalDate {
        var date: LocalDate = LocalDate.now()
        MaterialDialog(requireContext()).show { datePicker { dialog, datetime ->
            val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.UK)
            dewormDate.setText(dateFormat.format(datetime.time))

            date = LocalDate(datetime.year, datetime.month + 1, datetime.dayOfMonth)
        }
        }
        return date
    }

    /**
     * Do not delete. Keep it safe for future reference
     * */
    private fun triggerAlarm(){
        // Get AlarmManager instance
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent part
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = "FOO_ACTION"
        intent.putExtra("KEY_FOO_STRING", "Time to take your Dewormer")
        intent.putExtra("notificationId", ++notificationId)

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        // Alarm time
        val ALARM_DELAY_IN_SECOND = 10
        val alarmTimeAtUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L

        // Set with system Alarm Service
        // Other possible functions: setExact() / setRepeating() / setWindow(), etc
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent)
        } else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent)
        }
    }

    val requestCode = 0

    fun getAlarmIntent(): Intent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = "FOO_ACTION"
        return intent
    }

    fun cancelAlarm() {
        val sender = PendingIntent.getBroadcast(context, requestCode, getAlarmIntent(), 0)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

    /**
     * method that schedules the job based on the parameters set
     * */
    private fun scheduleJob() {
        val serviceName = ComponentName(requireContext().packageName, NotificationScheduler::class.java.name)

        val jobBuilder = JobInfo.Builder(JOB_ID, serviceName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
            .setPeriodic(ONE_DAY_INTERVAL)
            .setRequiresCharging(false)

        mScheduler.schedule(jobBuilder.build())
    }

    fun cancelJobs() {
        mScheduler.cancel(JOB_ID)
    }
}
