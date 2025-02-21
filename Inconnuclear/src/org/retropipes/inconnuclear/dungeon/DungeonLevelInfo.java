/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.dungeon;

import java.io.IOException;

import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.diane.storage.NumberStorage;
import org.retropipes.inconnuclear.locale.Difficulty;
import org.retropipes.inconnuclear.locale.Generic;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.utility.DungeonConstants;

public final class DungeonLevelInfo {
    public static DungeonLevelInfo readLevelInfo(final DataIOReader reader) throws IOException {
	final var li = new DungeonLevelInfo();
	int x, y;
	for (y = 0; y < 3; y++) {
	    for (x = 0; x < DungeonConstants.NUM_PLAYERS; x++) {
		li.playerStartData.setCell(reader.readInt(), y, x);
	    }
	}
	li.horizontalWraparoundEnabled = reader.readBoolean();
	li.verticalWraparoundEnabled = reader.readBoolean();
	li.thirdDimensionWraparoundEnabled = reader.readBoolean();
	li.name = reader.readString();
	li.hint = reader.readString();
	li.author = reader.readString();
	li.difficulty = Difficulty.values()[reader.readInt()];
	li.moveShootAllowed = reader.readBoolean();
	return li;
    }

    // Properties
    private final NumberStorage playerStartData;
    private NumberStorage playerLocationData;
    private NumberStorage savedPlayerLocationData;
    private boolean horizontalWraparoundEnabled;
    private boolean verticalWraparoundEnabled;
    private boolean thirdDimensionWraparoundEnabled;
    private String name;
    private String hint;
    private String author;
    private Difficulty difficulty;
    private boolean moveShootAllowed;

    // Constructors
    public DungeonLevelInfo() {
	this.playerStartData = new NumberStorage(DungeonConstants.PLAYER_DIMS, DungeonConstants.NUM_PLAYERS);
	this.playerStartData.fill(-1);
	this.playerLocationData = new NumberStorage(DungeonConstants.PLAYER_DIMS, DungeonConstants.NUM_PLAYERS);
	this.playerLocationData.fill(-1);
	this.savedPlayerLocationData = new NumberStorage(DungeonConstants.PLAYER_DIMS, DungeonConstants.NUM_PLAYERS);
	this.savedPlayerLocationData.fill(-1);
	this.horizontalWraparoundEnabled = false;
	this.verticalWraparoundEnabled = false;
	this.thirdDimensionWraparoundEnabled = false;
	this.name = Strings.generic(Generic.UNNAMED_LEVEL);
	this.hint = Strings.EMPTY;
	this.author = Strings.generic(Generic.UNKNOWN_AUTHOR);
	this.difficulty = Difficulty.KIDS;
	this.moveShootAllowed = false;
    }

    public DungeonLevelInfo(final DungeonLevelInfo source) {
	this.playerStartData = new NumberStorage(source.playerStartData);
	this.horizontalWraparoundEnabled = source.horizontalWraparoundEnabled;
	this.verticalWraparoundEnabled = source.verticalWraparoundEnabled;
	this.thirdDimensionWraparoundEnabled = source.thirdDimensionWraparoundEnabled;
	this.name = source.name;
	this.hint = source.hint;
	this.author = source.author;
	this.difficulty = source.difficulty;
	this.moveShootAllowed = source.moveShootAllowed;
    }

    public void disableHorizontalWraparound() {
	this.horizontalWraparoundEnabled = false;
    }

    public void disableThirdDimensionWraparound() {
	this.thirdDimensionWraparoundEnabled = false;
    }

    public void disableVerticalWraparound() {
	this.verticalWraparoundEnabled = false;
    }

    public boolean doesPlayerLocationExist(final int pi) {
	for (var y = 0; y < DungeonConstants.PLAYER_DIMS; y++) {
	    if (this.playerLocationData.getCell(y, pi) == -1) {
		return false;
	    }
	}
	return true;
    }

    public boolean doesPlayerStartExist(final int pi) {
	for (var y = 0; y < DungeonConstants.PLAYER_DIMS; y++) {
	    if (this.playerStartData.getCell(y, pi) == -1) {
		return false;
	    }
	}
	return true;
    }

    public void enableHorizontalWraparound() {
	this.horizontalWraparoundEnabled = true;
    }

