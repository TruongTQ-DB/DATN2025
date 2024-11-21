package com.graduate.datn.ui.docterName.create_time_work_schedule

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.DocterNameAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.custom.dialog.BottomSheetWheelPicker
import com.graduate.datn.entity.User
import com.graduate.datn.entity.request.DateToWorkRequest
import com.graduate.datn.entity.request.TimeRange
import com.graduate.datn.extension.convertCalendarDayToString
import com.graduate.datn.extension.isNullOrEmpty
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.toast
import com.graduate.datn.ui.admin.work_schedule.ScheduleTime
import com.graduate.datn.utils.Constant
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.android.synthetic.main.fragment_create_time_work_schedule.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*

class CreateTimeWorkScheduleFragment : BaseFragment() {
    private val viewModel: CreateTimeWorkScheduleViewModel by activityViewModels()
    private val mAdapter: DocterNameAdapter by lazy { DocterNameAdapter(requireContext()) }
    private val selectedDays = mutableMapOf<CalendarDay, MutableList<Pair<String, String>>>()
    private val listApprove = mutableListOf<CalendarDay>()
    private val listDelete = mutableListOf<CalendarDay>()
    private val dayOld = mutableMapOf<CalendarDay, MutableList<Pair<String, String>>>()
    private val db = FirebaseFirestore.getInstance()
    private val workScheduleCollection = db.collection(Constant.TABLE_WORK_SCHEDULE)
    private val userCollection = db.collection(Constant.TABLE_USER)
    private lateinit var auth: FirebaseAuth

    //    private val databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_USER)
    private lateinit var picker: BottomSheetWheelPicker
    override fun backFromAddFragment() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_create_time_work_schedule

