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
package net.ripe.commons.certification.validation.fieldvalidators;

import static net.ripe.commons.certification.validation.ValidationString.*;
import static org.junit.Assert.*;

import java.util.List;

import net.ripe.commons.certification.validation.ValidationCheck;
import net.ripe.ipresource.IpRange;

import org.junit.Test;


public class MaximumLengthValidatorTest {

    private static final IpRange PREFIX = IpRange.parse("10/8");

    private MaximumLengthValidator subject = new MaximumLengthValidator(PREFIX);


    @Test
    public void shouldPassWithCorrectLength() {
        ValidationResult result = subject.validate("16");
        assertFalse(result.hasFailures());
    }

    @Test
    public void shouldPassWithoutMaxLengthSpecified() {
        ValidationResult result = subject.validate(null);
        assertFalse(result.hasFailures());
    }

    @Test
    public void shouldPassIfMaxLegthIsBlank() {
        ValidationResult result = subject.validate(" ");
        assertFalse(result.hasFailures());
    }

    @Test
    public void shouldCheckIfMaxLengthIsValid() {
        ValidationResult result = subject.validate("foo");

        assertTrue(result.hasFailures());

        List<ValidationCheck> failures = result.getFailures();
        assertEquals(1, failures.size());
        assertEquals(failures.iterator().next().getKey(), ROA_SPECIFICATION_MAX_LENGTH_VALID);
    }

    @Test
    public void shouldCheckIfMaxLengthIsLegal() {
        ValidationResult result = subject.validate("7");

        assertTrue(result.hasFailures());

        List<ValidationCheck> failures = result.getFailures();
        assertEquals(1, failures.size());
        assertEquals(failures.iterator().next().getKey(), ROA_SPECIFICATION_MAX_LENGTH_VALID);
    }

    @Test
    public void shouldCheckIfMaxLengthIsNotMoreSpecificThanBitsizeMinusTwo() {
        ValidationResult result = subject.validate("31");

        assertTrue(result.hasFailures());

        List<ValidationCheck> failures = result.getFailures();
        assertEquals(1, failures.size());
        assertEquals(failures.iterator().next().getKey(), ROA_SPECIFICATION_MAX_LENGTH_VALID);
    }
}