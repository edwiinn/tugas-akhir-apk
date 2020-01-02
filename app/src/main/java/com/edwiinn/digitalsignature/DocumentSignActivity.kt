package com.edwiinn.digitalsignature

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_document_sign.*


class DocumentSignActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_sign)

        loadFragment(DocumentPreviewFragment())

        nav_view.setOnNavigationItemSelectedListener{
            onNavigationItemSelected(it)
        }
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment!!)
                .commit()
            return true
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        Log.d("selected",item.itemId.toString())
        when(item.itemId){
            R.id.document_preview -> fragment = DocumentPreviewFragment()
            R.id.document_electronic_sign -> fragment = DocumentElectronicSignFragment()
            R.id.document_digital_sign -> fragment = DocumentDigitalSignFragment()
        }
        return loadFragment(fragment)
    }
}
