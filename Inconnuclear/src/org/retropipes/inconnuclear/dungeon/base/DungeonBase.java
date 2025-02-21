/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.dungeon.base;

import java.io.File;
import java.io.IOException;

import org.retropipes.diane.direction.Direction;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.dungeon.Dungeon;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.files.AbstractPrefixIO;
import org.retropipes.inconnuclear.files.AbstractSuffixIO;
import org.retropipes.inconnuclear.locale.Difficulty;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

public abstract class DungeonBase {
    private static final int MIN_LEVELS = 1;
    protected static final int MAX_LEVELS = Integer.MAX_VALUE;
    protected static final int ERA_COUNT = 5;
    private static final int MAX_COLUMNS = 250;
    private static final int MAX_ROWS = 250;

    public static String getDungeonTempFolder() {
	return System.getProperty(Strings.untranslated(Untranslated.TEMP_DIR)) + File.separator
		+ Strings.untranslated(Untranslated.PROGRAM_NAME);
    }

    public static int getMaxColumns() {
	return DungeonBase.MAX_ROWS;
    }

    public static int getMaxFloors() {
	return DungeonDataBase.getMaxFloors();
    }

    public static int getMaxLevels() {
	return DungeonBase.MAX_LEVELS;
    }

    public static int getMaxRows() {
	return DungeonBase.MAX_ROWS;
    }

    public static int getMinColumns() {
	return DungeonBase.MAX_COLUMNS;
    }

    public static int getMinFloors() {
	return DungeonDataBase.getMinFloors();
    }

    public static int getMinLevels() {
	return DungeonBase.MIN_LEVELS;
    }

    public static int getMinRows() {
	return DungeonDataBase.getMinRows();
    }

    public static DungeonBase getTemporaryBattleCopy() throws IOException {
	final var temp = new Dungeon();
	temp.addFixedSizeLevel(Inconnuclear.getBattleDungeonSize(), Inconnuclear.getBattleDungeonSize(), 1);
	temp.fillDefault();
	return temp;
    }

    public static boolean radialScan(final int cx, final int cy, final int r, final int tx, final int ty) {
	return Math.abs(tx - cx) <= r && Math.abs(ty - cy) <= r;
    }

    // Constructors
    /**
     * @throws IOException
     */
    protected DungeonBase() throws IOException {
	// Do nothing
    }

    public abstract boolean addFixedSizeLevel(final int rows, final int cols, final int floors);

    public abstract boolean addLevel();

    public abstract int checkForMagnetic(int floor, int centerX, int centerY, Direction dir);

    public abstract int[] circularScan(final int x, final int y, final int z, final int maxR, final String targetName,
	    final boolean moved);

    public abstract boolean circularScanPlayer(final int x, final int y, final int z, final int maxR);

    public abstract int[] circularScanTunnel(final int x, final int y, final int z, final int maxR, final int tx,
	    final int ty, final GameObject target, final boolean moved);

    public abstract void clearDirtyFlags(int floor);

    public abstract void clearVirtualGrid();

    public abstract Direction computeFinalBossMoveDirection(final int locX, final int locY, final int locZ,
	    final int pi);

    public abstract void copyLevel();

    public abstract void cutLevel();

    public abstract void disableHorizontalWraparound();

    public abstract void disableThirdDimensionWraparound();

    public abstract void disableVerticalWraparound();

    public abstract boolean doesLevelExist(int level);

    public abstract boolean doesLevelExistOffset(int level);

    public abstract boolean doesPlayerExist(final int pi);

    public abstract void enableHorizontalWraparound();

    public abstract void enableThirdDimensionWraparound();

    public abstract void enableVerticalWraparound();

    public abstract void fillDefault();

    public abstract int[] findObject(int z, String targetName);

    public abstract int[] findPlayer(final int number);

    public abstract void fullScanAllButtonClose(int z, GameObject source);

    public abstract void fullScanAllButtonOpen(int z, GameObject source);

    public abstract void fullScanButtonBind(int dx, int dy, int z, GameObject source);

    public abstract void fullScanButtonCleanup(int px, int py, int z, GameObject button);

    public abstract void fullScanFindButtonLostDoor(int z, GameObject door);

    public abstract void fullScanFreezeGround();

    public abstract void generateLevelInfoList();

    public abstract int getActiveEra();

    public abstract int getActiveLevel();

    public abstract String getAuthor();

    public abstract String getBasePath();

    public abstract GameObject getCell(final int row, final int col, final int floor, final int layer);

    public abstract int getColumns();

    public abstract Difficulty getDifficulty();

    public abstract String getDungeonTempMusicFolder();

    public abstract int getFloors();

    public abstract String getHint();

    public abstract String[] getLevelInfoList();

    public abstract int getLevels();

    public abstract String getMusicFilename();

    public abstract String getName();

    public abstract int getPlayerLocationX(final int pi);

    public abstract int getPlayerLocationY(final int pi);

    public abstract int getPlayerLocationZ(final int pi);

    public abstract int getRows();

    public abstract int getStartColumn(final int pi);

    public abstract int getStartFloor(final int pi);

    public abstract int getStartLevel(final int pi);

    public abstract int getStartRow(final int pi);

    public abstract GameObject getVirtualCell(final int row, final int col, final int floor, final int layer);

    public abstract HistoryStatus getWhatWas();

    public abstract boolean insertLevelFromClipboard();

    public abstract boolean isCellDirty(final int row, final int col, final int floor);

