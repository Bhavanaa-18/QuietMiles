package com.example.aismartcall.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_log_entries")
data class CallLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneNumber: String,
    val timestamp: Long,
    val decision: String
)
