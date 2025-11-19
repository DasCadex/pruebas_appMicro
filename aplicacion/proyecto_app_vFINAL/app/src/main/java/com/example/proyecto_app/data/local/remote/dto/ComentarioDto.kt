package com.example.proyecto_app.data.local.remote.dto

import com.google.gson.annotations.SerializedName

data class ComentarioDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("publicationId") val publicationId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("authorName") val authorName: String,
    @SerializedName("text") val text: String,
    @SerializedName("createdAt") val createdAt: String?
)