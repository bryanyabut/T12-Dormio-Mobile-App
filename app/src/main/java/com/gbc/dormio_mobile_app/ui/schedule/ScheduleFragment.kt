package com.gbc.dormio_mobile_app.ui.schedule

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.schedule.ScheduleDto
import com.gbc.dormio_mobile_app.viewmodel.schedule.ScheduleViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    private lateinit var tvMonthYear: TextView
    private lateinit var tvSelectedDate: TextView
    private lateinit var calendarGrid: LinearLayout
    private lateinit var rvEvents: RecyclerView
    private lateinit var eventAdapter: ScheduleEventAdapter

    private val viewModel: ScheduleViewModel by activityViewModels()

    private val displayedCalendar = Calendar.getInstance()
    private var selectedDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    private var liveEvents: Map<String, List<ScheduleEvent>> = emptyMap()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMonthYear = view.findViewById(R.id.tvMonthYear)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)
        calendarGrid = view.findViewById(R.id.calendarGrid)
        rvEvents = view.findViewById(R.id.rvEvents)

        eventAdapter = ScheduleEventAdapter()
        rvEvents.layoutManager = LinearLayoutManager(requireContext())
        rvEvents.adapter = eventAdapter

        val btnPrev = view.findViewById<ImageView>(R.id.btnPrevMonth)
        val btnNext = view.findViewById<ImageView>(R.id.btnNextMonth)

        btnPrev.setOnClickListener {
            displayedCalendar.add(Calendar.MONTH, -1)
            renderCalendar()
        }

        btnNext.setOnClickListener {
            displayedCalendar.add(Calendar.MONTH, 1)
            renderCalendar()
        }

        view.findViewById<FloatingActionButton>(R.id.fabAddEvent).setOnClickListener {
            findNavController().navigate(R.id.action_scheduleFragment_to_createEventFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.errorMessage != null) {
                    Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                    viewModel.clearMessages()
                }
                liveEvents = groupSchedulesByDate(state.schedules)
                renderCalendar()
                loadEventsForSelectedDate()
            }
        }

        viewModel.fetchSchedules()
        updateSelectedDateLabel()
    }

    private fun renderCalendar() {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthYear.text = monthFormat.format(displayedCalendar.time)

        calendarGrid.removeAllViews()

        val cal = displayedCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val today = Calendar.getInstance()
        val todayDay = today.get(Calendar.DAY_OF_MONTH)
        val todayMonth = today.get(Calendar.MONTH)
        val todayYear = today.get(Calendar.YEAR)

        val currentMonth = displayedCalendar.get(Calendar.MONTH)
        val currentYear = displayedCalendar.get(Calendar.YEAR)

        val prevCal = displayedCalendar.clone() as Calendar
        prevCal.add(Calendar.MONTH, -1)
        val prevMonthMaxDay = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        var dayCounter = 1
        var nextMonthDay = 1
        val totalCells = if (firstDayOfWeek + maxDay > 35) 42 else 35
        val rows = totalCells / 7

        for (row in 0 until rows) {
            val rowLayout = LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
            }

            for (col in 0 until 7) {
                val cellIndex = row * 7 + col

                val cellLayout = LinearLayout(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(0, dpToPx(6), 0, dpToPx(6))
                }

                val dayText: Int
                val isCurrentMonth: Boolean

                when {
                    cellIndex < firstDayOfWeek -> {
                        dayText = prevMonthMaxDay - (firstDayOfWeek - cellIndex - 1)
                        isCurrentMonth = false
                    }
                    dayCounter <= maxDay -> {
                        dayText = dayCounter
                        isCurrentMonth = true
                        dayCounter++
                    }
                    else -> {
                        dayText = nextMonthDay
                        isCurrentMonth = false
                        nextMonthDay++
                    }
                }

                val isToday = isCurrentMonth && dayText == todayDay &&
                        currentMonth == todayMonth && currentYear == todayYear

                val isSelected = isCurrentMonth && dayText == selectedDay &&
                        currentMonth == selectedMonth && currentYear == selectedYear

                val dayTextView = TextView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(dpToPx(36), dpToPx(36))
                    gravity = Gravity.CENTER
                    text = dayText.toString()
                    textSize = 14f

                    when {
                        isToday -> {
                            setBackgroundResource(R.drawable.bg_calendar_today)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            setTypeface(null, Typeface.BOLD)
                        }
                        isSelected && !isToday -> {
                            setBackgroundResource(R.drawable.bg_calendar_selected)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                            setTypeface(null, Typeface.BOLD)
                        }
                        isCurrentMonth -> {
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        }
                        else -> {
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.calendar_gray_text))
                        }
                    }
                }

                if (isCurrentMonth) {
                    val capturedDay = dayText
                    dayTextView.setOnClickListener {
                        selectedDay = capturedDay
                        selectedMonth = currentMonth
                        selectedYear = currentYear
                        renderCalendar()
                        updateSelectedDateLabel()
                        loadEventsForSelectedDate()
                    }
                }

                cellLayout.addView(dayTextView)

                val dateKey = String.format("%04d-%02d-%02d", currentYear, currentMonth + 1, dayText)
                if (isCurrentMonth && liveEvents.containsKey(dateKey)) {
                    val events = liveEvents[dateKey]!!
                    val dotsLayout = LinearLayout(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER
                        setPadding(0, dpToPx(2), 0, 0)
                    }

                    val categories = events.map { it.category }.distinct().take(3)
                    for (cat in categories) {
                        val dot = View(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(dpToPx(6), dpToPx(6)).also {
                                it.marginStart = dpToPx(1)
                                it.marginEnd = dpToPx(1)
                            }
                            val colorRes = when (cat) {
                                EventCategory.PERSONAL -> R.color.category_personal
                                EventCategory.CLASS -> R.color.category_class
                                EventCategory.WORK -> R.color.category_work
                            }
                            setBackgroundResource(R.drawable.bg_event_indicator)
                            backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
                        }
                        dotsLayout.addView(dot)
                    }
                    cellLayout.addView(dotsLayout)
                }

                rowLayout.addView(cellLayout)
            }

            calendarGrid.addView(rowLayout)
        }
    }

    private fun updateSelectedDateLabel() {
        val cal = Calendar.getInstance()
        cal.set(selectedYear, selectedMonth, selectedDay)
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        tvSelectedDate.text = dateFormat.format(cal.time)
    }

    private fun loadEventsForSelectedDate() {
        val dateKey = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
        val events = liveEvents[dateKey] ?: emptyList()
        eventAdapter.updateEvents(events)
    }

    private fun groupSchedulesByDate(schedules: List<ScheduleDto>): Map<String, List<ScheduleEvent>> {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")
        val dateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateKeyFormat.timeZone = TimeZone.getTimeZone("UTC")
        return schedules
            .groupBy { dto ->
                try {
                    val date = isoFormat.parse(dto.startTime.substringBefore('.'))
                    date?.let { dateKeyFormat.format(it) } ?: ""
                } catch (e: Exception) { "" }
            }
            .filter { it.key.isNotEmpty() }
            .mapValues { (_, dtos) -> dtos.map { it.toScheduleEvent() } }
    }

    private fun ScheduleDto.toScheduleEvent(): ScheduleEvent {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")
        val displayFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val startDate = try { isoFormat.parse(startTime.substringBefore('.')) } catch (e: Exception) { null }
        val endDate = try { isoFormat.parse(endTime.substringBefore('.')) } catch (e: Exception) { null }
        val startLabel = startDate?.let { displayFormat.format(it) } ?: startTime
        val endLabel = endDate?.let { displayFormat.format(it) } ?: endTime
        val category = when (type) {
            "WORK" -> EventCategory.WORK
            "CLASS" -> EventCategory.CLASS
            else -> EventCategory.PERSONAL
        }
        return ScheduleEvent(
            title = title,
            startTime = startLabel,
            endTime = endLabel,
            category = category,
            detail = description ?: location,
            timeLabel = startLabel
        )
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
