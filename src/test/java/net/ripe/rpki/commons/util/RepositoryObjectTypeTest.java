package net.ripe.rpki.commons.util;

import org.junit.Test;

import java.security.cert.CRL;

import static net.ripe.rpki.commons.util.RepositoryObjectType.Certificate;
import static net.ripe.rpki.commons.util.RepositoryObjectType.Crl;
import static net.ripe.rpki.commons.util.RepositoryObjectType.Gbr;
import static net.ripe.rpki.commons.util.RepositoryObjectType.Manifest;
import static net.ripe.rpki.commons.util.RepositoryObjectType.Roa;
import static net.ripe.rpki.commons.util.RepositoryObjectType.Unknown;
import static net.ripe.rpki.commons.util.RepositoryObjectType.parse;
import static org.junit.Assert.*;

public class RepositoryObjectTypeTest {

    @Test
    public void should_detect_roas() {
        assertExtensionsMatch(Roa, "filename.roa", ".roa", ".ROA", ".RoA");
    }

    @Test
    public void should_detect_crls() {
        assertExtensionsMatch(Crl, "filename.crl", ".crl", ".CRL", ".CRl");
    }

    @Test
    public void should_detect_certificates() {
        assertExtensionsMatch(Certificate, "filename.cer", ".cer", ".CER", ".cER");
    }

    @Test
    public void should_detect_manifests() {
        assertExtensionsMatch(Manifest, "filename.mft", ".mft", ".MFT", ".Mft");
    }

    @Test
    public void should_detect_ghostbuster_records() {
        assertExtensionsMatch(Gbr, "filename.gbr", ".gbr", ".GBR", ".gbR");
    }

    @Test
    public void should_fallback_to_unknown() {
        assertExtensionsMatch(Unknown, "", "..", ".foo", "abc", "roa", "cer", "/", "filename");
    }

    private void assertExtensionsMatch(RepositoryObjectType expected, String... extensions) {
        for (String extension : extensions) {
            assertEquals(expected, parse(extension));
        }
    }
}
