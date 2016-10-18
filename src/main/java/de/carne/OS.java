/*
 * Copyright (c) 2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne;

/**
 * Utility class to support OS specific behavior.
 */
public class OS {

	private OS() {
		// To prevent instantiation
	}

	/**
	 * OS name.
	 */
	public static final String OS_NAME = System.getProperty("os.name");

	/**
	 * Windows platform flag.
	 */
	public static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows");

	/**
	 * OS X platform flag.
	 */
	public static final boolean IS_OSX = OS_NAME.startsWith("Mac OS X");

}
