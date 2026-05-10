package com.example.aismartcall.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.aismartcall.data.entity.CallLogEntry

@Dao
interface CallLogDao {
    @Insert
    suspend fun insert(entry: CallLogEntry)

    @Query("SELECT COUNT(*) FROM call_log_entries WHERE phoneNumber = :number AND timestamp >= :startTime")
    suspend fun countCallsFromNumberInTimeWindow(number: String, startTime: Long): Int
}
