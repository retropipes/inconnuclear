/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.dungeon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.retropipes.diane.LocaleUtils;
import org.retropipes.diane.direction.Direction;
import org.retropipes.diane.direction.DirectionResolver;
import org.retropipes.diane.fileio.DataIOFactory;
import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.diane.fileio.DataMode;
import org.retropipes.diane.fileio.utility.FileUtilities;
import org.retropipes.diane.random.RandomLongRange;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.dungeon.base.DungeonDataBase;
import org.retropipes.inconnuclear.dungeon.base.HistoryStatus;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.files.AbstractPrefixIO;
import org.retropipes.inconnuclear.files.AbstractSuffixIO;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Difficulty;
import org.retropipes.inconnuclear.locale.ErrorString;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;
import org.retropipes.inconnuclear.settings.Settings;
import org.retropipes.inconnuclear.utility.DirectionRotator;
import org.retropipes.inconnuclear.utility.FileFormats;

public class Dungeon extends DungeonBase {
    // Properties
    private DungeonData dungeonData;
    private DungeonData clipboard;
    private DungeonLevelInfo activeLevelInfo;
    private int levelCount;
    private int startLevel;
    private int activeLevel;
    private int activeEra;
    private int startEra;
    private String basePath;
    private AbstractPrefixIO prefixHandler;
    private AbstractSuffixIO suffixHandler;
    private String musicFilename;
    private boolean moveShootAllowed;
    private final ArrayList<DungeonLevelInfo> levelInfoData;
    private ArrayList<String> levelInfoList;

    // Constructors
    public Dungeon() throws IOException {
	this.dungeonData = null;
	this.clipboard = null;
	this.levelCount = 0;
	this.activeLevel = 0;
	this.activeEra = 0;
	this.prefixHandler = null;
	this.suffixHandler = null;
	this.musicFilename = "null";
	this.moveShootAllowed = false;
	this.levelInfoData = new ArrayList<>();
	this.levelInfoList = new ArrayList<>();
	final var random = new RandomLongRange(0, Long.MAX_VALUE).generate();
	final var randomID = Long.toHexString(random);
	this.basePath = System.getProperty(Strings.untranslated(Untranslated.TEMP_DIR)) + File.separator
		+ Strings.untranslated(Untranslated.PROGRAM_NAME) + File.separator + randomID;
	final var base = new File(this.basePath);
	final var res = base.mkdirs();
	if (!res) {
	    throw new IOException(Strings.error(ErrorString.TEMP_DIR));
	}
    }

    @Override
    public boolean addFixedSizeLevel(final int rows, final int cols, final int floors) {
	if (this.levelCount >= DungeonBase.MAX_LEVELS) {
	    return false;
	}
	if (this.dungeonData != null) {
	    try (var writer = this.getLevelWriter()) {
		// Save old level
		this.writeDungeonLevel(writer);
		writer.close();
	    } catch (final IOException io) {
		// Ignore
	    }
	}
	// Add all eras for the new level
	final var saveEra = this.activeEra;
	this.dungeonData = new DungeonData(rows, cols, floors);
	for (var e = 0; e < DungeonBase.ERA_COUNT; e++) {
	    this.switchEra(e);
	    this.dungeonData = new DungeonData();
	}
	this.switchEra(saveEra);
	// Clean up
	this.levelCount++;
	this.activeLevel = this.levelCount - 1;
	this.levelInfoData.add(new DungeonLevelInfo());
	this.levelInfoList.add(this.generateCurrentLevelInfo());
	return true;
    }

    @Override
    public boolean addLevel() {
	if (this.levelCount >= DungeonBase.MAX_LEVELS) {
	    return false;
	}
	if (this.dungeonData != null) {
	    try (var writer = this.getLevelWriter()) {
		// Save old level
		this.writeDungeonLevel(writer);
		writer.close();
	    } catch (final IOException io) {
		// Ignore
	    }
	}
	// Add all eras for the new level
	final var saveEra = this.activeEra;
	this.dungeonData = new DungeonData();
	for (var e = 0; e < DungeonBase.ERA_COUNT; e++) {
	    this.switchEra(e);
	    this.dungeonData = new DungeonData();
	}
	this.switchEra(saveEra);
	// Clean up
	this.levelCount++;
	this.activeLevel = this.levelCount - 1;
	this.levelInfoData.add(new DungeonLevelInfo());
	this.levelInfoList.add(this.generateCurrentLevelInfo());
	return true;
    }

