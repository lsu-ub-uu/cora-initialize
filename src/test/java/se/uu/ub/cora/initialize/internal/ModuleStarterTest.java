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
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import se.uu.ub.cora.initialize.ImplementationForTypes;
import se.uu.ub.cora.initialize.InitializationException;
import se.uu.ub.cora.initialize.SelectOrder;
import se.uu.ub.cora.initialize.SelectType;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.logger.spies.LoggerSpy;

public class ModuleStarterTest {

	private static final String SOME_IMPLEMENTING_CLASSNAME = "someImplementingClassname";
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
	public void testLoggerCreated() {
		loggerFactorySpy.MCR.assertParameters("factorForClass", 0, ModuleStarterImp.class);
	}

	@DataProvider(name = "noImplementationsFoundData")
	public Object[][] noImplementationsFoundData() {

		Runnable getImplementationThrowErrorIfNoneOrMoreThanOne = () -> moduleStarter
				.getImplementationThrowErrorIfNoneOrMoreThanOne(Collections.emptyList(),
						SOME_IMPLEMENTING_CLASSNAME);

		Runnable getImplementationBasedOnSelectOrderThrowErrorIfNone = () -> moduleStarter
				.getImplementationBasedOnSelectOrderThrowErrorIfNone(Collections.emptyList(),
						SOME_IMPLEMENTING_CLASSNAME);

		Runnable getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType = () -> moduleStarter
				.getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType(
						Collections.emptyList(), SOME_IMPLEMENTING_CLASSNAME);

		return new Object[][] {
				{ getImplementationThrowErrorIfNoneOrMoreThanOne, SOME_IMPLEMENTING_CLASSNAME },
				{ getImplementationBasedOnSelectOrderThrowErrorIfNone,
						SOME_IMPLEMENTING_CLASSNAME },
				{ getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType,
						SOME_IMPLEMENTING_CLASSNAME }, };
	}

