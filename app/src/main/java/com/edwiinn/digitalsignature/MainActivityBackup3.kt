package com.edwiinn.digitalsignature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.edwiinn.digitalsignature.Constants.Companion.ANDROID_KEYALIAS
import com.edwiinn.digitalsignature.Constants.Companion.ANDROID_KEYSTORE
import retrofit2.Retrofit
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.io.*
import com.edwiinn.digitalsignature.service.CertificationService
import com.edwiinn.digitalsignature.service.DocumentService
import com.edwiinn.digitalsignature.utility.CsrHelper
import com.edwiinn.digitalsignature.utility.SignHelper
import okhttp3.*
import com.itextpdf.signatures.DigestAlgorithms
import com.itextpdf.signatures.PdfSigner
import java.lang.Exception
import java.security.*
import java.security.cert.CertificateFactory
import com.edwiinn.digitalsignature.model.UploadDocumentResponse


class MainActivityBackup3 : AppCompatActivity() {

    private lateinit var kpg: KeyPairGenerator
    private val keySize: Int = 2048

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(ANDROID_KEYALIAS)){
            Log.d("tag", "Creating key")
            try {
                kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            kpg.initialize(
                KeyGenParameterSpec.Builder(
                    ANDROID_KEYALIAS,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_ENCRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setKeySize(keySize)
                    .build())
            kpg.generateKeyPair()
        }

        val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("http://52.184.100.231/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val kp = getKeyPair(keyStore, ANDROID_KEYALIAS)
        if (kp != null){
            Log.d("key pair", "There is a key pair owo")
            val csr = CsrHelper.generateCSR(kp, "Edwin")
            val encryptedCsr = Base64.encodeToString(csr.encoded, Base64.DEFAULT)
            val csrString =
                "-----BEGIN CERTIFICATE REQUEST-----\n$encryptedCsr-----END CERTIFICATE REQUEST-----\n"

            val mCertificationService = retrofit.create<CertificationService>(CertificationService::class.java!!)
            mCertificationService.signCSR(csrString).enqueue(object:
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.code() == 200){
//                        pubkey_text.text = response.body()?.contentType().toString()
                        val file = File(applicationContext.filesDir, "certificate.cert")
                        val isWrittenToDisk = writeResponseBodyToDisk(response.body(), file)
                        if (isWrittenToDisk) {
                            Log.d("tag", "Download Succesfull")
                            val certificateFile = File(applicationContext.filesDir,"certificate.cert")
                            Log.d("certificate", certificateFile.readText(Charsets.UTF_8))
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    Log.e("tag", "Error ${error.message}")
                }
            })
        }

        val documentService = retrofit.create<DocumentService>(
            DocumentService::class.java!!)

        documentService.getDocument("document/blank.pdf").enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if(response.code() == 200){
//                    pubkey_text.text = response.body()?.contentType().toString()
                    val pdfLoc =  File(applicationContext.filesDir, "blank.pdf")
                    val isWrittenToDisk = writeResponseBodyToDisk(response.body(), pdfLoc)
                    if (isWrittenToDisk) {
                        try {
                            val x509CertString = File(applicationContext.filesDir,"certificate.cert").readText()
                                .replace("-----BEGIN CERTIFICATE-----","")
                                .replace("-----END CERTIFICATE-----","")
                            val x509Cert = Base64.decode(x509CertString, Base64.DEFAULT)
                            val isX509Cert = ByteArrayInputStream(x509Cert)
                            Log.d("x509Cert", x509CertString)
                            val cert = CertificateFactory.getInstance("X.509").generateCertificate(isX509Cert)
                            isX509Cert.close()
                            val chain2 = Array(20) { cert }
//
                            val ks: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
                            ks.load(null)
                            val entry: KeyStore.Entry = ks.getEntry(ANDROID_KEYALIAS, null)
                            if (entry !is KeyStore.PrivateKeyEntry) {
                                Log.w("tag", "Not an instance of a PrivateKeyEntry")
                                return
                            }
                            val privateKey = entry.privateKey
                            val src = File(applicationContext.filesDir,"blank.pdf").toString()
                            val dst = File(applicationContext.filesDir,"new-blank-2.pdf").toString()
                            SignHelper.sign(src, dst, chain2, privateKey, DigestAlgorithms.SHA256, "AndroidKeyStoreBCWorkaround", PdfSigner.CryptoStandard.CMS, "Only Testing", "Surabaya")
                        } catch (e: Exception){
                            Log.e("something failed", e.message)
                        }

                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, error: Throwable){
                Log.e("tag", "Error ${error.message}")
            }
        })
        val file = File(applicationContext.filesDir, "new-blank-2.pdf")
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val fileToUpload = MultipartBody.Part.createFormData("document", file.name, requestBody)

        documentService.saveDocument(fileToUpload).enqueue(object:
            Callback<UploadDocumentResponse> {
            override fun onResponse(
                call: Call<UploadDocumentResponse>,
                response: Response<UploadDocumentResponse>
            ) {
                if(response.code() == 200){
                    Log.d("stat","Sent Succesfully")
//                    middle_text.text = response.body()?.createdAt
                } else{
                    Log.e("ohno", "something bad happpen")
                    Log.e("code", response.code().toString())
                    Log.e("m", response.message())
                }
            }

            override fun onFailure(call: Call<UploadDocumentResponse>, error: Throwable){
                Log.e("tag", "Error ${error.message}")
            }
        })
    }
    //
    private fun writeResponseBodyToDisk(body: ResponseBody?, fileLocation: File): Boolean {
        try {
            Log.d("tag", "Location $fileLocation")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                val fileSize = body?.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body?.byteStream()
                outputStream = FileOutputStream(fileLocation)

                while (true) {
                    val read = inputStream!!.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream!!.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("tag", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream!!.flush()
                return true
            } catch (e: IOException) {
                return false
            } finally {
                if (inputStream != null) {
                    inputStream!!.close()
                }
                if (outputStream != null) {
                    outputStream!!.close()
                }
            }
        } catch (e: IOException) {
            return false
        }

    }

    fun getKeyPair(
        keystore: KeyStore,
        alias: String
    ): KeyPair? {
//        val key = keystore.getKey(alias, password.toCharArray()) as PrivateKey
        val entry: KeyStore.Entry = keystore.getEntry(alias, null)
        if (entry !is KeyStore.PrivateKeyEntry) {
            Log.w("tag", "Not an instance of a PrivateKeyEntry")
            return null
        }
        val privateKey = entry.privateKey
        val cert = keystore.getCertificate(alias)
        val publicKey = cert.publicKey

        return KeyPair(publicKey, privateKey)
    }
}