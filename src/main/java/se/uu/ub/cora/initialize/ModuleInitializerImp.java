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

public class ModuleInitializerImp implements ModuleInitializer {
	private Logger log = LoggerProvider.getLoggerForClass(ModuleInitializerImp.class);
	private ModuleStarter starter = new ModuleStarterImp();

	@Override
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

	@Override
	public <T extends Object> T loadTheOnlyExistingImplementation(Class<T> factoryClass) {
		String nameOfClass = factoryClass.getSimpleName();
		logStartMessage(nameOfClass);
		T loadedImp = starter.getImplementationThrowErrorIfNoneOrMoreThanOne(
				ServiceLoader.load(factoryClass), nameOfClass);
		logFinishedMessage(nameOfClass);
		return loadedImp;
	}

	/**
	 * onlyForTestSetStarter is only intended to be used in testing
	 * 
	 * @param starter
	 *            A ModuleStarter that returns a test factory
	 */
	void onlyForTestSetStarter(ModuleStarter starter) {
		this.starter = starter;
	}

	ModuleStarter onlyForTestGetStarter() {
		return starter;
	}
}
