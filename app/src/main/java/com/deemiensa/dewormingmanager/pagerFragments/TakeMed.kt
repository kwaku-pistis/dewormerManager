package com.deemiensa.dewormingmanager.pagerFragments


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.deemiensa.dewormingmanager.AlarmReceiver
import com.deemiensa.dewormingmanager.R
import com.deemiensa.dewormingmanager.offline.SharedPref
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_take_med.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class TakeMed : Fragment() {

    private lateinit var lastDate: TextView
    private lateinit var nextDewormDate: TextView
    private lateinit var sharedPref: SharedPref
    private lateinit var pieChart: PieChart

    private lateinit var dewormDate: EditText
    private var dateDewormerTaken: String? = null
    private var dialog_view: View? = null

    var formattedDate: String? = null
    var notificationId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_take_med, container, false)

        val fab: FloatingActionButton = root.findViewById(R.id.fab)

        fab.setOnClickListener {
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            // show a dialog for user to select date
            val dialog = MaterialDialog(context!!).title(text = "TAKE DEWORMER")
                .customView(R.layout.dialog_layout)
                .positiveButton(text = "DONE") { takeDewormDate() }
                .negativeButton(text = "CANCEL") {dialog -> dialog.dismiss()}

            dialog_view = dialog.getCustomView()

            dewormDate = dialog_view!!.findViewById(R.id.deworm_date)
            dewormDate.setOnClickListener { openCalenderDialog() }

            dialog.show()
        }

        sharedPref = SharedPref(context!!)

        lastDate = root.findViewById(R.id.last_taken_tv_2)
        lastDate.text = sharedPref.datetaken

        nextDewormDate = root.findViewById(R.id.next_taken_tv_2)
        nextDewormDate.text = sharedPref.nextDate

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

        val parseDate = SimpleDateFormat("yyyy-MM-dd")
        val date = sharedPref.unFormattedNextDate

        if (!date.isNullOrEmpty()){
            val nextDate = parseDate.parse(date)
            val diff: Long = nextDate!!.time - currentDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            Log.d("DAYS LEFT", days.toString())

            if (days.toInt() < 1){
                triggerAlarm()
            }

            //return (days.toDouble() / total_days.toDouble()) * 100.00
            return days
        }

        return 0
    }

    private fun takeDewormDate(){
        sharedPref.datetaken = dewormDate.text.toString()
        last_taken_tv_2.text = dewormDate.text.toString()
        next_taken_tv_2.text = formattedDate
        loadPieChart()
//        newInstance()
    }

    private fun openCalenderDialog(){
        //val dialog = MaterialDialog.Builder(this).title("Date of deworming")
        MaterialDialog(context!!).show { datePicker { dialog, datetime ->
            val yr = datetime.year
            val month = datetime.month + 1
            val day = datetime.dayOfMonth

            val dateFormat = SimpleDateFormat("EEE, d MMM yyyy")
            val parseDate = SimpleDateFormat("yyyy-MM-dd")

            dateDewormerTaken = "$yr-$month-$day"
            val unf_date = parseDate.parse(dateDewormerTaken)

//            val name = dateFormat.format(dateDewormerTaken)
            Log.d("DATE", unf_date!!.toString())

            val nowDate = dateFormat.format(datetime.time)
            dewormDate.setText(nowDate)

            val date = LocalDate.of(datetime.year, datetime.month + 1, datetime.dayOfMonth)
            sharedPref.lastdatetaken = date.toString()
            val nextDate = date.plusDays(90)
            val parsedDate = parseDate.parse(nextDate.toString())
            sharedPref.unFormattedNextDate = nextDate.toString()
            formattedDate = dateFormat.format(parsedDate!!)
            Log.d("NEXT DATE", nextDate.toString())
            sharedPref.nextDate = formattedDate.toString()
        }
        }
    }

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
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent)
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
}
