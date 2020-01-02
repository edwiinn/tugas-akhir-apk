package com.edwiinn.digitalsignature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class DocumentDigitalSignFragment : Fragment() {
    companion object {
        fun newInstance(): DocumentDigitalSignFragment{
            return DocumentDigitalSignFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }
}