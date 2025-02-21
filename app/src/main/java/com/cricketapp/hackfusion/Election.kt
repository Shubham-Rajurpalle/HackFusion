package com.cricketapp.hackfusion

data class Election(
    val id: String = "",
    val electionName: String = "",
    val status: Boolean = false,
    val candidates: List<String> = emptyList(),
    val votes: Map<String, Int> = emptyMap(),
    val totalVotes: Int = 0,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val winner: String = "",
    val voters: Map<String, String> = emptyMap()
)
