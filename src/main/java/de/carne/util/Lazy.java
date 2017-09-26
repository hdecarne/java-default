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
package de.carne.util;

import java.util.function.Supplier;

import de.carne.check.Check;
import de.carne.check.Nullable;

/**
 * Utility class used to handle lazy initialized objects in a {@code null}-safe way.
 *
 * @param <T> The actual object type.
 */
public class Lazy<T> {

	private final Supplier<T> initializer;

	@Nullable
	private T object = null;

	/**
	 * Construct {@link Lazy}.
	 *
	 * @param initializer The {@link Supplier} to use for object initialization.
	 */
	public Lazy(Supplier<T> initializer) {
		this.initializer = initializer;
	}

	/**
	 * Get the object.
	 * <p>
	 * The actual object will be created lazily the first time this function is invoked.
	 *
	 * @return The object.
	 */
	public synchronized T get() {
		T checkedObject = this.object;

		if (checkedObject == null) {
			checkedObject = this.object = Check.notNull(this.initializer.get());
		}
		return checkedObject;
	}

	@Override
	public String toString() {
		T checkedObject = this.object;

		return (checkedObject != null ? checkedObject.toString() : "<not initialized>");
	}

}
