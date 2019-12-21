package com.edwiinn.digitalsignature.repository

interface DigitalCertificateRepository{
    fun signCSR(csr: String)

}