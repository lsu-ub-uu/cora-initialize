/*
 * Copyright 2022 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.initialize;

import java.util.Map;

import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

/**
 * SettingsProvider contains initialization settings needed to start different parts of a Cora
 * system.
 * </p>
 * Intended use is that on system starup settings are collected and {@link #setSettings(Map)} is
 * called with these settings so that other parts of the system has direct access to the settings
 * through {@link #getSetting(String)}.
 */
public class SettingsProvider {

	private static Map<String, String> settings;
	private static Logger log = LoggerProvider.getLoggerForClass(SettingsProvider.class);

	private SettingsProvider() {
		// prevent call to constructor
		throw new UnsupportedOperationException();
	}

	/**
	 * getSettings returns the setting for the specified setting name.
	 * 
	 * @param name
	 *            A String with the name of the setting to return
	 * @return The String value corresponding to the passed setting name.
	 * @throws InitializationException
	 *             if the setting name is not present
	 */
	public static String getSetting(String name) {
		try {
			return tryToGetSetting(name);
		} catch (Exception e) {
			log.logFatalUsingMessage(createMessageForName(name));
			throw new InitializationException(createMessageForName(name), e);
		}
	}

	private static String createMessageForName(String name) {
		return "Setting name: " + name + " not found in SettingsProvider.";
	}

	private static String tryToGetSetting(String name) {
		if (settings.containsKey(name)) {
			String value = settings.get(name);
			log.logInfoUsingMessage("Found: " + value + " as: " + name);
			return value;
		}
		throw new InitializationException(createMessageForName(name));
	}

	/**
	 * setSettings sets all settings using a Map
	 * 
	 * @param settings
	 *            A {@link Map} with the settings to keep track of
	 */
	public static void setSettings(Map<String, String> settings) {
		SettingsProvider.settings = settings;

	}

}
