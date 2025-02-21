/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.effect;

import java.util.Arrays;
import java.util.Objects;

import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.StatConstants;

public class Effect {
    private static final double DEFAULT_ADDITION = 0;
    private static final double DEFAULT_MULTIPLIER = 1;
    public static final int EFFECT_ADD = 0;
    public static final int EFFECT_MULTIPLY = 1;
    public static final double DEFAULT_SCALE_FACTOR = 1.0;
    public static final int DEFAULT_SCALE_STAT = StatConstants.STAT_NONE;
    private static final int ROUNDS_INFINITE = -1;
    public static final int MESSAGE_INITIAL = 0;
    public static final int MESSAGE_SUBSEQUENT = 1;
    public static final int MESSAGE_WEAR_OFF = 2;
    private static final int MAX_EFFECT_TYPES = 2;
    private static final int MAX_MESSAGES = 3;

    public static String getNullMessage() {
	return "";
    }

    // Fields
    private final String name;
    private final double[][] initialEffect;
    private final double[][] effect;
    private double effectScaleFactor;
    private int effectScaleStat;
    private final double[][] effectDecayRate;
    private int rounds;
    private final int initialRounds;
    private final String[] messages;

    // Constructors
    public Effect() {
	this.name = "Un-named";
	this.messages = new String[Effect.MAX_MESSAGES];
	this.effect = new double[Effect.MAX_EFFECT_TYPES][StatConstants.MAX_STATS];
	this.initialEffect = new double[Effect.MAX_EFFECT_TYPES][StatConstants.MAX_STATS];
	this.effectScaleFactor = Effect.DEFAULT_SCALE_FACTOR;
	this.effectScaleStat = Effect.DEFAULT_SCALE_STAT;
	this.effectDecayRate = new double[Effect.MAX_EFFECT_TYPES][StatConstants.MAX_STATS];
	int x, y;
	for (x = 0; x < Effect.MAX_EFFECT_TYPES; x++) {
	    for (y = 0; y < StatConstants.MAX_STATS; y++) {
		if (x == Effect.EFFECT_ADD) {
		    this.effect[x][y] = Effect.DEFAULT_ADDITION;
		    this.initialEffect[x][y] = Effect.DEFAULT_ADDITION;
		    this.effectDecayRate[x][y] = Effect.DEFAULT_ADDITION;
		} else if (x == Effect.EFFECT_MULTIPLY) {
		    this.effect[x][y] = Effect.DEFAULT_MULTIPLIER;
		    this.initialEffect[x][y] = Effect.DEFAULT_MULTIPLIER;
		    this.effectDecayRate[x][y] = Effect.DEFAULT_MULTIPLIER;
		} else {
		    this.effect[x][y] = 0;
		    this.initialEffect[x][y] = 0;
		    this.effectDecayRate[x][y] = 0;
		}
	    }
	}
	for (x = 0; x < Effect.MAX_MESSAGES; x++) {
	    this.messages[x] = "";
	}
	this.rounds = 0;
	this.initialRounds = 0;
    }

    public Effect(final String effectName, final int newRounds) {
	this.name = effectName;
	this.messages = new String[Effect.MAX_MESSAGES];
	this.effect = new double[Effect.MAX_EFFECT_TYPES][StatConstants.MAX_STATS];
	this.initialEffect = new double[Effect.MAX_EFFECT_TYPES][StatConstants.MAX_STATS];
	this.effectScaleFactor = Effect.DEFAULT_SCALE_FACTOR;
	this.effectScaleStat = Effect.DEFAULT_SCALE_STAT;
	this.effectDecayRate = new double[Effect.MAX_EFFECT_TYPES][StatConstants.MAX_STATS];
	int x, y;
	for (x = 0; x < Effect.MAX_EFFECT_TYPES; x++) {
	    for (y = 0; y < StatConstants.MAX_STATS; y++) {
		if (x == Effect.EFFECT_ADD) {
		    this.effect[x][y] = Effect.DEFAULT_ADDITION;
		    this.initialEffect[x][y] = Effect.DEFAULT_ADDITION;
		    this.effectDecayRate[x][y] = Effect.DEFAULT_ADDITION;
		} else if (x == Effect.EFFECT_MULTIPLY) {
		    this.effect[x][y] = Effect.DEFAULT_MULTIPLIER;
		    this.initialEffect[x][y] = Effect.DEFAULT_MULTIPLIER;
		    this.effectDecayRate[x][y] = Effect.DEFAULT_MULTIPLIER;
		} else {
		    this.effect[x][y] = 0;
		    this.initialEffect[x][y] = 0;
		    this.effectDecayRate[x][y] = 0;
		}
	    }
	}
	for (x = 0; x < Effect.MAX_MESSAGES; x++) {
	    this.messages[x] = "";
	}
	this.rounds = newRounds;
	this.initialRounds = newRounds;
    }

