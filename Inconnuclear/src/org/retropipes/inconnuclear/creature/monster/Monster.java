/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.monster;

import java.util.Objects;

import org.retropipes.diane.random.RandomRange;
import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.GameDifficulty;
import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.creature.spell.SpellBook;

public abstract class Monster extends Creature {
    protected static final double MINIMUM_EXPERIENCE_RANDOM_VARIANCE = -5.0 / 2.0;
    protected static final double MAXIMUM_EXPERIENCE_RANDOM_VARIANCE = 5.0 / 2.0;
    protected static final int PERFECT_GOLD_MIN = 1;
    protected static final int PERFECT_GOLD_MAX = 3;
    private static final int BATTLES_SCALE_FACTOR = 2;
    private static final int BATTLES_START = 2;
    // Fields
    private String type;
    private int monID;

    // Constructors
    Monster(final GameDifficulty diff) {
	super(1, diff);
	final SpellBook spells = new MonsterSpellBook();
	spells.learnAllSpells();
	this.setSpellBook(spells);
    }

    protected double adjustForLevelDifference() {
	return Math.max(0.0, this.getLevelDifference() / 4.0 + 1.0);
    }

    @Override
    public boolean checkLevelUp() {
	return false;
    }

    protected void configureDefaults() {
	this.monID = RandomRange.generate(0, 99);
	final var zoneID = PartyManager.getParty().getZone();
	this.type = Monsters.getType(zoneID, this.monID);
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!super.equals(obj) || !(obj instanceof final Monster other) || !Objects.equals(this.type, other.type)) {
	    return false;
	}
	return true;
    }

    protected final int getBattlesToNextLevel() {
	return Monster.BATTLES_START + (this.getLevel() + 1) * Monster.BATTLES_SCALE_FACTOR;
    }

    @Override
    protected final int getInitialPerfectBonusGold() {
	final var tough = this.getToughness();
	final var min = tough * Monster.PERFECT_GOLD_MIN;
	final var max = tough * Monster.PERFECT_GOLD_MAX;
	final var r = new RandomRange(min, max);
	return (int) (r.generate() * this.adjustForLevelDifference());
    }

    @Override
    public int getLevelDifference() {
	return this.getLevel() - PartyManager.getParty().getLeader().getLevel();
    }

    public final int getMonsterID() {
	return this.monID;
    }

    @Override
    public String getName() {
	return this.type;
    }

    @Override
    public int getSpeed(GameDifficulty diff) {
	final var base = this.getBaseSpeed();
	return switch (diff) {
	case GameDifficulty.VERY_EASY -> (int) (base * Creature.SPEED_ADJUST_SLOWEST);
	case GameDifficulty.EASY -> (int) (base * Creature.SPEED_ADJUST_SLOW);
	case GameDifficulty.NORMAL -> (int) (base * Creature.SPEED_ADJUST_NORMAL);
	case GameDifficulty.HARD -> (int) (base * Creature.SPEED_ADJUST_FAST);
	case GameDifficulty.VERY_HARD -> (int) (base * Creature.SPEED_ADJUST_FASTEST);
	default -> (int) (base * Creature.SPEED_ADJUST_NORMAL);
	};
    }

    private int getToughness() {
	return this.getStrength() + this.getBlock() + this.getAgility() + this.getVitality() + this.getIntelligence()
		+ this.getLuck();
    }

    final String getType() {
	return this.type;
    }

    @Override
    public int hashCode() {
	final var prime = 31;
	final var result = super.hashCode();
	return prime * result + (this.type == null ? 0 : this.type.hashCode());
    }

    @Override
    protected void levelUpHook() {
	// Do nothing
    }

    protected void overrideDefaults(final int oID, final String oType) {
	this.monID = oID;
	this.type = oType;
    }
}
