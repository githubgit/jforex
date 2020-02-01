/*
 * Copyright 1998-2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Class allows detailed configuration of commissions for historical testing.
 * Default values of this class differs from values used in real (DEMO/LIVE environments) orders processing.
 */
public class Commissions implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected SortedMap<Double, Double> depositLimits = new TreeMap<>();
    protected SortedMap<Double, Double> equityLimits = new TreeMap<>();
    protected SortedMap<Double, Double> turnoverLimits = new TreeMap<>();

    protected double maxCommission = 35d;

    protected double[] last30DaysTurnoverAtStart = new double[30];

    protected boolean custodianBankOrGuarantee;

    /**
     * Creates Commissions object with default values
     *
     * @param custodianBankOrGuarantee if true then deposit with custodian bank or bank guarantee is assumed
     */
    public Commissions(boolean custodianBankOrGuarantee) {
        this.custodianBankOrGuarantee = custodianBankOrGuarantee;

        depositLimits.put(5000d, 33d);
        depositLimits.put(10000d, 30d);
        depositLimits.put(25000d, 25d);
        depositLimits.put(50000d, 18d);
        if (custodianBankOrGuarantee) {
            depositLimits.put(250000d, 18d);
            depositLimits.put(500000d, 18d);
            depositLimits.put(1000000d, 18d);
            depositLimits.put(5000000d, 18d);
            depositLimits.put(10000000d, 18d);
        } else {
            depositLimits.put(250000d, 16d);
            depositLimits.put(500000d, 15d);
            depositLimits.put(1000000d, 14d);
            depositLimits.put(5000000d, 12d);
            depositLimits.put(10000000d, 10d);
        }

        equityLimits.put(5000d, 33d);
        equityLimits.put(10000d, 30d);
        equityLimits.put(25000d, 25d);
        equityLimits.put(50000d, 18d);
        if (custodianBankOrGuarantee) {
            equityLimits.put(250000d, 18d);
            equityLimits.put(500000d, 18d);
            equityLimits.put(1000000d, 18d);
            equityLimits.put(5000000d, 18d);
            equityLimits.put(10000000d, 18d);
        } else {
            equityLimits.put(250000d, 16d);
            equityLimits.put(500000d, 15d);
            equityLimits.put(1000000d, 14d);
            equityLimits.put(5000000d, 12d);
            equityLimits.put(10000000d, 10d);
        }

        turnoverLimits.put(5000000d, 33d);
        turnoverLimits.put(10000000d, 30d);
        turnoverLimits.put(25000000d, 25d);
        turnoverLimits.put(50000000d, 18d);
        turnoverLimits.put(250000000d, 16d);
        turnoverLimits.put(500000000d, 15d);
        turnoverLimits.put(1000000000d, 14d);
        turnoverLimits.put(2000000000d, 12d);
        turnoverLimits.put(4000000000d, 10d);
    }

    /**
     * Sets commission for situations when deposit, equity and turnover is less than minimum limit
     *
     * @param maxCommission commission for minimum deposit, equity and turnover
     */
    public void setMaxCommission(double maxCommission) {
        this.maxCommission = maxCommission;
    }

    /**
     * Returns commission for situations when deposit, equity and turnover is less than minimum limit
     *
     * @return commission for minimum deposit, equity and turnover
     */
    public double getMaxCommission() {
        return maxCommission;
    }

    /**
     * Sets turnover values for the last 30 days that will be used at tester start time. Size of the array must be 30 elements
     *
     * @param last30DaysTurnoverAtStart turnover values for the last 30 days
     * @throws ArrayIndexOutOfBoundsException when array length is not 30
     */
    public void setLast30DaysTurnoverAtStart(double[] last30DaysTurnoverAtStart) throws ArrayIndexOutOfBoundsException {
        if (last30DaysTurnoverAtStart.length != 30) {
            throw new ArrayIndexOutOfBoundsException("Array length must be 30");
        }
        this.last30DaysTurnoverAtStart = last30DaysTurnoverAtStart;
    }

    /**
     * Returns turnover values for the last 30 days that will be used at tester start time. By default all values are
     * zeros. Changing values in array will change values in Commissions class
     *
     * @return turnover values for the last 30 days
     */
    public double[] getLast30DaysTurnoverAtStart() {
        return last30DaysTurnoverAtStart;
    }

    /**
     * Sets limits for deposit amounts. If deposit amount at the commission calculation time is &ge; limit then it's value
     * will be used. The biggest limit that complies the condition will be used to get the commission
     *
     * @param limit deposit amount limit
     * @param commission commission to use when deposit amount is over the limit
     */
    public void setDepositLimit(double limit, double commission) {
        depositLimits.put(limit, commission);
    }

    /**
     * Returns all deposit limits. Changes in map will result in changes of the actual map of this class
     *
     * @return deposit limits
     */
    public SortedMap<Double, Double> getDepositLimits() {
        return depositLimits;
    }

    /**
     * Sets limits for equity amounts. If equity amount at the commission calculation time is &ge; limit then it's value
     * will be used. The biggest limit that complies the condition will be used to get the commission
     *
     * @param limit equity amount limit
     * @param commission commission to use when equity amount is over the limit
     */
    public void setEquityLimit(double limit, double commission) {
        equityLimits.put(limit, commission);
    }

    /**
     * Returns all equity limits. Changes in map will result in changes of the actual map of this class
     *
     * @return equity limits
     */
    public SortedMap<Double, Double> getEquityLimits() {
        return equityLimits;
    }

    /**
     * Sets limits for turnover amounts. If turnover amount at the commission calculation time is &ge; limit then it's value
     * will be used. The biggest limit that complies the condition will be used to get the commission
     *
     * @param limit turnover amount limit
     * @param commission commission to use when turnover amount is over the limit
     */
    public void setTurnoverLimit(double limit, double commission) {
        turnoverLimits.put(limit, commission);
    }

    /**
     * Returns all turnover limits. Changes in map will result in changes of the actual map of this class
     *
     * @return turnover limits
     */
    public SortedMap<Double, Double> getTurnoverLimits() {
        return turnoverLimits;
    }

    /**
     * Returns true if deposit is with custodian bank or there is a bank guarantee. Once used in constructor to fill in
     * correct levels, this field becomes only informational
     *
     * @return true if deposit is with custodian bank or there is a bank guarantee
     */
    public boolean isCustodianBankOrGuarantee() {
        return custodianBankOrGuarantee;
    }
}
