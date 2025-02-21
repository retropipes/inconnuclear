/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import org.retropipes.diane.storage.NumberStorage;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.utility.DungeonConstants;

public final class PlayerLocationManager {
    // Fields
    private int playerInstance;
    private NumberStorage playerData;
    private NumberStorage savedPlayerData;
    private NumberStorage savedRemoteData;

    // Constructors
    public PlayerLocationManager() {
	this.playerInstance = 0;
	this.playerData = new NumberStorage(DungeonConstants.PLAYER_DIMS, DungeonConstants.NUM_PLAYERS);
	this.playerData.fill(-1);
	this.savedPlayerData = new NumberStorage(DungeonConstants.PLAYER_DIMS, DungeonConstants.NUM_PLAYERS);
	this.savedPlayerData.fill(-1);
	this.savedRemoteData = new NumberStorage(DungeonConstants.PLAYER_DIMS, DungeonConstants.NUM_PLAYERS);
	this.savedRemoteData.fill(-1);
    }

    public int getActivePlayerNumber() {
	return this.playerInstance;
    }

    public int getPlayerLocationX() {
	return this.playerData.getCell(1, this.playerInstance);
    }

    public int getPlayerLocationY() {
	return this.playerData.getCell(0, this.playerInstance);
    }

    public int getPlayerLocationZ() {
	return this.playerData.getCell(2, this.playerInstance);
    }

    private void initPlayerLocation(final int valX, final int valY, final int valZ, final int pi) {
	this.playerData.setCell(valX, 1, pi);
	this.playerData.setCell(valY, 0, pi);
	this.playerData.setCell(valZ, 2, pi);
    }

    void offsetPlayerLocationX(final int val) {
	this.playerData.setCell(this.getPlayerLocationX() + val, 1, this.playerInstance);
    }

    void offsetPlayerLocationY(final int val) {
	this.playerData.setCell(this.getPlayerLocationY() + val, 0, this.playerInstance);
    }

    public void resetPlayerLocation() {
	final var a = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase();
	for (var pi = 0; pi < DungeonConstants.NUM_PLAYERS; pi++) {
	    final var found = a.findPlayer(pi);
	    if (found != null) {
		final var valX = found[0];
		final var valY = found[1];
		final var valZ = found[2];
		this.initPlayerLocation(valX, valY, valZ, pi);
	    }
	}
    }

    void restorePlayerLocation() {
	this.playerData = new NumberStorage(this.savedPlayerData);
    }

    void restoreRemoteLocation() {
	this.playerData = new NumberStorage(this.savedRemoteData);
    }

    void savePlayerLocation() {
	this.savedPlayerData = new NumberStorage(this.playerData);
    }

    void saveRemoteLocation() {
	this.savedRemoteData = new NumberStorage(this.playerData);
    }

    public void setActivePlayerNumber(final int value) {
	this.playerInstance = value;
    }

    public void setPlayerLocation(final int valX, final int valY, final int valZ) {
	this.setPlayerLocationX(valX);
	this.setPlayerLocationY(valY);
	this.setPlayerLocationZ(valZ);
    }

    private void setPlayerLocationX(final int val) {
	this.playerData.setCell(val, 1, this.playerInstance);
    }

    private void setPlayerLocationY(final int val) {
	this.playerData.setCell(val, 0, this.playerInstance);
    }

    private void setPlayerLocationZ(final int val) {
	this.playerData.setCell(val, 2, this.playerInstance);
    }

    public void togglePlayerInstance() {
	var doesNotExist = true;
	while (doesNotExist) {
	    this.playerInstance++;
	    if (this.playerInstance >= DungeonConstants.NUM_PLAYERS) {
		this.playerInstance = 0;
	    }
	    final var px = this.getPlayerLocationX();
	    final var py = this.getPlayerLocationY();
	    final var pz = this.getPlayerLocationZ();
	    if (px != -1 && py != -1 && pz != -1) {
		doesNotExist = false;
	    }
	}
    }
}
