/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.dungeon;

import java.io.IOException;
import java.util.ArrayDeque;

import org.retropipes.diane.direction.Direction;
import org.retropipes.diane.direction.DirectionResolver;
import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.diane.random.RandomRange;
import org.retropipes.diane.storage.FlagStorage;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.StuffBag;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.dungeon.base.DungeonDataBase;
import org.retropipes.inconnuclear.dungeon.base.DungeonDataStorage;
import org.retropipes.inconnuclear.dungeon.base.HistoryStatus;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.dungeon.gameobject.Material;
import org.retropipes.inconnuclear.game.Game;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.locale.ErrorString;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.utility.DungeonConstants;
import org.retropipes.inconnuclear.utility.FileFormats;
import org.retropipes.inconnuclear.utility.VisionModes;

public final class DungeonData extends DungeonDataBase {
    private class ImageUndoEngine {
	// Inner classes
	private class HistoryEntry {
	    // Fields
	    private final DungeonDataStorage histImage;
	    private final HistoryStatus histWhatWas;

	    HistoryEntry(final DungeonDataStorage i, final HistoryStatus hww) {
		this.histImage = i;
		this.histWhatWas = hww;
	    }

	    HistoryEntry(final HistoryEntry source) {
		this.histImage = source.histImage;
		this.histWhatWas = source.histWhatWas;
	    }

	    public DungeonDataStorage getImage() {
		return this.histImage;
	    }

	    public HistoryStatus getWhatWas() {
		return this.histWhatWas;
	    }
	}

	private class HistoryStack {
	    // Fields
	    private final ArrayDeque<HistoryEntry> stack;

	    HistoryStack() {
		this.stack = new ArrayDeque<>();
	    }

	    HistoryStack(final HistoryStack source) {
		this.stack = new ArrayDeque<>();
		for (final HistoryEntry entry : source.stack) {
		    this.stack.addFirst(new HistoryEntry(entry));
		}
	    }

	    public boolean isEmpty() {
		return this.stack.isEmpty();
	    }

	    public HistoryEntry pop() {
		return this.stack.removeFirst();
	    }

	    public void push(final DungeonDataStorage i, final HistoryStatus hww) {
		final var newEntry = new HistoryEntry(i, hww);
		this.stack.addFirst(newEntry);
	    }
	}

	// Fields
	private HistoryStack undoHistory, redoHistory;
	private HistoryStatus whatWas;
	private DungeonDataStorage image;

	// Constructors
	public ImageUndoEngine() {
	    this.undoHistory = new HistoryStack();
	    this.redoHistory = new HistoryStack();
	    this.image = null;
	    this.whatWas = null;
	}

	public ImageUndoEngine(final ImageUndoEngine source) {
	    this.undoHistory = new HistoryStack(source.undoHistory);
	    this.redoHistory = new HistoryStack(source.redoHistory);
	    this.image = source.image;
	    this.whatWas = source.whatWas;
	}

	public void clearRedoHistory() {
	    this.redoHistory = new HistoryStack();
	}

	public void clearUndoHistory() {
	    this.undoHistory = new HistoryStack();
	}

	public DungeonDataStorage getImage() {
	    return this.image;
	}

	public HistoryStatus getWhatWas() {
	    return this.whatWas;
	}

	public void redo() {
	    if (!this.redoHistory.isEmpty()) {
		final var entry = this.redoHistory.pop();
		this.image = entry.getImage();
		this.whatWas = entry.getWhatWas();
	    } else {
		this.image = null;
		this.whatWas = null;
	    }
	}

	public boolean tryRedo() {
	    return !this.redoHistory.isEmpty();
	}

	public boolean tryUndo() {
	    return !this.undoHistory.isEmpty();
	}

	// Public methods
	public void undo() {
	    if (!this.undoHistory.isEmpty()) {
		final var entry = this.undoHistory.pop();
		this.image = entry.getImage();
		this.whatWas = entry.getWhatWas();
	    } else {
		this.image = null;
		this.whatWas = null;
	    }
	}

	public void updateRedoHistory(final DungeonDataStorage newImage, final HistoryStatus newWhatWas) {
	    this.redoHistory.push(newImage, newWhatWas);
	}

	public void updateUndoHistory(final DungeonDataStorage newImage, final HistoryStatus newWhatWas) {
	    this.undoHistory.push(newImage, newWhatWas);
	}
    }

    public static final DungeonLock LOCK_OBJECT = new DungeonLock();
    // Properties
    private DungeonDataStorage data;
    private DungeonDataStorage virtualData;
    private final FlagStorage visionData;
    private FlagStorage dirtyData;
    private DungeonDataStorage savedState;
    private int foundX, foundY;
    private ImageUndoEngine iue;
    private final int visionMode;
    private final int visionModeExploreRadius;

    // Constructors
    public DungeonData() {
	this.data = new DungeonDataStorage(DungeonDataBase.MIN_COLUMNS, DungeonDataBase.MIN_ROWS,
		DungeonDataBase.MIN_FLOORS, Layer.values().length);
	this.virtualData = new DungeonDataStorage(DungeonDataBase.MIN_COLUMNS, DungeonDataBase.MIN_ROWS,
		DungeonDataBase.MIN_FLOORS, DungeonConstants.NUM_VIRTUAL_LAYERS);
	this.fillVirtual();
	this.dirtyData = new FlagStorage(DungeonDataBase.MIN_COLUMNS, DungeonDataBase.MIN_ROWS,
		DungeonDataBase.MIN_FLOORS);
	this.visionData = new FlagStorage(DungeonDataBase.MIN_COLUMNS, DungeonDataBase.MIN_ROWS,
		DungeonDataBase.MIN_FLOORS);
	this.savedState = new DungeonDataStorage(DungeonDataBase.MIN_ROWS, DungeonDataBase.MIN_COLUMNS,
		DungeonDataBase.MIN_FLOORS, Layer.values().length);
	this.foundX = -1;
	this.foundY = -1;
	this.iue = new ImageUndoEngine();
	this.visionMode = VisionModes.EXPLORE_AND_LOS;
	this.visionModeExploreRadius = 2;
    }

