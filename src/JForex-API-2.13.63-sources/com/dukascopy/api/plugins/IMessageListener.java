package com.dukascopy.api.plugins;

import com.dukascopy.api.IMessage;
import com.dukascopy.api.JFException;

public interface IMessageListener {

    void onMessage(IMessage message) throws JFException;
}
