/*
 * Copyright 2026 Uppsala University Library
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

/**
 * InitializedTypes holds one implementation for each type.
 */
public interface InitializedTypes<T extends SelectType> {
	/**
	 * getImplementionByType returns the found implementation for the requested type. <br>
	 * 
	 * @param <T>
	 *            An instance of the specified interface (implementation)
	 * @param type
	 *            The type to get implementaion for
	 * @return The found implementation of type T <SelectType>.
	 * 
	 * @throws InitializationException
	 *             If the implementation type do not exists should an
	 *             {@link InitializationException} be thrown.
	 */
	T getImplementationByType(String type);
}
