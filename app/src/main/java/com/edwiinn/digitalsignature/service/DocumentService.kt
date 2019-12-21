package com.edwiinn.digitalsignature.service

import com.edwiinn.digitalsignature.model.DocumentsNameResponse
import com.edwiinn.digitalsignature.model.UploadDocumentResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST



interface DocumentService {
    @GET("/document/")
    fun listDocuments(): Call<DocumentsNameResponse>

    @GET
    fun getDocument(@Url filename: String): Call<ResponseBody>

    @POST("/document/")
    @Multipart
    fun saveDocument(@Part document: MultipartBody.Part): Call<UploadDocumentResponse>
}