    @Override
    public int checkForMagnetic(final int floor, final int centerX, final int centerY, final Direction dir) {
	return this.dungeonData.checkForMagnetic(this, floor, centerX, centerY, dir);
    }

    @Override
    public int[] circularScan(final int x, final int y, final int z, final int maxR, final String targetName,
	    final boolean moved) {
	return this.dungeonData.circularScan(this, x, y, z, maxR, targetName, moved);
    }

    @Override
    public boolean circularScanPlayer(final int x, final int y, final int z, final int maxR) {
	return this.dungeonData.circularScanPlayer(this, x, y, z, maxR);
    }

    @Override
    public int[] circularScanTunnel(final int x, final int y, final int z, final int maxR, final int tx, final int ty,
	    final GameObject target, final boolean moved) {
	return this.dungeonData.circularScanTunnel(this, x, y, z, maxR, tx, ty, target, moved);
    }

    @Override
    public void clearDirtyFlags(final int floor) {
	this.dungeonData.clearDirtyFlags(floor);
    }

    @Override
    public void clearVirtualGrid() {
	this.dungeonData.clearVirtualGrid(this);
    }

    @Override
    public Direction computeFinalBossMoveDirection(final int locX, final int locY, final int locZ, final int pi) {
	final var px = this.getPlayerLocationX(pi);
	final var py = this.getPlayerLocationY(pi);
	final var relX = px - locX;
	final var relY = py - locY;
	var moveX = 0;
	var moveY = 0;
	if (relX != 0) {
	    moveX = relX / Math.abs(relX);
	}
	if (relY != 0) {
	    moveY = relY / Math.abs(relY);
	}
	final var canMove = !this.getCell(locX + moveX, locY + moveY, locZ, Layer.STATUS.ordinal()).isSolid();
	if (canMove) {
	    return DirectionResolver.resolve(moveX, moveY);
	}
	final var moveX1L = DirectionRotator.rotate45LeftX(moveX, moveY);
	final var moveY1L = DirectionRotator.rotate45LeftY(moveX, moveY);
	final var canMove1L = !this.getCell(locX + moveX1L, locY + moveY1L, locZ, Layer.STATUS.ordinal()).isSolid();
	if (canMove1L) {
	    return DirectionResolver.resolve(moveX1L, moveY1L);
	}
	final var moveX1R = DirectionRotator.rotate45RightX(moveX, moveY);
	final var moveY1R = DirectionRotator.rotate45RightY(moveX, moveY);
	final var canMove1R = !this.getCell(locX + moveX1R, locY + moveY1R, locZ, Layer.STATUS.ordinal()).isSolid();
	if (canMove1R) {
	    return DirectionResolver.resolve(moveX1R, moveY1R);
	}
	final var moveX2L = DirectionRotator.rotate45LeftX(moveX1L, moveY1L);
	final var moveY2L = DirectionRotator.rotate45LeftY(moveX1L, moveY1L);
	final var canMove2L = !this.getCell(locX + moveX2L, locY + moveY2L, locZ, Layer.STATUS.ordinal()).isSolid();
	if (canMove2L) {
	    return DirectionResolver.resolve(moveX2L, moveY2L);
	}
	final var moveX2R = DirectionRotator.rotate45RightX(moveX1R, moveY1R);
	final var moveY2R = DirectionRotator.rotate45RightY(moveX1R, moveY1R);
	final var canMove2R = !this.getCell(locX + moveX2R, locY + moveY2R, locZ, Layer.STATUS.ordinal()).isSolid();
	if (canMove2R) {
	    return DirectionResolver.resolve(moveX2R, moveY2R);
	}
	final var moveX3L = DirectionRotator.rotate45LeftX(moveX2L, moveY2L);
	final var moveY3L = DirectionRotator.rotate45LeftY(moveX2L, moveY2L);
	final var canMove3L = !this.getCell(locX + moveX3L, locY + moveY3L, locZ, Layer.STATUS.ordinal()).isSolid();
	if (canMove3L) {
	    return DirectionResolver.resolve(moveX3L, moveY3L);
	}
	final var moveX3R = DirectionRotator.rotate45RightX(moveX2R, moveY2R);
	final var moveY3R = DirectionRotator.rotate45RightY(moveX2R, moveY2R);
	final var canMove3R = !this.getCell(locX + moveX3R, locY + moveY3R, locZ, Layer.STATUS.ordinal()).isSolid();
	if (canMove3R) {
	    return DirectionResolver.resolve(moveX3R, moveY3R);
	}
	final var moveX4 = DirectionRotator.rotate45LeftX(moveX3L, moveY3L);
	final var moveY4 = DirectionRotator.rotate45LeftY(moveX3L, moveY3L);
	return DirectionResolver.resolve(moveX4, moveY4);
    }

