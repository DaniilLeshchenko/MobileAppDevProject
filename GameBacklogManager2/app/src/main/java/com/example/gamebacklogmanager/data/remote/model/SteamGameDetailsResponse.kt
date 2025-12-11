package com.example.gamebacklogmanager.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper for Steam game details API.
 */
data class SteamGameDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SteamGameDetails?
)

data class SteamGameDetails(
    val type: String?,
    val name: String,
    @SerializedName("steam_appid") val steamAppId: Int,
    @SerializedName("is_free") val isFree: Boolean?,
    @SerializedName("short_description") val shortDescription: String?,
    @SerializedName("header_image") val headerImage: String?,
    @SerializedName("genres") val genres: List<Genre>?,
    @SerializedName("categories") val categories: List<Category>?,
    @SerializedName("price_overview") val priceOverview: PriceOverview?,
    @SerializedName("metacritic") val metacritic: Metacritic?,
    @SerializedName("release_date") val releaseDate: ReleaseDate?
)
/** Genre entry used in Steam API. */
data class Genre(
    val id: String,
    val description: String
)
/** Category info such as "Single-player", "Full controller support". */
data class Category(
    val id: Int,
    val description: String
)
/** Pricing information including currency and discount. */
data class PriceOverview(
    val currency: String,
    val initial: Int,
    val final: Int,
    @SerializedName("discount_percent") val discountPercent: Int
)
/** Supported platforms. */
data class Platforms(
    val windows: Boolean,
    val mac: Boolean,
    val linux: Boolean
)
/** Metacritic score and link. */
data class Metacritic(
    val score: Int,
)
/** Release date details. */
data class ReleaseDate(
    val coming_soon: Boolean,
    val date: String
)