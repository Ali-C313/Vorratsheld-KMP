package com.example.vorratsheld.data.remote

import com.example.vorratsheld.data.remote.model.EmailJsRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

// Verschickt die Support-Anfrage per Mail
class EmailJsApi(private val client: HttpClient) {

    suspend fun send(params: Map<String, String>) {
        val response = client.post("https://api.emailjs.com/api/v1.0/email/send") {
            contentType(ContentType.Application.Json)
            setBody(
                EmailJsRequest(
                    serviceId = EmailJsConfig.SERVICE_ID,
                    templateId = EmailJsConfig.TEMPLATE_ID,
                    publicKey = EmailJsConfig.PUBLIC_KEY,
                    accessToken = EmailJsConfig.PRIVATE_KEY,
                    templateParams = params
                )
            )
        }
        if (!response.status.isSuccess()) {
            throw Exception("EmailJS: ${response.status}")
        }
    }
}