    @Override
    public void copyLevel() {
	this.clipboard = this.dungeonData.clone();
	this.activeLevelInfo = new DungeonLevelInfo(this.levelInfoData.get(this.activeLevel));
    }

    @Override
    public void cutLevel() {
	if (this.levelCount > 1) {
	    this.clipboard = this.dungeonData;
	    this.activeLevelInfo = this.levelInfoData.get(this.activeLevel);
	    this.removeActiveLevel();
	}
    }

    @Override
    public void disableHorizontalWraparound() {
	this.levelInfoData.get(this.activeLevel).disableHorizontalWraparound();
    }

    @Override
    public void disableThirdDimensionWraparound() {
	this.levelInfoData.get(this.activeLevel).disableThirdDimensionWraparound();
    }

    @Override
    public void disableVerticalWraparound() {
	this.levelInfoData.get(this.activeLevel).disableVerticalWraparound();
    }

    @Override
    public boolean doesLevelExist(final int level) {
	return level < this.levelCount && level >= 0;
    }

    @Override
    public boolean doesLevelExistOffset(final int level) {
	return this.activeLevel + level < this.levelCount && this.activeLevel + level >= 0;
    }

    @Override
    public boolean doesPlayerExist(final int pi) {
	return this.levelInfoData.get(this.activeLevel).doesPlayerStartExist(pi);
    }

    @Override
    public void enableHorizontalWraparound() {
	this.levelInfoData.get(this.activeLevel).enableHorizontalWraparound();
    }

    @Override
    public void enableThirdDimensionWraparound() {
	this.levelInfoData.get(this.activeLevel).enableThirdDimensionWraparound();
    }

    @Override
    public void enableVerticalWraparound() {
	this.levelInfoData.get(this.activeLevel).enableVerticalWraparound();
    }

    @Override
    public void fillDefault() {
	final var fill = Settings.getEditorDefaultFill();
	this.dungeonData.fill(this, fill);
    }

    @Override
    public int[] findObject(final int z, final String targetName) {
	return this.dungeonData.findObject(this, z, targetName);
    }

    @Override
    public int[] findPlayer(final int number) {
	return this.dungeonData.findPlayer(this, number);
    }

    @Override
    public void fullScanAllButtonClose(final int z, final GameObject source) {
	this.dungeonData.fullScanAllButtonClose(this, z, source);
    }

    @Override
    public void fullScanAllButtonOpen(final int z, final GameObject source) {
	this.dungeonData.fullScanAllButtonOpen(this, z, source);
    }

    @Override
    public void fullScanButtonBind(final int dx, final int dy, final int z, final GameObject source) {
	this.dungeonData.fullScanButtonBind(this, dx, dy, z, source);
    }

    @Override
    public void fullScanButtonCleanup(final int px, final int py, final int z, final GameObject button) {
	this.dungeonData.fullScanButtonCleanup(this, px, py, z, button);
    }

    @Override
    public void fullScanFindButtonLostDoor(final int z, final GameObject door) {
	this.dungeonData.fullScanFindButtonLostDoor(this, z, door);
    }

    @Override
    public void fullScanFreezeGround() {
	this.dungeonData.fullScanFreezeGround(this);
    }

    private String generateCurrentLevelInfo() {
	return LocaleUtils.subst(Strings.dialog(DialogString.CURRENT_LEVEL_INFO),
		Integer.toString(this.getActiveLevel() + 1), this.getName().trim(), this.getAuthor().trim(),
		Strings.difficulty(this.getDifficulty()));
    }

    @Override
    public void generateLevelInfoList() {
	final var saveLevel = this.getActiveLevel();
	final var tempStorage = new ArrayList<String>();
	for (var x = 0; x < this.levelCount; x++) {
	    this.switchLevel(x);
	    tempStorage.add(this.generateCurrentLevelInfo());
	}
	this.switchLevel(saveLevel);
	this.levelInfoList = tempStorage;
    }

