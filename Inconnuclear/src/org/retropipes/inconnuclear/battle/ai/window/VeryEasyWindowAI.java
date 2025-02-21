/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.ai.window;

import org.retropipes.diane.random.RandomRange;
import org.retropipes.inconnuclear.battle.BattleAction;
import org.retropipes.inconnuclear.creature.Creature;

public class VeryEasyWindowAI extends WindowAI {
    private static final int CAST_SPELL_CHANCE = 5;
    private static final int STEAL_CHANCE = 1;
    private static final int DRAIN_CHANCE = 5;
    private static final int HEAL_THRESHOLD = 5;
    private static final int FLEE_CHANCE = 40;
    // Fields
    private int[] roundsRemaining;

    // Constructors
    public VeryEasyWindowAI() {
	// Do nothing
    }

    @Override
    public BattleAction getNextAction(final Creature c) {
	if (this.roundsRemaining == null) {
	    this.roundsRemaining = new int[c.getSpellBook().getSpellCount()];
	}
	if (this.spellCheck(c)) {
	    // Cast a spell
	    return BattleAction.CAST_SPELL;
	} else if (CommonWindowAIParts.check(VeryEasyWindowAI.STEAL_CHANCE)) {
	    // Steal
	    return BattleAction.STEAL;
	} else if (CommonWindowAIParts.check(VeryEasyWindowAI.DRAIN_CHANCE)) {
	    // Drain MP
	    return BattleAction.DRAIN;
	} else if (CommonWindowAIParts.check(VeryEasyWindowAI.FLEE_CHANCE)) {
	    // Flee
	    return BattleAction.FLEE;
	} else {
	    // Something hostile is nearby, so attack it
	    return BattleAction.ATTACK;
	}
    }

    @Override
    public void newRoundHook() {
	// Decrement effect counters
	for (var z = 0; z < this.roundsRemaining.length; z++) {
	    if (this.roundsRemaining[z] > 0) {
		this.roundsRemaining[z]--;
	    }
	}
    }

    private boolean spellCheck(final Creature c) {
	final var random = new RandomRange(1, 100);
	final var chance = random.generate();
	if (chance <= VeryEasyWindowAI.CAST_SPELL_CHANCE) {
	    final var maxIndex = CommonWindowAIParts.getMaxCastIndex(c);
	    if (maxIndex > -1) {
		// Select a random spell to cast
		final var randomSpell = new RandomRange(0, maxIndex);
		final var randomSpellID = randomSpell.generate();
		// Healing spell was selected - is healing needed?
		if ((randomSpellID == CommonWindowAIParts.SPELL_INDEX_HEAL)
			&& (c.getCurrentHP() > c.getMaximumHP() * VeryEasyWindowAI.HEAL_THRESHOLD / 100)) {
		    // Do not need healing
		    return false;
		}
		if (this.roundsRemaining[randomSpellID] == 0) {
		    this.spell = c.getSpellBook().getSpellByID(randomSpellID);
		    this.roundsRemaining[randomSpellID] = this.spell.getEffect().getInitialRounds();
		    return true;
		}
		// Spell selected already active
		return false;
	    }
	    // Not enough MP to cast anything
	    return false;
	}
	// Not casting a spell
	return false;
    }
}
