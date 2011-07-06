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
package net.ripe.commons.certification.cms.roa;

import static junit.framework.Assert.*;
import net.ripe.ipresource.IpRange;

import org.junit.Test;


public class RoaPrefixTest {

    @Test
    public void shouldEnsureIpAddressIsValidPrefix() {
        assertTrue(new RoaPrefix(IpRange.parse("10.0.0.0/8"), null).isValidPrefix());
        assertFalse(new RoaPrefix(IpRange.parse("10.0.0.0-10.0.2.1"), null).isValidPrefix());
    }

    @Test
    public void shouldEnsureMaximumLengthValidity() {
        assertTrue(new RoaPrefix(IpRange.parse("10.0.0.0/8"), null).isMaximumLengthValid());
        assertFalse(new RoaPrefix(IpRange.parse("10.0.0.0/8"), -1).isMaximumLengthValid());
        assertTrue(new RoaPrefix(IpRange.parse("10.0.0.0/8"), 8).isMaximumLengthValid());
        assertTrue(new RoaPrefix(IpRange.parse("10.0.0.0/8"), 17).isMaximumLengthValid());
        assertTrue(new RoaPrefix(IpRange.parse("10.0.0.0/8"), 32).isMaximumLengthValid());
        assertTrue(new RoaPrefix(IpRange.parse("ffe0::/16"), 128).isMaximumLengthValid());
        assertFalse(new RoaPrefix(IpRange.parse("10.0.0.0/8"), 0).isMaximumLengthValid());
        assertFalse(new RoaPrefix(IpRange.parse("10.0.0.0/8"), 7).isMaximumLengthValid());
        assertFalse(new RoaPrefix(IpRange.parse("10.0.0.0/8"), 33).isMaximumLengthValid());
        assertFalse(new RoaPrefix(IpRange.parse("ffe0::/16"), 129).isMaximumLengthValid());
    }
}