    public void enableThirdDimensionWraparound() {
	this.thirdDimensionWraparoundEnabled = true;
    }

    public void enableVerticalWraparound() {
	this.verticalWraparoundEnabled = true;
    }

    public String getAuthor() {
	return this.author;
    }

    public Difficulty getDifficulty() {
	return this.difficulty;
    }

    public String getHint() {
	return this.hint;
    }

    public String getName() {
	return this.name;
    }

    public int getPlayerLocationX(final int pi) {
	return this.playerLocationData.getCell(1, pi);
    }

    public int getPlayerLocationY(final int pi) {
	return this.playerLocationData.getCell(0, pi);
    }

    public int getPlayerLocationZ(final int pi) {
	return this.playerLocationData.getCell(2, pi);
    }

    public int getStartColumn(final int pi) {
	return this.playerStartData.getCell(0, pi);
    }

    public int getStartFloor(final int pi) {
	return this.playerStartData.getCell(2, pi);
    }

    public int getStartRow(final int pi) {
	return this.playerStartData.getCell(1, pi);
    }

    public boolean isHorizontalWraparoundEnabled() {
	return this.horizontalWraparoundEnabled;
    }

    public boolean isMoveShootAllowed() {
	return this.moveShootAllowed;
    }

    public boolean isThirdDimensionWraparoundEnabled() {
	return this.thirdDimensionWraparoundEnabled;
    }

    public boolean isVerticalWraparoundEnabled() {
	return this.verticalWraparoundEnabled;
    }

    public void offsetPlayerLocationX(final int pi, final int value) {
	this.playerLocationData.offsetCell(value, 1, pi);
    }

    public void offsetPlayerLocationY(final int pi, final int value) {
	this.playerLocationData.offsetCell(value, 0, pi);
    }

    public void offsetPlayerLocationZ(final int pi, final int value) {
	this.playerLocationData.offsetCell(value, 2, pi);
    }

    public void restorePlayerLocation() {
	this.playerLocationData = new NumberStorage(this.savedPlayerLocationData);
    }

    public void savePlayerLocation() {
	this.savedPlayerLocationData = new NumberStorage(this.playerLocationData);
    }

    public void setAuthor(final String newAuthor) {
	this.author = newAuthor;
    }

    public void setDifficulty(final Difficulty newDifficulty) {
	this.difficulty = newDifficulty;
    }

    public void setHint(final String newHint) {
	this.hint = newHint;
    }

    public void setMoveShootAllowed(final boolean value) {
	this.moveShootAllowed = value;
    }

    public void setName(final String newName) {
	this.name = newName;
    }

    public void setPlayerLocationX(final int pi, final int value) {
	this.playerLocationData.setCell(value, 1, pi);
    }

    public void setPlayerLocationY(final int pi, final int value) {
	this.playerLocationData.setCell(value, 0, pi);
    }

    public void setPlayerLocationZ(final int pi, final int value) {
	this.playerLocationData.setCell(value, 2, pi);
    }

    public void setPlayerToStart() {
	this.playerLocationData = new NumberStorage(this.playerStartData);
    }

    public void setStartColumn(final int pi, final int value) {
	this.playerStartData.setCell(value, 0, pi);
    }

    public void setStartFloor(final int pi, final int value) {
	this.playerStartData.setCell(value, 2, pi);
    }

    public void setStartRow(final int pi, final int value) {
	this.playerStartData.setCell(value, 1, pi);
    }

    public void writeLevelInfo(final DataIOWriter writer) throws IOException {
	int x, y;
	for (y = 0; y < DungeonConstants.PLAYER_DIMS; y++) {
	    for (x = 0; x < DungeonConstants.NUM_PLAYERS; x++) {
		writer.writeInt(this.playerStartData.getCell(y, x));
	    }
	}
	writer.writeBoolean(this.horizontalWraparoundEnabled);
	writer.writeBoolean(this.verticalWraparoundEnabled);
	writer.writeBoolean(this.thirdDimensionWraparoundEnabled);
	writer.writeString(this.name);
	writer.writeString(this.hint);
	writer.writeString(this.author);
	writer.writeInt(this.difficulty.ordinal());
	writer.writeBoolean(this.moveShootAllowed);
    }
}
