package com.dukascopy.api;

import java.util.List;

public interface ISignalsProcessor {

    void add(ISignal signal);

    void add(List<ISignal> signals);

    List<ISignal> retrieve();
}
