package com.example.gamebacklogmanager.data.remote.model

import com.google.gson.annotations.SerializedName

data class StoreSearchResponse(
    val total: Int,
    val items: List<StoreItem>?
)

data class StoreItem(
    val id: Int,
    val name: String,
    @SerializedName("tiny_image") val tinyImage: String?, // Usually small header
    @SerializedName("header_image") val headerImage: String?, // Sometimes available
    val price: StorePrice?,
    val metascore: String?
)

data class StorePrice(
    val currency: String,
    val initial: Int,
    val final: Int, // e.g. 1999 for $19.99
    @SerializedName("discount_percent") val discountPercent: Int
)

enum class StoreSortOption {
    RELEVANCE,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    NAME_AZ
}