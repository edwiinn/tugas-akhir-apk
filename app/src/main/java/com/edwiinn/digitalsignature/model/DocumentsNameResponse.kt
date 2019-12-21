package com.edwiinn.digitalsignature.model

import com.google.gson.annotations.SerializedName

data class DocumentsNameResponse(
    @SerializedName("documentsName")
    val names:List<String>
)