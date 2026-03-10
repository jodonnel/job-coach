package com.jodonnel.jobcoach.core.network

import com.jodonnel.jobcoach.core.model.CloudEvent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class OpenShiftClient @Inject constructor(
    private val httpClient: OkHttpClient,
    @Named("openshift_endpoint") private val endpoint: String
) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val eventAdapter = moshi.adapter(CloudEvent::class.java)

    suspend fun sendCloudEvent(
        type: String,
        source: String,
        data: Map<String, Any?>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val event = CloudEvent(
                type = type,
                source = source,
                data = data
            )

            val json = eventAdapter.toJson(event)
            val body = json.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$endpoint/ingest")
                .post(body)
                .header("Content-Type", "application/cloudevents+json")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
