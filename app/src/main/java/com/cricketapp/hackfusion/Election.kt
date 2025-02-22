package com.cricketapp.hackfusion

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Election(
    val id: String = "",
    val electionName: String = "",
    val candidates: List<String> = emptyList(),
    val votes: Map<String, Int> = emptyMap(),  // Changed Int to Long
    val voters: Map<String, String> = emptyMap(),
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val totalVotes:Int=0,
    val status: Boolean = false,
    val winner: String? = null  // Made nullable
) : Parcelable {
    // Empty constructor for Firebase
    constructor() : this("", "", emptyList(), emptyMap(), emptyMap(), 0L, 0L, 0,false, null)
}