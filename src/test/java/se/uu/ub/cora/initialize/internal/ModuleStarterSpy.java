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

import se.uu.ub.cora.initialize.ImplementationForTypes;
import se.uu.ub.cora.initialize.SelectOrder;
import se.uu.ub.cora.initialize.SelectType;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class ModuleStarterSpy implements ModuleStarter {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public ModuleStarterSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getImplementationBasedOnSelectOrderThrowErrorIfNone",
				SelectOrderSpy::new);
		MRV.setDefaultReturnValuesSupplier("getImplementationThrowErrorIfNoneOrMoreThanOne",
				SelectOrderSpy::new);
		MRV.setDefaultReturnValuesSupplier(
				"getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType",
				ImplementationForTypesSpy::new);
	}

	@Override
	public <T extends SelectOrder> T getImplementationBasedOnSelectOrderThrowErrorIfNone(
			Iterable<T> implementations, String interfaceClassName) {
		return (T) MCR.addCallAndReturnFromMRV("implementations", implementations,
				"interfaceClassName", interfaceClassName);
	}

	@Override
	public <T> T getImplementationThrowErrorIfNoneOrMoreThanOne(Iterable<T> implementations,
			String interfaceClassName) {
		return (T) MCR.addCallAndReturnFromMRV("implementations", implementations,
				"interfaceClassName", interfaceClassName);
	}

	@Override
	public <T extends SelectType> ImplementationForTypes getImplementationBasedOnSelectTypeThrowErrorIfNoneOrMoreThanOneForEachType(
			Iterable<T> implementations, String interfaceClassName) {
		return (ImplementationForTypes) MCR.addCallAndReturnFromMRV("implementations",
				implementations, "interfaceClassName", interfaceClassName);
	}

}
