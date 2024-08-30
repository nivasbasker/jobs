package com.zio.jobs

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.zio.jobs.database.Job
import org.json.JSONObject

@SuppressLint("StaticFieldLeak") //using application context for safety, i guess it should not be a problem
object DataFetcher {

    interface ResultBack {
        fun onResult(response: ArrayList<Job>)
        fun onError(code: Int)
    }

    private lateinit var requestQueue: RequestQueue
    private lateinit var context: Context
    private val url: String = "https://testapi.getlokalapp.com/common/jobs?page="

    fun init(cont: Context) {

        context = cont.applicationContext
        requestQueue = Volley.newRequestQueue(context)
    }

    fun fetchData(page: Int = 1, call: ResultBack) {

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url + page, null,
            { response ->

                Log.e("Volley", "fetched data")
                call.onResult(unwrap(response))
            },
            { error ->
                call.onError(1)
                Log.e("Volley", "Error fetching data: ${error.message}")
            })

        requestQueue.add(jsonObjectRequest)
    }

    private fun unwrap(response: JSONObject): ArrayList<Job> {
        val list = response.getJSONArray("results")
        val result: MutableList<Job> = mutableListOf()
        Log.e("Volley", "fetched data ${list.length()}")

        for (i in 0 until list.length()) {

            var obj = list.getJSONObject(i)
            var job = fromJson(obj)
            job?.let {
                result.add(job)
                //Log.e("Volley", "unwrapped data : ${job.toString()}")
            }
        }

        Log.e("Volley", "${result.size}")

        return ArrayList(result)
    }

    private fun fromJson(obj: JSONObject): Job? {

        val id = obj.optInt("id", 0)
        val title = obj.optString("title", null)
        val companyName = obj.optString("company_name", null)
        val jobRole = obj.optString("job_role", null)
        val jobHours = obj.optString("job_hours", null)
        val createdOn = obj.optString("created_on", null)

        if (id == null || title == null || companyName == null || jobRole == null || jobHours == null || createdOn == null) {
            Log.e("Volley", "no 1")
            return null
        }

        // Handle optional fields or those with default values
        val otherDetails = obj.optString("other_details", "")
        val openingsCount = obj.optInt("openings_count", 1)
        val jobCategory = obj.optString("job_category", "")
        val numApplications = obj.optInt("num_applications", 0)
        val salaryMax = obj.optInt("salary_max", 0)
        val salaryMin = obj.optInt("salary_min", 0)

        // Check for required fields in primaryDetails
        val primaryDetails = obj.optJSONObject("primary_details")
        val place = primaryDetails?.optString("Place", null)
        val salary = primaryDetails?.optString("Salary", null)
        val experience = primaryDetails?.optString("Experience", null)
        val qualification = primaryDetails?.optString("Qualification", null)

        if (place == null || salary == null || experience == null || qualification == null) {
            // Return null if any of the essential fields in primaryDetails are missing
            Log.e("Volley", "no 2")

            return null
        }

        // Check for required fields in creatives
        val creatives = obj.optJSONArray("creatives").getJSONObject(0)
        val file = creatives?.optString("file", null)
        val thumbUrl = creatives?.optString("thumb_url", null)

        if (file == null || thumbUrl == null) {
            // Return null if any of the essential fields in creatives are missing
            Log.e("Volley", "no 3")

            return null
        }

        // If all required fields are present, create and return the Job object
        return Job(
            id = id,
            title = title,
            companyName = companyName,
            jobRole = jobRole,
            jobHours = jobHours,
            otherDetails = otherDetails,
            openingsCount = openingsCount,
            jobCategory = jobCategory,
            numApplications = numApplications,
            salaryMax = salaryMax,
            salaryMin = salaryMin,
            createdOn = createdOn,
            place = place,
            salary = salary,
            experience = experience,
            qualification = qualification,
            file = file,
            thumbUrl = thumbUrl
        )
    }


}