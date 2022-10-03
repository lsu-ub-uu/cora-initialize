/*
 * Copyright 2019 Olov McKie
 * Copyright 2019, 2022 Uppsala University Library
 * 
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
//import se.uu.ub.cora.storage.RecordStorageProvider;

public class ModuleInitializer {
	private Logger log = LoggerProvider.getLoggerForClass(ModuleInitializer.class);
	private ModuleStarter starter = new ModuleStarterImp();

	public <T extends SelectOrder> T loadImplementation(Class<T> factoryClass) {
		String nameOfClass = factoryClass.getSimpleName();
		log.logInfoUsingMessage(
				"ModuleInitializer start loading implementation of: " + nameOfClass + "...");

		starter.getImplementationBasedOnSelectOrderThrowErrorIfNone(
				ServiceLoader.load(factoryClass), nameOfClass);
		log.logInfoUsingMessage(
				"...moduleInitializer finished loading implementation of: " + nameOfClass);
		return null;
	}

	ModuleStarter onlyForTestGetStarter() {
		return starter;
	}

	void onlyForTestSetStarter(ModuleStarter starter) {
		this.starter = starter;
	}
	// TODO: rename to getOneImplementation
	// TODO: add second method getOnlyImplementation
	// TODO: should only find and start implementations of factory once
	// TODO:etc.. :)

}
