package com.dukascopy.api.system.tester;

import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
import com.dukascopy.api.system.ITesterClient.InterpolationMethod;

public interface ITesterDataInterval {

	long getFrom();
	
	long getTo();
	
	public static interface ILoading extends ITesterDataInterval {
		
		DataLoadingMethod getDataLoadingMethod();
	}
	
	public static interface IInterpolation extends ITesterDataInterval {
		
		InterpolationMethod getInterpolationMethod();
		
		OfferSide getOfferSide();
		
		Period getPeriod();
	}
}