    private boolean areRoundsInfinite() {
	return this.rounds == Effect.ROUNDS_INFINITE;
    }

    private void decayEffect() {
	var currVal = 0.0;
	for (var stat = 0; stat < StatConstants.MAX_STATS; stat++) {
	    currVal = 0.0;
	    for (var type = 0; type < Effect.MAX_EFFECT_TYPES; type++) {
		currVal += this.getEffect(type, stat);
	    }
	    for (var type = 0; type < Effect.MAX_EFFECT_TYPES; type++) {
		var keepGoing = true;
		final var currDecay = this.getDecayRate(type, stat);
		if (currDecay == 0) {
		    keepGoing = false;
		}
		double modVal;
		if (type == Effect.EFFECT_ADD) {
		    modVal = currVal - currDecay;
		} else if (type == Effect.EFFECT_MULTIPLY) {
		    modVal = currVal / currDecay;
		    if (currDecay > 1 && modVal < 1) {
			this.setDecayRate(type, stat, 1);
			modVal = 1;
		    }
		    if (currDecay < 1 && modVal > 1) {
			this.setDecayRate(type, stat, 1);
			modVal = 1;
		    }
		} else {
		    modVal = currVal;
		}
		if (keepGoing) {
		    final var scst = this.effectScaleStat;
		    final var factor = this.effectScaleFactor;
		    this.modifyEffect(type, stat, modVal, factor, scst);
		}
	    }
	}
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null || !(obj instanceof final Effect other) || !Arrays.equals(this.effect, other.effect)
		|| !Arrays.equals(this.effectDecayRate, other.effectDecayRate)) {
	    return false;
	}
	if (Double.doubleToLongBits(this.effectScaleFactor) != Double.doubleToLongBits(other.effectScaleFactor)
		|| this.effectScaleStat != other.effectScaleStat
		|| !Arrays.equals(this.initialEffect, other.initialEffect)
		|| this.initialRounds != other.initialRounds) {
	    return false;
	}
	if (!Arrays.equals(this.messages, other.messages) || !Objects.equals(this.name, other.name)
		|| this.rounds != other.rounds) {
	    return false;
	}
	return true;
    }

    public void extendEffect(final int additionalRounds) {
	this.rounds += additionalRounds;
    }

    public String getCurrentMessage() {
	var msg = Effect.getNullMessage();
	if (this.rounds == this.initialRounds - 1
		&& !this.messages[Effect.MESSAGE_INITIAL].equals(Effect.getNullMessage())) {
	    msg += this.messages[Effect.MESSAGE_INITIAL] + "\n";
	}
	if (!this.messages[Effect.MESSAGE_SUBSEQUENT].equals(Effect.getNullMessage())) {
	    msg += this.messages[Effect.MESSAGE_SUBSEQUENT] + "\n";
	}
	if (this.rounds == 0 && !this.messages[Effect.MESSAGE_WEAR_OFF].equals(Effect.getNullMessage())) {
	    msg += this.messages[Effect.MESSAGE_WEAR_OFF] + "\n";
	}
	// Strip final newline character, if it exists
	if (!msg.equals(Effect.getNullMessage())) {
	    msg = msg.substring(0, msg.length() - 1);
	}
	return msg;
    }

    private double getDecayRate(final int type, final int stat) {
	return this.effectDecayRate[type][stat];
    }

    public double getEffect(final int type, final int stat) {
	return this.effect[type][stat];
    }

    public String getEffectString() {
	if (this.name.equals("")) {
	    return "";
	}
	if (this.areRoundsInfinite()) {
	    return this.name;
	}
	return this.name + " (" + this.rounds + " Rounds Left)";
    }

    public int getInitialRounds() {
	return this.initialRounds;
    }

    public String getName() {
	return this.name;
    }

    @Override
    public int hashCode() {
	final var prime = 31;
	var result = 1;
	result = prime * result + Arrays.hashCode(this.effect);
	long temp;
	result = prime * result + Arrays.hashCode(this.effectDecayRate);
	temp = Double.doubleToLongBits(this.effectScaleFactor);
	result = prime * result + (int) (temp ^ temp >>> 32);
	result = prime * result + this.effectScaleStat;
	result = prime * result + Arrays.hashCode(this.initialEffect);
	result = prime * result + this.initialRounds;
	result = prime * result + Arrays.hashCode(this.messages);
	result = prime * result + (this.name == null ? 0 : this.name.hashCode());
	return prime * result + this.rounds;
    }

    public boolean isActive() {
	if (this.areRoundsInfinite()) {
	    return true;
	}
	return this.rounds > 0;
    }

    private void modifyEffect(final int type, final int stat, final double value, final double factor,
	    final int scaleStat) {
	this.effect[type][stat] = value;
	this.effectScaleFactor = factor;
	this.effectScaleStat = scaleStat;
    }

    public void resetEffect() {
	int x, y;
	for (x = 0; x < Effect.MAX_EFFECT_TYPES; x++) {
	    for (y = 0; y < StatConstants.MAX_STATS; y++) {
		this.effect[x][y] = this.initialEffect[x][y];
	    }
	}
    }

    public void restoreEffect() {
	if (!this.areRoundsInfinite()) {
	    this.rounds = this.initialRounds;
	}
    }

    public void scaleEffect(final int type, final Creature scaleTo) {
	for (var stat = 0; stat < StatConstants.MAX_STATS; stat++) {
	    final var base = this.effect[type][stat];
	    final var scst = this.effectScaleStat;
	    if (scst != StatConstants.STAT_NONE) {
		final var factor = this.effectScaleFactor;
		final var scstVal = scaleTo.getStat(scst);
		this.effect[type][stat] = scstVal * base * factor;
	    }
	}
    }

    public void setDecayRate(final int type, final int stat, final double value) {
	this.effectDecayRate[type][stat] = value;
    }

    public void setEffect(final int type, final int stat, final double value) {
	this.effect[type][stat] = value;
	this.initialEffect[type][stat] = value;
    }

    public void setEffect(final int type, final int stat, final double value, final double factor,
	    final int scaleStat) {
	this.effect[type][stat] = value;
	this.initialEffect[type][stat] = value;
	this.effectScaleFactor = factor;
	this.effectScaleStat = scaleStat;
    }

    public void setMessage(final int which, final String newMessage) {
	this.messages[which] = newMessage;
    }

    public void setScaleFactor(final double factor) {
	this.effectScaleFactor = factor;
    }

    public void setScaleStat(final int scaleStat) {
	this.effectScaleStat = scaleStat;
    }

    public void useEffect(final Creature target) {
	final var hpAddEffect = this.getEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP);
	final var mpAddEffect = this.getEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_MP);
	final var hpMultEffect = this.getEffect(Effect.EFFECT_MULTIPLY, StatConstants.STAT_CURRENT_HP);
	final var mpMultEffect = this.getEffect(Effect.EFFECT_MULTIPLY, StatConstants.STAT_CURRENT_MP);
	if (hpAddEffect < 0) {
	    if (target.isAlive()) {
		target.doDamage((int) -hpAddEffect);
	    }
	} else if (hpAddEffect > 0) {
	    target.heal((int) hpAddEffect);
	}
	if (mpAddEffect < 0) {
	    if (target.isAlive()) {
		target.drain((int) -mpAddEffect);
	    }
	} else if (mpAddEffect > 0) {
	    target.regenerate((int) mpAddEffect);
	}
	if (hpMultEffect < 1) {
	    if (target.isAlive()) {
		final var damage = hpMultEffect;
		boolean max;
		if (this.effectScaleStat == StatConstants.STAT_MAXIMUM_HP) {
		    max = true;
		} else {
		    max = false;
		}
		target.doDamageMultiply(damage, max);
	    }
	} else if (hpMultEffect > 1) {
	    boolean max;
	    if (this.effectScaleStat == StatConstants.STAT_MAXIMUM_HP) {
		max = true;
	    } else {
		max = false;
	    }
	    target.healMultiply(hpMultEffect, max);
	}
	if (mpMultEffect < 1) {
	    if (target.isAlive()) {
		boolean max;
		if (this.effectScaleStat == StatConstants.STAT_MAXIMUM_MP) {
		    max = true;
		} else {
		    max = false;
		}
		target.drainMultiply(mpMultEffect, max);
	    }
	} else if (mpMultEffect > 1) {
	    boolean max;
	    if (this.effectScaleStat == StatConstants.STAT_MAXIMUM_MP) {
		max = true;
	    } else {
		max = false;
	    }
	    target.regenerateMultiply(mpMultEffect, max);
	}
	if (!this.areRoundsInfinite()) {
	    this.rounds--;
	    if (this.rounds < 0) {
		this.rounds = 0;
	    }
	}
	this.decayEffect();
    }
}