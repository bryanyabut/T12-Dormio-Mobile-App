package com.gbc.dormio_mobile_app.ui.schedule

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R

class ScheduleEventAdapter(
    private var events: List<ScheduleEvent> = emptyList()
) : RecyclerView.Adapter<ScheduleEventAdapter.EventViewHolder>() {

    fun updateEvents(newEvents: List<ScheduleEvent>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTimeLabel: TextView = itemView.findViewById(R.id.tvTimeLabel)
        private val tvEventTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        private val tvEventTime: TextView = itemView.findViewById(R.id.tvEventTime)
        private val tvEventDetail: TextView = itemView.findViewById(R.id.tvEventDetail)
        private val tvRecurrence: TextView = itemView.findViewById(R.id.tvRecurrence)
        private val tvCategoryBadge: TextView = itemView.findViewById(R.id.tvCategoryBadge)
        private val eventStripe: View = itemView.findViewById(R.id.eventStripe)
        private val detailsRow: View = itemView.findViewById(R.id.detailsRow)

        fun bind(event: ScheduleEvent) {
            tvTimeLabel.text = event.timeLabel
            tvEventTitle.text = event.title
            tvEventTime.text = "${event.startTime} - ${event.endTime}"

            val context = itemView.context

            val (stripeColorRes, badgeColorRes, categoryName) = when (event.category) {
                EventCategory.PERSONAL -> Triple(
                    R.color.category_personal,
                    R.color.category_personal,
                    "Personal"
                )
                EventCategory.CLASS -> Triple(
                    R.color.category_class,
                    R.color.category_class,
                    "Class"
                )
                EventCategory.WORK -> Triple(
                    R.color.category_work,
                    R.color.category_work,
                    "Work"
                )
            }

            eventStripe.setBackgroundColor(ContextCompat.getColor(context, stripeColorRes))

            tvCategoryBadge.text = categoryName
            val badgeBg = GradientDrawable()
            badgeBg.cornerRadius = 12f * context.resources.displayMetrics.density
            badgeBg.setColor(ContextCompat.getColor(context, badgeColorRes))
            tvCategoryBadge.background = badgeBg

            if (event.detail != null) {
                tvEventDetail.text = event.detail
                tvEventDetail.visibility = View.VISIBLE
                detailsRow.visibility = View.VISIBLE
            } else {
                tvEventDetail.visibility = View.GONE
                detailsRow.visibility = View.GONE
            }

            if (event.recurrence != null) {
                tvRecurrence.text = event.recurrence
                tvRecurrence.visibility = View.VISIBLE
            } else {
                tvRecurrence.visibility = View.GONE
            }
        }
    }
}