    @Override
    public int getActiveEra() {
	return this.activeEra;
    }

    @Override
    public int getActiveLevel() {
	return this.activeLevel;
    }

    @Override
    public String getAuthor() {
	return this.levelInfoData.get(this.activeLevel).getAuthor();
    }

    @Override
    public String getBasePath() {
	return this.basePath;
    }

    @Override
    public GameObject getCell(final int row, final int col, final int floor, final int layer) {
	return this.dungeonData.getCell(this, row, col, floor, layer);
    }

    @Override
    public int getColumns() {
	return this.dungeonData.getColumns();
    }

    @Override
    public Difficulty getDifficulty() {
	return this.levelInfoData.get(this.activeLevel).getDifficulty();
    }

    @Override
    public String getDungeonTempMusicFolder() {
	return this.basePath + File.pathSeparator + Strings.untranslated(Untranslated.EXTERNAL_MUSIC_TEMP_FOLDER)
		+ File.pathSeparator;
    }

    @Override
    public int getFloors() {
	return this.dungeonData.getFloors();
    }

    @Override
    public String getHint() {
	return this.levelInfoData.get(this.activeLevel).getHint();
    }

    private File getLevelFile(final int level, final int era) {
	return new File(this.basePath + File.separator + Strings.fileExtension(FileExtension.LEVEL) + level
		+ Strings.fileExtension(FileExtension.ERA) + era + Strings.fileExtension(FileExtension.LEVEL));
    }

    @Override
    public String[] getLevelInfoList() {
	return this.levelInfoList.toArray(new String[this.levelInfoList.size()]);
    }

    private DataIOReader getLevelReaderG5() throws IOException {
	return DataIOFactory.createReader(DataMode.CUSTOM_XML,
		this.basePath + File.separator + Strings.fileExtension(FileExtension.LEVEL) + this.activeLevel
			+ Strings.fileExtension(FileExtension.LEVEL));
    }

    private DataIOReader getLevelReaderG6() throws IOException {
	return DataIOFactory.createReader(DataMode.CUSTOM_XML,
		this.basePath + File.separator + Strings.fileExtension(FileExtension.LEVEL) + this.activeLevel
			+ Strings.fileExtension(FileExtension.ERA) + this.activeEra
			+ Strings.fileExtension(FileExtension.LEVEL));
    }

    @Override
    public int getLevels() {
	return this.levelCount;
    }

    private DataIOWriter getLevelWriter() throws IOException {
	return DataIOFactory.createWriter(DataMode.CUSTOM_XML,
		this.basePath + File.separator + Strings.fileExtension(FileExtension.LEVEL) + this.activeLevel
			+ Strings.fileExtension(FileExtension.ERA) + this.activeEra
			+ Strings.fileExtension(FileExtension.LEVEL));
    }

    @Override
    public String getMusicFilename() {
	return this.musicFilename;
    }

    @Override
    public String getName() {
	return this.levelInfoData.get(this.activeLevel).getName();
    }

    @Override
    public int getPlayerLocationX(final int pi) {
	return this.levelInfoData.get(this.activeLevel).getPlayerLocationX(pi);
    }

    @Override
    public int getPlayerLocationY(final int pi) {
	return this.levelInfoData.get(this.activeLevel).getPlayerLocationY(pi);
    }

    @Override
    public int getPlayerLocationZ(final int pi) {
	return this.levelInfoData.get(this.activeLevel).getPlayerLocationZ(pi);
    }

    @Override
    public int getRows() {
	return this.dungeonData.getRows();
    }

    @Override
    public int getStartColumn(final int pi) {
	return this.levelInfoData.get(this.activeLevel).getStartColumn(pi);
    }

    @Override
    public int getStartFloor(final int pi) {
	return this.levelInfoData.get(this.activeLevel).getStartFloor(pi);
    }

    @Override
    public int getStartLevel(final int pi) {
	return this.startLevel;
    }

    @Override
    public int getStartRow(final int pi) {
	return this.levelInfoData.get(this.activeLevel).getStartRow(pi);
    }

    @Override
    public GameObject getVirtualCell(final int row, final int col, final int floor, final int layer) {
	return this.dungeonData.getVirtualCell(this, row, col, floor, layer);
    }

