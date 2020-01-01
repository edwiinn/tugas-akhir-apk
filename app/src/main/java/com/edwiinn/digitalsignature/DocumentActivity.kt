package com.edwiinn.digitalsignature

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.edwiinn.digitalsignature.utility.DocumentsServiceHelper
import com.edwiinn.digitalsignature.utility.ResponseBodyDownloader
import kotlinx.android.synthetic.main.activity_document.*
import kotlinx.android.synthetic.main.documents_page.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DocumentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        val filename = intent.getStringExtra("filename")
        title_txt.text = filename

        DocumentsServiceHelper.getDocument("document/$filename").enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if(response.code() == 200){
                    val pdfLoc =  File(applicationContext.filesDir, filename)
                    val isWrittenToDisk = ResponseBodyDownloader.download(response.body(), pdfLoc)
                    if (!isWrittenToDisk) {
                        Log.e("document", response.message())
                        return
                    }
                    Log.d("document", "Success Downloading")
                    if (pdfLoc.exists()){
                        Log.d("document", "it exist")
                    }
                    pdfView.fromFile(pdfLoc)
                        .swipeHorizontal(false)
                        .load()
                    document_progress_bar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseBody>, error: Throwable){
                Log.e("document", "Error ${error.message}")
            }
        })
    }
}
