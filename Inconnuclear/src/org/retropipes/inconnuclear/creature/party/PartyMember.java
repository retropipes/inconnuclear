/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.party;

import java.io.IOException;

import org.retropipes.diane.ack.AvatarConstructionKit;
import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.diane.polytable.PolyTable;
import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.GameDifficulty;
import org.retropipes.inconnuclear.creature.StatConstants;
import org.retropipes.inconnuclear.creature.characterfile.CharacterFileFormats;
import org.retropipes.inconnuclear.creature.characterfile.CharacterVersionException;
import org.retropipes.inconnuclear.creature.gender.Gender;
import org.retropipes.inconnuclear.creature.item.ItemInventory;
import org.retropipes.inconnuclear.creature.job.Job;
import org.retropipes.inconnuclear.creature.job.JobManager;

public class PartyMember extends Creature {
    private static final int START_GOLD = 0;
    private static final double BASE_COEFF = 10.0;

    public static PartyMember read(final DataIOReader worldFile, final GameDifficulty diff) throws IOException {
	final int version = worldFile.readByte();
	if (version < CharacterFileFormats.CHARACTER_1) {
	    throw new CharacterVersionException("Invalid character version found: " + version);
	}
	final var k = worldFile.readInt();
	final var pAtk = worldFile.readInt();
	final var pDef = worldFile.readInt();
	final var pHP = worldFile.readInt();
	final var pMP = worldFile.readInt();
	final var strength = worldFile.readInt();
	final var block = worldFile.readInt();
	final var agility = worldFile.readInt();
	final var vitality = worldFile.readInt();
	final var intelligence = worldFile.readInt();
	final var luck = worldFile.readInt();
	final var lvl = worldFile.readInt();
	final var cHP = worldFile.readInt();
	final var cMP = worldFile.readInt();
	final var gld = worldFile.readInt();
	final var apr = worldFile.readInt();
	final var spr = worldFile.readInt();
	final var load = worldFile.readInt();
	final var exp = worldFile.readLong();
	final var c = worldFile.readInt();
	final var g = worldFile.readInt();
	final var max = worldFile.readInt();
	final var known = new boolean[max];
	for (var x = 0; x < max; x++) {
	    known[x] = worldFile.readBoolean();
	}
	final var n = worldFile.readString();
	final var aid = worldFile.readString();
	final var pm = PartyManager.getNewPCInstance(c, g, n, aid, diff);
	pm.setStrength(strength);
	pm.setBlock(block);
	pm.setAgility(agility);
	pm.setVitality(vitality);
	pm.setIntelligence(intelligence);
	pm.setLuck(luck);
	pm.setAttacksPerRound(apr);
	pm.setSpellsPerRound(spr);
	pm.setItems(ItemInventory.readItemInventory(worldFile));
	pm.kills = k;
	pm.permanentAttack = pAtk;
	pm.permanentDefense = pDef;
	pm.permanentHP = pHP;
	pm.permanentMP = pMP;
	pm.loadPartyMember(lvl, cHP, cMP, gld, load, exp, c, known);
	return pm;
    }

    // Fields
    private Job job;
    private Gender gender;
    private final String name;
    private int permanentAttack;
    private int permanentDefense;
    private int permanentHP;
    private int permanentMP;
    private int kills;
    private final String avatarID;

    // Constructors
    PartyMember(final Job c, final Gender g, final String n, final String aid, final GameDifficulty diff) {
	super(0, diff);
	this.avatarID = aid;
	this.name = n;
	this.job = c;
	this.gender = g;
	this.permanentAttack = 0;
	this.permanentDefense = 0;
	this.permanentHP = 0;
	this.permanentMP = 0;
	this.kills = 0;
	this.setLevel(1);
	this.setStrength(StatConstants.GAIN_STRENGTH);
	this.setBlock(StatConstants.GAIN_BLOCK);
	this.setVitality(StatConstants.GAIN_VITALITY);
	this.setIntelligence(StatConstants.GAIN_INTELLIGENCE);
	this.setAgility(StatConstants.GAIN_AGILITY);
	this.setLuck(StatConstants.GAIN_LUCK);
	this.setAttacksPerRound(1);
	this.setSpellsPerRound(1);
	this.healAndRegenerateFully();
	this.setGold(PartyMember.START_GOLD);
	this.setExperience(0L);
	final var nextLevelEquation = new PolyTable(3, 1, 0, true);
	final var value = PartyMember.BASE_COEFF;
	nextLevelEquation.setCoefficient(1, value);
	nextLevelEquation.setCoefficient(2, value);
	nextLevelEquation.setCoefficient(3, value);
	this.setToNextLevel(nextLevelEquation);
	this.setSpellBook(JobManager.getSpellBookByID(this.job.getJobID()));
    }

    @Override
    public int getAttack() {
	return super.getAttack() + this.getPermanentAttackPoints();
    }

    public Job getJob() {
	return this.job;
    }

    @Override
    public int getDefense() {
	return super.getDefense() + this.getPermanentDefensePoints();
    }

    protected Gender getGender() {
	return this.gender;
    }

    @Override
    protected BufferedImageIcon getInitialImage() {
	return AvatarConstructionKit.constructFromAvatarID(this.avatarID).generateAvatarImage();
    }

    @Override
    public int getMaximumHP() {
	return super.getMaximumHP() + this.getPermanentHPPoints();
    }

    @Override
    public int getMaximumMP() {
	return super.getMaximumMP() + this.getPermanentMPPoints();
    }

    @Override
    public String getName() {
	return this.name;
    }

    public int getPermanentAttackPoints() {
	return this.permanentAttack;
    }

    public int getPermanentDefensePoints() {
	return this.permanentDefense;
    }

