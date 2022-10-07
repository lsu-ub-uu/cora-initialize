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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class AbstractProviderTest {
	private LoggerFactory loggerFactory = new LoggerFactorySpy();

	@BeforeMethod
	public void beforeMethod() {
		LoggerProvider.setLoggerFactory(loggerFactory);
	}

	@Test
	public void testClassIsAbstract() throws Exception {
		Class<AbstractProvider> abstractClass = AbstractProvider.class;
		assertTrue(Modifier.isAbstract(abstractClass.getModifiers()));
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<AbstractProvider> constructor = AbstractProvider.class.getDeclaredConstructor();
		assertTrue(Modifier.isProtected(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InstantiationException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<AbstractProvider> constructor = AbstractProvider.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testDefaultInitializerIsModuleInitalizer() throws Exception {
		ModuleInitializer initializer = AbstractProvider.onlyForTestGetModuleInitializer();
		assertNotNull(initializer);
		assertTrue(initializer instanceof ModuleInitializerImp);
	}

	@Test
	public void testOnlyForTestSetModuleInitalizer() throws Exception {
		ModuleInitializerSpy initializerSpy = new ModuleInitializerSpy();
		AbstractProvider.onlyForTestSetModuleInitializer(initializerSpy);
		assertSame(AbstractProvider.onlyForTestGetModuleInitializer(), initializerSpy);
	}

}
