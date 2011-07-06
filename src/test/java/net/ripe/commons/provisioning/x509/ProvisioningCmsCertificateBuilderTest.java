/**
 * The BSD License
 *
 * Copyright (c) 2010, 2011 RIPE NCC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   - Neither the name of the RIPE NCC nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ripe.commons.provisioning.x509;

import static net.ripe.commons.certification.x509cert.X509CertificateBuilderHelper.*;
import static net.ripe.commons.provisioning.ProvisioningObjectMother.*;
import static net.ripe.commons.provisioning.x509.ProvisioningIdentityCertificateBuilderTest.*;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import javax.security.auth.x500.X500Principal;

import net.ripe.commons.certification.x509cert.X509CertificateInformationAccessDescriptor;
import net.ripe.commons.provisioning.keypair.ProvisioningKeyPairGenerator;

import org.junit.Before;
import org.junit.Test;

public class ProvisioningCmsCertificateBuilderTest {

    public static final KeyPair EE_KEYPAIR = ProvisioningKeyPairGenerator.generate();

    public static final ProvisioningCmsCertificate TEST_CMS_CERT = getTestProvisioningCmsCertificate();

    private ProvisioningCmsCertificateBuilder subject;


    private static ProvisioningCmsCertificate getTestProvisioningCmsCertificate() {
        ProvisioningCmsCertificateBuilder cmsCertificateBuilder = getTestBuilder();
        return cmsCertificateBuilder.build();
    }

    private static ProvisioningCmsCertificateBuilder getTestBuilder() {
        ProvisioningCmsCertificateBuilder builder = new ProvisioningCmsCertificateBuilder();
        builder.withIssuerDN(TEST_IDENTITY_CERT.getSubject());
        builder.withSerial(BigInteger.TEN);
        builder.withPublicKey(EE_KEYPAIR.getPublic());
        builder.withSubjectDN(new X500Principal("CN=end-entity"));
        builder.withSigningKeyPair(TEST_KEY_PAIR);
        builder.withSignatureProvider(DEFAULT_SIGNATURE_PROVIDER);
        return builder;
    }

    @Before
    public void setUp() {
        // Create a builder with all requirements so that we can exclude (nullify) each
        // requirement for easy unit testing of the builder
        subject = getTestBuilder();
    }

    @Test
    public void shouldBuild() {
        ProvisioningCmsCertificate cmsCertificate = subject.build();
        assertNotNull(cmsCertificate);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRequirePublicKey() {
        subject.withPublicKey(null);
        subject.build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRequireIssuerDN() {
        subject.withIssuerDN(null);
        subject.build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRequireSubjectDN() {
        subject.withSubjectDN(null);
        subject.build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRequireSerial() {
        subject.withSerial(null);
        subject.build();
    }

    // ======= the following unit tests test properties of the certificate built by this builder =====

    /**
     * http://tools.ietf.org/html/draft-huston-sidr-rpki-algs-00#section-2
     */
    @Test
    public void shouldUseSHA256withRSA() {
        assertEquals("SHA256withRSA", TEST_CMS_CERT.getCertificate().getSigAlgName());
    }

    /**
     * http://tools.ietf.org/html/draft-huston-sidr-rpki-algs-00#section-2
     */
    @Test
    public void shouldUse2048BitRsaKey() {
        assertTrue(TEST_CMS_CERT.getPublicKey() instanceof RSAPublicKey);
        assertEquals(((RSAPublicKey) TEST_CMS_CERT.getPublicKey()).getModulus().bitLength(), 2048);
    }

    @Test
    public void shouldNotHaveRsyncCrlPointer() {
        assertNull(TEST_CMS_CERT.findFirstRsyncCrlDistributionPoint());
    }

    @Test
    public void shouldNotHaveAiaPointer() {
        assertNull(TEST_CMS_CERT.getAuthorityInformationAccess());
    }

    @Test
    public void shouldHaveNoSiaPointer() {
        X509CertificateInformationAccessDescriptor[] subjectInformationAccess = TEST_CMS_CERT.getSubjectInformationAccess();
        assertNull(subjectInformationAccess);
    }

    @Test
    public void shouldBeAnEECertificate() {
        assertFalse(TEST_CMS_CERT.isCa());
    }

    @Test
    public void shouldHaveKeyUsageExtensionDigitalSignature() {
        boolean[] keyUsage = TEST_CMS_CERT.getCertificate().getKeyUsage();
        // For KeyUsage flags order see bouncy castle KeyUsage class
        assertTrue(Arrays.equals(new boolean[] { true, false, false, false, false, false, false, false, false }, keyUsage));
    }
}

