package com.example.data

data class CommunitySighting(
    val id: String,
    val travelerName: String,
    val travelerAvatar: String, // emoji or avatar name
    val speciesName: String,
    val note: String,
    val locationTag: String,
    val timestamp: Long,
    val photoPlaceholder: String, // lion, elephant, etc.
    val isHelpfulCount: Int,
    val hasUpvoted: Boolean = false,
    val isSavedToPlanning: Boolean = false,
    val viewingTips: String? = null
)
