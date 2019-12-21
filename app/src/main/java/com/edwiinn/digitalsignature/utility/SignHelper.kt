package com.edwiinn.digitalsignature.utility

import android.util.Log
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.signatures.BouncyCastleDigest
import com.itextpdf.signatures.PdfSigner
import com.itextpdf.signatures.PrivateKeySignature
import java.io.FileOutputStream
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.PrivateKey
import java.security.cert.Certificate

object SignHelper {
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

        signer.signDetached(digest, pks, chain, null, null, null, 4*8192, subfilter)
    }
}