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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SettingsProviderTest {
	private static final String SOME_VALUE = "someValue";
	private static final String SOME_NAME = "someName";

	@BeforeMethod
	public void beforeMethod() {
		SettingsProvider.setSettings(null);
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<SettingsProvider> constructor = SettingsProvider.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<SettingsProvider> constructor = SettingsProvider.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test(expectedExceptions = InitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "Setting name: someName not found in SettingsProvider.")
	public void testKeyNotFoundIfNotSet() throws Exception {
		String name = SOME_NAME;
		SettingsProvider.getSetting(name);
	}

	@Test
	public void testGetPreviouslySetValue() throws Exception {
		Map<String, String> mapOfInfo = new HashMap<>();
		mapOfInfo.put(SOME_NAME, SOME_VALUE);
		SettingsProvider.setSettings(mapOfInfo);
		String valueFromInitSetting = SettingsProvider.getSetting(SOME_NAME);
		assertEquals(valueFromInitSetting, SOME_VALUE);
	}

}
