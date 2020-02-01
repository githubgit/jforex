package com.dukascopy.api;

import java.io.PrintStream;

public class ConsoleAdapter implements IConsole {

	@Override
	public PrintStream getOut() {
		return null;
	}

	@Override
	public PrintStream getErr() {
		return null;
	}

	@Override
	public PrintStream getWarn() {
		return getOut();
	}

	@Override
	public PrintStream getInfo() {
		return getOut();
	}

	@Override
	public PrintStream getNotif() {
		return getOut();
	}

	@Override
	public void setMaxMessages(int maxNumber) {
	}
}