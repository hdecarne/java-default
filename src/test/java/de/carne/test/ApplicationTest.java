/*
 * Copyright (c) 2016-2017 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.Application;
import de.carne.Main;

/**
 * Test {@link Application} class.
 */
public class ApplicationTest implements Main {

	/**
	 * Setup the necessary system properties.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		System.setProperty("de.carne.Application", "test");
		System.setProperty("de.carne.Application.DEBUG", "true");
	}

	private static final String[] TEST_ARGS = new String[] { "arg1", "arg2" };

	/**
	 * Test application initialization and start.
	 */
	@Test
	public void testApplicationStart() {
		Application.main(TEST_ARGS);
	}

	@Override
	public int run(String[] args) {
		Assert.assertArrayEquals(TEST_ARGS, args);
		Assert.assertTrue(Boolean.getBoolean(ApplicationTest.class.getTypeName() + ".Property1"));
		Assert.assertFalse(Boolean.getBoolean(ApplicationTest.class.getTypeName() + ".Property2"));
		Assert.assertEquals("Any text...", System.getProperty(ApplicationTest.class.getTypeName() + ".Property3"));
		return 0;
	}

}