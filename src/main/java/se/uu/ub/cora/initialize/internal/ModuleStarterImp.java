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

import java.util.Map;

import se.uu.ub.cora.initialize.InitializedTypes;
import se.uu.ub.cora.initialize.InitializationException;
import se.uu.ub.cora.initialize.SelectOrder;
import se.uu.ub.cora.initialize.SelectType;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

public class ModuleStarterImp implements ModuleStarter {
	private static final int SMALLEST_PREFERENCE_LEVEL = -99999;
	private Logger log = LoggerProvider.getLoggerForClass(ModuleStarterImp.class);

	@Override
	public <T extends SelectOrder> T getImplementationBasedOnSelectOrderThrowErrorIfNone(
			Iterable<T> implementations, String interfaceClassName) {
		T implementation = findAndLogPreferedImplementation(implementations, interfaceClassName);
		throwErrorIfNoImplementationFound(implementation, interfaceClassName);
		logChosenImplementationClass(interfaceClassName, implementation);
		return implementation;
	}

	private <T extends Object> void logChosenImplementationClass(String interfaceClassName,
			T implementation) {
		log.logInfoUsingMessage("Using " + implementation.getClass().getName() + " as "
				+ interfaceClassName + " implementation.");
	}

	private <T extends SelectOrder> T findAndLogPreferedImplementation(Iterable<T> implementations,
			String interfaceClassName) {
		T implementation = null;
		int preferenceLevel = SMALLEST_PREFERENCE_LEVEL;
		for (T currentImplementation : implementations) {
			if (preferenceLevel < currentImplementation.getOrderToSelectImplementionsBy()) {
				preferenceLevel = currentImplementation.getOrderToSelectImplementionsBy();
				implementation = currentImplementation;
			}
			logFoundClassWithSelectOrder(interfaceClassName, currentImplementation);
		}
		return implementation;
	}

	private <T extends SelectOrder> void logFoundClassWithSelectOrder(String interfaceClassName,
			T currentImplementation) {
		log.logInfoUsingMessage("Found " + currentImplementation.getClass().getName() + " as "
				+ interfaceClassName + " implementation with select order "
				+ currentImplementation.getOrderToSelectImplementionsBy() + ".");
	}

	private <T extends Object> void throwErrorIfNoImplementationFound(T implementation,
			String interfaceClassName) {
		if (null == implementation) {
			String errorMessage = "No implementations found for: " + interfaceClassName;
			log.logFatalUsingMessage(errorMessage);
			throw new InitializationException(errorMessage);
		}
	}

	@Override
	public <T extends Object> T getImplementationThrowErrorIfNoneOrMoreThanOne(
			Iterable<T> implementations, String interfaceClassName) {
		T implementation = null;
		int noOfImplementationsFound = 0;
		for (T currentImplementation : implementations) {
			noOfImplementationsFound++;
			implementation = currentImplementation;
			logFoundClass(interfaceClassName, currentImplementation);
		}
		throwErrorIfNoImplementationFound(implementation, interfaceClassName);
		throwErrorIfMoreThanOne(noOfImplementationsFound, interfaceClassName);
		logChosenImplementationClass(interfaceClassName, implementation);
		return implementation;
	}

	private void throwErrorIfMoreThanOne(int noOfImplementationsFound, String interfaceClassName) {
		if (noOfImplementationsFound > 1) {
			String errorMessage = "More than one implementation found for: " + interfaceClassName;
			log.logFatalUsingMessage(errorMessage);
			throw new InitializationException(errorMessage);
		}
	}

	private <T extends Object> void logFoundClass(String interfaceClassName,
			T currentImplementation) {
		log.logInfoUsingMessage("Found " + currentImplementation.getClass().getName() + " as "
				+ interfaceClassName + " implementation.");
	}

	@Override
	public <T extends SelectType> InitializedTypes<T> getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType(
			Iterable<T> implementations, String interfaceClassName) {
		throwErrorIfNoImplementationsFound(implementations, interfaceClassName);
		return organizeFoundImplementationsByType(implementations, interfaceClassName);
	}

	private <T extends SelectType> void throwErrorIfNoImplementationsFound(
			Iterable<T> implementations, String interfaceClassName) {
		if (hasNoImplementations(implementations)) {
			throwErrorIfNoImplementationsFound(interfaceClassName);
		}
	}

	private <T extends SelectType> InitializedTypes<T> organizeFoundImplementationsByType(Iterable<T> implementations,
			String interfaceClassName) {
		ImplementationForTypesImpl<T> implementationForTypes = new ImplementationForTypesImpl<>();
		addImplementationsToMapByType(implementationForTypes.map, implementations,
				interfaceClassName);
		return implementationForTypes;
	}

	private <T extends SelectType> void addImplementationsToMapByType(Map<String, T> map,
			Iterable<T> implementations, String interfaceClassName) {
		for (T currentImplementation : implementations) {
			logFoundClassWithSelectType(interfaceClassName, currentImplementation);
			addImplementationToMapByType(interfaceClassName, map, currentImplementation);
		}
	}

	private <T extends SelectType> void addImplementationToMapByType(String interfaceClassName,
			Map<String, T> implementationsMap, T currentImplementation) {
		String currentType = currentImplementation.getTypeToSelectImplementionsBy();
		if (typeAlreadyExists(implementationsMap, currentType)) {
			throwExceptionWhenTypeAlreadyExists(interfaceClassName, currentType);
		}
		implementationsMap.put(currentType, currentImplementation);
	}

	private <T extends SelectType> boolean typeAlreadyExists(Map<String, T> map,
			String currentType) {
		return map.containsKey(currentType);
	}

	private void throwExceptionWhenTypeAlreadyExists(String interfaceClassName,
			String currentType) {
		String errorMessage = "More than one implementation found for: " + interfaceClassName
				+ " with type: " + currentType;
		log.logFatalUsingMessage(errorMessage);
		throw new InitializationException(errorMessage);
	}

	private <T extends SelectType> boolean hasNoImplementations(Iterable<T> implementations) {
		return !implementations.iterator().hasNext();
	}

	private void throwErrorIfNoImplementationsFound(String interfaceClassName) {
		String errorMessage = "No implementations found for: " + interfaceClassName;
		log.logFatalUsingMessage(errorMessage);
		throw new InitializationException(errorMessage);
	}

	private <T extends SelectType> void logFoundClassWithSelectType(String interfaceClassName,
			T currentImplementation) {
		log.logInfoUsingMessage("Found " + currentImplementation.getClass().getName() + " as "
				+ interfaceClassName + " implementation with select type "
				+ currentImplementation.getTypeToSelectImplementionsBy() + ".");
	}

}
