/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature;

import java.util.Objects;

class Statistic {
    // Fields
    private int value;
    private final int dynamism;
    private boolean hasMax;
    private int maxID;
    private final boolean hasMin;
    private int minVal;

    // Constructor
    Statistic() {
	this.value = 0;
	this.dynamism = 0;
	this.hasMax = false;
	this.maxID = StatConstants.STAT_NONE;
	this.hasMin = true;
	this.minVal = 0;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null || !(obj instanceof final Statistic other) || this.dynamism != other.dynamism
		|| this.hasMax != other.hasMax) {
	    return false;
	}
	if (this.hasMin != other.hasMin || this.maxID != other.maxID || this.minVal != other.minVal
		|| this.value != other.value) {
	    return false;
	}
	return true;
    }

    int getDynamism() {
	return this.dynamism;
    }

    int getMaxID() {
	return this.maxID;
    }

    int getMinVal() {
	return this.minVal;
    }

    int getValue() {
	return this.value;
    }

    @Override
    public int hashCode() {
	return Objects.hash(this.dynamism, this.hasMax, this.hasMin, this.maxID, this.minVal, this.value);
    }

    boolean hasMax() {
	return this.hasMax;
    }

    boolean hasMin() {
	return this.hasMin;
    }

    void offsetValue(final int newValue) {
	this.value += newValue;
    }

    void offsetValueMultiply(final double newValue, final boolean max, final int maxValue) {
	if (max) {
	    this.value -= (int) (maxValue - maxValue * newValue);
	} else {
	    this.value *= newValue;
	}
    }

    void setHasMax(final boolean newHasMax) {
	this.hasMax = newHasMax;
    }

    void setMaxID(final int newMaxID) {
	this.maxID = newMaxID;
    }

    void setMinVal(final int newMinVal) {
	this.minVal = newMinVal;
    }

    void setValue(final int newValue) {
	this.value = newValue;
    }
}
