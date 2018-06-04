/*
 * Copyright (c) 2016-2018 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.util.validation;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {@linkplain InputValidator} implementation for {@linkplain Path} input.
 */
public class PathValidator extends InputValidator<Path> {

	private PathValidator(Path input) {
		super(input);
	}

	/**
	 * Checks and converts a {@linkplain String} input value to a {@linkplain Path} input.
	 *
	 * @param input the input value to check and convert.
	 * @return a new {@linkplain PathValidator} instance for further validation steps.
	 */
	public static PathValidator fromString(String input) {
		Path path = Paths.get(input);

		return new PathValidator(path);
	}

}