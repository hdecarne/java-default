/*
 * Copyright (c) 2016-2019 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.test.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.carne.util.ManifestInfos;

/**
 * Test {@linkplain ManifestInfos} class.
 */
class ManifestInfosTest {

	@Test
	void testKnownManifestInfos() {
		ManifestInfos manifestInfos = new ManifestInfos("Test Application");

		// As defined in our test manifest
		Assertions.assertEquals("Test Application", manifestInfos.name());
		Assertions.assertEquals("1.0-test", manifestInfos.version());
		// As undefined in our test manifest
		Assertions.assertEquals("n/a", manifestInfos.build());
	}

	@Test
	void testUnknownManifestInfos() {
		ManifestInfos manifestInfos = new ManifestInfos("Unknown Application");

		Assertions.assertEquals("Unknown Application", manifestInfos.name());
		Assertions.assertEquals("n/a", manifestInfos.version());
		Assertions.assertEquals("n/a", manifestInfos.build());
	}

}
