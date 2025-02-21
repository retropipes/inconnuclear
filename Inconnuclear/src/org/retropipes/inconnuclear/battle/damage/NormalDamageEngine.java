/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.damage;

import org.retropipes.diane.random.RandomRange;
import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.StatConstants;

class NormalDamageEngine extends DamageEngine {
    private static final int MULTIPLIER_MIN = 7500;
    private static final int MULTIPLIER_MAX = 15000;
    private static final int MULTIPLIER_MIN_CRIT = 20000;
    private static final int MULTIPLIER_MAX_CRIT = 30000;
    private static final int FUMBLE_CHANCE = 500;
    private static final int PIERCE_CHANCE = 1000;
    private static final int CRIT_CHANCE = 1000;
    private boolean dodged = false;
    private boolean missed = false;
    private boolean crit = false;
    private boolean pierce = false;
    private boolean fumble = false;

    @Override
    public int computeDamage(final Creature enemy, final Creature acting) {
	// Compute Damage
	final var attack = acting.getEffectedAttack();
	final var defense = enemy.getEffectedStat(StatConstants.STAT_DEFENSE);
	final var power = acting.getItems().getTotalPower();
	this.didFumble();
	if (this.fumble) {
	    // Fumble!
	    return CommonDamageEngineParts.fumbleDamage(power);
	}
	this.didPierce();
	this.didCrit();
	double rawDamage;
	if (this.pierce) {
	    rawDamage = attack;
	} else {
	    rawDamage = attack - defense;
	}
	final var rHit = CommonDamageEngineParts.chance();
	var aHit = acting.getHit();
	if (this.crit || this.pierce) {
	    // Critical hits and piercing hits
	    // always connect
	    aHit = CommonDamageEngineParts.ALWAYS;
	}
	if (rHit > aHit) {
	    // Weapon missed
	    this.missed = true;
	    this.dodged = false;
	    this.crit = false;
	    return 0;
	}
	final var rEvade = CommonDamageEngineParts.chance();
	final var aEvade = enemy.getEvade();
	if (rEvade < aEvade) {
	    // Enemy dodged
	    this.missed = false;
	    this.dodged = true;
	    this.crit = false;
	    return 0;
	}
	// Hit
	this.missed = false;
	this.dodged = false;
	RandomRange rDamage;
	if (this.crit) {
	    rDamage = new RandomRange(NormalDamageEngine.MULTIPLIER_MIN_CRIT, NormalDamageEngine.MULTIPLIER_MAX_CRIT);
	} else {
	    rDamage = new RandomRange(NormalDamageEngine.MULTIPLIER_MIN, NormalDamageEngine.MULTIPLIER_MAX);
	}
	final var multiplier = rDamage.generate();
	return (int) (rawDamage * multiplier / CommonDamageEngineParts.MULTIPLIER_DIVIDE);
    }

    private void didCrit() {
	this.crit = CommonDamageEngineParts.didSpecial(NormalDamageEngine.CRIT_CHANCE);
    }

    private void didFumble() {
	this.fumble = CommonDamageEngineParts.didSpecial(NormalDamageEngine.FUMBLE_CHANCE);
    }

    private void didPierce() {
	this.pierce = CommonDamageEngineParts.didSpecial(NormalDamageEngine.PIERCE_CHANCE);
    }

    @Override
    public boolean enemyDodged() {
	return this.dodged;
    }

    @Override
    public boolean weaponCrit() {
	return this.crit;
    }

    @Override
    public boolean weaponFumble() {
	return this.fumble;
    }

    @Override
    public boolean weaponMissed() {
	return this.missed;
    }

    @Override
    public boolean weaponPierce() {
	return this.pierce;
    }
}