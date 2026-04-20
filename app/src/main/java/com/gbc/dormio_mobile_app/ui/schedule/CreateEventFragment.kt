package com.gbc.dormio_mobile_app.ui.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.viewmodel.schedule.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class CreateEventFragment : Fragment(R.layout.fragment_create_event) {

    private val viewModel: ScheduleViewModel by activityViewModels()

    private var selectedCategory: EventCategory = EventCategory.WORK
    private val calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val btnTypeWork = view.findViewById<LinearLayout>(R.id.btnTypeWork)
        val btnTypeClass = view.findViewById<LinearLayout>(R.id.btnTypeClass)
        val btnTypePersonal = view.findViewById<LinearLayout>(R.id.btnTypePersonal)
        val etEventTitle = view.findViewById<EditText>(R.id.etEventTitle)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvStartTime = view.findViewById<TextView>(R.id.tvStartTime)
        val layoutDatePicker = view.findViewById<LinearLayout>(R.id.layoutDatePicker)
        val layoutTimePicker = view.findViewById<LinearLayout>(R.id.layoutTimePicker)
        val switchRecurrence = view.findViewById<SwitchCompat>(R.id.switchRecurrence)
        val tvRecurrenceLabel = view.findViewById<TextView>(R.id.tvRecurrenceLabel)
        val btnSaveEvent = view.findViewById<Button>(R.id.btnSaveEvent)

        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Event type selection
        val typeButtons = listOf(btnTypeWork, btnTypeClass, btnTypePersonal)
        fun updateTypeSelection(selected: LinearLayout, category: EventCategory) {
            selectedCategory = category
            for (btn in typeButtons) {
                if (btn == selected) {
                    btn.setBackgroundResource(R.drawable.bg_event_type_selected)
                    for (i in 0 until btn.childCount) {
                        val child = btn.getChildAt(i)
                        if (child is ImageView) child.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
                        if (child is TextView) child.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                } else {
                    btn.setBackgroundResource(R.drawable.bg_event_type_unselected)
                    for (i in 0 until btn.childCount) {
                        val child = btn.getChildAt(i)
                        if (child is ImageView) child.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
                        if (child is TextView) child.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    }
                }
            }
        }

        btnTypeWork.setOnClickListener { updateTypeSelection(btnTypeWork, EventCategory.WORK) }
        btnTypeClass.setOnClickListener { updateTypeSelection(btnTypeClass, EventCategory.CLASS) }
        btnTypePersonal.setOnClickListener { updateTypeSelection(btnTypePersonal, EventCategory.PERSONAL) }

        // Date picker
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)

        layoutDatePicker.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    tvDate.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        tvStartTime.text = timeFormat.format(calendar.time)

        layoutTimePicker.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    tvStartTime.text = timeFormat.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }

        switchRecurrence.setOnCheckedChangeListener { _, isChecked ->
            tvRecurrenceLabel.text = if (isChecked) "Repeats weekly" else "Does not repeat"
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                btnSaveEvent.isEnabled = !state.isLoading
                if (state.successMessage != null) {
                    viewModel.clearMessages()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                if (state.errorMessage != null) {
                    Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                    viewModel.clearMessages()
                }
            }
        }

        btnSaveEvent.setOnClickListener {
            val title = etEventTitle.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an event title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")

            val endCalendar = calendar.clone() as Calendar
            endCalendar.add(Calendar.HOUR_OF_DAY, 1)

            val type = when (selectedCategory) {
                EventCategory.WORK -> "WORK"
                EventCategory.CLASS -> "CLASS"
                EventCategory.PERSONAL -> "PERSONAL"
            }

            viewModel.createSchedule(
                title = title,
                type = type,
                startTime = isoFormat.format(calendar.time),
                endTime = isoFormat.format(endCalendar.time)
            )
        }
    }
}
