package com.dukascopy.api.util;

/**
 * Represents a local or server response to the user's request.
 */
public interface IResponse {
	
	/**
	 * Returns <code>true</code> if the request failed.
	 * 
	 * @return <code>true</code> if the request failed.
	 */
	boolean isError();
	
	/**
	 * Contains error message if <code>isError()==true</code> if the request failed.
	 * 
	 * @return error message
	 */
	String getErrorMessage();
}