    public DungeonData(final DungeonData source) {
	this.data = new DungeonDataStorage(source.data);
	this.virtualData = new DungeonDataStorage(source.virtualData);
	this.dirtyData = new FlagStorage(source.dirtyData);
	this.visionData = new FlagStorage(source.visionData);
	this.savedState = new DungeonDataStorage(source.savedState);
	this.foundX = source.foundX;
	this.foundY = source.foundY;
	this.iue = new ImageUndoEngine(source.iue);
	this.visionMode = source.visionMode;
	this.visionModeExploreRadius = source.visionModeExploreRadius;
    }

    public DungeonData(final int rows, final int cols, final int floors) {
	this.data = new DungeonDataStorage(cols, rows, floors, Layer.values().length);
	this.virtualData = new DungeonDataStorage(cols, rows, floors, DungeonConstants.NUM_VIRTUAL_LAYERS);
	this.fillVirtual();
	this.dirtyData = new FlagStorage(cols, rows, floors);
	this.visionData = new FlagStorage(cols, rows, floors);
	this.savedState = new DungeonDataStorage(cols, rows, floors, Layer.values().length);
	this.foundX = -1;
	this.foundY = -1;
	this.iue = new ImageUndoEngine();
	this.visionMode = VisionModes.EXPLORE_AND_LOS;
	this.visionModeExploreRadius = 2;
    }

