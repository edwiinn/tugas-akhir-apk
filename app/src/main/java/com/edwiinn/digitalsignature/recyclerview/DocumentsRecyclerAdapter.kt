package com.edwiinn.digitalsignature.recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.edwiinn.digitalsignature.DocumentActivity
import com.edwiinn.digitalsignature.DocumentSignActivity
import com.edwiinn.digitalsignature.R
import com.edwiinn.digitalsignature.model.Document
import kotlinx.android.synthetic.main.document_row.view.*

class DocumentsRecyclerAdapter(var documents: List<Document>): RecyclerView.Adapter<DocumentsRecyclerAdapter.DocumentViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DocumentViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.document_row, parent, false))

    override fun getItemCount() = documents.size

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(documents[position])
    }

    class DocumentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val documentTitleView = view.title
        private val signButton = view.sign_btn
        private val context = view.context

        fun bind(document: Document) {
            documentTitleView.text = document.title
            signButton.setOnClickListener{
//                val documentIntent = Intent(context, DocumentActivity::class.java)
//                documentIntent.putExtra("filename", document.title)
//                context.startActivity(documentIntent)

                val documentSignIntent = Intent(context, DocumentSignActivity::class.java)
                context.startActivity(documentSignIntent)
            }
        }
    }
}
