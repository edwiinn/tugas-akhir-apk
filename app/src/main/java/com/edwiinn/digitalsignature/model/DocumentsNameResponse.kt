package com.edwiinn.digitalsignature.model

import com.google.gson.annotations.SerializedName

data class DocumentsNameResponse(
    @SerializedName("documents_name")
    val names:List<String>
)