/*
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
package se.uu.ub.cora.initialize.internal;

import java.util.ServiceLoader;

import se.uu.ub.cora.initialize.InitializedTypes;
import se.uu.ub.cora.initialize.InitializationException;
import se.uu.ub.cora.initialize.ModuleInitializerImp;
import se.uu.ub.cora.initialize.SelectOrder;
import se.uu.ub.cora.initialize.SelectType;

/**
 * ModuleStarter handles implementations found by {@link ModuleInitializerImp}. It helps to split
 * the part using {@link ServiceLoader} from the part choosing implementations. This split helps in
 * testing.
 */
public interface ModuleStarter {
	/**
	 * getImplementationBasedOnSelectOrderThrowErrorIfNone
	 * 
	 * @param <T>
	 * @param implementations
	 * @param interfaceClassName
	 * @throws InitializationException
	 *             if no implementations can be found
	 * @return
	 */
	<T extends SelectOrder> T getImplementationBasedOnSelectOrderThrowErrorIfNone(
			Iterable<T> implementations, String interfaceClassName);

	/**
	 * getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType organize an
	 * implementation for each type from the given implementation iterable.
	 * 
	 * If more than one implementation of a type or no impementations at all are found MUST
	 * an @throws InitializationException be thrown.
	 * 
	 * @param <T>
	 *            A found implementation of the specified classToLoad
	 * @param implementations
	 *            An Iterable with all the implementations for the class to load
	 * @param interfaceClassName
	 *            Name of the class to load
	 * @return An {@link InitializedTypes} object, with the implementation found for each
	 *         type.
	 */
	<T extends SelectType> InitializedTypes<T> getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType(
			Iterable<T> implementations, String interfaceClassName);

	/**
	 * getImplementationThrowErrorIfNoneOrMoreThanOne
	 * 
	 * @param <T>
	 * @param implementations
	 * @param interfaceClassName
	 * @throws InitializationException
	 *             if no implementations can be found
	 * @return
	 */
	<T extends Object> T getImplementationThrowErrorIfNoneOrMoreThanOne(Iterable<T> implementations,
			String interfaceClassName);

}