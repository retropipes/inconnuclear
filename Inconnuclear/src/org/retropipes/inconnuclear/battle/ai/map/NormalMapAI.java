/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.ai.map;

import org.retropipes.diane.random.RandomRange;
import org.retropipes.inconnuclear.battle.BattleAction;
import org.retropipes.inconnuclear.battle.ai.AIContext;

class NormalMapAI extends MapAI {
    private static final int CAST_SPELL_CHANCE = 20;
    private static final int STEAL_CHANCE = 4;
    private static final int DRAIN_CHANCE = 20;
    private static final int HEAL_THRESHOLD = 20;
    private static final int MAX_VISION = 5;
    private static final int FLEE_CHANCE = 10;
    // Fields
    private final RandomRange randMove;
    private int failedMoveAttempts;
    private int[] roundsRemaining;

    // Constructor
    public NormalMapAI() {
	this.randMove = new RandomRange(-1, 1);
	this.failedMoveAttempts = 0;
    }

    @Override
    public BattleAction getNextAction(final AIContext ac) {
	if (this.roundsRemaining == null) {
	    this.roundsRemaining = new int[ac.getCharacter().getCreature().getSpellBook().getSpellCount()];
	}
	if (this.spellCheck(ac)) {
	    // Cast a spell
	    return BattleAction.CAST_SPELL;
	}
	var there = ac.isEnemyNearby();
	if (there != null) {
	    if (CommonMapAIParts.check(ac, NormalMapAI.STEAL_CHANCE)) {
		// Steal
		return BattleAction.STEAL;
	    }
	    if (CommonMapAIParts.check(ac, NormalMapAI.DRAIN_CHANCE)) {
		// Drain MP
		return BattleAction.DRAIN;
	    }
	    if (ac.getCharacter().getAttacksLeft() > 0) {
		this.moveX = there.x;
		this.moveY = there.y;
		return BattleAction.MOVE;
	    }
	    this.failedMoveAttempts = 0;
	    return BattleAction.END_TURN;
	}
	if (CommonMapAIParts.check(ac, NormalMapAI.FLEE_CHANCE)) {
	    // Flee
	    final var awayDir = ac.runAway();
	    if (awayDir == null) {
		// Wander randomly
		this.moveX = this.randMove.generate();
		this.moveY = this.randMove.generate();
		// Don't attack self
		while (this.moveX == 0 && this.moveY == 0) {
		    this.moveX = this.randMove.generate();
		    this.moveY = this.randMove.generate();
		}
	    } else {
		this.moveX = awayDir.x;
		this.moveY = awayDir.y;
	    }
	    return BattleAction.MOVE;
	}
	// Look further
	for (var x = CommonMapAIParts.MIN_VISION + 1; x <= NormalMapAI.MAX_VISION; x++) {
	    there = ac.isEnemyNearby(x, x);
	    if (there != null) {
		// Found something hostile, move towards it
		if (!this.lastResult) {
		    this.failedMoveAttempts++;
		    if (this.failedMoveAttempts >= CommonMapAIParts.STUCK_THRESHOLD) {
			// We're stuck!
			this.failedMoveAttempts = 0;
			return BattleAction.END_TURN;
		    }
		    // Last move failed, try to move around object
		    final var randTurn = new RandomRange(0, 1);
		    final var rt = randTurn.generate();
		    if (rt == 0) {
			there = CommonMapAIParts.turnRight45(this.moveX, this.moveY);
		    } else {
			there = CommonMapAIParts.turnLeft45(this.moveX, this.moveY);
		    }
		    this.moveX = there.x;
		    this.moveY = there.y;
		} else {
		    this.moveX = (int) Math.signum(there.x);
		    this.moveY = (int) Math.signum(there.y);
		}
		break;
	    }
	}
	if (ac.getCharacter().getActionsLeft() <= 0) {
	    this.failedMoveAttempts = 0;
	    return BattleAction.END_TURN;
	}
	if (there == null) {
	    // Wander randomly
	    this.moveX = this.randMove.generate();
	    this.moveY = this.randMove.generate();
	    // Don't attack self
	    while (this.moveX == 0 && this.moveY == 0) {
		this.moveX = this.randMove.generate();
		this.moveY = this.randMove.generate();
	    }
	}
	return BattleAction.MOVE;
    }

    @Override
    public void newRoundHook() {
	// Decrement effect counters
	if (this.roundsRemaining != null) {
	    for (var z = 0; z < this.roundsRemaining.length; z++) {
		if (this.roundsRemaining[z] > 0) {
		    this.roundsRemaining[z]--;
		}
	    }
	}
    }

    private boolean spellCheck(final AIContext ac) {
	final var random = new RandomRange(1, 100);
	final var chance = random.generate();
	if (chance > NormalMapAI.CAST_SPELL_CHANCE) {
	    // Not casting a spell
	    return false;
	}
	final var maxIndex = CommonMapAIParts.getMaxCastIndex(ac);
	if (maxIndex <= -1 || ac.getCharacter().getSpellsLeft() <= 0) {
	    // Can't cast any more spells
	    return false;
	}
	// Select a random spell to cast
	final var randomSpell = new RandomRange(0, maxIndex);
	final var randomSpellID = randomSpell.generate();
	// Healing spell was selected - is healing needed?
	if (randomSpellID == CommonMapAIParts.SPELL_INDEX_HEAL && ac.getCharacter().getCreature()
		.getCurrentHP() > ac.getCharacter().getCreature().getMaximumHP() * NormalMapAI.HEAL_THRESHOLD / 100) {
	    // Do not need healing
	    return false;
	}
	if (this.roundsRemaining[randomSpellID] == 0) {
	    this.spell = ac.getCharacter().getCreature().getSpellBook().getSpellByID(randomSpellID);
	    this.roundsRemaining[randomSpellID] = this.spell.getEffect().getInitialRounds();
	    return true;
	}
	// Spell selected already active
	return false;
    }
}
