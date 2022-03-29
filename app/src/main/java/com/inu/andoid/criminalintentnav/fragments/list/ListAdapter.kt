package com.inu.andoid.criminalintentnav.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.inu.andoid.criminalintentnav.R
import com.inu.andoid.criminalintentnav.model.Crime
import kotlinx.android.synthetic.main.list_item_crime.view.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var crimeList = emptyList<Crime>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_crime, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = crimeList[position]
        holder.itemView.crime_item_id.text = currentItem.id.toString()
        holder.itemView.crime_item_title.text = currentItem.title
        // Date 출력 형식 변경
        val cal = Calendar.getInstance()
        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val tz: TimeZone = cal.getTimeZone()
        // Getting zone id
        val zoneId: ZoneId = tz.toZoneId()
        val localDateTime: LocalDateTime =
            LocalDateTime.ofInstant(currentItem.date.toInstant(), zoneId)
        val nowString = localDateTime.format(dtf)
        holder.itemView.crime_item_date.text = nowString
        holder.itemView.crime_item_solved.visibility = if (currentItem.isSolved) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.itemView.crimeItem_layout.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return crimeList.size
    }

    fun setData(crime: List<Crime>) {
        this.crimeList = crime
        notifyDataSetChanged()
    }
}