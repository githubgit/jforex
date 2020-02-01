package com.dukascopy.indicators;

import com.dukascopy.api.*;
import com.dukascopy.api.indicators.*;

/**
 * CORREL indicator for two instruments.
 */
public class CorrelTwoInstruments implements IIndicator, IChartInstrumentsListener {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private double[][] inputs = new double[2][];
    private double[][] outputs = new double[1][];

    private IIndicator correl;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("CORREL_2INSTRUMENTS",
                "Pearson's Correlation Coefficient(r) for two instruments", "Statistic Functions",
                false, false, false, 2, 3, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price 0", InputParameterInfo.Type.DOUBLE),
            new InputParameterInfo("Price 1", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(30, 1, 2000, 1)),
            new OptInputParameterInfo("First instrument", OptInputParameterInfo.Type.OTHER,
                    new InstrumentListDescription(Instrument.EURUSD, new Instrument[] {Instrument.EURUSD})),
            new OptInputParameterInfo("Second instrument", OptInputParameterInfo.Type.OTHER,
                    new InstrumentListDescription(Instrument.EURUSD, new Instrument[] {Instrument.EURUSD}))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, true)
        };

        correl = context.getIndicatorsProvider().getIndicator("CORREL");

        onInstrumentsChanged(context.getChartInstruments());
        context.addChartInstrumentsListener(this);
    }

    @Override
    public void onInstrumentsChanged(Instrument[] chartInstr) {
        Instrument[] masterInstr, slaveInstr;
        if (chartInstr != null) {
            masterInstr = new Instrument[] {chartInstr[0]};
            if (chartInstr.length > 1) {
                slaveInstr = new Instrument[chartInstr.length - 1];
                for (int i = 1; i < chartInstr.length; i++) {
                    slaveInstr[i - 1] = chartInstr[i];
                }
            } else {
                slaveInstr = masterInstr;
            }
        } else {
            // call from API
            masterInstr = slaveInstr = Instrument.values();
        }

        optInputParameterInfos[1].setDescription(new InstrumentListDescription(masterInstr[0], masterInstr));
        optInputParameterInfos[2].setDescription(new InstrumentListDescription(slaveInstr[0], slaveInstr));
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        if (startIndex < inputs[0].length && startIndex < inputs[1].length) {
            int maxLength = Math.max(inputs[0].length, inputs[1].length);
            int diffLength = Math.abs(inputs[0].length - inputs[1].length);

            double[][] inputs_ = new double[2][maxLength];
            double[][] outputs_ = new double[1][outputs[0].length - diffLength];

            System.arraycopy(inputs[0], 0, inputs_[0], maxLength - inputs[0].length, inputs[0].length);
            System.arraycopy(inputs[1], 0, inputs_[1], maxLength - inputs[1].length, inputs[1].length);

            correl.setInputParameter(0, inputs_[0]);
            correl.setInputParameter(1, inputs_[1]);
            correl.setOutputParameter(0, outputs_[0]);
            correl.calculate(startIndex + diffLength, endIndex);

            System.arraycopy(outputs_[0], 0, outputs[0], diffLength, outputs_[0].length);

            for (int i = 0; i < diffLength; i++) {
                outputs[0][i] = Double.NaN;
            }

        } else {
            // data for second instrument aren't ready yet
            for (int i = 0; i < outputs[0].length; i++) {
                outputs[0][i] = Double.NaN;
            }
        }

        return new IndicatorResult(startIndex, outputs[0].length);
    }

    @Override
    public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
    }

    @Override
    public InputParameterInfo getInputParameterInfo(int index) {
        if (index < inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

    @Override
    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    @Override
    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    @Override
    public void setInputParameter(int index, Object array) {
        inputs[index] = (double[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            correl.setOptInputParameter(0, (Integer) value);
            break;
        case 1:
        case 2:
            OptInputDescription descr = optInputParameterInfos[index].getDescription();
            Instrument[] values = ((InstrumentListDescription) descr).getValues();
            Instrument instr = null;
            if (value != null) {
                for (int i = 0; i < values.length; i++) {
                    if (value.equals(values[i])) {
                        instr = values[i];
                        break;
                    }
                }
            }
            if (instr == null) {
                // value not found
                instr = values[0];
            }
            inputParameterInfos[index - 1].setInstrument(instr);
            break;
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return correl.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
