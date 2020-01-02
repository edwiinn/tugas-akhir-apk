package com.edwiinn.digitalsignature

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment


class DocumentPreviewFragment : Fragment() {

    companion object {
        fun newInstance(): DocumentPreviewFragment {
            return DocumentPreviewFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}