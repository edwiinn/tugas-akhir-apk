package com.edwiinn.digitalsignature.model

import com.google.gson.annotations.SerializedName

data class UploadDocumentResponse(
    @SerializedName("documentName")
    val name: String,
    @SerializedName("createdAt")
    val createdAt: String
)