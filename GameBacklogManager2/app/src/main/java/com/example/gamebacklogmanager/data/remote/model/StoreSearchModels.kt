package com.example.gamebacklogmanager.data.remote.model

import com.google.gson.annotations.SerializedName
/**
 * Response for Steam Store search results.
 */
data class StoreSearchResponse(
    val total: Int, // Total number of matches
    val items: List<StoreItem>? // List of returned store items
)
/**
 * A single item returned from the Steam Store search API.
 */
data class StoreItem(
    val id: Int,
    val name: String,
    @SerializedName("tiny_image") val tinyImage: String?,
    @SerializedName("header_image") val headerImage: String?,
    val price: StorePrice?,
    val metascore: String?
)
/**
 * Pricing details for a Store item.
 */
data class StorePrice(
    val currency: String,
    val initial: Int,
    val final: Int,
    @SerializedName("discount_percent") val discountPercent: Int
)
