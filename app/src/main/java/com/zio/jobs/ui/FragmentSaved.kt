package com.zio.jobs.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.zio.jobs.MainViewModel
import com.zio.jobs.database.Job
import com.zio.jobs.R
import com.zio.jobs.database.DataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentSaved : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: AdapterJobs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_saved, container, false)
        adapter = AdapterJobs(context = requireContext())
        root.findViewById<RecyclerView>(R.id.jobs_view).adapter = adapter

        setListeners()

        context?.let {
            viewModel.getSavedList(it)
            viewModel.getSavedIdList(it)
        }

        return root
    }

    private fun setListeners() {
        viewModel.savedJobs.observe(viewLifecycleOwner, Observer { jobs ->
            adapter.setItemList(jobs as ArrayList)
        })

        viewModel.savedIds.observe(viewLifecycleOwner, Observer { jobs ->
            adapter.setSavedIds(jobs as ArrayList)
        })

        adapter.setUpdateListener(object : Updater {
            override fun onUpdate(save: Boolean, job: Job) {
                if (save) viewModel.saveJob(job,context!!)
                else viewModel.removeJob(job, context!!)
            }

        })
    }


}