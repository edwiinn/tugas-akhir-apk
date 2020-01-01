package com.edwiinn.digitalsignature.utility

import com.edwiinn.digitalsignature.BuildConfig
import com.edwiinn.digitalsignature.model.Document
import com.edwiinn.digitalsignature.model.DocumentsNameResponse
import com.edwiinn.digitalsignature.service.DocumentService
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.concurrent.TimeUnit


object DocumentsServiceHelper {
    private val ENDPOINT = "http://52.184.100.231/"
    private var documentsService:DocumentService

    init {
        val retrofit = createAdapter().build()
        documentsService = retrofit.create<DocumentService>(DocumentService::class.java)
    }


    private fun createAdapter(): Retrofit.Builder {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
    }

    fun getAllDocuments() : Call<DocumentsNameResponse> {
        return documentsService.listDocuments()
    }

    fun getDocument(filename :String) : Call<ResponseBody>{
        return documentsService.getDocument(filename)
    }
}