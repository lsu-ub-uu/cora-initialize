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
package se.uu.ub.cora.initialize;

import java.util.ServiceLoader;

import se.uu.ub.cora.initialize.internal.ModuleStarter;
import se.uu.ub.cora.initialize.internal.ModuleStarterImp;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

/**
 * ModuleInitializer is intended to be used by different providers as a help to find the (factory)
 * classes that implement the functionality they need with the help of javas module system. It
 * provied two functions depending on if the implementation is expected to only exist once or if it
 * is expected that there will be more than one implementation available.
 * </p>
 * There is a method {@link #onlyForTestSetStarter(ModuleStarter)} that makes it possible to change
 * the starter to enable easier testing.
 */
public class ModuleInitializer {
	private Logger log = LoggerProvider.getLoggerForClass(ModuleInitializer.class);
	private ModuleStarter starter = new ModuleStarterImp();

	/**
	 * @throws InitializationException
	 *             if no implementations can be found
	 * @param <T>
	 * @param factoryClass
	 * @return
	 */
	public <T extends SelectOrder> T loadOneImplementationBySelectOrder(Class<T> factoryClass) {
		String nameOfClass = factoryClass.getSimpleName();
		logStartMessage(nameOfClass);
		T loadedImpl = starter.getImplementationBasedOnSelectOrderThrowErrorIfNone(
				ServiceLoader.load(factoryClass), nameOfClass);
		logFinishedMessage(nameOfClass);
		return loadedImpl;
	}

	private void logStartMessage(String nameOfClass) {
		log.logInfoUsingMessage(
				"ModuleInitializer start loading implementation of: " + nameOfClass + "...");
	}

	private void logFinishedMessage(String nameOfClass) {
		log.logInfoUsingMessage(
				"...moduleInitializer finished loading implementation of: " + nameOfClass);
	}

	ModuleStarter onlyForTestGetStarter() {
		return starter;
	}

	void onlyForTestSetStarter(ModuleStarter starter) {
		this.starter = starter;
	}
	// TODO: rename to loadOneImplementationBySelectOrder
	// TODO: add second method loadOnlyExistingImplementation
	// TODO: should only find and start implementations of factory once
	// TODO:etc.. :)

	public <T extends Object> void loadOnlyExistingImplementation(Class<T> factoryClass) {
		String nameOfClass = factoryClass.getSimpleName();
		logStartMessage(nameOfClass);
		logFinishedMessage(nameOfClass);
		// TODO Auto-generated method stub

	}

}
