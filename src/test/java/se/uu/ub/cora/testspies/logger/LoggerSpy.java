/*
 * Copyright 2022 Olov McKie
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
package se.uu.ub.cora.testspies.logger;

import java.util.function.Supplier;

import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class LoggerSpy implements Logger {
	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public void logFatalUsingMessage(String message) {
		MCR.addCall("message", message);
	}

	@Override
	public void logFatalUsingMessageAndException(String message, Exception exception) {
		MCR.addCall("message", message, "exception", exception);
	}

	@Override
	public void logErrorUsingMessage(String message) {
		MCR.addCall("message", message);
	}

	@Override
	public void logErrorUsingMessageAndException(String message, Exception exception) {
		MCR.addCall("message", message, "exception", exception);
	}

	@Override
	public void logWarnUsingMessage(String message) {
		MCR.addCall("message", message);
	}

	@Override
	public void logWarnUsingMessageAndException(String message, Exception exception) {
		MCR.addCall("message", message, "exception", exception);
	}

	@Override
	public void logInfoUsingMessage(String message) {
		MCR.addCall("message", message);
	}

	@Override
	public void logDebugUsingMessage(String message) {
		MCR.addCall("message", message);
	}

	@Override
	public void logDebugUsingMessageSupplier(Supplier<String> messageSupplier) {
		MCR.addCall("messageSupplier", messageSupplier);
	}

	@Override
	public void logTraceUsingMessage(String message) {
		MCR.addCall("message", message);
	}

	@Override
	public void logTraceUsingMessageSupplier(Supplier<String> messageSupplier) {
		MCR.addCall("messageSupplier", messageSupplier);
	}

}