    private boolean areCoordsInBounds(final DungeonBase dungeonBase, final int x1, final int y1, final int x2,
	    final int y2) {
	int fx1, fx2, fy1, fy2;
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    fx1 = this.normalizeColumn(x1);
	    fx2 = this.normalizeColumn(x2);
	} else {
	    fx1 = x1;
	    fx2 = x2;
	}
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    fy1 = this.normalizeRow(y1);
	    fy2 = this.normalizeRow(y2);
	} else {
	    fy1 = y1;
	    fy2 = y2;
	}
	return fx1 >= 0 && fx1 <= this.getRows() && fx2 >= 0 && fx2 <= this.getRows() && fy1 >= 0
		&& fy1 <= this.getColumns() && fy2 >= 0 && fy2 <= this.getColumns();
    }

    @Override
    public int checkForMagnetic(final DungeonBase dungeonBase, final int floor, final int centerX, final int centerY,
	    final Direction dir) {
	if (dir == Direction.EAST) {
	    return this.linearScanMagnetic(dungeonBase, centerX, centerY, floor, Direction.EAST);
	}
	if (dir == Direction.WEST) {
	    return this.linearScanMagnetic(dungeonBase, centerX, centerY, floor, Direction.WEST);
	}
	if (dir == Direction.SOUTH) {
	    return this.linearScanMagnetic(dungeonBase, centerX, centerY, floor, Direction.SOUTH);
	}
	if (dir == Direction.NORTH) {
	    return this.linearScanMagnetic(dungeonBase, centerX, centerY, floor, Direction.NORTH);
	}
	return 0;
    }

    @Override
    public int[] circularScan(final DungeonBase dungeonBase, final int xIn, final int yIn, final int zIn, final int r,
	    final String targetName, final boolean moved) {
	var xFix = xIn;
	var yFix = yIn;
	var zFix = zIn;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    xFix = this.normalizeColumn(xFix);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    yFix = this.normalizeRow(yFix);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    zFix = this.normalizeFloor(zFix);
	}
	int u, v, w;
	u = v = 0;
	// Perform the scan
	for (u = xFix - r; u <= xFix + r; u++) {
	    for (v = yFix - r; v <= yFix + r; v++) {
		for (w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, v, u, zFix, w);
			final var savedObj = obj.getSavedObject();
			String testName;
			if (!obj.isPlayer() || moved) {
			    testName = obj.getCacheName();
			} else {
			    testName = savedObj.getCacheName();
			}
			if (testName.equals(targetName)) {
			    return new int[] { v, u, zFix };
			}
		    } catch (final ArrayIndexOutOfBoundsException aioob) {
			// Do nothing
		    }
		}
	    }
	}
	return null;
    }

    @Override
    public boolean circularScanPlayer(final DungeonBase dungeonBase, final int x, final int y, final int z,
	    final int r) {
	final var tankLoc = Inconnuclear.getStuffBag().getGame().getPlayerLocation();
	var fX = x;
	var fY = y;
	var fZ = z;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    fX = this.normalizeColumn(fX);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    fY = this.normalizeRow(fY);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    fZ = this.normalizeFloor(fZ);
	}
	final var tx = tankLoc[0];
	final var ty = tankLoc[1];
	final var tz = tankLoc[2];
	return fZ == tz && Math.abs(fX - tx) <= r && Math.abs(fY - ty) <= r;
    }

    @Override
    public int[] circularScanTunnel(final DungeonBase dungeonBase, final int xIn, final int yIn, final int zIn,
	    final int r, final int tx, final int ty, final GameObject target, final boolean moved) {
	var xFix = xIn;
	var yFix = yIn;
	var zFix = zIn;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    xFix = this.normalizeColumn(xFix);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    yFix = this.normalizeRow(yFix);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    zFix = this.normalizeFloor(zFix);
	}
	int u, v, w;
	w = Layer.STATUS.ordinal();
	// Perform the scan
	for (u = xFix - r; u <= xFix + r; u++) {
	    for (v = yFix - r; v <= yFix + r; v++) {
		if (v == tx && u == ty && moved) {
		    continue;
		}
		if (v >= 0 && v < DungeonDataBase.MIN_ROWS && u >= 0 && u < DungeonDataBase.MIN_COLUMNS) {
		    final var obj = this.getCell(dungeonBase, v, u, zFix, w);
		    final var savedObj = obj.getSavedObject();
		    GameObject test;
		    if (obj.isPlayer()) {
			test = savedObj;
		    } else {
			test = obj;
		    }
		    if (target.equals(test)) {
			return new int[] { v, u, zFix };
		    }
		}
	    }
	}
	return null;
    }

    @Override
    public void clearDirtyFlags(final int floor) {
	for (var row = 0; row < this.getRows(); row++) {
	    for (var col = 0; col < this.getColumns(); col++) {
		this.dirtyData.setCell(false, col, row, floor);
	    }
	}
    }

    @Override
    public void clearRedoHistory() {
	this.iue.clearRedoHistory();
    }

    @Override
    public void clearUndoHistory() {
	this.iue.clearUndoHistory();
    }

    @Override
    public void clearVirtualGrid(final DungeonBase dungeonBase) {
	for (var row = 0; row < this.getRows(); row++) {
	    for (var col = 0; col < this.getColumns(); col++) {
		for (var floor = 0; floor < this.getFloors(); floor++) {
		    for (var layer = 0; layer < DungeonConstants.NUM_VIRTUAL_LAYERS; layer++) {
			this.setVirtualCell(dungeonBase, new GameObject(ObjectImageId.EMPTY), row, col, floor, layer);
		    }
		}
	    }
	}
    }

    @Override
    public DungeonData clone() {
	return new DungeonData(this);
    }

    @Override
    public void fill(final DungeonBase dungeonBase, final GameObject fill) {
	int y, x, z, w;
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    for (w = 0; w < Layer.values().length; w++) {
			if (w == Layer.GROUND.ordinal()) {
			    this.setCell(dungeonBase, fill, y, x, z, w);
			} else {
			    this.setCell(dungeonBase, new GameObject(ObjectImageId.EMPTY), y, x, z, w);
			}
		    }
		}
	    }
	}
    }

    @Override
    public void fillNulls(final DungeonBase dungeonBase, final GameObject fill1, final GameObject fill2,
	    final boolean was16) {
	int y, x, z, w;
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    for (w = 0; w < Layer.values().length; w++) {
			if (this.getCell(dungeonBase, y, x, z, w) == null) {
			    if (w == Layer.GROUND.ordinal()) {
				this.setCell(dungeonBase, fill1, y, x, z, w);
			    } else if (w == Layer.STATUS.ordinal() && was16 && (x >= 16 || y >= 16)) {
				this.setCell(dungeonBase, fill2, y, x, z, w);
			    } else {
				this.setCell(dungeonBase, new GameObject(ObjectImageId.EMPTY), y, x, z, w);
			    }
			}
		    }
		}
	    }
	}
    }

    @Override
    public void fillSTSNulls(final GameObject fill) {
	int y, x, z, w;
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    for (w = 0; w < Layer.values().length; w++) {
			if (this.savedState.getCell(y, x, z, w) == null) {
			    if (w == Layer.GROUND.ordinal()) {
				this.savedState.setCell(fill, y, x, z, w);
			    } else {
				this.savedState.setCell(new GameObject(ObjectImageId.EMPTY), y, x, z, w);
			    }
			}
		    }
		}
	    }
	}
    }

    @Override
    public void fillVirtual() {
	int y, x, z, w;
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    for (w = 0; w < DungeonConstants.NUM_VIRTUAL_LAYERS; w++) {
			this.virtualData.setCell(new GameObject(ObjectImageId.EMPTY), y, x, z, w);
		    }
		}
	    }
	}
    }

    @Override
    public int[] findObject(final DungeonBase dungeonBase, final int z, final String targetName) {
	// Perform the scan
	for (var x = 0; x < DungeonDataBase.MIN_COLUMNS; x++) {
	    for (var y = 0; y < DungeonDataBase.MIN_ROWS; y++) {
		for (var w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, x, y, z, w);
			final var testName = obj.getName();
			if (testName.equals(targetName)) {
			    return new int[] { x, y };
			}
		    } catch (final ArrayIndexOutOfBoundsException aioob) {
			// Do nothing
		    }
		}
	    }
	}
	return null;
    }

    @Override
    public int[] findPlayer(final DungeonBase dungeonBase, final int number) {
	final var t = new GameObject(ObjectImageId.PARTY);
	int y, x, z;
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    final var mo = this.getCell(dungeonBase, y, x, z, t.getLayer());
		    if (mo != null && t.equals(mo)) {
			return new int[] { y, x, z };
		    }
		}
	    }
	}
	return null;
    }

    @Override
    public void fullScanAllButtonClose(final DungeonBase dungeonBase, final int zIn, final GameObject source) {
	// Perform the scan
	var zFix = zIn;
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    zFix = this.normalizeFloor(zFix);
	}
	var flag = !source.isTriggered();
	for (var x = 0; x < DungeonDataBase.MIN_COLUMNS; x++) {
	    if (flag) {
		break;
	    }
	    for (var y = 0; y < DungeonDataBase.MIN_ROWS; y++) {
		if (flag) {
		    break;
		}
		final var obj = this.getCell(dungeonBase, y, x, zFix, source.getLayer());
		if (source.hasSameBoundObject(obj) && !obj.isTriggered()) {
		    flag = true;
		}
	    }
	}
	if (flag) {
	    // Scan said OK to proceed
	    final var dx = source.getBoundObjectX();
	    final var dy = source.getBoundObjectY();
	    if (!this.getCell(dungeonBase, dx, dy, zFix, source.getLayer()).getClass()
		    .equals(source.getBoundObject().getClass())) {
		this.setCell(dungeonBase, source.getBoundObject(), dx, dy, zFix, source.getLayer());
		SoundLoader.playSound(Sounds.DOOR_CLOSES);
	    }
	}
    }

    @Override
    public void fullScanAllButtonOpen(final DungeonBase dungeonBase, final int zIn, final GameObject source) {
	// Perform the scan
	var zFix = zIn;
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    zFix = this.normalizeFloor(zFix);
	}
	var flag = true;
	for (var x = 0; x < DungeonDataBase.MIN_COLUMNS; x++) {
	    if (!flag) {
		break;
	    }
	    for (var y = 0; y < DungeonDataBase.MIN_ROWS; y++) {
		if (!flag) {
		    break;
		}
		final var obj = this.getCell(dungeonBase, y, x, zFix, source.getLayer());
		if (source.hasSameBoundObject(obj) && !obj.isTriggered()) {
		    flag = false;
		}
	    }
	}
	if (flag) {
	    // Scan said OK to proceed
	    final var dx = source.getBoundObjectX();
	    final var dy = source.getBoundObjectY();
	    if (!(this.getCell(dungeonBase, dx, dy, zFix, source.getLayer()).getId() != ObjectImageId.GRASS)) {
		this.setCell(dungeonBase, new GameObject(ObjectImageId.GRASS), dx, dy, zFix, source.getLayer());
		SoundLoader.playSound(Sounds.DOOR_OPENS);
	    }
	}
    }

    @Override
    public void fullScanButtonBind(final DungeonBase dungeonBase, final int dx, final int dy, final int zIn,
	    final GameObject source) {
	// Perform the scan
	var z = zIn;
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    z = this.normalizeFloor(z);
	}
	for (var x = 0; x < DungeonDataBase.MIN_COLUMNS; x++) {
	    for (var y = 0; y < DungeonDataBase.MIN_ROWS; y++) {
		final var obj = this.getCell(dungeonBase, x, y, z, source.getLayer());
		if (source.getClass().equals(obj.getBoundObject().getClass())) {
		    obj.setBoundObjectX(dx);
		    obj.setBoundObjectY(dy);
		    obj.setTriggered(false);
		}
	    }
	}
	for (var x = 0; x < DungeonDataBase.MIN_COLUMNS; x++) {
	    for (var y = 0; y < DungeonDataBase.MIN_ROWS; y++) {
		final var obj = this.getCell(dungeonBase, x, y, z, source.getLayer());
		if (source.getClass().equals(obj.getClass())) {
		    this.setCell(dungeonBase, new GameObject(ObjectImageId.GRASS), x, y, z, source.getLayer());
		}
	    }
	}
    }

    @Override
    public void fullScanButtonCleanup(final DungeonBase dungeonBase, final int px, final int py, final int zIn,
	    final GameObject button) {
	// Perform the scan
	var zFix = zIn;
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    zFix = this.normalizeFloor(zFix);
	}
	for (var x = 0; x < DungeonDataBase.MIN_COLUMNS; x++) {
	    for (var y = 0; y < DungeonDataBase.MIN_ROWS; y++) {
		if (x == px && y == py) {
		    continue;
		}
		final var obj = this.getCell(dungeonBase, x, y, zFix, button.getLayer());
		if (obj.hasSameBoundObject(button)) {
		    this.setCell(dungeonBase, new GameObject(ObjectImageId.GRASS), x, y, zFix, button.getLayer());
		}
	    }
	}
    }

    @Override
    public void fullScanFindButtonLostDoor(final DungeonBase dungeonBase, final int zIn, final GameObject door) {
	// Perform the scan
	var zFix = zIn;
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    zFix = this.normalizeFloor(zFix);
	}
	for (var x = 0; x < DungeonDataBase.MIN_COLUMNS; x++) {
	    for (var y = 0; y < DungeonDataBase.MIN_ROWS; y++) {
		final var obj = this.getCell(dungeonBase, x, y, zFix, door.getLayer());
		if (obj.boundToSameObject(door)) {
		    obj.setTriggered(true);
		    return;
		}
	    }
	}
    }

    @Override
    public void fullScanFreezeGround(final DungeonBase dungeonBase) {
	// Perform the scan
	for (var x = 0; x < DungeonDataBase.MIN_COLUMNS; x++) {
	    for (var y = 0; y < DungeonDataBase.MIN_ROWS; y++) {
		for (var z = 0; z < this.getFloors(); z++) {
		    final var obj = this.getCell(dungeonBase, y, x, z, Layer.GROUND.ordinal());
		    if (!(obj.getId() != ObjectImageId.GRASS)) {
			Inconnuclear.getStuffBag().getGame();
			// Freeze the ground
			Game.morph(obj.changesToOnExposure(Material.ICE), y, x, z, Layer.GROUND.ordinal());
		    }
		}
	    }
	}
    }

    private void generateOneMonster(final DungeonBase dungeonBase) {
	final var row = new RandomRange(0, this.getRows() - 1);
	final var column = new RandomRange(0, this.getColumns() - 1);
	int randomRow, randomColumn;
	randomRow = row.generate();
	randomColumn = column.generate();
	var currObj = this.getCell(dungeonBase, randomRow, randomColumn, 0, Layer.STATUS.ordinal());
	if (!currObj.isSolid()) {
	    final GameObject m = new GameObject(ObjectImageId._CREATURE);
	    m.setSavedObject(currObj);
	    this.setCell(dungeonBase, m, randomRow, randomColumn, 0, Layer.STATUS.ordinal());
	} else {
	    while (currObj.isSolid()) {
		randomRow = row.generate();
		randomColumn = column.generate();
		currObj = this.getCell(dungeonBase, randomRow, randomColumn, 0, Layer.STATUS.ordinal());
	    }
	    final GameObject m = new GameObject(ObjectImageId._CREATURE);
	    m.setSavedObject(currObj);
	    this.setCell(dungeonBase, m, randomRow, randomColumn, 0, Layer.STATUS.ordinal());
	}
    }

    @Override
    public GameObject getCell(final DungeonBase dungeonBase, final int row, final int col, final int floor,
	    final int layer) {
	var fR = row;
	var fC = col;
	var fF = floor;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    fC = this.normalizeColumn(fC);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    fR = this.normalizeRow(fR);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    fF = this.normalizeFloor(fF);
	}
	return this.data.getCell(fC, fR, fF, layer);
    }

    @Override
    public int getColumns() {
	return this.data.getShape()[0];
    }

    @Override
    public int getFloors() {
	return this.data.getShape()[2];
    }

    @Override
    public int getRows() {
	return this.data.getShape()[1];
    }

    @Override
    public GameObject getVirtualCell(final DungeonBase dungeonBase, final int row, final int col, final int floor,
	    final int layer) {
	var fR = row;
	var fC = col;
	var fF = floor;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    fC = this.normalizeColumn(fC);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    fR = this.normalizeRow(fR);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    fF = this.normalizeFloor(fF);
	}
	return this.virtualData.getCell(fC, fR, fF, layer);
    }

    @Override
    public HistoryStatus getWhatWas() {
	return this.iue.getWhatWas();
    }

    @Override
    public boolean isCellDirty(final DungeonBase dungeonBase, final int row, final int col, final int floor) {
	var fR = row;
	var fC = col;
	var fF = floor;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    fC = this.normalizeColumn(fC);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    fR = this.normalizeRow(fR);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    fF = this.normalizeFloor(fF);
	}
	return this.dirtyData.getCell(fC, fR, fF);
    }

    @Override
    public boolean isSquareVisible(final DungeonBase dungeonBase, final int x1, final int y1, final int x2,
	    final int y2, final int zp) {
	if (this.visionMode == VisionModes.NONE) {
	    return true;
	}
	var result = false;
	if ((this.visionMode | VisionModes.EXPLORE) == this.visionMode) {
	    result = result || this.isSquareVisibleExplore(dungeonBase, x2, y2);
	    if (result && (this.visionMode | VisionModes.LOS) == this.visionMode) {
		if (this.areCoordsInBounds(dungeonBase, x1, y1, x2, y2)) {
		    // In bounds
		    result = result || this.isSquareVisibleLOS(dungeonBase, x1, y1, x2, y2, zp);
		} else {
		    // Out of bounds
		    result = result && this.isSquareVisibleLOS(dungeonBase, x1, y1, x2, y2, zp);
		}
	    }
	} else if (this.areCoordsInBounds(dungeonBase, x1, y1, x2, y2)) {
	    // In bounds
	    result = result || this.isSquareVisibleLOS(dungeonBase, x1, y1, x2, y2, zp);
	} else {
	    // Out of bounds
	    result = result && this.isSquareVisibleLOS(dungeonBase, x1, y1, x2, y2, zp);
	}
	return result;
    }

    private boolean isSquareVisibleExplore(final DungeonBase dungeonBase, final int x2, final int y2) {
	int fx2, fy2;
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    fx2 = this.normalizeColumn(x2);
	} else {
	    fx2 = x2;
	}
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    fy2 = this.normalizeRow(y2);
	} else {
	    fy2 = y2;
	}
	try {
	    return this.visionData.getCell(fx2, fy2);
	} catch (final ArrayIndexOutOfBoundsException aioobe) {
	    return true;
	}
    }

    private boolean isSquareVisibleLOS(final DungeonBase dungeonBase, final int x1, final int y1, final int x2,
	    final int y2, final int zp) {
	int fx1, fx2, fy1, fy2;
	fx1 = x1;
	fx2 = x2;
	fy1 = y1;
	fy2 = y2;
	final var dx = Math.abs(fx2 - fx1);
	final var dy = Math.abs(fy2 - fy1);
	int sx, sy;
	if (fx1 < fx2) {
	    sx = 1;
	} else {
	    sx = -1;
	}
	if (fy1 < fy2) {
	    sy = 1;
	} else {
	    sy = -1;
	}
	var err = dx - dy;
	int e2;
	do {
	    if (fx1 == fx2 && fy1 == fy2) {
		break;
	    }
	    // Does object block LOS?
	    try {
		final var obj = this.getCell(dungeonBase, fx1, fy1, zp, Layer.STATUS.ordinal());
		// This object blocks LOS
		if (obj.isSightBlocking() && (fx1 != x1 || fy1 != y1)) {
		    return false;
		}
	    } catch (final ArrayIndexOutOfBoundsException aioobe) {
		// Void blocks LOS
		return false;
	    }
	    e2 = 2 * err;
	    if (e2 > -dy) {
		err = err - dy;
		fx1 = fx1 + sx;
	    }
	    if (e2 < dx) {
		err = err + dx;
		fy1 = fy1 + sy;
	    }
	} while (true);
	// No objects block LOS
	return true;
    }

    @Override
    public boolean linearScan(final DungeonBase dungeonBase, final int xIn, final int yIn, final int zIn,
	    final Direction d) {
	// Perform the scan
	var xFix = xIn;
	var yFix = yIn;
	var zFix = zIn;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    xFix = this.normalizeColumn(xFix);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    yFix = this.normalizeRow(yFix);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    zFix = this.normalizeFloor(zFix);
	}
	int u, w;
	if (d == Direction.NORTH) {
	    final GameObject tank = Inconnuclear.getStuffBag().getGame().getPlayer();
	    if (tank.getSavedObject().isSolid()) {
		return false;
	    }
	    for (u = yFix - 1; u >= 0; u--) {
		for (w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, xFix, u, zFix, w);
			if (obj.canShoot()) {
			    final var unres = DirectionResolver.unresolve(obj.getDirection());
			    final var invert = DirectionResolver.resolveInvert(unres[0], unres[1]);
			    if (d == invert) {
				this.foundX = xFix;
				this.foundY = u;
				return true;
			    }
			}
			if (obj.isSolid()) {
			    return false;
			}
		    } catch (final ArrayIndexOutOfBoundsException aioobe) {
			return false;
		    }
		}
	    }
	    return false;
	}
	if (d == Direction.SOUTH) {
	    final GameObject tank = Inconnuclear.getStuffBag().getGame().getPlayer();
	    if (tank.getSavedObject().isSolid()) {
		return false;
	    }
	    for (u = yFix + 1; u < 24; u++) {
		for (w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, xFix, u, zFix, w);
			if (obj.canShoot()) {
			    final var unres = DirectionResolver.unresolve(obj.getDirection());
			    final var invert = DirectionResolver.resolveInvert(unres[0], unres[1]);
			    if (d == invert) {
				this.foundX = xFix;
				this.foundY = u;
				return true;
			    }
			}
			if (obj.isSolid()) {
			    return false;
			}
		    } catch (final ArrayIndexOutOfBoundsException aioobe) {
			return false;
		    }
		}
	    }
	} else if (d == Direction.WEST) {
	    final GameObject tank = Inconnuclear.getStuffBag().getGame().getPlayer();
	    if (tank.getSavedObject().isSolid()) {
		return false;
	    }
	    for (u = xFix - 1; u >= 0; u--) {
		for (w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, u, yFix, zFix, w);
			if (obj.canShoot()) {
			    final var unres = DirectionResolver.unresolve(obj.getDirection());
			    final var invert = DirectionResolver.resolveInvert(unres[0], unres[1]);
			    if (d == invert) {
				this.foundX = u;
				this.foundY = yFix;
				return true;
			    }
			}
			if (obj.isSolid()) {
			    return false;
			}
		    } catch (final ArrayIndexOutOfBoundsException aioobe) {
			return false;
		    }
		}
	    }
	} else if (d == Direction.EAST) {
	    final GameObject tank = Inconnuclear.getStuffBag().getGame().getPlayer();
	    if (tank.getSavedObject().isSolid()) {
		return false;
	    }
	    for (u = xFix + 1; u < 24; u++) {
		for (w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, u, yFix, zFix, w);
			if (obj.canShoot()) {
			    final var unres = DirectionResolver.unresolve(obj.getDirection());
			    final var invert = DirectionResolver.resolveInvert(unres[0], unres[1]);
			    if (d == invert) {
				this.foundX = u;
				this.foundY = yFix;
				return true;
			    }
			}
			if (obj.isSolid()) {
			    return false;
			}
		    } catch (final ArrayIndexOutOfBoundsException aioobe) {
			return false;
		    }
		}
	    }
	}
	return false;
    }

    @Override
    public int linearScanMagnetic(final DungeonBase dungeonBase, final int xIn, final int yIn, final int zIn,
	    final Direction d) {
	// Perform the scan
	var xFix = xIn;
	var yFix = yIn;
	var zFix = zIn;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    xFix = this.normalizeColumn(xFix);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    yFix = this.normalizeRow(yFix);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    zFix = this.normalizeFloor(zFix);
	}
	int u, w;
	if (d == Direction.NORTH) {
	    for (u = yFix - 1; u >= 0; u--) {
		for (w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, xFix, u, zFix, w);
			if (obj.getMaterial() == Material.MAGNETIC) {
			    return yFix - u - 1;
			}
			if (obj.isSolid()) {
			    return 0;
			}
		    } catch (final ArrayIndexOutOfBoundsException aioobe) {
			return 0;
		    }
		}
	    }
	    return 0;
	}
	if (d == Direction.SOUTH) {
	    for (u = yFix + 1; u < 24; u++) {
		for (w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, xFix, u, zFix, w);
			if (obj.getMaterial() == Material.MAGNETIC) {
			    return u - yFix - 1;
			}
			if (obj.isSolid()) {
			    return 0;
			}
		    } catch (final ArrayIndexOutOfBoundsException aioobe) {
			return 0;
		    }
		}
	    }
	} else if (d == Direction.WEST) {
	    for (u = xFix - 1; u >= 0; u--) {
		for (w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, u, yFix, zFix, w);
			if (obj.getMaterial() == Material.MAGNETIC) {
			    return xFix - u - 1;
			}
			if (obj.isSolid()) {
			    return 0;
			}
		    } catch (final ArrayIndexOutOfBoundsException aioobe) {
			return 0;
		    }
		}
	    }
	} else if (d == Direction.EAST) {
	    for (u = xFix + 1; u < 24; u++) {
		for (w = 0; w < Layer.values().length; w++) {
		    try {
			final var obj = this.getCell(dungeonBase, u, yFix, zFix, w);
			if (obj.getMaterial() == Material.MAGNETIC) {
			    return u - xFix - 1;
			}
			if (obj.isSolid()) {
			    return 0;
			}
		    } catch (final ArrayIndexOutOfBoundsException aioobe) {
			return 0;
		    }
		}
	    }
	}
	return 0;
    }

    @Override
    public void markAsDirty(final DungeonBase dungeonBase, final int row, final int col, final int floor) {
	var fR = row;
	var fC = col;
	var fF = floor;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    fC = this.normalizeColumn(fC);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    fR = this.normalizeRow(fR);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    fF = this.normalizeFloor(fF);
	}
	this.dirtyData.setCell(true, fC, fR, fF);
    }

    @Override
    public void postBattle(final DungeonBase dungeonBase, final GameObject m, final int xLoc, final int yLoc,
	    final boolean player) {
	final var saved = m.getSavedObject();
	if (!player) {
	    this.setCell(dungeonBase, saved, xLoc, yLoc, 0, Layer.STATUS.ordinal());
	}
	this.generateOneMonster(dungeonBase);
    }

    @Override
    public DungeonDataBase readData(final DungeonBase dungeonBase, final DataIOReader reader, final int formatVersion)
	    throws IOException {
	if (FileFormats.isFormatVersionValidGeneration6(formatVersion)) {
	    int y, x, z, w, dungeonSizeX, dungeonSizeY, dungeonSizeZ;
	    dungeonSizeX = reader.readInt();
	    dungeonSizeY = reader.readInt();
	    dungeonSizeZ = reader.readInt();
	    final var lt = new DungeonData();
	    lt.resize(dungeonBase, dungeonSizeZ, new GameObject(ObjectImageId.GRASS));
	    for (x = 0; x < dungeonSizeX; x++) {
		for (y = 0; y < dungeonSizeY; y++) {
		    for (z = 0; z < dungeonSizeZ; z++) {
			for (w = 0; w < Layer.values().length; w++) {
			    lt.setCell(dungeonBase, GameObject.read(reader), y, x, z, w);
			}
		    }
		}
	    }
	    // Fill nulls
	    lt.fillNulls(dungeonBase, new GameObject(ObjectImageId.GRASS), null, false);
	    lt.fillVirtual();
	    return lt;
	}
	throw new IOException(Strings.error(ErrorString.UNKNOWN_FILE_FORMAT));
    }

    @Override
    public void readSavedState(final DataIOReader reader, final int formatVersion) throws IOException {
	if (FileFormats.isFormatVersionValidGeneration6(formatVersion)) {
	    int y, x, z, w, saveSizeX, saveSizeY, saveSizeZ;
	    saveSizeX = reader.readInt();
	    saveSizeY = reader.readInt();
	    saveSizeZ = reader.readInt();
	    this.savedState = new DungeonDataStorage(saveSizeY, saveSizeX, saveSizeZ, Layer.values().length);
	    for (x = 0; x < saveSizeX; x++) {
		for (y = 0; y < saveSizeY; y++) {
		    for (z = 0; z < saveSizeZ; z++) {
			for (w = 0; w < Layer.values().length; w++) {
			    this.savedState.setCell(GameObject.read(reader), y, x, z, w);
			}
		    }
		}
	    }
	    if (saveSizeX != DungeonDataBase.MIN_COLUMNS || saveSizeY != DungeonDataBase.MIN_ROWS) {
		this.resizeSavedState(saveSizeZ, new GameObject(ObjectImageId.GRASS));
	    }
	} else {
	    throw new IOException(Strings.error(ErrorString.UNKNOWN_FILE_FORMAT));
	}
    }

    @Override
    public void redo(final DungeonBase dungeonBase) {
	this.iue.redo();
	this.data = this.iue.getImage();
	this.setAllDirtyFlags();
	this.clearVirtualGrid(dungeonBase);
    }

    @Override
    public void resetHistoryEngine() {
	this.iue = new ImageUndoEngine();
    }

    @Override
    public void resetVisibleSquares(final int floor) {
	for (var row = 0; row < this.getRows(); row++) {
	    for (var col = 0; col < this.getColumns(); col++) {
		this.visionData.setCell(false, row, col, floor);
	    }
	}
    }

    @Override
    public void resize(final DungeonBase dungeonBase, final int zIn, final GameObject nullFill) {
	final var x = DungeonDataBase.MIN_ROWS;
	final var y = DungeonDataBase.MIN_COLUMNS;
	var z = zIn;
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    z = this.normalizeFloor(z);
	}
	// Allocate temporary storage array
	final var tempStorage = new DungeonDataStorage(y, x, z, Layer.values().length);
	// Copy existing maze into temporary array
	int u, v, w, t;
	for (u = 0; u < y; u++) {
	    for (v = 0; v < x; v++) {
		for (w = 0; w < z; w++) {
		    for (t = 0; t < Layer.values().length; t++) {
			try {
			    tempStorage.setCell(this.getCell(dungeonBase, v, u, w, t), u, v, w, t);
			} catch (final ArrayIndexOutOfBoundsException aioob) {
			    // Do nothing
			}
		    }
		}
	    }
	}
	// Set the current data to the temporary array
	this.data = tempStorage;
	this.virtualData = new DungeonDataStorage(x, y, z, DungeonConstants.NUM_VIRTUAL_LAYERS);
	this.dirtyData = new FlagStorage(x, y, z);
	// Fill any blanks
	this.fillNulls(dungeonBase, nullFill, null, false);
	// Fix saved tower state
	this.resizeSavedState(z, nullFill);
    }

    @Override
    public void resizeSavedState(final int z, final GameObject nullFill) {
	final var x = DungeonDataBase.MIN_ROWS;
	final var y = DungeonDataBase.MIN_COLUMNS;
	// Allocate temporary storage array
	final var tempStorage = new DungeonDataStorage(y, x, z, Layer.values().length);
	// Copy existing maze into temporary array
	int u, v, w, t;
	for (u = 0; u < y; u++) {
	    for (v = 0; v < x; v++) {
		for (w = 0; w < z; w++) {
		    for (t = 0; t < Layer.values().length; t++) {
			try {
			    tempStorage.setCell(this.savedState.getCell(v, u, w, t), u, v, w, t);
			} catch (final ArrayIndexOutOfBoundsException aioob) {
			    // Do nothing
			}
		    }
		}
	    }
	}
	// Set the current data to the temporary array
	this.savedState = tempStorage;
	// Fill any blanks
	this.fillSTSNulls(nullFill);
    }

    @Override
    public void restore(final DungeonBase dungeonBase) {
	int y, x, z, w;
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    for (w = 0; w < Layer.values().length; w++) {
			this.setCell(dungeonBase, new GameObject(this.savedState.getCell(x, y, z, w)), y, x, z, w);
		    }
		}
	    }
	}
    }

    @Override
    public void save(final DungeonBase dungeonBase) {
	int y, x, z, w;
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    for (w = 0; w < Layer.values().length; w++) {
			this.savedState.setCell(new GameObject(this.savedState.getCell(x, y, z, w)), x, y, z, w);
		    }
		}
	    }
	}
    }

    @Override
    public void setAllDirtyFlags() {
	for (var floor = 0; floor < this.getFloors(); floor++) {
	    this.setDirtyFlags(floor);
	}
    }

    @Override
    public void setCell(final DungeonBase dungeonBase, final GameObject mo, final int row, final int col,
	    final int floor, final int layer) {
	var fR = row;
	var fC = col;
	var fF = floor;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    fC = this.normalizeColumn(fC);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    fR = this.normalizeRow(fR);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    fF = this.normalizeFloor(fF);
	}
	this.data.setCell(mo, fC, fR, fF, layer);
	this.dirtyData.setCell(true, fC, fR, fF);
    }

    @Override
    public void setDirtyFlags(final int floor) {
	for (var row = 0; row < this.getRows(); row++) {
	    for (var col = 0; col < this.getColumns(); col++) {
		this.dirtyData.setCell(true, col, row, floor);
	    }
	}
    }

    @Override
    public void setVirtualCell(final DungeonBase dungeonBase, final GameObject mo, final int row, final int col,
	    final int floor, final int layer) {
	var fR = row;
	var fC = col;
	var fF = floor;
	if (dungeonBase.isVerticalWraparoundEnabled()) {
	    fC = this.normalizeColumn(fC);
	}
	if (dungeonBase.isHorizontalWraparoundEnabled()) {
	    fR = this.normalizeRow(fR);
	}
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    fF = this.normalizeFloor(fF);
	}
	this.virtualData.setCell(mo, fC, fR, fF, layer);
	this.dirtyData.setCell(true, fC, fR, fF);
    }

    @Override
    public void tickTimers(final DungeonBase dungeonBase) {
	// Tick all object timers
	int y, x, z, w;
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    for (w = 0; w < Layer.values().length; w++) {
			final var mo = this.getCell(dungeonBase, y, x, 0, Layer.STATUS.ordinal());
			if (mo != null) {
			    mo.tickTimer();
			}
		    }
		}
	    }
	}
    }

    @Override
    public void tickTimers(final DungeonBase dungeonBase, final int floor, final int actionType) {
	var floorFix = floor;
	if (dungeonBase.isThirdDimensionWraparoundEnabled()) {
	    floorFix = this.normalizeFloor(floorFix);
	}
	int x, y, z, w;
	var game = Inconnuclear.getStuffBag().getGame();
	var px = game.getPlayerLocation()[0];
	var py = game.getPlayerLocation()[1];
	// Tick all DungeonObject timers
	GameObject.checkTunnels(px, py, dungeonBase);
	for (z = Direction.NORTH.ordinal(); z <= Direction.NORTH_WEST.ordinal(); z += 2) {
	    for (x = 0; x < this.getColumns(); x++) {
		for (y = 0; y < this.getRows(); y++) {
		    for (w = 0; w < Layer.values().length; w++) {
			final var mo = this.getCell(dungeonBase, y, x, floorFix, w);
			if (mo != null && z == Direction.NORTH.ordinal()) {
			    // Handle objects waiting for a tunnel to open
			    final var saved = mo.getSavedObject();
			    final var color = saved.getColor();
			    if (mo.waitingOnTunnel() && !GameObject.tunnelsFull(color)) {
				mo.setWaitingOnTunnel(false);
				saved.pushIntoAction(mo, y, x, floorFix);
			    }
			    if (GameObject.tunnelsFull(color)) {
				mo.setWaitingOnTunnel(true);
			    }
			    mo.tickTimer();
			}
		    }
		}
	    }
	}
    }

    @Override
    public boolean tryRedo() {
	return this.iue.tryRedo();
    }

    @Override
    public boolean tryUndo() {
	return this.iue.tryUndo();
    }

    @Override
    public void undo(final DungeonBase dungeonBase) {
	this.iue.undo();
	this.data = this.iue.getImage();
	this.setAllDirtyFlags();
	this.clearVirtualGrid(dungeonBase);
    }

    @Override
    public void updateMonsterPosition(final DungeonBase dungeonBase, final Direction move, final int xLoc,
	    final int yLoc, final GameObject monster, final int pi) {
	final var app = Inconnuclear.getStuffBag();
	final var dirMove = DirectionResolver.unresolve(move);
	final var pLocX = dungeonBase.getPlayerLocationX(pi);
	final var pLocY = dungeonBase.getPlayerLocationY(pi);
	try {
	    final var there = this.getCell(dungeonBase, xLoc + dirMove[0], yLoc + dirMove[1], 0,
		    Layer.STATUS.ordinal());
	    final var ground = this.getCell(dungeonBase, xLoc + dirMove[0], yLoc + dirMove[1], 0,
		    Layer.GROUND.ordinal());
	    if (!there.isSolid() && there.getId() != ObjectImageId.TUNNEL) {
		if (DungeonBase.radialScan(xLoc, yLoc, 0, pLocX, pLocY)) {
		    if (app.getMode() != StuffBag.STATUS_BATTLE) {
			app.getGame().stopMovement();
			if (monster.isFinalBoss()) {
			    app.getBattle().doFinalBossBattle();
			} else {
			    app.getBattle().doBattle();
			    this.postBattle(dungeonBase, monster, xLoc, yLoc, false);
			}
		    }
		} else {
		    // Move the monster
		    this.setCell(dungeonBase, monster.getSavedObject(), xLoc, yLoc, 0, Layer.STATUS.ordinal());
		    monster.setSavedObject(there);
		    this.setCell(dungeonBase, monster, xLoc + dirMove[0], yLoc + dirMove[1], 0, Layer.STATUS.ordinal());
		    // Does the ground have friction?
		    if (!ground.hasFriction()) {
			// No - move the monster again
			this.updateMonsterPosition(dungeonBase, move, xLoc + dirMove[0], yLoc + dirMove[1], monster,
				pi);
		    }
		}
	    }
	} catch (final ArrayIndexOutOfBoundsException aioob) {
	    // Do nothing
	}
    }

    @Override
    public void updateRedoHistory(final HistoryStatus whatWas) {
	this.iue.updateRedoHistory(new DungeonDataStorage(this.data), whatWas);
    }

    @Override
    public void updateUndoHistory(final HistoryStatus whatWas) {
	this.iue.updateUndoHistory(new DungeonDataStorage(this.data), whatWas);
    }

    @Override
    public void updateVisibleSquares(final DungeonBase dungeonBase, final int xp, final int yp, final int zp) {
	if ((this.visionMode | VisionModes.EXPLORE) == this.visionMode) {
	    for (var x = xp - this.visionModeExploreRadius; x <= xp + this.visionModeExploreRadius; x++) {
		for (var y = yp - this.visionModeExploreRadius; y <= yp + this.visionModeExploreRadius; y++) {
		    int fx, fy;
		    if (dungeonBase.isHorizontalWraparoundEnabled()) {
			fx = this.normalizeColumn(x);
		    } else {
			fx = x;
		    }
		    if (dungeonBase.isVerticalWraparoundEnabled()) {
			fy = this.normalizeRow(y);
		    } else {
			fy = y;
		    }
		    var alreadyVisible = false;
		    try {
			alreadyVisible = this.visionData.getCell(fx, fy);
		    } catch (final ArrayIndexOutOfBoundsException aioobe) {
			// Ignore
		    }
		    if (!alreadyVisible) {
			if ((this.visionMode | VisionModes.LOS) == this.visionMode) {
			    if (this.isSquareVisibleLOS(dungeonBase, x, y, xp, yp, zp)) {
				try {
				    this.visionData.setCell(true, fx, fy);
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
				    // Ignore
				}
			    }
			} else {
			    try {
				this.visionData.setCell(true, fx, fy);
			    } catch (final ArrayIndexOutOfBoundsException aioobe) {
				// Ignore
			    }
			}
		    }
		}
	    }
	}
    }

    @Override
    public void writeData(final DungeonBase dungeonBase, final DataIOWriter writer) throws IOException {
	int y, x, z, w;
	writer.writeInt(this.getColumns());
	writer.writeInt(this.getRows());
	writer.writeInt(this.getFloors());
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    for (w = 0; w < Layer.values().length; w++) {
			this.getCell(dungeonBase, y, x, z, w).write(writer);
		    }
		}
	    }
	}
    }

    @Override
    public void writeSavedState(final DataIOWriter writer) throws IOException {
	int y, x, z, w;
	writer.writeInt(this.getColumns());
	writer.writeInt(this.getRows());
	writer.writeInt(this.getFloors());
	for (x = 0; x < this.getColumns(); x++) {
	    for (y = 0; y < this.getRows(); y++) {
		for (z = 0; z < this.getFloors(); z++) {
		    for (w = 0; w < Layer.values().length; w++) {
			this.savedState.getCell(y, x, z, w).write(writer);
		    }
		}
	    }
	}
    }
}
