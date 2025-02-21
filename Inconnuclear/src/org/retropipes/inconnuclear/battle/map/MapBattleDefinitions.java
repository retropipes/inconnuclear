/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map;

import org.retropipes.inconnuclear.battle.BattleCharacter;
import org.retropipes.inconnuclear.battle.ai.AIContext;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;

public class MapBattleDefinitions {
    private static final int MAX_BATTLERS = 100;
    // Fields
    private BattleCharacter activeCharacter;
    private final BattleCharacter[] battlers;
    private final AIContext[] aiContexts;
    private DungeonBase battleMap;
    private int battlerCount;

    // Constructors
    public MapBattleDefinitions() {
	this.battlers = new BattleCharacter[MapBattleDefinitions.MAX_BATTLERS];
	this.aiContexts = new AIContext[MapBattleDefinitions.MAX_BATTLERS];
	this.battlerCount = 0;
    }

    public boolean addBattler(final BattleCharacter battler) {
	if (this.battlerCount < MapBattleDefinitions.MAX_BATTLERS) {
	    this.battlers[this.battlerCount] = battler;
	    this.battlerCount++;
	    return true;
	}
	return false;
    }

    public int findBattler(final String name) {
	return this.findBattler(name, 0, this.battlers.length);
    }

    private int findBattler(final String name, final int start, final int limit) {
	for (var x = start; x < limit; x++) {
	    if (this.battlers[x] != null && this.battlers[x].getName().equals(name)) {
		return x;
	    }
	}
	return -1;
    }

    public int findFirstBattlerOnTeam(final int teamID) {
	return this.findFirstBattlerOnTeam(teamID, 0, this.battlers.length);
    }

    private int findFirstBattlerOnTeam(final int teamID, final int start, final int limit) {
	for (var x = start; x < limit; x++) {
	    if (this.battlers[x] != null && this.battlers[x].getTeamID() == teamID) {
		return x;
	    }
	}
	return -1;
    }

    private int findFirstBattlerNotOnTeam(final int teamID, final int start, final int limit) {
	for (var x = start; x < limit; x++) {
	    if (this.battlers[x] != null && this.battlers[x].getTeamID() != teamID) {
		return x;
	    }
	}
	return -1;
    }

    public BattleCharacter getFirstBattlerOnTeam(final int teamID) {
	var pos = this.findFirstBattlerOnTeam(teamID, 0, this.battlers.length);
	if (pos >= 0 && pos < this.battlers.length) {
	    return this.battlers[pos];
	}
	return null;
    }

    public BattleCharacter getFirstBattlerNotOnTeam(final int teamID) {
	var pos = this.findFirstBattlerNotOnTeam(teamID, 0, this.battlers.length);
	if (pos >= 0 && pos < this.battlers.length) {
	    return this.battlers[pos];
	}
	return null;
    }

    public BattleCharacter getActiveCharacter() {
	return this.activeCharacter;
    }

    public DungeonBase getBattleDungeonBase() {
	return this.battleMap;
    }

    public AIContext[] getBattlerAIContexts() {
	return this.aiContexts;
    }

    public BattleCharacter[] getBattlers() {
	return this.battlers;
    }

    public void resetBattlers() {
	for (final BattleCharacter battler : this.battlers) {
	    if (battler != null && battler.getCreature().isAlive()) {
		battler.activate();
		battler.resetAP();
		battler.resetAttacks();
		battler.resetSpells();
		battler.resetLocation();
	    }
	}
    }

    public void roundResetBattlers() {
	for (final BattleCharacter battler : this.battlers) {
	    if (battler != null && battler.getCreature().isAlive()) {
		battler.resetAP();
		battler.resetAttacks();
		battler.resetSpells();
	    }
	}
    }

    public void setActiveCharacter(final BattleCharacter bc) {
	this.activeCharacter = bc;
    }

    public void setBattleDungeonBase(final DungeonBase bMap) {
	this.battleMap = bMap;
    }
}
