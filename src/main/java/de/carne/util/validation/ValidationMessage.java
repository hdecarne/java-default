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

/**
 * Functional interface used to generate a validation message.
 */
@FunctionalInterface
public interface ValidationMessage {

	/**
	 * Format the validation message.
	 * <p>
	 * This function is always invoked with the invalid input value as a single argument.
	 *
	 * @param arguments the format arguments.
	 * @return the formatted message.
	 */
	String format(Object... arguments);

}
