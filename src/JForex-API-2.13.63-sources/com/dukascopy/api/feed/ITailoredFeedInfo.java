package com.dukascopy.api.feed;

import com.dukascopy.api.ITimedData;

/**
 * Feed descriptor which works only with a certain feed type
 *
 * @param <T> feed data type
 */
@Deprecated
public interface ITailoredFeedInfo<T extends ITimedData> extends IFeedInfo {

}