    @Override
    public HistoryStatus getWhatWas() {
	return this.dungeonData.getWhatWas();
    }

    @Override
    public boolean insertLevelFromClipboard() {
	if (this.levelCount < DungeonBase.MAX_LEVELS) {
	    this.dungeonData = this.clipboard.clone();
	    this.levelCount++;
	    return true;
	}
	return false;
    }

    @Override
    public boolean isCellDirty(final int row, final int col, final int floor) {
	return this.dungeonData.isCellDirty(this, row, col, floor);
    }

    @Override
    public boolean isCutBlocked() {
	return this.levelCount <= 1;
    }

    @Override
    public boolean isHorizontalWraparoundEnabled() {
	return this.levelInfoData.get(this.activeLevel).isHorizontalWraparoundEnabled();
    }

    @Override
    public boolean isMoveShootAllowed() {
	return this.isMoveShootAllowedGlobally() && this.isMoveShootAllowedThisLevel();
    }

    @Override
    public boolean isMoveShootAllowedGlobally() {
	return this.moveShootAllowed;
    }

    @Override
    public boolean isMoveShootAllowedThisLevel() {
	return this.levelInfoData.get(this.activeLevel).isMoveShootAllowed();
    }

    @Override
    public boolean isPasteBlocked() {
	return this.clipboard == null;
    }

    @Override
    public boolean isSquareVisible(final int x1, final int y1, final int x2, final int y2, final int zp) {
	return this.dungeonData.isSquareVisible(this, x1, y1, x2, y2, zp);
    }

    @Override
    public boolean isThirdDimensionWraparoundEnabled() {
	return this.levelInfoData.get(this.activeLevel).isThirdDimensionWraparoundEnabled();
    }

    @Override
    public boolean isVerticalWraparoundEnabled() {
	return this.levelInfoData.get(this.activeLevel).isVerticalWraparoundEnabled();
    }

    @Override
    public void markAsDirty(final int row, final int col, final int floor) {
	this.dungeonData.markAsDirty(this, row, col, floor);
    }

    @Override
    public void offsetPlayerLocationX(final int pi, final int newPlayerLocationX) {
	this.levelInfoData.get(this.activeLevel).offsetPlayerLocationX(pi, newPlayerLocationX);
    }

    @Override
    public void offsetPlayerLocationY(final int pi, final int newPlayerLocationY) {
	this.levelInfoData.get(this.activeLevel).offsetPlayerLocationX(pi, newPlayerLocationY);
    }

    @Override
    public void offsetPlayerLocationZ(final int pi, final int newPlayerLocationZ) {
	this.levelInfoData.get(this.activeLevel).offsetPlayerLocationX(pi, newPlayerLocationZ);
    }

    @Override
    public void pasteLevel() {
	if (this.clipboard != null) {
	    this.dungeonData = this.clipboard.clone();
	    this.levelInfoData.set(this.activeLevel, new DungeonLevelInfo(this.activeLevelInfo));
	    this.levelInfoList.set(this.activeLevel, this.generateCurrentLevelInfo());
	    Inconnuclear.getStuffBag().getDungeonManager().setDirty(true);
	}
    }

    @Override
    public void postBattle(final GameObject m, final int xLoc, final int yLoc, final boolean player) {
	this.dungeonData.postBattle(this, m, xLoc, yLoc, player);
    }

    @Override
    public Dungeon readDungeonBase() throws IOException {
	final var m = new Dungeon();
	// Attach handlers
	m.setPrefixHandler(this.prefixHandler);
	m.setSuffixHandler(this.suffixHandler);
	// Make base paths the same
	m.basePath = this.basePath;
	var version = -1;
	// Create metafile reader
	try (DataIOReader metaReader = DataIOFactory.createReader(DataMode.CUSTOM_XML, m.basePath + File.separator
		+ Strings.fileExtension(FileExtension.METAFILE) + Strings.fileExtension(FileExtension.LEVEL))) {
	    // Read metafile
	    version = m.readDungeonMetafileVersion(metaReader);
	    if (FileFormats.isFormatVersionValidGeneration7(version)) {
		m.readDungeonMetafileG7(metaReader, version);
	    } else if (FileFormats.isFormatVersionValidGeneration6(version)) {
		m.readDungeonMetafileG6(metaReader, version);
	    } else if (FileFormats.isFormatVersionValidGeneration4(version)
		    || FileFormats.isFormatVersionValidGeneration5(version)) {
		m.readDungeonMetafileG4(metaReader, version);
	    } else {
		m.readDungeonMetafileG3(metaReader, version);
	    }
	} catch (final IOException ioe) {
	    throw ioe;
	}
	if (!FileFormats.isLevelListStored(version)) {
	    // Create data reader
	    try (var dataReader = m.getLevelReaderG5()) {
		// Read data
		m.readDungeonLevel(dataReader, version);
	    } catch (final IOException ioe) {
		throw ioe;
	    }
	    // Update level info
	    m.generateLevelInfoList();
	} else {
	    // Create data reader
	    try (var dataReader = m.getLevelReaderG6()) {
		// Read data
		m.readDungeonLevel(dataReader, version);
	    } catch (final IOException ioe) {
		throw ioe;
	    }
	}
	return m;
    }

