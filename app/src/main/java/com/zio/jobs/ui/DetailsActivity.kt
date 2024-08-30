package com.zio.jobs.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.zio.jobs.MainViewModel
import com.zio.jobs.database.Job
import com.zio.jobs.R
import com.zio.jobs.database.DataBase
import com.zio.jobs.databinding.ActivityDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var job: Job? = null
    private val viewModel: MainViewModel by viewModels()
    private var saved = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        job = intent.getParcelableExtra<Job>("job_key")

        job?.let {
            setListener()
            viewModel.getSavedIdList(baseContext)
            loadDetails(it)
        }


    }

    private fun loadDetails(it: Job) {
        Glide.with(this)
            .load(it.thumbUrl)
            .into(binding.img)

        binding.title.text = it.title
        binding.company.text = it.companyName
        binding.role.text = it.jobRole
        binding.salary.text = it.salary
        binding.loc.text = it.place
        binding.qual.text = it.qualification
        binding.details.text = it.otherDetails
        binding.num.text = "applicants : ${it.numApplications}"


        binding.save.setOnClickListener {
            if (saved) viewModel.removeJob(job!!, baseContext)
            else viewModel.saveJob(job!!, baseContext)
        }
    }

    private fun setListener() {
        viewModel.savedIds.observe(this, Observer { ids ->
            if (job?.id in ids) saved = true
            else saved = false

            if (saved) binding.save.setImageResource(R.drawable.icon_saved)
            else binding.save.setImageResource(R.drawable.icon_unsaved)
        })

    }

}