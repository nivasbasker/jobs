package com.zio.jobs.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Job)

    @Query("SELECT * FROM jobs")
    suspend fun getSavedJobs(): List<Job>

    @Query("SELECT id FROM jobs")
    suspend fun getSavedIds(): List<Int>

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun remove(id: Int)

}