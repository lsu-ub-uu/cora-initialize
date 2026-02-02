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

import static org.testng.Assert.assertTrue;

import java.util.ServiceLoader;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.initialize.example.SelectOrderExample;
import se.uu.ub.cora.initialize.example.SelectOrderImplementationExample;
import se.uu.ub.cora.initialize.example.SelectTypeExample;
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
	private Class<SelectOrderExample> classToLoadSelectOrder;
	private Class<SelectTypeExample> classToLoadSelectType;
	private ModuleStarterSpy starter;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		classToLoadSelectOrder = SelectOrderExample.class;
		classToLoadSelectType = SelectTypeExample.class;

		initializer = new ModuleInitializerImp();
		loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		starter = new ModuleStarterSpy();
		initializer.onlyForTestSetStarter(starter);
		starter.MRV.setDefaultReturnValuesSupplier(
				"getImplementationBasedOnSelectOrderThrowErrorIfNone",
				SelectOrderImplementationExample::new);
		starter.MRV.setDefaultReturnValuesSupplier("getImplementationThrowErrorIfNoneOrMoreThanOne",
				SelectOrderImplementationExample::new);
	}

	@Test
	public void testLogMessagesOnStartup_selectOrder() {
		initializer.loadOneImplementationBySelectOrder(classToLoadSelectOrder);

		String simpleName = classToLoadSelectOrder.getSimpleName();
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"ModuleInitializer start loading implementation of: " + simpleName + "...");

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"...moduleInitializer finished loading implementation of: " + simpleName);
	}

	@Test
	public void testRecordStorageProviderImplementationsArePassedOnToStarter_selectOrder() {
		SelectOrderImplementationExample loadedImpl = (SelectOrderImplementationExample) initializer
				.loadOneImplementationBySelectOrder(classToLoadSelectOrder);

		String methodName = "getImplementationBasedOnSelectOrderThrowErrorIfNone";
		assertParametersAndReturnForStaterModule(methodName, loadedImpl);
	}

	@Test
	public void testLogMessagesOnStartup_oneImplementation() {
		initializer.loadTheOnlyExistingImplementation(classToLoadSelectOrder);

		String simpleName = classToLoadSelectOrder.getSimpleName();
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"ModuleInitializer start loading implementation of: " + simpleName + "...");

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"...moduleInitializer finished loading implementation of: " + simpleName);
	}

	@Test
	public void testRecordStorageProviderImplementationsArePassedOnToStarter_oneImplementation() {
		SelectOrderImplementationExample loadedImpl = (SelectOrderImplementationExample) initializer
				.loadTheOnlyExistingImplementation(classToLoadSelectOrder);

		String methodName = "getImplementationThrowErrorIfNoneOrMoreThanOne";
		assertParametersAndReturnForStaterModule(methodName, loadedImpl);

	}

	private void assertParametersAndReturnForStaterModule(String methodName,
			Object loadedImplementations) {
		assertServiceLoaderAsPassedParameter(methodName);
		starter.MCR.assertParameter(methodName, 0, "interfaceClassName",
				classToLoadSelectOrder.getSimpleName());
		starter.MCR.assertReturn(methodName, 0, loadedImplementations);
	}

	private void assertServiceLoaderAsPassedParameter(String methodName) {
		ServiceLoader<?> implementations = (ServiceLoader<?>) starter.MCR
				.getParameterForMethodAndCallNumberAndParameter(methodName, 0, "implementations");
		assertTrue(implementations instanceof ServiceLoader);
	}

	@Test
	public void testLogMessagesOnStartup_selectType() {
		initializer.loadOneImplementationOfEachType(classToLoadSelectType);

		String simpleName = classToLoadSelectType.getSimpleName();
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"ModuleInitializer start loading implementation of: " + simpleName + "...");

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"...moduleInitializer finished loading implementation of: " + simpleName);
	}

	@Test
	public void testRecordStorageProviderImplementationsArePassedOnToStarter_selectType() {
		InitializedTypes loadedImplementations = initializer
				.loadOneImplementationOfEachType(classToLoadSelectType);

		String methodName = "getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType";
		assertParametersAndReturnForStaterModuleForSelectType(methodName, loadedImplementations);
	}

	private void assertParametersAndReturnForStaterModuleForSelectType(String methodName,
			Object loadedImplementations) {
		assertServiceLoaderAsPassedParameter(methodName);
		starter.MCR.assertParameter(methodName, 0, "interfaceClassName",
				classToLoadSelectType.getSimpleName());

		starter.MCR.assertReturn(methodName, 0, loadedImplementations);
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
