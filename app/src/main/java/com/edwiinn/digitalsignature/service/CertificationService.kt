package com.edwiinn.digitalsignature.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CertificationService {

    @FormUrlEncoded
    @POST("certificate/csr/sign")
    fun signCSR(@Field("csr") csr:String): Call<ResponseBody>
}