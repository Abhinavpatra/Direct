package com.nexus.app.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ParsedTaskPayload(
    val task: String,
    val time: String,
    val reason: String,
    val actionType: String,
)