	@Test(dataProvider = "noImplementationsFoundData")
	public void testNoImplementationsFound(Runnable loader, String interfaceClassName) {
		try {
			loader.run();
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof InitializationException,
					"Expected exception to be of type InitializationException");

			String errorMessage = "No implementations found for: " + interfaceClassName;
			assertEquals(e.getMessage(), errorMessage, "Exception message did not match expected");

			loggerSpy.MCR.assertParameters("logFatalUsingMessage", 0, errorMessage);
		}
	}

	@Test
	public void testOneImplementation() {
		List<SelectOrder> implementations = new ArrayList<>();
		SelectOrderSpy implementationOne = new SelectOrderSpy();
		implementations.add(implementationOne);

		SelectOrder startedImplementation = moduleStarter
				.getImplementationBasedOnSelectOrderThrowErrorIfNone(implementations,
						SOME_IMPLEMENTING_CLASSNAME);

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation with select order 0.");
		assertSame(startedImplementation, implementationOne);
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"Using se.uu.ub.cora.initialize.internal.SelectOrderSpy as someImplementingClassname "
						+ "implementation.");
	}

	@Test
	public void testThreeImplementations() {
		List<SelectOrder> implementations = createImplementationsWithOrder(0, 2, 1);
		SelectOrder startedImplementation = moduleStarter
				.getImplementationBasedOnSelectOrderThrowErrorIfNone(implementations,
						SOME_IMPLEMENTING_CLASSNAME);

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

	private List<SelectOrder> createImplementationsWithOrder(int... orders) {
		List<SelectOrder> implementations = new ArrayList<>();
		for (int order : orders) {
			SelectOrderSpy implementation = createSelectOrderImplementation(order);
			implementations.add(implementation);
		}
		return implementations;
	}

	private SelectOrderSpy createSelectOrderImplementation(int order) {
		SelectOrderSpy implementation = new SelectOrderSpy();
		implementation.MRV.setDefaultReturnValuesSupplier("getOrderToSelectImplementionsBy",
				() -> order);
		return implementation;
	}

	private List<SelectType> createImplementationsWithType(String... types) {
		List<SelectType> implementations = new ArrayList<>();
		for (String type : types) {
			SelectTypeSpy implementation = createSelectTypeImplementation(type);
			implementations.add(implementation);
		}
		return implementations;
	}

	private SelectTypeSpy createSelectTypeImplementation(String type) {
		SelectTypeSpy implementation = new SelectTypeSpy();
		implementation.MRV.setDefaultReturnValuesSupplier("getTypeToSelectImplementionsBy",
				() -> type);
		return implementation;
	}

	@Test
	public void testOneImplementation_onlyOne() {
		List<SelectOrder> implementations = new ArrayList<>();
		SelectOrderSpy implementationOne = new SelectOrderSpy();
		implementations.add(implementationOne);

		SelectOrder startedImplementation = moduleStarter
				.getImplementationThrowErrorIfNoneOrMoreThanOne(implementations,
						SOME_IMPLEMENTING_CLASSNAME);

		assertSame(startedImplementation, implementationOne);
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"Found se.uu.ub.cora.initialize.internal.SelectOrderSpy as "
						+ "someImplementingClassname implementation.");
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"Using se.uu.ub.cora.initialize.internal.SelectOrderSpy as someImplementingClassname "
						+ "implementation.");
	}

	@Test
	public void testThreeImplementations_onlyOne() {
		List<SelectOrder> implementations = createImplementationsWithOrder(0, 2, 1);
		try {
			moduleStarter.getImplementationThrowErrorIfNoneOrMoreThanOne(implementations,
					SOME_IMPLEMENTING_CLASSNAME);
			fail();
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

	@Test
	public void testFindImplForEachTypeLogs_ThreeOfEachType() {
		List<SelectType> implementations = createImplementationsWithType("typeOne", "typeTwo",
				"typeThree");

		moduleStarter.getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType(
				implementations, SOME_IMPLEMENTING_CLASSNAME);

		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 0,
				"Found se.uu.ub.cora.initialize.internal.SelectTypeSpy as "
						+ "someImplementingClassname implementation with select type typeOne.");
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 1,
				"Found se.uu.ub.cora.initialize.internal.SelectTypeSpy as "
						+ "someImplementingClassname implementation with select type typeTwo.");
		loggerSpy.MCR.assertParameters("logInfoUsingMessage", 2,
				"Found se.uu.ub.cora.initialize.internal.SelectTypeSpy as "
						+ "someImplementingClassname implementation with select type typeThree.");
	}

	@Test
	public void testFindImplForEachTypeCheckReturn_ThreeOfEachType() {
		List<SelectType> implementations = createImplementationsWithType("typeOne", "typeTwo",
				"typeThree");

		ImplementationForTypes<?> implForTypes = moduleStarter
				.getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType(
						implementations, SOME_IMPLEMENTING_CLASSNAME);

		SelectType implOne = implForTypes.getImplementionByType("typeOne");
		assertEquals(implOne.getTypeToSelectImplementionsBy(), "typeOne");
		SelectType implTwo = implForTypes.getImplementionByType("typeTwo");
		assertEquals(implTwo.getTypeToSelectImplementionsBy(), "typeTwo");
		SelectType implThree = implForTypes.getImplementionByType("typeThree");
		assertEquals(implThree.getTypeToSelectImplementionsBy(), "typeThree");
	}

	@Test
	public void testImplementations_onlyOneOfEachType() {
		List<SelectType> implementations = createImplementationsWithType("typeOne", "typeTwo",
				"typeTwo");
		try {
			moduleStarter
					.getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType(
							implementations, SOME_IMPLEMENTING_CLASSNAME);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof InitializationException);
			String errorMessage = "More than one implementation found for: "
					+ "someImplementingClassname with type: typeTwo";
			assertEquals(e.getMessage(), errorMessage);
			loggerSpy.MCR.assertParameters("logFatalUsingMessage", 0, errorMessage);
		}
	}
}