    private void readDungeonLevel(final DataIOReader reader) throws IOException {
	this.readDungeonLevel(reader, FileFormats.DUNGEON_LATEST);
    }

    private void readDungeonLevel(final DataIOReader reader, final int formatVersion) throws IOException {
	this.dungeonData = (DungeonData) new DungeonData().readData(this, reader, formatVersion);
	this.dungeonData.readSavedState(reader, formatVersion);
    }

    private void readDungeonMetafileG3(final DataIOReader reader, final int ver) throws IOException {
	this.levelCount = reader.readInt();
	this.musicFilename = "null";
	if (this.suffixHandler != null) {
	    this.suffixHandler.readSuffix(reader, ver);
	}
    }

    private void readDungeonMetafileG4(final DataIOReader reader, final int ver) throws IOException {
	this.levelCount = reader.readInt();
	this.musicFilename = reader.readString();
	if (this.suffixHandler != null) {
	    this.suffixHandler.readSuffix(reader, ver);
	}
    }

    private void readDungeonMetafileG6(final DataIOReader reader, final int ver) throws IOException {
	this.levelCount = reader.readInt();
	this.musicFilename = reader.readString();
	this.moveShootAllowed = reader.readBoolean();
	for (var l = 0; l < this.levelCount; l++) {
	    this.levelInfoData.add(DungeonLevelInfo.readLevelInfo(reader));
	    this.levelInfoList.add(reader.readString());
	}
	if (this.suffixHandler != null) {
	    this.suffixHandler.readSuffix(reader, ver);
	}
    }

    private void readDungeonMetafileG7(final DataIOReader reader, final int ver) throws IOException {
	this.levelCount = reader.readInt();
	this.startLevel = reader.readInt();
	this.startEra = reader.readInt();
	this.musicFilename = reader.readString();
	this.moveShootAllowed = reader.readBoolean();
	for (var l = 0; l < this.levelCount; l++) {
	    this.levelInfoData.add(DungeonLevelInfo.readLevelInfo(reader));
	    this.levelInfoList.add(reader.readString());
	}
	if (this.suffixHandler != null) {
	    this.suffixHandler.readSuffix(reader, ver);
	}
    }

    private int readDungeonMetafileVersion(final DataIOReader reader) throws IOException {
	var ver = FileFormats.DUNGEON_LATEST;
	if (this.prefixHandler != null) {
	    ver = this.prefixHandler.readPrefix(reader);
	}
	this.moveShootAllowed = FileFormats.isMoveShootAllowed(ver);
	return ver;
    }

    @Override
    public void redo() {
	this.dungeonData.redo(this);
    }

    @Override
    protected boolean removeActiveLevel() {
	if (this.levelCount <= 1 || this.activeLevel < 0 || this.activeLevel > this.levelCount) {
	    return false;
	}
	this.dungeonData = null;
	// Delete all files corresponding to current level
	for (var e = 0; e < DungeonBase.ERA_COUNT; e++) {
	    final var res = this.getLevelFile(this.activeLevel, e).delete();
	    if (!res) {
		return false;
	    }
	}
	// Shift all higher-numbered levels down
	for (var x = this.activeLevel; x < this.levelCount - 1; x++) {
	    for (var e = 0; e < DungeonBase.ERA_COUNT; e++) {
		final var sourceLocation = this.getLevelFile(x + 1, e);
		final var targetLocation = this.getLevelFile(x, e);
		try {
		    FileUtilities.moveFile(sourceLocation, targetLocation);
		} catch (final IOException io) {
		    // Ignore
		}
	    }
	}
	this.levelCount--;
	this.levelInfoData.remove(this.activeLevel);
	this.levelInfoList.remove(this.activeLevel);
	return true;
    }

