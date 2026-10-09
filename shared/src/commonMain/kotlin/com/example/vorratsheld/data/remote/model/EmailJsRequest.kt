package com.example.vorratsheld.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Anfrage an die EmailJS-API
@Serializable
data class EmailJsRequest(
    @SerialName("service_id") val serviceId: String,
    @SerialName("template_id") val templateId: String,
    @SerialName("user_id") val publicKey: String,
    val accessToken: String,
    @SerialName("template_params") val templateParams: Map<String, String>
)
