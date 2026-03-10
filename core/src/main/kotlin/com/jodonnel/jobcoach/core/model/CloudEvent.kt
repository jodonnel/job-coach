package com.jodonnel.jobcoach.core.model

import com.squareup.moshi.JsonClass
import java.time.Instant
import java.util.UUID

@JsonClass(generateAdapter = true)
data class CloudEvent(
    val specversion: String = "1.0",
    val type: String,
    val source: String,
    val id: String = UUID.randomUUID().toString(),
    val time: String = Instant.now().toString(),
    val datacontenttype: String = "application/json",
    val eventclass: String = "job-coach",
    val data: Map<String, Any?>
)
