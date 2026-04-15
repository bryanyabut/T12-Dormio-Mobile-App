package com.gbc.dormio_mobile_app.ui.notifications

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.notification.NotificationModel
import com.gbc.dormio_mobile_app.data.model.notification.NotificationType

class NotificationAdapter(private val items: List<NotificationModel>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: com.google.android.material.card.MaterialCardView = view.findViewById(R.id.notificationCard)
        val title: TextView = view.findViewById(R.id.notifTitle)
        val message: TextView = view.findViewById(R.id.notifMessage)
        val time: TextView = view.findViewById(R.id.notifTime)
        val icon: ImageView = view.findViewById(R.id.notifIcon)
        val iconBg: View = view.findViewById(R.id.iconBackground)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.title.text = item.title
        holder.message.text = item.message
        holder.time.text = "Just now"

        val (colorRes, iconRes) = when (item.type) {
            NotificationType.BILL -> R.color.feature_green to R.drawable.outline_attach_money_24
            NotificationType.CHORE -> R.color.feature_orange to android.R.drawable.ic_menu_delete
            NotificationType.MAINTENANCE -> R.color.feature_light_blue to android.R.drawable.ic_menu_preferences
            else -> R.color.home_summary_blue to android.R.drawable.ic_popup_reminder
        }

        val color = ContextCompat.getColor(context, colorRes)

        holder.card.strokeColor = color
        holder.icon.setImageResource(iconRes)
        holder.icon.imageTintList = ColorStateList.valueOf(color)

        holder.iconBg.backgroundTintList = ColorStateList.valueOf(color).withAlpha(30)
    }

    override fun getItemCount() = items.size
}