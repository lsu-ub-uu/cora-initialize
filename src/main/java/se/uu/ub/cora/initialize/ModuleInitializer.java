/*
 * Copyright 2022, 2026 Uppsala University Library
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

import se.uu.ub.cora.initialize.internal.ModuleStarter;

/**
 * ModuleInitializer is intended to be used by different providers in order to find the (factory)
 * classes that implement the functionality they need with the help of javas module system. This
 * class provides two functions that can be used depending on how many implementations we expect to
 * find:
 * <ol>
 * <li>for one use {@link #loadTheOnlyExistingImplementation(Class)}</li>
 * <li>for more than one use {@link #loadOneImplementationBySelectOrder(Class)}</li>
 * </ol>
 * </p>
 * There is a method {@link #onlyForTestSetStarter(ModuleStarter)} that makes it possible to change
 * the starter to enable easier testing.
 * 
 */
public interface ModuleInitializer {

	/**
	 * loadOneImplementationBySelectOrder uses javas module system to find and return an
	 * implementation of the specified classToLoad.
	 * </p>
	 * If no implementations can be found MUST an @throws InitializationException be thrown
	 * 
	 * @param <T>
	 *            A found implementation of the specified classToLoad
	 * @param classToLoad
	 *            A Class to load
	 * @return An instance of the specified classToLoad
	 */
	<T extends SelectOrder> T loadOneImplementationBySelectOrder(Class<T> classToLoad);

	/**
	 * loadTheOnlyExistingImplementation uses javas module system to find and return an
	 * implementation of the specified classToLoad.
	 * 
	 * If none or more than one implementation is found MUST an @throws InitializationException be
	 * thrown
	 * 
	 * @param <T>
	 *            A found implementation of the specified classToLoad
	 * @param classToLoad
	 *            A Class to load
	 * @return An instance of the specified classToLoad
	 */
	<T extends Object> T loadTheOnlyExistingImplementation(Class<T> classToLoad);

	/**
	 * loadOneImplementationOfEachType uses javas module system to find and return an implementation
	 * of each type of the specified classToLoad.
	 * 
	 * If more than one implementation of a type or no impementations at all are found MUST
	 * an @throws InitializationException be thrown.
	 * 
	 * @param <T>
	 *            A found implementation of the specified classToLoad
	 * @param classToLoad
	 *            A Class to load
	 * @return An {@link ImplementationForTypes} object, with the implementation found for each
	 *         type.
	 */
	<T extends SelectType> ImplementationForTypes loadOneImplementationOfEachType(
			Class<T> classToLoad);

}
