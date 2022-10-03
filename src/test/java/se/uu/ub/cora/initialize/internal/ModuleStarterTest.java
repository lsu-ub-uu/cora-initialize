/*
 * Copyright 2019 Olov McKie
 * Copyright 2019, 2022 Uppsala University Library
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
package se.uu.ub.cora.initialize.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.initialize.InitializationException;
import se.uu.ub.cora.initialize.SelectOrder;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.testspies.logger.LoggerFactorySpy;
import se.uu.ub.cora.testspies.logger.LoggerSpy;

public class ModuleStarterTest {

	private LoggerFactorySpy loggerFactorySpy;
	private LoggerSpy loggerSpy;
	private ModuleStarter moduleStarter;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		moduleStarter = new ModuleStarterImp();
		loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);

	}

	@Test
	public void testLoggerCreated() throws Exception {
		loggerFactorySpy.MCR.assertParameters("factorForClass", 0, ModuleStarterImp.class);
	}

	@Test
	public void testNoImplementationsFound() throws Exception {
		Iterable<SelectOrder> implementations = new ArrayList<>();
		String interfaceClassName = "someImplementingClassname";
		try {

			moduleStarter.getImplementationBasedOnSelectOrderThrowErrorIfNone(implementations,
					interfaceClassName);

			assertTrue(false);
		} catch (Exception e) {
			assertTrue(e instanceof InitializationException);
			String errorMessage = "No implementations found for: someImplementingClassname";
			assertEquals(e.getMessage(), errorMessage);
			loggerSpy.MCR.assertParameters("logFatalUsingMessage", 0, errorMessage);
		}
	}

	@Test
	public void testOneImplementation() throws Exception {
		List<SelectOrder> implementations = new ArrayList<>();
		String interfaceClassName = "someImplementingClassname";
		SelectOrderSpy implementationOne = new SelectOrderSpy();
		implementations.add(implementationOne);

		SelectOrder startedImplementation = moduleStarter
				.getImplementationBasedOnSelectOrderThrowErrorIfNone(implementations,
						interfaceClassName);

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation with select order 0.");
		assertSame(startedImplementation, implementationOne);
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"Using se.uu.ub.cora.initialize.internal.SelectOrderSpy as someImplementingClassname "
						+ "implementation.");
	}

	@Test
	public void testThreeImplementations() throws Exception {
		List<SelectOrder> implementations = createListOfThreeImplementationsWithOrderZeroTwoOne();
		String interfaceClassName = "someImplementingClassname";

		SelectOrder startedImplementation = moduleStarter
				.getImplementationBasedOnSelectOrderThrowErrorIfNone(implementations,
						interfaceClassName);

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation with select order 0.");
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation with select order 2.");
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 2,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation with select order 1.");
		assertSame(startedImplementation, implementations.get(1));
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 3,
				"Using se.uu.ub.cora.initialize.internal.SelectOrderSpy as someImplementingClassname "
						+ "implementation.");
	}

	private List<SelectOrder> createListOfThreeImplementationsWithOrderZeroTwoOne() {
		SelectOrderSpy implementationZero = new SelectOrderSpy();
		SelectOrderSpy implementationOne = new SelectOrderSpy();
		implementationOne.MRV.setDefaultReturnValuesSupplier("getOrderToSelectImplementionsBy",
				(Supplier<Integer>) () -> 1);
		SelectOrderSpy implementationTwo = new SelectOrderSpy();
		implementationTwo.MRV.setDefaultReturnValuesSupplier("getOrderToSelectImplementionsBy",
				(Supplier<Integer>) () -> 2);

		List<SelectOrder> implementations = new ArrayList<>();
		implementations.add(implementationZero);
		implementations.add(implementationTwo);
		implementations.add(implementationOne);
		return implementations;
	}

	@Test
	public void testNoImplementationsFound_onlyOne() throws Exception {
		Iterable<SelectOrder> implementations = new ArrayList<>();
		String interfaceClassName = "someImplementingClassname";
		try {

			moduleStarter.getImplementationThrowErrorIfNoneOrMoreThanOne(implementations,
					interfaceClassName);

			assertTrue(false);
		} catch (Exception e) {
			assertTrue(e instanceof InitializationException);
			String errorMessage = "No implementations found for: someImplementingClassname";
			assertEquals(e.getMessage(), errorMessage);
			loggerSpy.MCR.assertParameters("logFatalUsingMessage", 0, errorMessage);
		}
	}

	@Test
	public void testOneImplementation_onlyOne() throws Exception {
		List<SelectOrder> implementations = new ArrayList<>();
		String interfaceClassName = "someImplementingClassname";
		SelectOrderSpy implementationOne = new SelectOrderSpy();
		implementations.add(implementationOne);

		SelectOrder startedImplementation = moduleStarter
				.getImplementationThrowErrorIfNoneOrMoreThanOne(implementations,
						interfaceClassName);

		assertSame(startedImplementation, implementationOne);
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation.");
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"Using se.uu.ub.cora.initialize.internal.SelectOrderSpy as someImplementingClassname "
						+ "implementation.");
	}

	@Test
	public void testThreeImplementations_onlyOne() throws Exception {
		List<SelectOrder> implementations = createListOfThreeImplementationsWithOrderZeroTwoOne();
		String interfaceClassName = "someImplementingClassname";
		try {

			moduleStarter.getImplementationThrowErrorIfNoneOrMoreThanOne(implementations,
					interfaceClassName);
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(e instanceof InitializationException);
			String errorMessage = "More than one implementation found for: someImplementingClassname";
			assertEquals(e.getMessage(), errorMessage);
			loggerSpy.MCR.assertParameters("logFatalUsingMessage", 0, errorMessage);
		}
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation.");
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation.");
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 2,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation.");
	}
}
