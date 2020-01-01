package com.edwiinn.digitalsignature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.edwiinn.digitalsignature.model.Document
import com.edwiinn.digitalsignature.model.DocumentsNameResponse
import com.edwiinn.digitalsignature.recyclerview.DocumentsRecyclerAdapter
import com.edwiinn.digitalsignature.utility.DocumentsServiceHelper
import kotlinx.android.synthetic.main.documents_page.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DocumentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.documents_page)

        Log.d("activity", "documents")
        DocumentsServiceHelper.getAllDocuments().enqueue(object: Callback<DocumentsNameResponse> {
            override fun onResponse(
                call: Call<DocumentsNameResponse>,
                response: Response<DocumentsNameResponse>
            ) {
                if(response.code() == 200){
                    Log.d("documents", response.body()?.names.toString())
                    val documentsRaw = response.body()?.names
                    val documents = ArrayList<Document>()
                    if (documentsRaw == null){
                        Log.w("documents","Documents is Null")
                        return
                    }
                    documentsRaw.forEach {
                        documents.add(Document(it))
                    }
                    val mDocumentsAdapter = DocumentsRecyclerAdapter(documents)
                    documentsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    documentsRecyclerView.adapter = mDocumentsAdapter
                } else{
                    Log.e("code", response.code().toString())
                    Log.e("message", response.message())
                }
            }

            override fun onFailure(call: Call<DocumentsNameResponse>, error: Throwable){
                Log.e("tag", "Error ${error.message}")
            }
        })

    }
}
