package com.edwiinn.digitalsignature.utility

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.ExtensionsGenerator
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.OperatorCreationException
import org.bouncycastle.asn1.x509.AlgorithmIdentifier
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.pkcs.PKCS10CertificationRequest
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.security.PrivateKey
import java.security.Signature


object CsrHelper {

    private const val DEFAULT_SIGNATURE_ALGORITHM = "SHA256withRSA"
    private const val CN_PATTERN = "CN=%s, O=Aralink, OU=OrgUnit"

    private class JCESigner(privateKey: PrivateKey, sigAlgo: String) : ContentSigner {

        private val mAlgo: String = sigAlgo.toLowerCase()
        private var signature: Signature? = null
        private var outputStream: ByteArrayOutputStream? = null

        init {
            //Utils.throwIfNull(privateKey, sigAlgo);
            try {
                this.outputStream = ByteArrayOutputStream()
                this.signature = Signature.getInstance(sigAlgo)
                this.signature!!.initSign(privateKey)
            } catch (gse: GeneralSecurityException) {
                throw IllegalArgumentException(gse.message)
            }

        }

        override fun getAlgorithmIdentifier(): AlgorithmIdentifier {
            return ALGOS[mAlgo] ?: throw IllegalArgumentException("Does not support algo: $mAlgo")
        }

        override fun getOutputStream(): OutputStream? {
            return outputStream
        }

        override fun getSignature(): ByteArray? {
            return try {
                signature!!.update(outputStream!!.toByteArray())
                signature!!.sign()
            } catch (gse: GeneralSecurityException) {
                gse.printStackTrace()
                null
            }

        }

        companion object {

            private val ALGOS = HashMap<String, AlgorithmIdentifier>()

            init {
                ALGOS["SHA256withRSA".toLowerCase()] = AlgorithmIdentifier(
                    ASN1ObjectIdentifier("1.2.840.113549.1.1.11")
                )
                ALGOS["SHA1withRSA".toLowerCase()] = AlgorithmIdentifier(
                    ASN1ObjectIdentifier("1.2.840.113549.1.1.5")
                )

            }
        }
    }

    fun generateCSR(keyPair: KeyPair, cn: String): PKCS10CertificationRequest {
        val principal = String.format(CN_PATTERN, cn)

        val signer = JCESigner(keyPair.private, DEFAULT_SIGNATURE_ALGORITHM)

        val csrBuilder = JcaPKCS10CertificationRequestBuilder(
            X500Name(principal), keyPair.public
        )
        val extensionsGenerator = ExtensionsGenerator()
        extensionsGenerator.addExtension(
            Extension.basicConstraints, true, BasicConstraints(
                true
            )
        )
        csrBuilder.addAttribute(
            PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
            extensionsGenerator.generate()
        )

        return csrBuilder.build(signer)
    }
}