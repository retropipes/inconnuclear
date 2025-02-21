/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.monster;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.diane.random.RandomLongRange;
import org.retropipes.diane.random.RandomRange;
import org.retropipes.inconnuclear.creature.GameDifficulty;
import org.retropipes.inconnuclear.creature.item.ItemPrices;
import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.loader.image.monster.MonsterImageLoader;

class NormalMonster extends Monster {
    // Constants
    private static final int STAT_MULT_VERY_EASY = 3;
    private static final int STAT_MULT_EASY = 4;
    private static final int STAT_MULT_NORMAL = 5;
    private static final int STAT_MULT_HARD = 6;
    private static final int STAT_MULT_VERY_HARD = 7;
    private static final double GOLD_MULT_VERY_EASY = 2.0;
    private static final double GOLD_MULT_EASY = 1.5;
    private static final double GOLD_MULT_NORMAL = 1.0;
    private static final double GOLD_MULT_HARD = 0.75;
    private static final double GOLD_MULT_VERY_HARD = 0.5;
    private static final double EXP_MULT_VERY_EASY = 1.2;
    private static final double EXP_MULT_EASY = 1.1;
    private static final double EXP_MULT_NORMAL = 1.0;
    private static final double EXP_MULT_HARD = 0.9;
    private static final double EXP_MULT_VERY_HARD = 0.8;

    private double getExpMultiplierForDifficulty() {
	return switch (this.difficulty) {
	case GameDifficulty.VERY_EASY -> NormalMonster.EXP_MULT_VERY_EASY;
	case GameDifficulty.EASY -> NormalMonster.EXP_MULT_EASY;
	case GameDifficulty.NORMAL -> NormalMonster.EXP_MULT_NORMAL;
	case GameDifficulty.HARD -> NormalMonster.EXP_MULT_HARD;
	case GameDifficulty.VERY_HARD -> NormalMonster.EXP_MULT_VERY_HARD;
	default -> NormalMonster.EXP_MULT_NORMAL;
	};
    }

    private double getGoldMultiplierForDifficulty() {
	return switch (this.difficulty) {
	case GameDifficulty.VERY_EASY -> NormalMonster.GOLD_MULT_VERY_EASY;
	case GameDifficulty.EASY -> NormalMonster.GOLD_MULT_EASY;
	case GameDifficulty.NORMAL -> NormalMonster.GOLD_MULT_NORMAL;
	case GameDifficulty.HARD -> NormalMonster.GOLD_MULT_HARD;
	case GameDifficulty.VERY_HARD -> NormalMonster.GOLD_MULT_VERY_HARD;
	default -> NormalMonster.GOLD_MULT_NORMAL;
	};
    }

    private int getStatMultiplierForDifficulty() {
	return switch (this.difficulty) {
	case GameDifficulty.VERY_EASY -> NormalMonster.STAT_MULT_VERY_EASY;
	case GameDifficulty.EASY -> NormalMonster.STAT_MULT_EASY;
	case GameDifficulty.NORMAL -> NormalMonster.STAT_MULT_NORMAL;
	case GameDifficulty.HARD -> NormalMonster.STAT_MULT_HARD;
	case GameDifficulty.VERY_HARD -> NormalMonster.STAT_MULT_VERY_HARD;
	default -> NormalMonster.STAT_MULT_NORMAL;
	};
    }

    // Constructors
    NormalMonster(final GameDifficulty diff) {
	super(diff);
	this.image = this.getInitialImage();
    }

    private int getInitialAgility() {
	final var r = new RandomRange(1, Math.max(this.getLevel() * this.getStatMultiplierForDifficulty(), 1));
	return r.generate();
    }

    private int getInitialBlock() {
	final var r = new RandomRange(0, this.getLevel() * this.getStatMultiplierForDifficulty());
	return r.generate();
    }

    private long getInitialExperience() {
	int minvar, maxvar;
	minvar = (int) (this.getLevel() * Monster.MINIMUM_EXPERIENCE_RANDOM_VARIANCE);
	maxvar = (int) (this.getLevel() * Monster.MAXIMUM_EXPERIENCE_RANDOM_VARIANCE);
	final var r = new RandomLongRange(minvar, maxvar);
	final var expbase = PartyManager.getParty().getPartyMaxToNextLevel();
	final long factor = this.getBattlesToNextLevel();
	return (int) (expbase / factor
		+ r.generate() * this.adjustForLevelDifference() * this.getExpMultiplierForDifficulty());
    }

    private int getInitialGold() {
	final var playerCharacter = PartyManager.getParty().getLeader();
	final var needed = ItemPrices.getEquipmentCost(playerCharacter.getLevel() + 1) * 4;
	final var factor = this.getBattlesToNextLevel();
	final var min = 0;
	final var max = needed / factor * 2;
	final var r = new RandomRange(min, max);
	return (int) (r.generate() * this.adjustForLevelDifference() * this.getGoldMultiplierForDifficulty());
    }

    @Override
    protected BufferedImageIcon getInitialImage() {
	final var monID = this.getMonsterID();
	return MonsterImageLoader.load(monID);
    }

    private int getInitialIntelligence() {
	final var r = new RandomRange(0, this.getLevel() * this.getStatMultiplierForDifficulty());
	return r.generate();
    }

    private int getInitialLuck() {
	final var r = new RandomRange(0, this.getLevel() * this.getStatMultiplierForDifficulty());
	return r.generate();
    }

    private int getInitialStrength() {
	final var r = new RandomRange(1, Math.max(this.getLevel() * this.getStatMultiplierForDifficulty(), 1));
	return r.generate();
    }

    private int getInitialVitality() {
	final var r = new RandomRange(1, Math.max(this.getLevel() * this.getStatMultiplierForDifficulty(), 1));
	return r.generate();
    }

    @Override
    public void loadCreature() {
	this.configureDefaults();
	final var newLevel = PartyManager.getParty().getZone() + 1;
	this.setLevel(newLevel);
	this.setVitality(this.getInitialVitality());
	this.setCurrentHP(this.getMaximumHP());
	this.setIntelligence(this.getInitialIntelligence());
	this.setCurrentMP(this.getMaximumMP());
	this.setStrength(this.getInitialStrength());
	this.setBlock(this.getInitialBlock());
	this.setAgility(this.getInitialAgility());
	this.setLuck(this.getInitialLuck());
	this.setGold(this.getInitialGold());
	this.setExperience((long) (this.getInitialExperience() * this.adjustForLevelDifference()));
	this.setAttacksPerRound(1);
	this.setSpellsPerRound(1);
	this.image = this.getInitialImage();
    }
}
