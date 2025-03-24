/*
 * Copyright 2022, 2025 Uppsala University Library
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
import static org.testng.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.logger.spies.LoggerSpy;

public class SettingsProviderTest {
	private static final String SOME_VALUE = "someValue";
	private static final String SOME_NAME = "someName";
	private LoggerFactorySpy loggerFactorySpy = new LoggerFactorySpy();
	private LoggerSpy onlyForTestlogger;

	@BeforeMethod
	public void beforeMethod() {
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		setOnlyForTestLogger();
	}

	private void setOnlyForTestLogger() {
		onlyForTestlogger = new LoggerSpy();
		SettingsProvider.onlyForTestSetLogger(onlyForTestlogger);
	}

	@AfterMethod
	private void afterMeth() {
		SettingsProvider.setSettings(null);
		SettingsProvider.onlyForTestClearLoggedNames();
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

	@Test
	public void testLoggerStarted() {
		loggerFactorySpy.MCR.assertParameters("factorForClass", 0, SettingsProvider.class);
	}

	@Test(expectedExceptions = InitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "Setting name: someName not found in SettingsProvider.")
	public void testSettingsNameNotFoundIfNotSetSettingsCalled() {
		SettingsProvider.getSetting(SOME_NAME);
	}

	@Test
	public void testSettingsNameNotFoundIfNotSet() {
		Map<String, String> mapOfInfo = new HashMap<>();
		SettingsProvider.setSettings(mapOfInfo);
		try {
			SettingsProvider.getSetting(SOME_NAME);
			fail("An exception should have been thrown");
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof InitializationException);
			assertEquals(e.getMessage(), "Setting name: someName not found in SettingsProvider.");
			assertErrorIsLogged();
		}
	}

	private void assertErrorIsLogged() {
		onlyForTestlogger.MCR.assertParameters("logFatalUsingMessage", 0,
				"Setting name: someName not found in SettingsProvider.");
	}

	@Test
	public void testThrowExceptionKeepsRootException() {
		try {
			SettingsProvider.getSetting(SOME_NAME);
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof NullPointerException);
			assertEquals(e.getCause().getMessage(),
					"Cannot invoke \"java.util.Map.containsKey(Object)\" "
							+ "because \"se.uu.ub.cora.initialize.SettingsProvider.settings\" is null");
			assertErrorIsLogged();
		}
	}

	@Test
	public void testGetPreviouslySetValue() {
		setOneSetting();

		String valueFromInitSetting = SettingsProvider.getSetting(SOME_NAME);

		assertEquals(valueFromInitSetting, SOME_VALUE);
		onlyForTestlogger.MCR.assertParameters("logInfoUsingMessage", 0,
				"Found: someValue as: someName");
	}

	private void setOneSetting() {
		Map<String, String> mapOfInfo = new HashMap<>();
		mapOfInfo.put(SOME_NAME, SOME_VALUE);
		SettingsProvider.setSettings(mapOfInfo);
	}

	@Test
	public void testGetSettingOnlyLogsFirstRequestOfASettingName() {
		setOneSetting();

		SettingsProvider.getSetting(SOME_NAME);
		SettingsProvider.getSetting(SOME_NAME);
		SettingsProvider.getSetting(SOME_NAME);

		onlyForTestlogger.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 1);
		onlyForTestlogger.MCR.assertParameters("logInfoUsingMessage", 0,
				"Found: someValue as: someName");
		System.err.println("end");
	}
}
