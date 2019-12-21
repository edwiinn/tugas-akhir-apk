package com.edwiinn.digitalsignature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.KeyChain
import android.util.Log
import com.edwiinn.digitalsignature.Constants.Companion.ANDROID_KEYALIAS
import com.edwiinn.digitalsignature.Constants.Companion.ANDROID_KEYSTORE
import com.edwiinn.digitalsignature.model.DocumentsNameResponse
import retrofit2.Retrofit
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.io.*
import com.edwiinn.digitalsignature.model.UploadDocumentResponse
import okhttp3.*
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.signatures.BouncyCastleDigest
import com.itextpdf.signatures.DigestAlgorithms
import com.itextpdf.signatures.PdfSigner
import com.itextpdf.signatures.PrivateKeySignature
import org.bouncycastle.asn1.ASN1OutputStream
import org.bouncycastle.asn1.x500.X500NameBuilder
import org.bouncycastle.asn1.x500.style.BCStyle
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v1CertificateBuilder
import org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi
import org.bouncycastle.jcajce.provider.digest.SHA256
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.OperatorCreationException
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.util.*
import kotlin.math.log


class MainActivityBackup : AppCompatActivity() {

    private lateinit var privKey: PrivateKey
    private lateinit var kpg: KeyPairGenerator
    private lateinit var sigGen: ContentSigner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        Log.d("tag", "Creating key")
        try {
            kpg = KeyPairGenerator.getInstance("RSA")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        kpg.initialize(2048)
        val kp = kpg.generateKeyPair()
        privKey = kp.private

        val VALIDITY_IN_DAYS = 365
        val startDate = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
        val endDate = Date(System.currentTimeMillis() + VALIDITY_IN_DAYS * 24 * 60 * 60 * 1000)

        val nameBuilder = X500NameBuilder(BCStyle.INSTANCE)
        nameBuilder.addRDN(BCStyle.C, "ID")
        nameBuilder.addRDN(BCStyle.O, "ITS")
        nameBuilder.addRDN(BCStyle.OU, "MyITS")
        nameBuilder.addRDN(BCStyle.CN, "Edwin Hartoyo")

        val x500Name = nameBuilder.build()
        val random = Random()
        val subjectPublicKeyInfo: SubjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(kp.public.encoded)
        val v1CertGen = X509v1CertificateBuilder(x500Name
            ,BigInteger.valueOf(random.nextLong())
            ,startDate
            ,endDate
            ,x500Name
            ,subjectPublicKeyInfo)
        // Self sign
        // Prepare Signature:
        try {
            Security.addProvider(BouncyCastleProvider())
            sigGen = JcaContentSignerBuilder("SHA256withRSA").setProvider("AndroidOpenSSL").build(kp.private)
        } catch (e: OperatorCreationException) {
            e.printStackTrace()
        }

        // Self sign :
        val x509CertificateHolder = v1CertGen.build(sigGen)
        val x509Certificate= x509CertificateHolder.toASN1Structure()
        Log.d("certificate", x509Certificate.toString())
//        val  myCert : java.security.cert.Certificate[] = java.security.cert.Certificate[] { (java.security.cert.Certificate) mCurrentCertificate}
//        val myCert = java.security.cert.Certificate[] {(java.security.cert.Certificate) x509Ce}
//        val myCert = mutableListOf<java.security.cert.Certificate>()
//        myCert.add(x509Certificate)

        val baos = ByteArrayOutputStream(4096)
        val out = ASN1OutputStream(baos)
        out.writeObject(x509Certificate)
        out.close()
        val x509 = baos.toByteArray()


        val inputStream:InputStream =  ByteArrayInputStream(x509)
        val x509Cert = CertificateFactory.getInstance("X.509").generateCertificate(inputStream)

//        val chain2 = Array(20) { x509Cert}
//        Log.d("cert", chain2[0].toString())

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("http://45.76.182.87")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

//        val documentService = retrofit.create<DocumentService>(DocumentService::class.java!!)
//        documentService.listDocuments().enqueue(object :
//            Callback<DocumentsNameResponse> {
//            override fun onResponse(call: Call<DocumentsNameResponse>, response: Response<DocumentsNameResponse>) {
//
//                if(response.code() == 200) {
//                    val names = response.body()?.names
//                    middle_text.text = names.toString()
//                }
//            }
//            override fun onFailure(call: Call<DocumentsNameResponse>, error: Throwable){
//                Log.e("tag", "Error ${error.message}")
//            }
//        })
//
//        documentService.getDocument("documents/sample.pdf").enqueue(object :
//            Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: Response<ResponseBody>
//            ) {
//                if(response.code() == 200){
//                    pubkey_text.text = response.body()?.contentType().toString()
//                    val isWrittenToDisk = writeResponseBodyToDisk(response.body())
//                    if (isWrittenToDisk) {
//                        Log.d("tag", "Download Succesfull")
////                        val ks: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
////                        ks.load(null)
////                        val alias: String = ks.aliases().nextElement()
////                        val entry: KeyStore.Entry = ks.getEntry(ANDROID_KEYALIAS, null)
////                        if (entry !is KeyStore.PrivateKeyEntry) {
////                            Log.w("tag", "Not an instance of a PrivateKeyEntry")
////                            return
////                        }
////                        val chain = ks.getCertificateChain(alias)
//                        val src = File(applicationContext.filesDir,"sample.pdf").toString()
//                        val dst = File(applicationContext.filesDir,"sample-signed3.pdf").toString()
//                        sign(src, dst, chain2, privKey, DigestAlgorithms.SHA256, "AndroidOpenSSL", PdfSigner.CryptoStandard.CMS, "Only Testing", "Surabaya")
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, error: Throwable){
//                Log.e("tag", "Error ${error.message}")
//            }
//        })
//
//        val file = File(applicationContext.filesDir, "sample-signed3.pdf")
//
//        // Parsing any Media type file
//        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
//        val fileToUpload = MultipartBody.Part.createFormData("document", file.name, requestBody)
//        documentService.saveDocument(fileToUpload).enqueue(object:
//            Callback<UploadDocumentResponse> {
//            override fun onResponse(
//                call: Call<UploadDocumentResponse>,
//                response: Response<UploadDocumentResponse>
//            ) {
//                if(response.code() == 200){
//                    middle_text.text = response.body()?.createdAt
//                }
//            }
//
//            override fun onFailure(call: Call<UploadDocumentResponse>, error: Throwable){
//                Log.e("tag", "Error ${error.message}")
//            }
//        })
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun sign(
        src: String, dest: String,
        chain: Array<Certificate>,
        pk: PrivateKey, digestAlgorithm: String, provider: String,
        subfilter: PdfSigner.CryptoStandard,
        reason: String, location: String
    ) {
        // Creating the reader and the signer
        val reader = PdfReader(src)
        val signer = PdfSigner(reader, FileOutputStream(dest), false)
        // Creating the appearance
        val appearance = signer.signatureAppearance
            .setReason(reason)
            .setLocation(location)
            .setReuseAppearance(false)
        val rect = Rectangle(36F, 648F, 200F, 100F)
        appearance.setPageRect(rect).pageNumber = 1
        signer.fieldName = "sig"
        // Creating the signature
        val pks = PrivateKeySignature(pk, digestAlgorithm, provider)
        val digest = BouncyCastleDigest()

        signer.signDetached(digest, pks, chain, null, null, null, 2*8192, subfilter)
    }

    private fun writeResponseBodyToDisk(body: ResponseBody?): Boolean {
        try {
            val futureStudioIconFile =
                File(applicationContext.filesDir,"sample.pdf")
            Log.d("tag", "Location $futureStudioIconFile")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                val fileSize = body?.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body?.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)

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
}