    public int getPermanentHPPoints() {
	return this.permanentHP;
    }

    public int getPermanentMPPoints() {
	return this.permanentMP;
    }

    @Override
    public int getSpeed(GameDifficulty diff) {
	final var base = this.getBaseSpeed();
	switch (diff) {
	case GameDifficulty.VERY_EASY:
	    return (int) (base * Creature.SPEED_ADJUST_FASTEST);
	case GameDifficulty.EASY:
	    return (int) (base * Creature.SPEED_ADJUST_FAST);
	case GameDifficulty.NORMAL:
	    return (int) (base * Creature.SPEED_ADJUST_NORMAL);
	case GameDifficulty.HARD:
	    return (int) (base * Creature.SPEED_ADJUST_SLOW);
	case GameDifficulty.VERY_HARD:
	    return (int) (base * Creature.SPEED_ADJUST_SLOWEST);
	default:
	    break;
	}
	return (int) (base * Creature.SPEED_ADJUST_NORMAL);
    }

    public String getXPString() {
	return this.getExperience() + "/" + this.getToNextLevelValue();
    }

    public void initPostKill(final Job c, final Gender g) {
	this.job = c;
	this.gender = g;
	this.setLevel(1);
	this.setStrength(StatConstants.GAIN_STRENGTH);
	this.setBlock(StatConstants.GAIN_BLOCK);
	this.setVitality(StatConstants.GAIN_VITALITY);
	this.setIntelligence(StatConstants.GAIN_INTELLIGENCE);
	this.setAgility(StatConstants.GAIN_AGILITY);
	this.setLuck(StatConstants.GAIN_LUCK);
	this.setAttacksPerRound(1);
	this.setSpellsPerRound(1);
	this.healAndRegenerateFully();
	this.setGold(PartyMember.START_GOLD);
	this.setExperience(0L);
	this.getItems().resetInventory();
	final var nextLevelEquation = new PolyTable(3, 1, 0, true);
	final var value = PartyMember.BASE_COEFF;
	nextLevelEquation.setCoefficient(1, value);
	nextLevelEquation.setCoefficient(2, value);
	nextLevelEquation.setCoefficient(3, value);
	this.setToNextLevel(nextLevelEquation);
	this.setSpellBook(JobManager.getSpellBookByID(this.job.getJobID()));
	PartyManager.getParty().resetZone();
    }

    // Transformers
    @Override
    protected void levelUpHook() {
	this.offsetStrength(StatConstants.GAIN_STRENGTH);
	this.offsetBlock(StatConstants.GAIN_BLOCK);
	this.offsetVitality(StatConstants.GAIN_VITALITY);
	this.offsetIntelligence(StatConstants.GAIN_INTELLIGENCE);
	this.offsetAgility(StatConstants.GAIN_AGILITY);
	this.offsetLuck(StatConstants.GAIN_LUCK);
	this.healAndRegenerateFully();
    }

    @Override
    public void loadCreature() {
	// Do nothing
    }

    private void loadPartyMember(final int newLevel, final int chp, final int cmp, final int newGold, final int newLoad,
	    final long newExperience, final int bookID, final boolean[] known) {
	this.setLevel(newLevel);
	this.setCurrentHP(chp);
	this.setCurrentMP(cmp);
	this.setGold(newGold);
	this.setLoad(newLoad);
	this.setExperience(newExperience);
	final var book = JobManager.getSpellBookByID(bookID);
	for (var x = 0; x < known.length; x++) {
	    if (known[x]) {
		book.learnSpellByID(x);
	    }
	}
	this.setSpellBook(book);
    }

    public void onDeath(final int penalty) {
	this.offsetExperiencePercentage(penalty);
	this.healAndRegenerateFully();
	this.setGold(0);
    }

    public void spendPointOnAttack() {
	this.kills++;
	this.permanentAttack++;
    }

    public void spendPointOnDefense() {
	this.kills++;
	this.permanentDefense++;
    }

    public void spendPointOnHP() {
	this.kills++;
	this.permanentHP++;
    }

    public void spendPointOnMP() {
	this.kills++;
	this.permanentMP++;
    }

    public void write(final DataIOWriter worldFile) throws IOException {
	worldFile.writeByte(CharacterFileFormats.CHARACTER_LATEST);
	worldFile.writeInt(this.kills);
	worldFile.writeInt(this.getPermanentAttackPoints());
	worldFile.writeInt(this.getPermanentDefensePoints());
	worldFile.writeInt(this.getPermanentHPPoints());
	worldFile.writeInt(this.getPermanentMPPoints());
	worldFile.writeInt(this.getStrength());
	worldFile.writeInt(this.getBlock());
	worldFile.writeInt(this.getAgility());
	worldFile.writeInt(this.getVitality());
	worldFile.writeInt(this.getIntelligence());
	worldFile.writeInt(this.getLuck());
	worldFile.writeInt(this.getLevel());
	worldFile.writeInt(this.getCurrentHP());
	worldFile.writeInt(this.getCurrentMP());
	worldFile.writeInt(this.getGold());
	worldFile.writeInt(this.getAttacksPerRound());
	worldFile.writeInt(this.getSpellsPerRound());
	worldFile.writeInt(this.getLoad());
	worldFile.writeLong(this.getExperience());
	worldFile.writeInt(this.getJob().getJobID());
	worldFile.writeInt(this.getGender().getGenderID());
	final var max = this.getSpellBook().getSpellCount();
	worldFile.writeInt(max);
	for (var x = 0; x < max; x++) {
	    worldFile.writeBoolean(this.getSpellBook().isSpellKnown(x));
	}
	worldFile.writeString(this.getName());
	worldFile.writeString(this.avatarID);
	this.getItems().writeItemInventory(worldFile);
    }
}
