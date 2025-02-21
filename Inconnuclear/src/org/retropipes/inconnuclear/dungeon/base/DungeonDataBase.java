/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.dungeon.base;

import java.io.IOException;

import org.retropipes.diane.direction.Direction;
import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;

public abstract class DungeonDataBase {
    protected final static int MIN_FLOORS = 1;
    protected final static int MAX_FLOORS = 9;
    protected final static int MIN_COLUMNS = 24;
    protected final static int MIN_ROWS = 24;

    public static int getMaxFloors() {
	return DungeonDataBase.MAX_FLOORS;
    }

    public static int getMinColumns() {
	return DungeonDataBase.MIN_COLUMNS;
    }

    public static int getMinFloors() {
	return DungeonDataBase.MIN_FLOORS;
    }

    public static int getMinRows() {
	return DungeonDataBase.MIN_ROWS;
    }

    public abstract int checkForMagnetic(final DungeonBase dungeonBase, final int floor, final int centerX,
	    final int centerY, final Direction dir);

    public abstract int[] circularScan(final DungeonBase dungeonBase, final int xIn, final int yIn, final int zIn,
	    final int r, final String targetName, boolean moved);

    public abstract boolean circularScanPlayer(final DungeonBase dungeonBase, final int x, final int y, final int z,
	    final int r);

    public abstract int[] circularScanTunnel(final DungeonBase dungeonBase, final int x, final int y, final int z,
	    final int maxR, final int tx, final int ty, final GameObject target, final boolean moved);

    public abstract void clearDirtyFlags(final int floor);

    public abstract void clearRedoHistory();

    public abstract void clearUndoHistory();

    public abstract void clearVirtualGrid(final DungeonBase dungeonBase);

    public abstract void fill(final DungeonBase dungeonBase, final GameObject fillWith);

    public abstract void fillNulls(final DungeonBase dungeonBase, final GameObject fill1, final GameObject fill2,
	    final boolean was16);

    public abstract void fillSTSNulls(final GameObject fillWith);

    public abstract void fillVirtual();

    public abstract int[] findObject(final DungeonBase dungeonBase, final int z, final String targetName);

    public abstract int[] findPlayer(final DungeonBase dungeonBase, final int number);

    public abstract void fullScanAllButtonClose(final DungeonBase dungeonBase, final int zIn, final GameObject source);

    public abstract void fullScanAllButtonOpen(final DungeonBase dungeonBase, final int zIn, final GameObject source);

    public abstract void fullScanButtonBind(final DungeonBase dungeonBase, final int dx, final int dy, final int zIn,
	    final GameObject source);

    public abstract void fullScanButtonCleanup(final DungeonBase dungeonBase, final int px, final int py, final int zIn,
	    final GameObject button);

    public abstract void fullScanFindButtonLostDoor(final DungeonBase dungeonBase, final int zIn,
	    final GameObject door);

    public abstract void fullScanFreezeGround(final DungeonBase dungeonBase);

    public abstract GameObject getCell(final DungeonBase dungeonBase, final int row, final int col, final int floor,
	    final int layer);

    public abstract int getColumns();

    public abstract int getFloors();

    public abstract int getRows();

    public abstract GameObject getVirtualCell(final DungeonBase dungeonBase, final int row, final int col,
	    final int floor, final int layer);

    public abstract HistoryStatus getWhatWas();

    public abstract boolean isCellDirty(final DungeonBase dungeonBase, final int row, final int col, final int floor);

    public abstract boolean isSquareVisible(final DungeonBase dungeonBase, final int x1, final int y1, final int x2,
	    final int y2, final int zp);

    public abstract boolean linearScan(final DungeonBase dungeonBase, final int xIn, final int yIn, final int zIn,
	    final Direction d);

    public abstract int linearScanMagnetic(final DungeonBase dungeonBase, final int xIn, final int yIn, final int zIn,
	    final Direction d);

    public abstract void markAsDirty(final DungeonBase dungeonBase, final int row, final int col, final int floor);

    protected final int normalizeColumn(final int column) {
	var fC = column;
	if (fC < 0) {
	    fC += this.getColumns();
	    while (fC < 0) {
		fC += this.getColumns();
	    }
	} else if (fC > this.getColumns() - 1) {
	    fC -= this.getColumns();
	    while (fC > this.getColumns() - 1) {
		fC -= this.getColumns();
	    }
	}
	return fC;
    }

    protected final int normalizeFloor(final int floor) {
	var fF = floor;
	if (fF < 0) {
	    fF += this.getFloors();
	    while (fF < 0) {
		fF += this.getFloors();
	    }
	} else if (fF > this.getFloors() - 1) {
	    fF -= this.getFloors();
	    while (fF > this.getFloors() - 1) {
		fF -= this.getFloors();
	    }
	}
	return fF;
    }

    protected final int normalizeRow(final int row) {
	var fR = row;
	if (fR < 0) {
	    fR += this.getRows();
	    while (fR < 0) {
		fR += this.getRows();
	    }
	} else if (fR > this.getRows() - 1) {
	    fR -= this.getRows();
	    while (fR > this.getRows() - 1) {
		fR -= this.getRows();
	    }
	}
	return fR;
    }

    public abstract void postBattle(final DungeonBase dungeonBase, final GameObject m, final int xLoc, final int yLoc,
	    final boolean player);

    public abstract DungeonDataBase readData(final DungeonBase dungeonBase, final DataIOReader reader, final int ver)
	    throws IOException;

    public abstract void readSavedState(final DataIOReader reader, final int formatVersion) throws IOException;

    public abstract void redo(final DungeonBase dungeonBase);

    public abstract void resetHistoryEngine();

    public abstract void resetVisibleSquares(final int floor);

    public abstract void resize(final DungeonBase dungeonBase, final int zIn, final GameObject nullFill);

    public abstract void resizeSavedState(final int z, final GameObject nullFill);

    public abstract void restore(final DungeonBase dungeonBase);

    public abstract void save(final DungeonBase dungeonBase);

    public abstract void setAllDirtyFlags();

    public abstract void setCell(final DungeonBase dungeonBase, final GameObject mo, final int row, final int col,
	    final int floor, final int layer);

    public abstract void setDirtyFlags(final int floor);

    public abstract void setVirtualCell(final DungeonBase dungeonBase, final GameObject mo, final int row,
	    final int col, final int floor, final int layer);

    public abstract void tickTimers(final DungeonBase dungeonBase);

    public abstract void tickTimers(final DungeonBase dungeonBase, final int floor, final int actionType);

    public abstract boolean tryRedo();

    public abstract boolean tryUndo();

    public abstract void undo(final DungeonBase dungeonBase);

    public abstract void updateMonsterPosition(final DungeonBase dungeonBase, final Direction move, final int xLoc,
	    final int yLoc, final GameObject monster, final int pi);

    public abstract void updateRedoHistory(final HistoryStatus whatIs);

    public abstract void updateUndoHistory(final HistoryStatus whatIs);

    public abstract void updateVisibleSquares(final DungeonBase dungeonBase, final int xp, final int yp, final int zp);

    public abstract void writeData(final DungeonBase dungeonBase, final DataIOWriter writer) throws IOException;

    public abstract void writeSavedState(final DataIOWriter writer) throws IOException;
}
