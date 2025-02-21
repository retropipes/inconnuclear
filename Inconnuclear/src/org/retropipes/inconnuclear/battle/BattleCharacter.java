/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.


All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.inconnuclear.battle.ai.AIContext;
import org.retropipes.inconnuclear.battle.ai.CreatureAI;
import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.StatConstants;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;

public class BattleCharacter {
    // Fields
    private final Creature creature;
    private final AIContext aic;
    private CreatureAI ai;
    private int actionCounter;
    private int attackCounter;
    private int spellCounter;
    private boolean isActive;
    private GameObject tile;

    // Constructors
    public BattleCharacter(final Creature newTemplate, final int rows, final int columns) {
	this.creature = newTemplate;
	this.actionCounter = newTemplate.getMapBattleActionsPerRound();
	this.attackCounter = (int) newTemplate.getEffectedStat(StatConstants.STAT_ATTACKS_PER_ROUND);
	this.spellCounter = (int) newTemplate.getEffectedStat(StatConstants.STAT_SPELLS_PER_ROUND);
	this.isActive = true;
	this.aic = new AIContext(this, rows, columns);
    }

    public final void activate() {
	this.isActive = true;
    }

    public BufferedImageIcon battleRender() {
	return this.creature.getImage();
    }

    public final void deactivate() {
	this.isActive = false;
    }

    public final String getAPString() {
	return "Moves Left: " + (this.actionCounter >= 0 ? this.actionCounter : 0);
    }

    public final String getAttackString() {
	return "Attacks Left: " + (this.attackCounter >= 0 ? this.attackCounter : 0);
    }

    public final int getActionsLeft() {
	return this.actionCounter;
    }

    public final int getAttacksLeft() {
	return this.attackCounter;
    }

    public final CreatureAI getAI() {
	return this.ai;
    }

    public final AIContext getAIContext() {
	return this.aic;
    }

    public final int getSpellsLeft() {
	return this.spellCounter;
    }

    public String getName() {
	return this.creature.getName();
    }

    public final String getSpellString() {
	return "Spells Left: " + (this.spellCounter >= 0 ? this.spellCounter : 0);
    }

    public final int getTeamID() {
	return this.creature.getTeamID();
    }

    public final String getTeamString() {
	if (this.getCreature().getTeamID() == 0) {
	    return "Team: Party";
	}
	return "Team: Enemies " + this.getCreature().getTeamID();
    }

    public final GameObject getTile() {
	return this.tile;
    }

    public final Creature getCreature() {
	return this.creature;
    }

    public final int getX() {
	return this.creature.getX();
    }

    public final int getY() {
	return this.creature.getY();
    }

    public final boolean hasAI() {
	return this.ai != null;
    }

    public final boolean isActive() {
	return this.isActive;
    }

    public final void modifyAP(final int mod) {
	this.actionCounter -= mod;
    }

    public final void modifyAttacks(final int mod) {
	this.attackCounter -= mod;
    }

    public final void modifySpells(final int mod) {
	this.spellCounter -= mod;
    }

    public final void offsetX(final int newX) {
	this.creature.offsetX(newX);
    }

    public final void offsetY(final int newY) {
	this.creature.offsetY(newY);
    }

    public final void resetAll() {
	this.resetAP();
	this.resetAttacks();
	this.resetSpells();
    }

    public final void resetAP() {
	this.actionCounter = this.creature.getMapBattleActionsPerRound();
    }

    public final void resetAttacks() {
	this.attackCounter = (int) this.creature.getEffectedStat(StatConstants.STAT_ATTACKS_PER_ROUND);
    }

    public final void resetLocation() {
	this.creature.setX(-1);
	this.creature.setY(-1);
    }

    public final void resetSpells() {
	this.spellCounter = (int) this.creature.getEffectedStat(StatConstants.STAT_SPELLS_PER_ROUND);
    }

    public final void restoreLocation() {
	this.creature.restoreLocation();
    }

    public final void saveLocation() {
	this.creature.saveLocation();
    }

    public final void setAI(final CreatureAI newAI) {
	this.ai = newAI;
    }

    public final void setTile(final GameObject newTile) {
	this.tile = newTile;
    }

    public final void setX(final int newX) {
	this.creature.setX(newX);
    }

    public final void setY(final int newY) {
	this.creature.setY(newY);
    }
}
