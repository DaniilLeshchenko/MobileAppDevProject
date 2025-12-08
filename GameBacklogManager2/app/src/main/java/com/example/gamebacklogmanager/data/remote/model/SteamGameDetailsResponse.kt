package com.example.gamebacklogmanager.data.remote.model

import com.google.gson.annotations.SerializedName

data class SteamGameDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SteamGameDetails?
)

data class SteamGameDetails(
    val type: String?,
    val name: String,
    @SerializedName("steam_appid") val steamAppId: Int,
    @SerializedName("required_age") val requiredAge: Any?, 
    @SerializedName("is_free") val isFree: Boolean?,
    @SerializedName("short_description") val shortDescription: String?,
    @SerializedName("header_image") val headerImage: String?,
    @SerializedName("genres") val genres: List<Genre>?,
    @SerializedName("categories") val categories: List<Category>?, // Features/Tags
    @SerializedName("developers") val developers: List<String>?,
    @SerializedName("publishers") val publishers: List<String>?,
    @SerializedName("price_overview") val priceOverview: PriceOverview?,
    @SerializedName("platforms") val platforms: Platforms?,
    @SerializedName("metacritic") val metacritic: Metacritic?,
    @SerializedName("release_date") val releaseDate: ReleaseDate?
)

data class Genre(
    val id: String,
    val description: String
)

data class Category(
    val id: Int,
    val description: String
)

data class PriceOverview(
    val currency: String,
    val initial: Int,
    val final: Int, // price in cents
    @SerializedName("discount_percent") val discountPercent: Int
)

data class Platforms(
    val windows: Boolean,
    val mac: Boolean,
    val linux: Boolean
)

data class Metacritic(
    val score: Int,
    val url: String?
)

data class ReleaseDate(
    val coming_soon: Boolean,
    val date: String // e.g. "21 Aug, 2012"
)