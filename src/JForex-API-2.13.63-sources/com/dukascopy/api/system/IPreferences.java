package com.dukascopy.api.system;

import com.dukascopy.api.Filter;

/**
 * Global preference holder for JForex-SDK 
 */
public interface IPreferences {
	
	/**
	 * Returns global chart settings
	 * 
	 * @return global chart settings
	 */
	IPreferences.Chart chart();
	
	
	/**
	 * Returns global platform settings
	 * 
	 * @return global platfomr settings
	 */
	IPreferences.Platform platform();
	
	/**
	 * Global platform settings
	 */
	interface Platform extends PreferenceRoot{
		
		/**
		 * Returns global preferences settings
		 * 
		 * @return global preferences settings
		 */
		PreferencesSettings platformSettings();
		
		/**
		 * Global preferences settings
		 */
		interface PreferencesSettings extends PreferenceRoot{
			
			/**
			 * This parameter allows to skip incoming ticks (skipTicks variable is true) if the system is not fast enough (ticks are received faster than they can be processed).
			 * This means that not all incoming ticks will be processed, but those, who will be processed, will not wait in the queue, so more actual data are available for users.   
			 * Otherwise, if the client cannot process incoming ticks fast enough, then these ticks are added to the queue (skipTicks variable is false). In such a case the incoming 
			 * new ticks will be processed with a delay, but the accumulated history will be more correct.  
			 * 
			 * Default value for this parameter is true;
			 * 
			 * @param skipTicks skip ticks
			 * @return this
			 */
			PreferencesSettings skipTicks(boolean skipTicks);
			
			boolean isSkipTicks();
		}
		
	}
	
	/**
	 * Global chart settings
	 */
	interface Chart extends PreferenceRoot{
		
		/**
		 * Returns global order on-chart appearance settings
		 * @deprecated Global visibility settings removed from preferences and set separately for each chart.
		 * @return global order on-chart appearance settings
		 */
	    @Deprecated
		Orders orders();
		
		/**
		 * Returns global position on-chart appearance settings
		 * @deprecated Global visibility settings removed from preferences and set separately for each chart.
		 * @return global position on-chart appearance settings
		 */
		@Deprecated
		Positions positions();
		
		/**
		 * Returns global chart filtration settings
		 * 
		 * @return global chart filtration settings
		 */
		Filtration filtration();
		
		/**
		 * Global chart filtration settings
		 */
		interface Filtration extends PreferenceRoot{
			
		    public enum SundayFilter {
		        NONE,
		        SUNDAY_IN_MONDAY,
		        SKIP_SUNDAY
		    }
			
			Filtration filter(Filter filter);
			
			Filtration sundayCandleFilter(SundayFilter sundayCandles);
			
			Filter getFilter();
			
			SundayFilter getSundayCandleFilter();
		}
		
		/**
		 * Global order on-chart appearance settings
		 */
		interface Orders extends PreferenceRoot{
			Orders entryOrders(boolean showEntryOrders);
			boolean showEntryOrders();
		}
		
		/**
		 * Global position on-chart appearance settings
		 */
		interface Positions extends PreferenceRoot{			
			Positions openPositions(boolean showOpenPositions);
			Positions closedPositions(boolean showClosedPositions);
			
			/**
             * @param showPositionIdLabels show labels
             * @return this
			 * @deprecated positions info implemented as tooltips
			 */
			@Deprecated
			Positions positionExternalIdLabels(boolean showPositionIdLabels);
			
			boolean showOpenPositions();
			boolean showClosedPositions();			
			
			/**
             * @return show labels
             * @deprecated positions info implemented as tooltips
             */
			@Deprecated
			boolean showPositionExternalIdLabels();
		}
	}
	
	/**
	 * Implemented by all settings classes to be able to navigate to the 
	 * preference root	
	 */
	interface PreferenceRoot{
		IPreferences preferences();
	}
}