    public abstract boolean isCutBlocked();

    public abstract boolean isHorizontalWraparoundEnabled();

    public abstract boolean isMoveShootAllowed();

    public abstract boolean isMoveShootAllowedGlobally();

    public abstract boolean isMoveShootAllowedThisLevel();

    public abstract boolean isPasteBlocked();

    public abstract boolean isSquareVisible(final int x1, final int y1, final int x2, final int y2, final int zp);

    public abstract boolean isThirdDimensionWraparoundEnabled();

    public abstract boolean isVerticalWraparoundEnabled();

    public abstract void markAsDirty(final int row, final int col, final int floor);

    public abstract void offsetPlayerLocationX(final int pi, final int newPlayerLocationX);

    public abstract void offsetPlayerLocationY(final int pi, final int newPlayerLocationY);

    public abstract void offsetPlayerLocationZ(final int pi, final int newPlayerLocationZ);

    public abstract void pasteLevel();

    public abstract void postBattle(final GameObject m, final int xLoc, final int yLoc, final boolean player);

    public abstract DungeonBase readDungeonBase() throws IOException;

    public abstract void redo();

    protected abstract boolean removeActiveLevel();

    public final boolean removeLevel(final int num) {
	final var saveLevel = this.getActiveLevel();
	this.switchLevel(num);
	final var success = this.removeActiveLevel();
	if (success) {
	    if (saveLevel == 0) {
		// Was at first level
		this.switchLevel(0);
	    } else // Was at level other than first
	    if (saveLevel > num) {
		// Saved level was shifted down
		this.switchLevel(saveLevel - 1);
	    } else if (saveLevel < num) {
		// Saved level was NOT shifted down
		this.switchLevel(saveLevel);
	    } else {
		// Saved level was deleted
		this.switchLevel(0);
	    }
	} else {
	    this.switchLevel(saveLevel);
	}
	return success;
    }

    public abstract void resetHistoryEngine();

    public abstract void resetVisibleSquares(final int floor);

    public abstract void resize(int z, GameObject nullFill);

    public abstract void restore();

    public abstract void restorePlayerLocation();

    public abstract void save();

    public abstract void savePlayerLocation();

    public abstract void setAuthor(String newAuthor);

    public abstract void setCell(final GameObject mo, final int row, final int col, final int floor, final int layer);

    public abstract void setData(DungeonDataBase newData, int count);

    public abstract void setDifficulty(Difficulty newDifficulty);

    public abstract void setDirtyFlags(int floor);

    public abstract void setHint(String newHint);

    public abstract void setMoveShootAllowedGlobally(boolean value);

    public abstract void setMoveShootAllowedThisLevel(boolean value);

    public abstract void setMusicFilename(final String newMusicFilename);

    public abstract void setName(String newName);

    public abstract void setPlayerLocationX(final int pi, final int newPlayerLocationX);

    public abstract void setPlayerLocationY(final int pi, final int newPlayerLocationY);

    public abstract void setPlayerLocationZ(final int pi, final int newPlayerLocationZ);

    public abstract void setPlayerToStart();

    public abstract void setPrefixHandler(AbstractPrefixIO xph);

    public abstract void setStartColumn(final int pi, final int newStartColumn);

    public abstract void setStartFloor(final int pi, final int newStartFloor);

    public abstract void setStartRow(final int pi, final int newStartRow);

    public abstract void setSuffixHandler(AbstractSuffixIO xsh);

    public abstract void setVirtualCell(final GameObject mo, final int row, final int col, final int floor,
	    final int layer);

    public abstract void switchEra(final int era);

    public abstract void switchEraOffset(final int era);

    protected abstract void switchInternal(int level, int era);

    public abstract void switchLevel(int level);

    public abstract void switchLevelOffset(int level);

    public final boolean switchToNextLevelWithDifficulty(final int[] difficulty) {
	var keepGoing = true;
	while (keepGoing) {
	    final var diff = this.getDifficulty().ordinal();
	    for (final int element : difficulty) {
		if (diff - 1 == element) {
		    keepGoing = false;
		    return true;
		}
	    }
	    if (!this.doesLevelExistOffset(1)) {
		keepGoing = false;
		return false;
	    }
	    if (keepGoing) {
		this.switchLevelOffset(1);
	    }
	}
	return false;
    }

    public final boolean switchToPreviousLevelWithDifficulty(final int[] difficulty) {
	var keepGoing = true;
	while (keepGoing) {
	    final var diff = this.getDifficulty().ordinal();
	    for (final int element : difficulty) {
		if (diff - 1 == element) {
		    keepGoing = false;
		    return true;
		}
	    }
	    if (!this.doesLevelExistOffset(-1)) {
		keepGoing = false;
		return false;
	    }
	    if (keepGoing) {
		this.switchLevelOffset(-1);
	    }
	}
	return false;
    }

    public abstract void tickTimers();

    public abstract void tickTimers(final int floor, final int actionType);

    public abstract boolean tryRedo();

    public abstract boolean tryUndo();

    public abstract void undo();

    public abstract void updateMonsterPosition(final Direction move, final int xLoc, final int yLoc,
	    final GameObject monster, final int pi);

    public abstract void updateRedoHistory(final HistoryStatus whatIs);

    public abstract void updateUndoHistory(final HistoryStatus whatIs);

    public abstract void updateVisibleSquares(final int xp, final int yp, final int zp);

    public abstract void writeDungeon() throws IOException;
}