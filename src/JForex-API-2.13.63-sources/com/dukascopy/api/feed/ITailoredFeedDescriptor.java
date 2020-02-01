package com.dukascopy.api.feed;

import com.dukascopy.api.ITimedData;

/**
 * Feed descriptor which works only with a certain feed type
 *
 * @param <T> type of feed data element
 */
public interface ITailoredFeedDescriptor<T extends ITimedData> extends IFeedDescriptor {

}