    @Override
    public void resetHistoryEngine() {
	this.dungeonData.resetHistoryEngine();
    }

    @Override
    public void resetVisibleSquares(final int floor) {
	this.dungeonData.resetVisibleSquares(floor);
    }

    @Override
    public void resize(final int z, final GameObject nullFill) {
	this.dungeonData.resize(this, z, nullFill);
    }

    @Override
    public void restore() {
	this.dungeonData.restore(this);
    }

    @Override
    public void restorePlayerLocation() {
	this.levelInfoData.get(this.activeLevel).restorePlayerLocation();
    }

    @Override
    public void save() {
	this.dungeonData.save(this);
    }

    @Override
    public void savePlayerLocation() {
	this.levelInfoData.get(this.activeLevel).savePlayerLocation();
    }

    @Override
    public void setAuthor(final String newAuthor) {
	this.levelInfoData.get(this.activeLevel).setAuthor(newAuthor);
	this.levelInfoList.set(this.activeLevel, this.generateCurrentLevelInfo());
    }

    @Override
    public void setCell(final GameObject mo, final int row, final int col, final int floor, final int layer) {
	this.dungeonData.setCell(this, mo, row, col, floor, layer);
    }

    @Override
    public void setData(final DungeonDataBase newData, final int count) {
	if (newData instanceof DungeonData) {
	    this.dungeonData = (DungeonData) newData;
	    this.levelCount = count;
	}
    }

    @Override
    public void setDifficulty(final Difficulty newDifficulty) {
	this.levelInfoData.get(this.activeLevel).setDifficulty(newDifficulty);
	this.levelInfoList.set(this.activeLevel, this.generateCurrentLevelInfo());
    }

    @Override
    public void setDirtyFlags(final int floor) {
	this.dungeonData.setDirtyFlags(floor);
    }

    @Override
    public void setHint(final String newHint) {
	this.levelInfoData.get(this.activeLevel).setHint(newHint);
    }

    @Override
    public void setMoveShootAllowedGlobally(final boolean value) {
	this.moveShootAllowed = value;
    }

    @Override
    public void setMoveShootAllowedThisLevel(final boolean value) {
	this.levelInfoData.get(this.activeLevel).setMoveShootAllowed(value);
    }

    @Override
    public void setMusicFilename(final String newMusicFilename) {
	this.musicFilename = newMusicFilename;
    }

    @Override
    public void setName(final String newName) {
	this.levelInfoData.get(this.activeLevel).setName(newName);
	this.levelInfoList.set(this.activeLevel, this.generateCurrentLevelInfo());
    }

    @Override
    public void setPlayerLocationX(final int pi, final int newPlayerLocationX) {
	this.levelInfoData.get(this.activeLevel).setPlayerLocationX(pi, newPlayerLocationX);
    }

    @Override
    public void setPlayerLocationY(final int pi, final int newPlayerLocationY) {
	this.levelInfoData.get(this.activeLevel).setPlayerLocationX(pi, newPlayerLocationY);
    }

    @Override
    public void setPlayerLocationZ(final int pi, final int newPlayerLocationZ) {
	this.levelInfoData.get(this.activeLevel).setPlayerLocationX(pi, newPlayerLocationZ);
    }

    @Override
    public void setPlayerToStart() {
	this.levelInfoData.get(this.activeLevel).setPlayerToStart();
    }

    @Override
    public void setPrefixHandler(final AbstractPrefixIO xph) {
	this.prefixHandler = xph;
    }

    @Override
    public void setStartColumn(final int pi, final int newStartColumn) {
	this.levelInfoData.get(this.activeLevel).setStartColumn(pi, newStartColumn);
    }

    @Override
    public void setStartFloor(final int pi, final int newStartFloor) {
	this.levelInfoData.get(this.activeLevel).setStartFloor(pi, newStartFloor);
    }

    @Override
    public void setStartRow(final int pi, final int newStartRow) {
	this.levelInfoData.get(this.activeLevel).setStartRow(pi, newStartRow);
    }

