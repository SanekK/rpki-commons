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
package net.ripe.commons.certification.rsync;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class RsyncTest {

	@Test
	public void shouldExecuteCommand() {
		Rsync rsync = new Rsync();
		rsync.addOptions("--version");

		assertFalse(rsync.isCompleted());
		assertNull(rsync.getException());
		assertNull(rsync.getErrorLines());
		assertNull(rsync.getOutputLines());

		int exitStatus = rsync.execute();

		assertTrue(exitStatus == 0);
		assertTrue(rsync.isCompleted());
		assertNull(rsync.getException());
		assertNotNull(rsync.getErrorLines());
		assertTrue(rsync.getErrorLines().length == 0);
		assertNotNull(rsync.getOutputLines());
		assertTrue(rsync.getOutputLines().length > 0);
	}

	@Test
	public void shouldFailOnInvalidOption() {
		Rsync rsync = new Rsync();
		rsync.addOptions("--invalid_option");

		rsync.execute();

		assertTrue(rsync.getExitStatus() != 0);
		assertTrue(rsync.isCompleted());
		assertNull(rsync.getException());
		assertNotNull(rsync.getErrorLines());
		assertTrue(rsync.getErrorLines().length > 0);
		assertNotNull(rsync.getOutputLines());
		assertTrue(rsync.getOutputLines().length == 0);
	}

	@Test
	public void shouldResetProperly() {
		Rsync rsync = new Rsync();
		rsync.addOptions(Arrays.asList(new String[] {"--version"}));

		assertFalse(rsync.isCompleted());
		assertNull(rsync.getException());
		assertNull(rsync.getErrorLines());
		assertNull(rsync.getOutputLines());

		int exitStatus = rsync.execute();

		assertTrue(exitStatus == 0);
		assertTrue(rsync.isCompleted());
		assertNull(rsync.getException());
		assertNotNull(rsync.getErrorLines());
		assertTrue(rsync.getErrorLines().length == 0);
		assertNotNull(rsync.getOutputLines());
		assertTrue(rsync.getOutputLines().length > 0);

		rsync.reset();

		assertFalse(rsync.isCompleted());
		assertNull(rsync.getException());
		assertNull(rsync.getErrorLines());
		assertNull(rsync.getOutputLines());
	}
}
