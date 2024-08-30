package com.zio.jobs

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zio.jobs.database.DataBase
import com.zio.jobs.database.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {


    // MutableLiveData to hold the list of jobs
    private val _allJobs = MutableLiveData<List<Job>>()
    val allJobs: LiveData<List<Job>> get() = _allJobs

    private val _savedJobs = MutableLiveData<List<Job>>()
    val savedJobs: LiveData<List<Job>> get() = _savedJobs

    private val _savedIds = MutableLiveData<List<Int>>()
    val savedIds: LiveData<List<Int>> get() = _savedIds

    private val _page = MutableLiveData<Int>(1)
    val page: LiveData<Int> get() = _page

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> get() = _loading


    fun downloadJobs(it: Context, currentPage: Int = 1) {
        _loading.value = true
        val fetcher = DataFetcher
        fetcher.init(it)
        fetcher.fetchData(currentPage, object : DataFetcher.ResultBack {
            override fun onResult(response: ArrayList<Job>) {
                _allJobs.value = response
                _page.value = currentPage
                _loading.value = false
                Log.d("VM", "got all jobs")
            }

            override fun onError(code: Int) {

            }
        })
    }

    fun getSavedIdList(it: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DataBase.getInstance(it)
                val ids = db.jobDao().getSavedIds()

                withContext(Dispatchers.Main) {
                    _savedIds.value = ids
                }
            } catch (e: Exception) {
                Log.e("com.zio.jobs.ui.AdapterJobs", "Error saving job", e)
            }
        }
    }

    fun getSavedList(it: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DataBase.getInstance(it)
                val jobs = db.jobDao().getSavedJobs()
                Log.d("VM", "got saved jobs")
                withContext(Dispatchers.Main) {
                    _savedJobs.value = jobs
                }
            } catch (e: Exception) {
                Log.e("com.zio.jobs.ui.AdapterJobs", "Error saving job", e)
            }
        }
    }

    fun saveJob(job: Job, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DataBase.getInstance(context)
                db.jobDao().insert(job)
            } catch (e: Exception) {
                Log.e("com.zio.jobs.ui.AdapterJobs", "Error saving job", e)
            }
        }
        getSavedList(context)
        getSavedIdList(context)
    }

    fun removeJob(job: Job, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DataBase.getInstance(context)
                db.jobDao().remove(job.id)
            } catch (e: Exception) {
                Log.e("com.zio.jobs.ui.AdapterJobs", "Error saving job", e)
            }
        }
        getSavedList(context)
        getSavedIdList(context)
    }

    fun loadPrev() {

    }

    fun loadNext() {

    }
}