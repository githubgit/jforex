package com.dukascopy.api;

public interface IJFRunnable<T extends IContext> {

	void onStart(T t) throws JFException;

	void onStop() throws JFException;
}