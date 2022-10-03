package se.uu.ub.cora.initialize.internal;

import java.util.function.Supplier;

import se.uu.ub.cora.initialize.SelectOrder;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class SelectOrderSpy implements SelectOrder {
	MethodCallRecorder MCR = new MethodCallRecorder();
	MethodReturnValues MRV = new MethodReturnValues();

	public SelectOrderSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getOrderToSelectImplementionsBy",
				(Supplier<Integer>) () -> 0);
	}

	@Override
	public int getOrderToSelectImplementionsBy() {
		return (int) MCR.addCallAndReturnFromMRV();
	}

}
