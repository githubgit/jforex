package com.dukascopy.api.strategy;

import com.dukascopy.api.util.IResponse;

/**
 * Represents the local strategy processor or remote server response to the user's request.
 *
 * @param <T> type of result value
 */
public interface IStrategyResponse<T> extends IResponse {

	/**
	 * Returns request's response result value, should be null if <code>isError()==true</code>.
	 * 
	 * @return request's response result value
	 */
	T getResult();
	
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
