/**
 * The BSD License
 *
 * Copyright (c) 2010-2012 RIPE NCC
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
package net.ripe.rpki.xml.converters;

import com.thoughtworks.xstream.XStream;
import net.ripe.rpki.commons.crypto.cms.manifest.ManifestCms;
import net.ripe.rpki.commons.crypto.cms.manifest.ManifestCmsTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;


public class ManifestCmsConverterTest {

    private XStream xStream;
    private ManifestCmsConverter subject;

    private String expectedXmlRegEx =
        "<net\\.ripe\\..*\\.ManifestCms>\n" +
        "  <encoded>[^<]*</encoded>\n" +
        "</net\\.ripe\\..*\\.ManifestCms>";

    @Before
    public void setUp() {
        subject = new ManifestCmsConverter();
        xStream = new XStream();
        xStream.registerConverter(subject);
    }

    @Test
    public void shouldSupportResourceCertificate() {
        Assert.assertTrue(subject.canConvert(ManifestCms.class));
    }

    @Test
    public void shouldOnlyUseEncodedWhenSerializingManifest() {
        ManifestCms manifestCms = ManifestCmsTest.getRootManifestCms();
        String xml = xStream.toXML(manifestCms);
        Assert.assertTrue(Pattern.matches(expectedXmlRegEx, xml));
    }

    @Test
    public void shouldDoRoundTripSerializeAndDesirializeManifest() {
        ManifestCms manifestCms = ManifestCmsTest.getRootManifestCms();

        String xml = xStream.toXML(manifestCms);
        ManifestCms processedManifest = (ManifestCms) xStream.fromXML(xml);

        Assert.assertEquals(manifestCms, processedManifest);
    }
}