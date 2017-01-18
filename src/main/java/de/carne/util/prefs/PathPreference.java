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
package de.carne.util.prefs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

import de.carne.check.NonNullByDefault;
import de.carne.util.Exceptions;

/**
 * Utility class providing access to a {@link Path} preference.
 */
@NonNullByDefault
public class PathPreference extends Preference<Path> {

	/**
	 * {@link Files#isRegularFile(Path, java.nio.file.LinkOption...)} validator.
	 */
	public static final Predicate<Path> IS_REGULAR_FILE = (p) -> Files.isRegularFile(p);

	/**
	 * {@link Files#isDirectory(Path, java.nio.file.LinkOption...)} validator.
	 */
	public static final Predicate<Path> IS_DIRECTORY = (p) -> Files.isDirectory(p);

	private final Predicate<Path> validator;

	/**
	 * Construct {@code PathPreference}.
	 *
	 * @param preferences The {@link Preferences} object storing this preference.
	 * @param key The preference key.
	 */
	public PathPreference(Preferences preferences, String key) {
		this(preferences, key, (p) -> true);
	}

	/**
	 * Construct {@code PathPreference}.
	 *
	 * @param preferences The {@link Preferences} object storing this preference.
	 * @param key The preference key.
	 * @param validator {@link Predicate} to use for preference value validation.
	 */
	public PathPreference(Preferences preferences, String key, Predicate<Path> validator) {
		super(preferences, key);
		this.validator = validator;
	}

	/**
	 * Get the preference value as a {@link File} object.
	 *
	 * @return The found preference value, or {@code null} if the preference is undefined.
	 */
	public File getValueAsFile() {
		return getValueAsFile(null);
	}

	/**
	 * Get the preference value as a {@link File} object.
	 *
	 * @param defaultValue The default preference value to return in case the preference is undefined.
	 * @return The found preference value.
	 */
	public File getValueAsFile(File defaultValue) {
		Path value = get();

		return (value != null ? value.toFile() : defaultValue);
	}

	/**
	 * Set the preference value from a {@link File} object.
	 *
	 * @param value The value to set. If {@code null} the preference is removed.
	 */
	public void putValueFromFile(File value) {
		put(value != null ? value.toPath() : null);
	}

	@Override
	protected Path toValue(String valueString) {
		Path value = null;

		try {
			value = validatePath(Paths.get(valueString));
		} catch (InvalidPathException e) {
			Exceptions.ignore(e);
		}
		return value;
	}

	@Override
	protected String fromValue(Path value) {
		return value.toString();
	}

	private Path validatePath(Path path) {
		return (path == null || this.validator.test(path) ? path : null);
	}

}