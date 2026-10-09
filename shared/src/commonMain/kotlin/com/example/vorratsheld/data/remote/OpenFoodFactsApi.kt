package com.example.vorratsheld.data.remote

import com.example.vorratsheld.data.remote.model.OpenFoodFactsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

// Zugriff auf Open Food Facts über Ktor (läuft auf Android und iOS)
class OpenFoodFactsApi(private val client: HttpClient) {

    // Beispiel: GET https://world.openfoodfacts.org/api/v2/product/4008400301012.json
    suspend fun getProduct(barcode: String): OpenFoodFactsResponse {
        return client.get("https://world.openfoodfacts.org/api/v2/product/$barcode.json").body()
    }
}
