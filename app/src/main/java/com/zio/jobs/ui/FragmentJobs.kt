package com.zio.jobs.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.zio.jobs.MainViewModel
import com.zio.jobs.R
import com.zio.jobs.database.Job
import com.zio.jobs.databinding.FragmentJobsBinding

class FragmentJobs : Fragment() {

    private lateinit var jobsView: RecyclerView
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: AdapterJobs
    private lateinit var pageView: TextView
    private lateinit var prev: Button
    private lateinit var next: Button
    private lateinit var pbar: ProgressBar

    private var currentPage = 1

    private lateinit var binding: FragmentJobsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FJ", " inside 0")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("FJ", " inside 1")
        binding = FragmentJobsBinding.inflate(inflater, container, false)
        val root = binding.root

        pageView = binding.page
        pbar = binding.pbar
        prev = binding.prev
        next = binding.next
        jobsView = binding.jobsView

        adapter = AdapterJobs(context = requireContext())
        jobsView.adapter = adapter

        setListeners()

        viewModel.getSavedIdList(requireContext())
        viewModel.downloadJobs(requireContext(), currentPage)

        return root
    }

    private fun setListeners() {
        Log.d("FJ", " inside 2")
        viewModel.allJobs.observe(viewLifecycleOwner, Observer { jobs ->
            adapter.setItemList(jobs as ArrayList)
        })

        viewModel.savedIds.observe(viewLifecycleOwner, Observer { jobs ->
            adapter.setSavedIds(jobs as ArrayList)
        })

        viewModel.page.observe(viewLifecycleOwner, Observer { page ->
            Log.d("FJ", " inside 3 $page")
            currentPage = page
            pageView.text = currentPage.toString()
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { load ->
            if (load) {
                pbar.visibility = View.VISIBLE
                prev.isEnabled = false
                next.isEnabled = false
            } else {
                pbar.visibility = View.INVISIBLE
                prev.isEnabled = true
                next.isEnabled = true
            }
        })

        adapter.setUpdateListener(object : Updater {
            override fun onUpdate(save: Boolean, job: Job) {
                if (save) viewModel.saveJob(job, requireContext())
                else viewModel.removeJob(job, requireContext())
            }

        })

        prev.setOnClickListener {
            if (currentPage > 1) {
                viewModel.downloadJobs(requireContext(), currentPage - 1)
            }
        }
        next.setOnClickListener {
            if (currentPage < 3)
                viewModel.downloadJobs(requireContext(), currentPage + 1)
        }
    }
}
