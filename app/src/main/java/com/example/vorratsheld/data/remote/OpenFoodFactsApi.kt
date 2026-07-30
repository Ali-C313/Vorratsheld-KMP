package com.example.vorratsheld.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {

    // Beispiel: GET https://world.openfoodfacts.org/api/v2/product/4008400301012.json
    @GET("api/v2/product/{barcode}.json")
    suspend fun getProduct(@Path("barcode") barcode: String): OpenFoodFactsResponse
}