    @Override
    public void setSuffixHandler(final AbstractSuffixIO xsh) {
	this.suffixHandler = xsh;
    }

    @Override
    public void setVirtualCell(final GameObject mo, final int row, final int col, final int floor, final int layer) {
	this.dungeonData.setVirtualCell(this, mo, row, col, floor, layer);
    }

    @Override
    public void switchEra(final int era) {
	this.switchInternal(this.activeLevel, era);
    }

    @Override
    public void switchEraOffset(final int era) {
	this.switchInternal(this.activeLevel, this.activeEra + era);
    }

    @Override
    protected void switchInternal(final int level, final int era) {
	if (this.activeLevel != level || this.activeEra != era || this.dungeonData == null) {
	    if (this.dungeonData != null) {
		try (var writer = this.getLevelWriter()) {
		    // Save old level
		    this.writeDungeonLevel(writer);
		    writer.close();
		} catch (final IOException io) {
		    // Ignore
		}
	    }
	    this.activeLevel = level;
	    this.activeEra = era;
	    try (var reader = this.getLevelReaderG6()) {
		// Load new level
		this.readDungeonLevel(reader);
		reader.close();
	    } catch (final IOException io) {
		// Ignore
	    }
	}
    }

    @Override
    public void switchLevel(final int level) {
	this.switchInternal(level, this.activeEra);
    }

    @Override
    public void switchLevelOffset(final int level) {
	this.switchInternal(this.activeLevel + level, this.activeEra);
    }

    @Override
    public void tickTimers() {
	this.dungeonData.tickTimers(this);
    }

    @Override
    public void tickTimers(final int floor, final int actionType) {
	this.dungeonData.tickTimers(this, floor, actionType);
    }

    @Override
    public boolean tryRedo() {
	return this.dungeonData.tryRedo();
    }

    @Override
    public boolean tryUndo() {
	return this.dungeonData.tryUndo();
    }

    @Override
    public void undo() {
	this.dungeonData.undo(this);
    }

    @Override
    public void updateMonsterPosition(final Direction move, final int xLoc, final int yLoc, final GameObject monster,
	    final int pi) {
	this.dungeonData.updateMonsterPosition(this, move, xLoc, yLoc, monster, pi);
    }

    @Override
    public void updateRedoHistory(final HistoryStatus whatIs) {
	this.dungeonData.updateRedoHistory(whatIs);
    }

    @Override
    public void updateUndoHistory(final HistoryStatus whatIs) {
	this.dungeonData.updateUndoHistory(whatIs);
    }

    @Override
    public void updateVisibleSquares(final int xp, final int yp, final int zp) {
	this.dungeonData.updateVisibleSquares(this, xp, yp, zp);
    }

    @Override
    public void writeDungeon() throws IOException {
	// Create metafile writer
	try (DataIOWriter metaWriter = DataIOFactory.createWriter(DataMode.CUSTOM_XML, this.basePath + File.separator
		+ Strings.fileExtension(FileExtension.METAFILE) + Strings.fileExtension(FileExtension.LEVEL))) {
	    // Write metafile
	    this.writeDungeonMetafile(metaWriter);
	} catch (final IOException ioe) {
	    throw ioe;
	}
	// Create data writer
	try (var dataWriter = this.getLevelWriter()) {
	    // Write data
	    this.writeDungeonLevel(dataWriter);
	} catch (final IOException ioe) {
	    throw ioe;
	}
    }

    private void writeDungeonLevel(final DataIOWriter writer) throws IOException {
	// Write the level
	this.dungeonData.writeData(this, writer);
	this.dungeonData.writeSavedState(writer);
    }

    private void writeDungeonMetafile(final DataIOWriter writer) throws IOException {
	if (this.prefixHandler != null) {
	    this.prefixHandler.writePrefix(writer);
	}
	writer.writeInt(this.levelCount);
	writer.writeInt(this.startLevel);
	writer.writeInt(this.startEra);
	writer.writeString(this.musicFilename);
	writer.writeBoolean(this.moveShootAllowed);
	for (var l = 0; l < this.levelCount; l++) {
	    this.levelInfoData.get(l).writeLevelInfo(writer);
	    writer.writeString(this.levelInfoList.get(l));
	}
	if (this.suffixHandler != null) {
	    this.suffixHandler.writeSuffix(writer);
	}
    }
}