    override fun initView() {
//        workScheduleCollection.get()
//            .addOnSuccessListener {
//                it.map {
//                    it.reference.delete()
//                }
//            }
        auth = Firebase.auth
        userCollection.whereEqualTo("id", auth.currentUser?.uid).limit(1).get()
            .addOnSuccessListener { documents ->
                val userData = documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(User::class.java)
                }
                viewModel.apply {
                    avatar = userData.first().avatar.toString()
                    name = userData.first().name.toString()
                }
            }
        viewModel.apply {
            avatar = ""
            name = ""
            dataTimeSchedule.clear()
            scheduleTimeValue = null
            alreadyTime = false
        }
        getDataDate()
        updateUI()
    }

    private fun getTimeSchedule(number: Int): ScheduleTime {
        return when (number) {
            30 -> {
                ScheduleTime("30", 30, "30 Phút")
            }
            40 -> {
                ScheduleTime("40", 40, "40 Phút")
            }
            50 -> {
                ScheduleTime("50", 50, "50 Phút")
            }
            60 -> {
                ScheduleTime("60", 60, "1 Giờ")
            }
            70 -> {
                ScheduleTime("70", 70, "1 Giờ 10 Phút")
            }
            80 -> {
                ScheduleTime("80", 80, "1 Giờ 20 Phút")
            }
            90 -> {
                ScheduleTime("90", 90, "1 Giờ 30 Phút")
            }
            100 -> {
                ScheduleTime("100", 100, "1 Giờ 40 Phút")
            }
            110 -> {
                ScheduleTime("110", 110, "1 Giờ 50 Phút")
            }
            120 -> {
                ScheduleTime("120", 120, "2 Giờ")
            }
            else -> {
                ScheduleTime("30", 30, "30 Phút")
            }
        }
    }

    private fun getDataDate() {
        val dataList = mutableMapOf<CalendarDay, MutableList<Pair<String, String>>>()
        val currentDate =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        val currentDateTimestamp =
            SimpleDateFormat("dd/MM/yyyy").parse(currentDate)?.let { Timestamp(it) }

        currentDateTimestamp?.let {
            workScheduleCollection.whereEqualTo("idBarberName", auth.currentUser?.uid)
                .whereGreaterThanOrEqualTo("dateTimestamp", it)
//                .orderBy("date")
                .get().addOnSuccessListener { documents ->
                    hideLoading()
                    if (documents.isEmpty) {
                        toast("Bạn chưa có lịch làm việc")
                        viewModel.alreadyTime = false
                        calendar_view.invalidateDecorators()
                    } else {
                        documents.map { document ->
                            val date = document["date"] as String
                            val timeRanges = document["timeRanges"] as List<Map<String, Any>>
                            val day = convertStringToDate(date)
                            val dayTimeRanges = mutableListOf<Pair<String, String>>()
                            for (timeRange in timeRanges) {
                                val startTime = timeRange["startTime"] as String
                                val endTime = timeRange["endTime"] as String
                                dayTimeRanges.add(Pair(startTime, endTime))
                            }
                            dataList[day] = dayTimeRanges
                            if ((document["approve"] as Boolean)) {
                                listApprove.add(day)
                            }
                        }
                        dataList.mapNotNull {
                            getTimeWordSchedule(it.value[0].first, it.value[0].second)
                            return@mapNotNull
                        }
                        for ((day, timeRanges) in dataList) {
                            val mergedTimeRanges = mergeTimeRanges(timeRanges)
                            val clonedMergedTimeRanges =
                                mergedTimeRanges.map { it.copy() }.toMutableList()
                            selectedDays[day] = clonedMergedTimeRanges.toMutableList()
                            dayOld[day] = clonedMergedTimeRanges.toMutableList()
                        }
                        calendar_view.invalidateDecorators()
                    }
                }.addOnFailureListener {
                    hideLoading()
                    Log.d("thiss", it.message.toString())
                    toast(R.string.error_400)
                }
        }
    }

    private fun getTimeWordSchedule(startTime: String, endTime: String) {
        Log.d("thiss", startTime + "sssss" + endTime)
        val minutesBetween =
            ChronoUnit.MINUTES.between(LocalTime.parse(startTime), LocalTime.parse(endTime))
        viewModel.scheduleTimeValue = getTimeSchedule(minutesBetween.toInt())
        edt_time_schedule.setText(viewModel.scheduleTimeValue?.stringValue)
        viewModel.alreadyTime = true
    }

    fun mergeTimeRanges(timeRanges: List<Pair<String, String>>): MutableList<Pair<String, String>> {
        val mergedTimeRanges = mutableListOf<Pair<String, String>>()

        if (timeRanges.isEmpty()) {
            return mergedTimeRanges
        }

        var startTime = timeRanges[0].first
        var endTime = timeRanges[0].second

        for (i in 1 until timeRanges.size) {
            val currentRange = timeRanges[i]
            val currentStartTime = currentRange.first
            val currentEndTime = currentRange.second

            if (currentStartTime <= endTime) {
                // Khoảng thời gian hiện tại nối tiếp với khoảng thời gian trước đó
                endTime = currentEndTime
            } else {
                // Khoảng thời gian hiện tại không nối tiếp, thêm khoảng thời gian trước đó vào danh sách mergedTimeRanges và bắt đầu một khoảng thời gian mới
                mergedTimeRanges.add(Pair(startTime, endTime))
                startTime = currentStartTime
                endTime = currentEndTime
            }
        }

        // Thêm khoảng thời gian cuối cùng vào danh sách mergedTimeRanges
        mergedTimeRanges.add(Pair(startTime, endTime))

        return mergedTimeRanges
    }

    fun convertStringToDate(stringDate: String): CalendarDay {
        val dateParts = stringDate.split("/")
        val day = dateParts[0].toInt()
        val month = dateParts[1].toInt()
        val year = dateParts[2].toInt()
        return CalendarDay.from(year, month, day)
    }

    private fun updateUI() {
        val pastDaysDecorator = PastDaysDecorator()
        val decorator = EventDecorator(Color.GREEN, selectedDays)
        val decoratorIsApprove = EventDecoratorIsApprove(Color.RED, listApprove)
        calendar_view.addDecorator(decorator)
        calendar_view.addDecorator(decoratorIsApprove)
        calendar_view.addDecorator(pastDaysDecorator)
    }

    class PastDaysDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.isBefore(CalendarDay.today())
        }

        override fun decorate(view: DayViewFacade) {
            view.setDaysDisabled(true)
        }
    }

    private class EventDecoratorIsApprove(
        private val color: Int,
        private val dates: MutableList<CalendarDay>,
    ) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(5F, color))
        }
    }

    private class EventDecorator(
        private val color: Int,
        private val dates: MutableMap<CalendarDay, MutableList<Pair<String, String>>>,
    ) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            Log.e("thiss", dates.toString())
            val timeRanges = dates.getOrDefault(day, null)
            return timeRanges != null && timeRanges.isNotEmpty()
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(5F, color))
        }
    }

    class DisabledDayDecorator(private val disabledDates: List<CalendarDay?>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return disabledDates.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setDaysDisabled(true)
        }
    }


    override fun initData() {

    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

        calendar_view.setOnDateChangedListener { _, date, selected ->
            if (selectedDays.contains(date)) {
                showEditOrDeleteDialog(date, selectedDays[date])
                Log.d("thisss", selectedDays[date].toString())
            } else {
                showTimeRangePickerDialogToAdd(date)
            }
        }

        mAdapter.onClick = {

        }

        edt_time_schedule.onAvoidDoubleClick {
            if (viewModel.alreadyTime == false) {
                picker = BottomSheetWheelPicker(requireContext(), isScheduleTime = true)
                picker.show(childFragmentManager, "")
                lifecycleScope.launch {
                    delay(500)
                    picker.addValueObject(listScheduleTime,
                        viewModel.scheduleTimeValue?.id)
                }

                picker.onclickProvincesPicker = { data ->
                    if (data is ScheduleTime) {
                        edt_time_schedule.setText(data.stringValue)
                        viewModel.scheduleTimeValue = data
                    }
                }
            } else {
                toast("Bạn đã có khung giờ làm việc!")
            }
        }

        btn_create.onAvoidDoubleClick {
            showLoading()
            if (!validateCreate()) {
                hideLoading()
            } else {
                val filteredSelectedDays = selectedDays.filterKeys { !listApprove.contains(it) }
                val data = filteredSelectedDays.let { splitTimeRangesInto30MinuteSlots(it) }
                    .mapNotNull { (date, listTime) ->
                        val day = convertCalendarDayToString(date)
                        val time = listTime.map { time ->
                            val startTime = time.first
                            val endTime = time.second
                            val stater = 0
                            TimeRange(startTime, endTime, stater)
                        }
                        Log.e("testDataa", day + "     " + time.toString())
                        DateToWorkRequest(auth.currentUser?.uid,
                            viewModel.avatar,
                            viewModel.name,
                            day,
                            time,
                            dateTimestamp = SimpleDateFormat("dd/MM/yyyy").parse(day)
                                ?.let { it1 -> Timestamp(it1) },
                            false)
                    }
                val dataOld = dayOld.let { splitTimeRangesInto30MinuteSlots(it) }
                    .mapNotNull { (date, listTime) ->
                        val day = convertCalendarDayToString(date)
                        val time = listTime.map { time ->
                            val startTime = time.first
                            val endTime = time.second
                            val stater = 0
                            TimeRange(startTime, endTime, stater)
                        }
                        Log.e("testDataa", day + "     " + time.toString())
                        DateToWorkRequest(auth.currentUser?.uid,
                            viewModel.avatar,
                            viewModel.name,
                            day,
                            time,
                            dateTimestamp = SimpleDateFormat("dd/MM/yyyy").parse(day)
                                ?.let { it1 -> Timestamp(it1) },
                            false)
                    }

                if (data.isNullOrEmpty()) {
                    toast("Vui lòng chọn thời gian hợp lệ")
                    hideLoading()
                    return@onAvoidDoubleClick
                }

                val completionTasks = mutableListOf<Task<Void>>() // Danh sách các Task

                data.forEach { it ->
                    Log.e("testDataaa", it.date + "     " + it.timeRanges.toString())
                    val query = workScheduleCollection.whereEqualTo("idBarberName",
                        auth.currentUser?.uid).whereEqualTo("date", it.date)

                    val existingScheduleTask = query.get()

                    existingScheduleTask.addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // Nếu không tìm thấy dữ liệu, thêm mới
                            val day = it.date
                            val datas = hashMapOf("idDocterName" to it.idDocterName,
                                "date" to day,
                                "avatar" to it.avatar,
                                "name" to it.name,
                                "approve" to false,
                                "timeRanges" to it.timeRanges?.map { timeRange ->
                                    hashMapOf("startTime" to timeRange.startTime,
                                        "endTime" to timeRange.endTime,
                                        "stater" to timeRange.stater,
                                        "selector" to timeRange.selector,
                                        "timeStamp" to SimpleDateFormat("dd/MM/yyyy HH:mm").parse("$day ${timeRange.startTime}")
                                            ?.let { it1 -> Timestamp(it1) })
                                },
                                "dateTimestamp" to SimpleDateFormat("dd/MM/yyyy").parse(day)
                                    ?.let { it1 -> Timestamp(it1) })

                            val documentRef = workScheduleCollection.document()
                            val task = documentRef.set(datas)
                            Log.d("dataaa", datas.toString())
                            completionTasks.add(task)
                        } else {
                            // Nếu tìm thấy dữ liệu, cập nhật
                            val existingSchedule = documents.documents[0]
                            val existingScheduleId = existingSchedule.id
                            val timeRangesData = it.timeRanges?.map { timeRange ->
                                hashMapOf("startTime" to timeRange.startTime,
                                    "endTime" to timeRange.endTime,
                                    "stater" to 0,
                                    "selector" to false,
                                    "timeStamp" to SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${it.date} ${timeRange.startTime}")
                                        ?.let { it1 -> Timestamp(it1) })
                            }
                            val updatedScheduleData = hashMapOf("avatar" to it.avatar,
                                "name" to it.name,
                                "timeRanges" to timeRangesData)

                            val existingScheduleRef =
                                workScheduleCollection.document(existingScheduleId)
                            val task = existingScheduleRef.update(updatedScheduleData)
                            completionTasks.add(task)

                            if (it.timeRanges?.isEmpty() != false && !it.date.isNullOrEmpty()) {
                                Log.e("testttt", it.toString())
                                documents.map {
                                    it.reference.delete()
                                }
                                val documentRef = workScheduleCollection.document(it.date)
                                val task = documentRef.delete()
                                completionTasks.add(task)
                            }
                        }
                    }
                }

                // Sử dụng Tasks.whenAllSuccess để đợi tất cả các task hoàn thành
                Tasks.whenAll(completionTasks).addOnSuccessListener {
                    hideLoading()
                    toast("Gửi yêu cầu thành công")
                    backPressed()
                }.addOnFailureListener { e ->
                    hideLoading()
                    toast(e.toString())
                }
            }
        }
    }

    private fun validateCreate(): Boolean {
        if (edt_time_schedule.text.trim().isEmpty()) {
            toast("Vui lòng chọn thời gian Lịch!")
            return false
        }
        if (selectedDays.isEmpty()) {
            toast("Vùi lòng chọn Lịch làm việc!")
            return false
        }
        if (mapsEqual(selectedDays, dayOld)) {
            toast("Vùi lòng chọn Lịch làm việc!")
            return false
        }
        return true
    }

    fun mapsEqual(
        map1: Map<CalendarDay, List<Pair<String, String>>>,
        map2: Map<CalendarDay, List<Pair<String, String>>>,
    ): Boolean {
        if (map1.size != map2.size) {
            return false
        }

        for ((key, value) in map1) {
            if (!map2.containsKey(key) || !listsEqual(map2[key], value)) {
                return false
            }
        }

        return true
    }

    fun listsEqual(
        list1: List<Pair<String, String>>?,
        list2: List<Pair<String, String>>?,
    ): Boolean {
        if (list1 == null && list2 == null) {
            return true
        }
        if (list1 == null || list2 == null) {
            return false
        }
        return list1 == list2
    }

    private fun showEditOrDeleteDialog(
        date: CalendarDay,
        time: MutableList<Pair<String, String>>?,
    ) {
        val builder = AlertDialog.Builder(context)
        val mess = time?.joinToString { times -> "${times.first} - ${times.second}" }
        builder.setMessage(mess)

        if (!dayOld.containsKey(date) || !listApprove.contains(date)) {
            builder.setTitle("Thời gian chờ xét duyệt")
            builder.setPositiveButton("Chọn lại") { _, _ ->
                showTimeRangePickerDialogToAdd(date, true)
            }
            builder.setNegativeButton("Xoá") { _, _ ->
                if (dayOld.containsKey(date)) {
                    selectedDays[date]?.clear()
                } else {
                    selectedDays.remove(date)
                }
//                listDelete.add()
                calendar_view.invalidateDecorators()
            }
            builder.setNeutralButton("Thêm") { _, _ ->
                showTimeRangePickerDialogToAdd(date, isAdd = true)
            }
        } else {
            builder.setTitle("Thời gian đã chọn")
        }
        builder.show()
    }

    private fun showTimeRangePickerDialogToAdd(
        date: CalendarDay,
        isReselect: Boolean = false,
        isAdd: Boolean = false,
    ) {
        val calendar = Calendar.getInstance()
        var hourOfDay: Int? = 0
        var minute: Int? = 0
        if (isAdd) {
            val startTime = selectedDays[date]?.last()!!.second
            val startTimeParts = startTime.split(":")
            hourOfDay = startTimeParts[0].toInt()
            minute = startTimeParts[1].toInt()
        } else {
            if (date == CalendarDay.today()) {
                hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                minute = calendar.get(Calendar.MINUTE)
            } else {
                hourOfDay = 0
                minute = 0
            }
        }


        val startTimePickerDialog = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener { _, startHour, startMinute ->
                val startSelectedTime = String.format("%02d:%02d", startHour, startMinute)
                if (isAdd) {
                    if (isEndTimeValid(selectedDays[date]?.last()!!.second, startSelectedTime)) {
                        showEndTimePickerDialogToAdd(date, startSelectedTime)
                    } else {
                        showTimeRangePickerDialogToAdd(date, isAdd = true)
                        toast("Thời gian bắt đầu phải lớn hơn ${selectedDays[date]?.last()?.second}")
                    }
                } else {
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val currentTime = sdf.format(Calendar.getInstance().time)
                    if (startSelectedTime <= currentTime && date == CalendarDay.today()) {
                        showTimeRangePickerDialogToAdd(date)
                        toast("Thời gian bắt đầu phải lớn hơn hiện tại ${Calendar.getInstance().time}")
                    } else {
                        showEndTimePickerDialogToAdd(date,
                            startSelectedTime,
                            isReselect = isReselect)
                    }
                }
            },
            hourOfDay,
            minute,
            true)

        startTimePickerDialog.setTitle("Chọn thời gian bắt đầu")
        startTimePickerDialog.show()
    }

    private fun showEndTimePickerDialogToAdd(
        date: CalendarDay,
        startTime: String,
        isReselect: Boolean = false,
    ) {
        val startTimeParts = startTime.split(":")
        val startHour = startTimeParts[0].toInt()
        val startMinute = startTimeParts[1].toInt()

        val endTimePickerDialog =
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, endHour, endMinute ->
                val endSelectedTime = String.format("%02d:%02d", endHour, endMinute)
                if (isEndTimeValid(startTime, endSelectedTime)) {
                    if (isReselect) selectedDays[date]?.clear()
                    addTimeRangeToSelectedDays(date, startTime, endSelectedTime)
                } else {
                    showEndTimePickerDialogToAdd(date, startTime, isReselect = isReselect)
                    toast("Thời gian kết thúc phải lớn hơn $startTime")
                }
            }, startHour, startMinute, true)
        endTimePickerDialog.setTitle("Chọn thời gian kết thúc")
        endTimePickerDialog.show()
    }

    private fun addTimeRangeToSelectedDays(date: CalendarDay, startTime: String, endTime: String) {
        val tempSelectedDays = selectedDays.toMutableMap()
        if (!tempSelectedDays.containsKey(date)) {
            tempSelectedDays[date] = mutableListOf()
        }
        tempSelectedDays[date]?.add(Pair(startTime, endTime).copy())

        selectedDays.clear()
        selectedDays.putAll(tempSelectedDays.mapValues { entry ->
            entry.value.map { it.copy() }.toMutableList().toList().toMutableList()
        }.toMutableMap())

        Log.d("thisssss", "ssss" + selectedDays.toString())
        Log.d("thisssss", "aaaa" + dayOld.toString())
        calendar_view.invalidateDecorators()
    }

    private fun splitTimeRangesInto30MinuteSlots(timeRangesByDate: Map<CalendarDay, List<Pair<String, String>>>): MutableMap<CalendarDay, MutableList<Pair<String, String>>> {
        val slotsByDate = mutableMapOf<CalendarDay, MutableList<Pair<String, String>>>()

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()

        for ((date, timeRanges) in timeRangesByDate) {
            val slots = mutableListOf<Pair<String, String>>()

            for (timeRange in timeRanges) {
                val startTime = timeRange.first
                val endTime = timeRange.second

                val startCalendar = Calendar.getInstance()
                startCalendar.time = sdf.parse(startTime) ?: Date()

                val endCalendar = Calendar.getInstance()
                endCalendar.time = sdf.parse(endTime) ?: Date()

                while (startCalendar.before(endCalendar)) {
                    val slotStart = sdf.format(startCalendar.time)
                    calendar.time = startCalendar.time
                    calendar.add(Calendar.MINUTE,
                        viewModel.scheduleTimeValue?.intValue ?: 30)
                    if (calendar.time <= endCalendar.time) {
                        val slotEnd = sdf.format(calendar.time)
                        slots.add(Pair(slotStart, slotEnd))
                    }
                    startCalendar.time = calendar.time
                }
            }

            slotsByDate[date] = slots
        }

        return slotsByDate
    }


    private fun isEndTimeValid(startTime: String, endTime: String): Boolean {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = sdf.parse(startTime)
        val end = sdf.parse(endTime)
        return start.before(end)
    }

    override fun <U> getObjectResponse(data: U) {

    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private val listScheduleTime = listOf(
        ScheduleTime("30", 30, "30 Phút"),
        ScheduleTime("40", 40, "40 Phút"),
        ScheduleTime("50", 50, "50 Phút"),
        ScheduleTime("60", 60, "1 Giờ"),
        ScheduleTime("70", 70, "1 Giờ 10 Phút"),
        ScheduleTime("80", 80, "1 Giờ 20 Phút"),
        ScheduleTime("90", 90, "1 Giờ 30 Phút"),
        ScheduleTime("100", 100, "1 Giờ 40 Phút"),
        ScheduleTime("110", 110, "1 Giờ 50 Phút"),
        ScheduleTime("120", 120, "2 Giờ"),
    )
}