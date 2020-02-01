package com.dukascopy.api.indicators;

import java.util.List;
import java.util.NavigableSet;
import java.util.stream.Stream;

import com.dukascopy.api.ITimedData;

public interface IFormulaTimeData extends NavigableSet<ITimedData> {

    ITimedData getFormulaBar(long time);

    ITimedData getFormulaBar(ITimedData bar);

    ITimedData getNextFormulaBar(long time);

    ITimedData getNextFormulaBar(ITimedData bar);

    ITimedData getPrevFormulaBar(long time);

    ITimedData getPrevFormulaBar(ITimedData bar);

    boolean isFormulaBarStart(ITimedData curBar, ITimedData prevBar);

    boolean isFormulaBarDisplayTime(ITimedData[] chartBars, int index, DisplayMode displayMode);

    List<ITimedData> getFormulaBars(ITimedData fromBar, boolean fromInclusive, ITimedData toBar, boolean toInclusive);

    List<ITimedData> getFormulaBarsFrom(ITimedData fromBar, boolean fromInclusive);

    List<ITimedData> getFormulaBarsTo(ITimedData toBar, boolean toInclusive);

    List<ITimedData> getFormulaBars(ITimedData curBar, ITimedData prevBar, ITimedData nextBar);

    List<ITimedData[]> getFormulaBars(ITimedData[] chartBars, int startIndex, int endIndex);

    Stream<ITimedData> getFormulaBarsStream(ITimedData fromBar, boolean fromInclusive, ITimedData toBar, boolean toInclusive);

    Stream<ITimedData> getFormulaBarsStreamFrom(ITimedData fromBar, boolean fromInclusive);

    Stream<ITimedData> getFormulaBarsStreamTo(ITimedData toBar, boolean toInclusive);

    Object getFormulaValue(long time, int outputIndex);

    Object getFormulaValue(ITimedData bar, int outputIndex);

    List<Object> getFormulaValues(int outputIndex);

    List<Object> getFormulaValues(ITimedData fromBar, boolean fromInclusive, ITimedData toBar, boolean toInclusive, int outputIndex);

    List<Object> getFormulaValuesFrom(ITimedData fromBar, boolean fromInclusive, int outputIndex);

    List<Object> getFormulaValuesTo(ITimedData toBar, boolean toInclusive, int outputIndex);

    List<Object> getFormulaValues(ITimedData curBar, ITimedData prevBar, ITimedData nextBar, int outputIndex);

    Stream<Object> getFormulaValuesStream(int outputIndex);

    Stream<Object> getFormulaValuesStream(ITimedData fromBar, boolean fromInclusive, ITimedData toBar, boolean toInclusive, int outputIndex);

    Stream<Object> getFormulaValuesStreamFrom(ITimedData fromBar, boolean fromInclusive, int outputIndex);

    Stream<Object> getFormulaValuesStreamTo(ITimedData toBar, boolean toInclusive, int outputIndex);

}
