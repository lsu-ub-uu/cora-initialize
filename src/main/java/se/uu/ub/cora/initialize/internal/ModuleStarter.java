/*
 * Copyright 2019 Uppsala University Library
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

import se.uu.ub.cora.initialize.ModuleInitializer;
import se.uu.ub.cora.initialize.SelectOrder;

/**
 * 
 * ModuleStarter is an interface used by {@link ModuleInitializer} to split the part using
 * {@link ServiceLoader} from the part choosing implementations. This split helps in testing. This
 * internal interface not intended to be used outside of this module.
 */
public interface ModuleStarter {
	/**
	 * 
	 * @param <T>
	 * @param implementations
	 * @param interfaceClassName
	 * @return
	 */
	<T extends SelectOrder> T getImplementationBasedOnSelectOrderThrowErrorIfNone(
			Iterable<T> implementations, String interfaceClassName);

	/**
	 * 
	 * @param <T>
	 * @param implementations
	 * @param interfaceClassName
	 * @return
	 */
	<T extends Object> T getImplementationThrowErrorIfNoneOrMoreThanOne(Iterable<T> implementations,
			String interfaceClassName);

}