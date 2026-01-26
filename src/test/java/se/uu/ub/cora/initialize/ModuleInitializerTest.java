/*
 * Copyright 2019 Olov McKie
 * Copyright 2019, 2026 Uppsala University Library
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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ServiceLoader;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.initialize.example.SelectOrderExample;
import se.uu.ub.cora.initialize.example.SelectOrderImplementationExample;
import se.uu.ub.cora.initialize.internal.ModuleStarter;
import se.uu.ub.cora.initialize.internal.ModuleStarterImp;
import se.uu.ub.cora.initialize.internal.ModuleStarterSpy;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.logger.spies.LoggerSpy;

public class ModuleInitializerTest {
	private ModuleInitializerImp initializer;
	private LoggerFactorySpy loggerFactorySpy;

	private LoggerSpy loggerSpy;
	private Class<SelectOrderExample> classToLoad;
	private ModuleStarterSpy starter;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		classToLoad = SelectOrderExample.class;

		initializer = new ModuleInitializerImp();
		loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		starter = new ModuleStarterSpy();
		initializer.onlyForTestSetStarter(starter);
		starter.MRV.setDefaultReturnValuesSupplier(
				"getImplementationBasedOnSelectOrderThrowErrorIfNone", SelectOrderImplementationExample::new);
		starter.MRV.setDefaultReturnValuesSupplier("getImplementationThrowErrorIfNoneOrMoreThanOne",
				SelectOrderImplementationExample::new);
	}

	@Test
	public void testLogMessagesOnStartup_selectOrder() {
		initializer.loadOneImplementationBySelectOrder(classToLoad);

		String simpleName = classToLoad.getSimpleName();
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"ModuleInitializer start loading implementation of: " + simpleName + "...");

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"...moduleInitializer finished loading implementation of: " + simpleName);
	}

	@Test
	public void testRecordStorageProviderImplementationsArePassedOnToStarter_selectOrder() {
		SelectOrderImplementationExample loadedImpl = (SelectOrderImplementationExample) initializer
				.loadOneImplementationBySelectOrder(classToLoad);

		starter.MCR.assertReturn("getImplementationBasedOnSelectOrderThrowErrorIfNone", 0,
				loadedImpl);
		String methodName = "getImplementationBasedOnSelectOrderThrowErrorIfNone";
		starter.MCR.assertParameters(methodName, 0);
		ServiceLoader<?> implementations = (ServiceLoader<?>) starter.MCR
				.getParameterForMethodAndCallNumberAndParameter(methodName, 0, "implementations");
		assertNotNull(implementations);
		assertTrue(implementations instanceof ServiceLoader);
		starter.MCR.assertParameter(methodName, 0, "interfaceClassName",
				classToLoad.getSimpleName());
	}

	@Test
	public void testLogMessagesOnStartup_oneImplementation() {
		initializer.loadTheOnlyExistingImplementation(classToLoad);

		String simpleName = classToLoad.getSimpleName();
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"ModuleInitializer start loading implementation of: " + simpleName + "...");

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"...moduleInitializer finished loading implementation of: " + simpleName);
	}

	@Test
	public void testRecordStorageProviderImplementationsArePassedOnToStarter_oneImplementation() {
		SelectOrderImplementationExample loadedImpl = (SelectOrderImplementationExample) initializer
				.loadTheOnlyExistingImplementation(classToLoad);

		starter.MCR.assertReturn("getImplementationThrowErrorIfNoneOrMoreThanOne", 0, loadedImpl);
		String methodName = "getImplementationThrowErrorIfNoneOrMoreThanOne";
		starter.MCR.assertParameters(methodName, 0);
		ServiceLoader<?> implementations = (ServiceLoader<?>) starter.MCR
				.getParameterForMethodAndCallNumberAndParameter(methodName, 0, "implementations");
		assertNotNull(implementations);
		assertTrue(implementations instanceof ServiceLoader);
		starter.MCR.assertParameter(methodName, 0, "interfaceClassName",
				classToLoad.getSimpleName());
	}

	@Test
	public void testLogMessagesOnStartup_selectType() {
		initializer.loadOneImplementationOfEachType(classToLoad);

		String simpleName = classToLoad.getSimpleName();
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"ModuleInitializer start loading implementation of: " + simpleName + "...");

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"...moduleInitializer finished loading implementation of: " + simpleName);
	}

	@Test
	public void testRecordStorageProviderImplementationsArePassedOnToStarter_selectType() {
		SelectOrderImplementationExample loadedImpl = (SelectOrderImplementationExample) initializer
				.loadOneImplementationOfEachType(classToLoad);

		starter.MCR.assertReturn("getImplementationBasedOnSelectOrderThrowErrorIfNone", 0,
				loadedImpl);
		String methodName = "getImplementationBasedOnSelectOrderThrowErrorIfNone";
		starter.MCR.assertParameters(methodName, 0);
		ServiceLoader<?> implementations = (ServiceLoader<?>) starter.MCR
				.getParameterForMethodAndCallNumberAndParameter(methodName, 0, "implementations");
		assertNotNull(implementations);
		assertTrue(implementations instanceof ServiceLoader);
		starter.MCR.assertParameter(methodName, 0, "interfaceClassName",
				classToLoad.getSimpleName());
	}

	@Test
	public void testInitUsesDefaultModuleStarter() {
		initializer = new ModuleInitializerImp();

		ModuleStarterImp defaultStarter = (ModuleStarterImp) initializer.onlyForTestGetStarter();

		assertStarterIsModuleStarter(defaultStarter);
	}

	private void assertStarterIsModuleStarter(ModuleStarter starter) {
		assertTrue(starter instanceof ModuleStarterImp);
	}

}
