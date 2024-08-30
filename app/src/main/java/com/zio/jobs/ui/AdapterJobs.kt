package com.zio.jobs.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zio.jobs.database.Job
import com.zio.jobs.R
import com.zio.jobs.database.DataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface Updater {
    fun onUpdate(save: Boolean, job: Job)
}

class AdapterJobs(
    private val context: Context,
    private var itemList: ArrayList<Job> = arrayListOf(),
    private var savedIds: ArrayList<Int> = arrayListOf()
) :
    RecyclerView.Adapter<AdapterJobs.ViewHolder>() {

    private var updater: Updater? = null

    fun setUpdateListener(updater: Updater) {
        this.updater = updater
    }

    fun setItemList(itemList: ArrayList<Job>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

    fun setSavedIds(savedIds: ArrayList<Int>) {
        this.savedIds = savedIds
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val loc: TextView = itemView.findViewById(R.id.location)
        val salary: TextView = itemView.findViewById(R.id.salary)
        val company: TextView = itemView.findViewById(R.id.company)
        val role: TextView = itemView.findViewById(R.id.role)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val save: ImageButton = itemView.findViewById(R.id.save)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_jobs, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val job = itemList.get(position)
        holder.title.text = job.title
        holder.salary.text = job.salary
        holder.loc.text = job.place
        holder.role.text = job.jobRole
        holder.company.text = job.companyName

        var saved = false
        if (job.id in savedIds) saved = true

        if (saved) holder.save.setImageResource(R.drawable.icon_saved)
        else holder.save.setImageResource(R.drawable.icon_unsaved)

        holder.save.setOnClickListener {
            updater?.onUpdate(!saved, job)

        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("job_key", job)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = itemList.size
}

