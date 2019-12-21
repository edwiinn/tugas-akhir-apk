package com.edwiinn.digitalsignature.model

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.edwiinn.digitalsignature.Constants
import java.io.File
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.NoSuchAlgorithmException

class DigitalSignature {
    companion object{
        const val SIGNING_KEYSTORE:String = "SigningKeyStore"
        const val SIGNING_KEYALIAS:String = "SigningKeyAlias"
    }

    private lateinit var kpg:KeyPairGenerator

    public fun generateKey(){
        val keyStore = KeyStore.getInstance(SIGNING_KEYSTORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(SIGNING_KEYALIAS)){
            Log.d("tag", "Creating New Key")
            try {
                kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA,
                    SIGNING_KEYSTORE
                )
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            kpg.initialize(
                KeyGenParameterSpec.Builder(
                    SIGNING_KEYALIAS,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_ENCRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setKeySize(2048)
                    .build())
            kpg.generateKeyPair()
        } else {
            Log.d("key generator", "Key Already Available")
        }
    }

    public fun sign(src: File, dst:File){

    }
}