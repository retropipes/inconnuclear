/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.ai;

import java.util.Objects;

import org.retropipes.inconnuclear.battle.BattleAction;
import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.spell.Spell;

public abstract class CreatureAI {
    // Fields
    protected Spell spell;
    protected int moveX;
    protected int moveY;
    protected boolean lastResult;

    // Constructor
    protected CreatureAI() {
	this.spell = null;
	this.moveX = 0;
	this.moveY = 0;
	this.lastResult = true;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null || !(obj instanceof final CreatureAI other) || this.lastResult != other.lastResult
		|| this.moveX != other.moveX) {
	    return false;
	}
	if (this.moveY != other.moveY || !Objects.equals(this.spell, other.spell)) {
	    return false;
	}
	return true;
    }

    public final int getMoveX() {
	return this.moveX;
    }

    public final int getMoveY() {
	return this.moveY;
    }

    public abstract BattleAction getNextAction(AIContext ac);

    public abstract BattleAction getNextAction(Creature c);

    public final Spell getSpellToCast() {
	return this.spell;
    }

    @Override
    public int hashCode() {
	return Objects.hash(this.lastResult, this.moveX, this.moveY, this.spell);
    }

    public void newRoundHook() {
	// Do nothing
    }

    public final void setLastResult(final boolean res) {
	this.lastResult = res;
    }
}
