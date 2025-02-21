/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JMenu;

import org.retropipes.diane.LocaleUtils;
import org.retropipes.diane.direction.DirectionResolver;
import org.retropipes.diane.fileio.DataIOFactory;
import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.diane.fileio.DataMode;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Accelerators;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.MenuSection;
import org.retropipes.inconnuclear.StuffBag;
import org.retropipes.inconnuclear.asset.ImageConstants;
import org.retropipes.inconnuclear.creature.party.Party;
import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.dungeon.GenerateDungeonTask;
import org.retropipes.inconnuclear.dungeon.base.HistoryStatus;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.loader.extmusic.ExternalMusicImporter;
import org.retropipes.inconnuclear.loader.extmusic.ExternalMusicLoader;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.ErrorString;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.GameString;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Menu;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;
import org.retropipes.inconnuclear.settings.Settings;
import org.retropipes.inconnuclear.utility.AlreadyDeadException;
import org.retropipes.inconnuclear.utility.CustomDialogs;
import org.retropipes.inconnuclear.utility.InvalidDungeonException;
import org.retropipes.inconnuclear.utility.PartyInventory;

public final class Game implements MenuSection {
    static final int OTHER_AMMO_MODE_MISSILES = 0;
    static final int OTHER_AMMO_MODE_STUNNERS = 1;
    static final int OTHER_AMMO_MODE_BLUE_LASERS = 2;
    static final int OTHER_AMMO_MODE_DISRUPTORS = 3;
    static final int OTHER_TOOL_MODE_BOOSTS = 0;
    static final int OTHER_TOOL_MODE_MAGNETS = 1;
    static final int OTHER_RANGE_MODE_BOMBS = 0;
    static final int OTHER_RANGE_MODE_HEAT_BOMBS = 1;
    static final int OTHER_RANGE_MODE_ICE_BOMBS = 2;
    static final int CHEAT_SWIMMING = 0;
    static final int CHEAT_GHOSTLY = 1;
    static final int CHEAT_INVINCIBLE = 2;
    static final int CHEAT_MISSILES = 3;
    static final int CHEAT_STUNNERS = 4;
    static final int CHEAT_BOOSTS = 5;
    static final int CHEAT_MAGNETS = 6;
    static final int CHEAT_BLUE_LASERS = 7;
    static final int CHEAT_DISRUPTORS = 8;
    static final int CHEAT_BOMBS = 9;
    static final int CHEAT_HEAT_BOMBS = 10;
    static final int CHEAT_ICE_BOMBS = 11;
    private static String[] OTHER_AMMO_CHOICES = { Strings.game(GameString.MISSILES), Strings.game(GameString.STUNNERS),
	    Strings.game(GameString.BLUE_LASERS), Strings.game(GameString.DISRUPTORS) };
    private static String[] OTHER_TOOL_CHOICES = { Strings.game(GameString.BOOSTS), Strings.game(GameString.MAGNETS) };
    private static String[] OTHER_RANGE_CHOICES = { Strings.game(GameString.BOMBS), Strings.game(GameString.HEAT_BOMBS),
	    Strings.game(GameString.ICE_BOMBS) };

    public static boolean canObjectMove(final int locX, final int locY, final int dirX, final int dirY) {
	return MLOTask.checkSolid(locX + dirX, locY + dirY);
    }

    private static void checkMenus() {
	final var edit = Inconnuclear.getStuffBag().getEditor();
	final var a = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase();
	if (a.tryUndo()) {
	    edit.enableUndo();
	} else {
	    edit.disableUndo();
	}
	if (a.tryRedo()) {
	    edit.enableRedo();
	} else {
	    edit.disableRedo();
	}
    }

    public static void morph(final GameObject morphInto) {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	m.setCell(morphInto, m.getPlayerLocationX(0), m.getPlayerLocationY(0), 0, morphInto.getLayer());
    }

    public static void morph(final GameObject morphInto, final int x, final int y, final int z, final int w) {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	try {
	    m.setCell(morphInto, x, y, z, w);
	    app.getDungeonManager().setDirty(true);
	} catch (final ArrayIndexOutOfBoundsException | NullPointerException np) {
	    // Do nothing
	}
    }

    public static void resetPlayerLocation(final int level) {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	if (m != null) {
	    m.switchLevel(level);
	    m.setPlayerToStart();
	}
    }

    private static void updateRedo(final boolean las, final boolean mis, final boolean stu, final boolean boo,
	    final boolean mag, final boolean blu, final boolean dis, final boolean bom, final boolean hbm,
	    final boolean ibm) {
	final var app = Inconnuclear.getStuffBag();
	final var a = app.getDungeonManager().getDungeonBase();
	a.updateRedoHistory(new HistoryStatus(las, mis, stu, boo, mag, blu, dis, bom, hbm, ibm));
	Game.checkMenus();
    }

    static void updateUndo(final boolean las, final boolean mis, final boolean stu, final boolean boo,
	    final boolean mag, final boolean blu, final boolean dis, final boolean bom, final boolean hbm,
	    final boolean ibm) {
	final var app = Inconnuclear.getStuffBag();
	final var a = app.getDungeonManager().getDungeonBase();
	a.updateUndoHistory(new HistoryStatus(las, mis, stu, boo, mag, blu, dis, bom, hbm, ibm));
	Game.checkMenus();
    }

    // Fields
    private boolean savedGameFlag;
    private final GameViewingWindowManager vwMgr;
    GameObject player;
    private boolean stateChanged;
    private final GameGUI gui;
    private final MovementTask mt;
    final PlayerLocationManager plMgr;
    private final CheatManager cMgr;
    private final ScoreTracker st;
    private JLabel scoreMoves;
    private JLabel scoreShots;
    private JLabel scoreOthers;
    private JLabel otherAmmoLeft;
    private JLabel otherToolsLeft;
    private JLabel otherRangesLeft;
    private JLabel levelInfo;
    private boolean delayedDecayActive;
    private GameObject delayedDecayObject;
    boolean shotActive;
    boolean moving;
    private boolean remoteDecay;
    private AnimationTask animator;
    private GameReplayEngine gre;
    private boolean recording;
    private boolean replaying;
    private MLOTask mlot;
    private boolean lpbLoaded;
    private final boolean[] cheatStatus;
    private boolean autoMove;
    private boolean dead;
    int otherAmmoMode;
    int otherToolMode;
    int otherRangeMode;

    // Constructors
    public Game() {
	this.vwMgr = new GameViewingWindowManager();
	this.plMgr = new PlayerLocationManager();
	this.cMgr = new CheatManager();
	this.st = new ScoreTracker();
	this.gui = new GameGUI();
	this.mt = new MovementTask(this.vwMgr, this.gui);
	this.mt.start();
	this.savedGameFlag = false;
	this.stateChanged = true;
	this.savedGameFlag = false;
	this.delayedDecayActive = false;
	this.delayedDecayObject = null;
	this.shotActive = false;
	this.remoteDecay = false;
	this.moving = false;
	this.gre = new GameReplayEngine();
	this.recording = false;
	this.replaying = false;
	this.lpbLoaded = false;
	this.cheatStatus = new boolean[this.cMgr.getCheatCount()];
	this.autoMove = false;
	this.dead = false;
	this.otherAmmoMode = Game.OTHER_AMMO_MODE_MISSILES;
	this.otherToolMode = Game.OTHER_TOOL_MODE_BOOSTS;
	this.otherRangeMode = Game.OTHER_RANGE_MODE_BOMBS;
    }

    public void abortAndWaitForMLOLoop() {
	if (this.mlot != null && this.mlot.isAlive()) {
	    this.mlot.abortLoop();
	    var waiting = true;
	    while (waiting) {
		try {
		    this.mlot.join();
		    waiting = false;
		} catch (final InterruptedException ie) {
		    // Ignore
		}
	    }
	}
	this.moveLoopDone();
	this.laserDone();
    }

    private void abortMovementLaserObjectLoop() {
	this.mlot.abortLoop();
	this.moveLoopDone();
	this.laserDone();
    }

    public void activeLanguageChanged() {
	this.gui.activeLanguageChanged();
	Game.OTHER_AMMO_CHOICES = new String[] { Strings.game(GameString.MISSILES), Strings.game(GameString.STUNNERS),
		Strings.game(GameString.BLUE_LASERS), Strings.game(GameString.DISRUPTORS) };
	Game.OTHER_TOOL_CHOICES = new String[] { Strings.game(GameString.BOOSTS), Strings.game(GameString.MAGNETS) };
	Game.OTHER_RANGE_CHOICES = new String[] { Strings.game(GameString.BOMBS), Strings.game(GameString.HEAT_BOMBS),
		Strings.game(GameString.ICE_BOMBS) };
    }

    @Override
    public void attachAccelerators(final Accelerators accel) {
	this.gui.attachAccelerators(accel);
    }

    public void changeOtherAmmoMode() {
	final var choice = CommonDialogs.showInputDialog(Strings.game(GameString.WHICH_AMMO),
		Strings.game(GameString.CHANGE_AMMO), Game.OTHER_AMMO_CHOICES,
		Game.OTHER_AMMO_CHOICES[this.otherAmmoMode]);
	if (choice != null) {
	    for (var z = 0; z < Game.OTHER_AMMO_CHOICES.length; z++) {
		if (choice.equals(Game.OTHER_AMMO_CHOICES[z])) {
		    this.otherAmmoMode = z;
		    break;
		}
	    }
	    this.updateScoreText();
	    CommonDialogs.showDialog(LocaleUtils.subst(Strings.game(GameString.AMMO_CHANGED),
		    Game.OTHER_AMMO_CHOICES[this.otherAmmoMode]));
	}
    }

    public void changeOtherRangeMode() {
	final var choice = CommonDialogs.showInputDialog(Strings.game(GameString.WHICH_RANGE),
		Strings.game(GameString.CHANGE_RANGE), Game.OTHER_RANGE_CHOICES,
		Game.OTHER_RANGE_CHOICES[this.otherRangeMode]);
	if (choice != null) {
	    for (var z = 0; z < Game.OTHER_RANGE_CHOICES.length; z++) {
		if (choice.equals(Game.OTHER_RANGE_CHOICES[z])) {
		    this.otherRangeMode = z;
		    break;
		}
	    }
	    this.updateScoreText();
	    CommonDialogs.showDialog(LocaleUtils.subst(Strings.game(GameString.RANGE_CHANGED),
		    Game.OTHER_RANGE_CHOICES[this.otherRangeMode]));
	}
    }

    public void changeOtherToolMode() {
	final var choice = CommonDialogs.showInputDialog(Strings.game(GameString.WHICH_TOOL),
		Strings.game(GameString.CHANGE_TOOL), Game.OTHER_TOOL_CHOICES,
		Game.OTHER_TOOL_CHOICES[this.otherToolMode]);
	if (choice != null) {
	    for (var z = 0; z < Game.OTHER_TOOL_CHOICES.length; z++) {
		if (choice.equals(Game.OTHER_TOOL_CHOICES[z])) {
		    this.otherToolMode = z;
		    break;
		}
	    }
	    this.updateScoreText();
	    CommonDialogs.showDialog(LocaleUtils.subst(Strings.game(GameString.TOOL_CHANGED),
		    Game.OTHER_TOOL_CHOICES[this.otherToolMode]));
	}
    }

    void clearDead() {
	this.dead = false;
    }

    public void clearReplay() {
	this.gre = new GameReplayEngine();
	this.lpbLoaded = true;
    }

    @Override
    public JMenu createCommandsMenu() {
	return this.gui.createCommandsMenu();
    }

    public void decay() {
	if (this.player != null) {
	    this.player.setSavedObject(new GameObject(ObjectImageId.EMPTY));
	}
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	m.setCell(new GameObject(ObjectImageId.EMPTY), m.getPlayerLocationX(0), m.getPlayerLocationY(0), 0,
		Layer.STATUS.ordinal());
    }

    @Override
    public void disableDirtyCommands() {
	this.gui.disableDirtyCommands();
    }

    public void disableEvents() {
	this.gui.disableEvents();
    }

    @Override
    public void disableLoadedCommands() {
	this.gui.disableLoadedCommands();
    }

    @Override
    public void disableModeCommands() {
	this.gui.disableModeCommands();
    }

    private void disableRecording() {
	this.gui.disableRecording();
    }

    void doDelayedDecay() {
	this.player.setSavedObject(this.delayedDecayObject);
	this.delayedDecayActive = false;
    }

    void doRemoteDelayedDecay(final GameObject o) {
	o.setSavedObject(this.delayedDecayObject);
	this.remoteDecay = false;
	this.delayedDecayActive = false;
    }

    @Override
    public void enableDirtyCommands() {
	this.gui.enableDirtyCommands();
    }

    public void enableEvents() {
	this.mt.fireStepActions();
	this.gui.enableEvents();
    }

    @Override
    public void enableLoadedCommands() {
	this.gui.enableLoadedCommands();
    }

    @Override
    public void enableModeCommands() {
	this.gui.enableModeCommands();
    }

    public void enterCheatCode() {
	final var rawCheat = this.cMgr.enterCheat();
	if (rawCheat != null) {
	    if (rawCheat.contains(Strings.game(GameString.ENABLE_CHEAT))) {
		// Enable cheat
		final var cheat = rawCheat.substring(7);
		for (var x = 0; x < this.cMgr.getCheatCount(); x++) {
		    if (this.cMgr.queryCheatCache(cheat) == x) {
			this.cheatStatus[x] = true;
			break;
		    }
		}
	    } else {
		// Disable cheat
		final var cheat = rawCheat.substring(8);
		for (var x = 0; x < this.cMgr.getCheatCount(); x++) {
		    if (this.cMgr.queryCheatCache(cheat) == x) {
			this.cheatStatus[x] = false;
			break;
		    }
		}
	    }
	}
    }

    public void exitGame() {
	// Stop music
	ExternalMusicLoader.stopExternalMusic();
	// Halt the animator
	if (this.animator != null) {
	    this.animator.stopAnimator();
	    this.animator = null;
	}
	// Halt the movement/laser processor
	if (this.mlot != null) {
	    this.abortMovementLaserObjectLoop();
	}
	this.mlot = null;
	this.stateChanged = true;
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	// Restore the dungeon
	m.restore();
	m.resetVisibleSquares(0);
	final var playerExists = m.doesPlayerExist(this.plMgr.getActivePlayerNumber());
	if (playerExists) {
	    this.resetViewingWindowAndPlayerLocation();
	} else {
	    app.getDungeonManager().setLoaded(false);
	}
	// Reset saved game flag
	this.savedGameFlag = false;
	app.getDungeonManager().setDirty(false);
	// Exit game
	this.hideOutput();
	app.getGUIManager().showGUI();
    }

    public void gameOver() {
	// Check cheats
	if (this.getCheatStatus(Game.CHEAT_INVINCIBLE)) {
	    return;
	}
	// Check dead
	if (this.dead) {
	    // Already dead
	    throw new AlreadyDeadException();
	}
	// We are dead
	this.dead = true;
	// Stop the movement/laser/object loop
	if (this.mlot != null && this.mlot.isAlive()) {
	    this.abortMovementLaserObjectLoop();
	}
	this.mlot = null;
	SoundLoader.playSound(Sounds.GAME_OVER);
	final var choice = CustomDialogs.showDeadDialog();
	switch (choice) {
	case CommonDialogs.CANCEL_OPTION:
	    // End
	    this.exitGame();
	    break;
	case CommonDialogs.YES_OPTION:
	    // Undo
	    this.undoLastMove();
	    break;
	case CommonDialogs.NO_OPTION:
	    // Restart
	    try {
		this.resetCurrentLevel();
	    } catch (final InvalidDungeonException iae) {
		CommonDialogs.showErrorDialog(Strings.error(ErrorString.PLAYER_LOCATION),
			Strings.untranslated(Untranslated.PROGRAM_NAME));
		this.exitGame();
		return;
	    }
	    break;
	default:
	    // Closed Dialog
	    this.exitGame();
	    break;
	}
    }

    boolean getCheatStatus(final int cheatID) {
	return this.cheatStatus[cheatID];
    }

    public GameObject getPlayer() {
	return this.player;
    }

    public int[] getPlayerLocation() {
	return new int[] { this.plMgr.getPlayerLocationX(), this.plMgr.getPlayerLocationY(),
		this.plMgr.getPlayerLocationZ() };
    }

    public PlayerLocationManager getPlayerManager() {
	return this.plMgr;
    }

    public GameViewingWindowManager getViewManager() {
	return this.vwMgr;
    }

    public void goToLevelOffset(final int level) {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	final var levelExists = m.doesLevelExistOffset(level);
	this.stopMovement();
	if (levelExists) {
	    new LevelLoadTask(level).start();
	} else {
	    new GenerateDungeonTask(false).start();
	}
    }

    public void haltMovingObjects() {
	if (this.mlot != null && this.mlot.isAlive()) {
	    this.mlot.haltMovingObjects();
	}
    }

    public void hideOutput() {
	this.stopMovement();
	this.gui.hideOutput();
    }

    void identifyObject(final int x, final int y) {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	final var destX = x / ImageConstants.SIZE;
	final var destY = y / ImageConstants.SIZE;
	final var destZ = this.plMgr.getPlayerLocationZ();
	final var target = m.getCell(destX, destY, destZ, Layer.STATUS.ordinal());
	final var gameName = target.getIdentityName();
	final var desc = target.getDescription();
	CommonDialogs.showTitledDialog(desc, gameName);
    }

    boolean isAutoMoveScheduled() {
	return this.autoMove;
    }

    boolean isDelayedDecayActive() {
	return this.delayedDecayActive;
    }

    boolean isRemoteDecayActive() {
	return this.remoteDecay;
    }

    boolean isReplaying() {
	return this.replaying;
    }

    public void keepNextMessage() {
	this.gui.keepNextMessage();
    }

    void laserDone() {
	this.shotActive = false;
	Game.checkMenus();
    }

    public void loadGameHookG1(final DataIOReader dungeonFile) throws IOException {
	final var app = Inconnuclear.getStuffBag();
	app.getDungeonManager().setScoresFileName(dungeonFile.readString());
	this.st.setMoves(dungeonFile.readLong());
	this.st.setShots(dungeonFile.readLong());
	this.st.setOthers(dungeonFile.readLong());
    }

    public void loadGameHookG2(final DataIOReader dungeonFile) throws IOException {
	final var app = Inconnuclear.getStuffBag();
	app.getDungeonManager().setScoresFileName(dungeonFile.readString());
	this.st.setMoves(dungeonFile.readLong());
	this.st.setShots(dungeonFile.readLong());
	this.st.setOthers(dungeonFile.readLong());
	PartyInventory.setRedKeysLeft(dungeonFile.readInt());
	PartyInventory.setGreenKeysLeft(dungeonFile.readInt());
	PartyInventory.setBlueKeysLeft(dungeonFile.readInt());
    }

    public void loadGameHookG3(final DataIOReader dungeonFile) throws IOException {
	final var app = Inconnuclear.getStuffBag();
	app.getDungeonManager().setScoresFileName(dungeonFile.readString());
	this.st.setMoves(dungeonFile.readLong());
	this.st.setShots(dungeonFile.readLong());
	this.st.setOthers(dungeonFile.readLong());
	PartyInventory.readInventory(dungeonFile);
    }

    public void loadGameHookG4(final DataIOReader dungeonFile) throws IOException {
	final var app = Inconnuclear.getStuffBag();
	app.getDungeonManager().setScoresFileName(dungeonFile.readString());
	this.st.setMoves(dungeonFile.readLong());
	this.st.setShots(dungeonFile.readLong());
	this.st.setOthers(dungeonFile.readLong());
	PartyInventory.readInventory(dungeonFile);
    }

    public void loadGameHookG5(final DataIOReader dungeonFile) throws IOException {
	final var app = Inconnuclear.getStuffBag();
	app.getDungeonManager().setScoresFileName(dungeonFile.readString());
	this.st.setMoves(dungeonFile.readLong());
	this.st.setShots(dungeonFile.readLong());
	this.st.setOthers(dungeonFile.readLong());
	PartyInventory.readInventory(dungeonFile);
    }

    public void loadGameHookG6(final DataIOReader dungeonFile) throws IOException {
	final var app = Inconnuclear.getStuffBag();
	app.getDungeonManager().setScoresFileName(dungeonFile.readString());
	this.st.setMoves(dungeonFile.readLong());
	this.st.setShots(dungeonFile.readLong());
	this.st.setOthers(dungeonFile.readLong());
	PartyInventory.readInventory(dungeonFile);
    }

    public void loadLevel() {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	final var choices = app.getLevelInfoList();
	final var res = CommonDialogs.showInputDialog(Strings.game(GameString.LOAD_LEVEL_PROMPT),
		Strings.game(GameString.LOAD_LEVEL), choices, choices[m.getActiveLevel()]);
	var number = -1;
	for (number = 0; number < m.getLevels(); number++) {
	    if (choices[number].equals(res)) {
		break;
	    }
	}
	if (m.doesLevelExist(number)) {
	    this.suspendAnimator();
	    m.restore();
	    m.switchLevel(number);
	    app.getDungeonManager().getDungeonBase().setDirtyFlags(this.plMgr.getPlayerLocationZ());
	    m.resetHistoryEngine();
	    this.gre = new GameReplayEngine();
	    Game.checkMenus();
	    this.processLevelExists();
	}
    }

    public void loadReplay(final boolean laser, final int x, final int y) {
	this.gre.updateRedoHistory(laser, x, y);
    }

    void markPlayerAsDirty() {
	Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().markAsDirty(this.plMgr.getPlayerLocationX(),
		this.plMgr.getPlayerLocationY(), this.plMgr.getPlayerLocationZ());
    }

    void moveLoopDone() {
	this.moving = false;
	Game.checkMenus();
    }

    public boolean newGame() {
	final var guiResult = this.gui.newGame();
	if (!guiResult) {
	    // User cancelled
	    return false;
	}
	if (this.savedGameFlag && PartyManager.getParty() != null) {
	    return true;
	}
	return PartyManager.createParty(Settings.getGameDifficulty());
    }

    public void playDungeon() {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	if (app.getDungeonManager().getLoaded()) {
	    this.gui.initViewManager();
	    app.getGUIManager().hideGUI();
	    if (this.stateChanged) {
		// Initialize only if the maze state has changed
		app.getDungeonManager().getDungeonBase()
			.switchLevel(app.getDungeonManager().getDungeonBase().getStartLevel(0));
		this.stateChanged = false;
	    }
	    app.setInGame();
	    app.getDungeonManager().getDungeonBase().switchLevel(0);
	    final var res = app.getDungeonManager().getDungeonBase()
		    .switchToNextLevelWithDifficulty(GameGUI.getEnabledDifficulties());
	    if (res) {
		try {
		    this.resetPlayerLocation();
		} catch (final InvalidDungeonException iae) {
		    CommonDialogs.showErrorDialog(Strings.error(ErrorString.PLAYER_LOCATION),
			    Strings.untranslated(Untranslated.PROGRAM_NAME));
		    this.exitGame();
		    return;
		}
		this.updatePlayer();
		this.player.setSavedObject(new GameObject(ObjectImageId.EMPTY));
		this.st.setScoreFile(app.getDungeonManager().getScoresFileName());
		if (!this.savedGameFlag) {
		    this.st.resetScore(app.getDungeonManager().getScoresFileName());
		}
		this.updateInfo();
		// Make sure message area is attached to the border pane
		this.gui.updateGameGUI();
		// Make sure initial area player is in is visible
		final var px = m.getPlayerLocationX(0);
		final var py = m.getPlayerLocationY(0);
		m.updateVisibleSquares(px, py, 0);
		this.showOutput();
		// Start music
		if (Settings.getMusicEnabled()) {
		    ExternalMusicLoader.playExternalMusic(ExternalMusicImporter.getMusicBasePath(),
			    m.getMusicFilename());
		}
		app.getDungeonManager().getDungeonBase().setDirtyFlags(this.plMgr.getPlayerLocationZ());
		this.updateScoreText();
		this.showOutput();
		this.redrawDungeon();
		this.replaying = false;
		// Start animator, if enabled
		if (Settings.enableAnimation()) {
		    this.animator = new AnimationTask();
		    this.animator.start();
		}
	    } else {
		CommonDialogs.showDialog(Strings.game(GameString.NO_LEVEL_WITH_DIFFICULTY));
		Inconnuclear.getStuffBag().getGUIManager().showGUI();
	    }
	} else {
	    CommonDialogs.showDialog(Strings.menu(Menu.ERROR_NO_DUNGEON_OPENED));
	}
    }

    public void previousLevel() {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	m.resetHistoryEngine();
	this.gre = new GameReplayEngine();
	Game.checkMenus();
	this.suspendAnimator();
	m.restore();
	if (m.doesLevelExistOffset(-1)) {
	    m.switchLevelOffset(-1);
	    final var levelExists = m.switchToPreviousLevelWithDifficulty(GameGUI.getEnabledDifficulties());
	    if (levelExists) {
		m.setDirtyFlags(this.plMgr.getPlayerLocationZ());
		this.processLevelExists();
	    } else {
		CommonDialogs.showErrorDialog(Strings.error(ErrorString.NO_PREVIOUS_LEVEL),
			Strings.untranslated(Untranslated.PROGRAM_NAME));
	    }
	} else {
	    CommonDialogs.showErrorDialog(Strings.error(ErrorString.NO_PREVIOUS_LEVEL),
		    Strings.untranslated(Untranslated.PROGRAM_NAME));
	}
    }

    private void processLevelExists() {
	final var app = Inconnuclear.getStuffBag();
	try {
	    this.resetPlayerLocation();
	} catch (final InvalidDungeonException iae) {
	    CommonDialogs.showErrorDialog(Strings.error(ErrorString.PLAYER_LOCATION),
		    Strings.untranslated(Untranslated.PROGRAM_NAME));
	    this.exitGame();
	    return;
	}
	this.updatePlayer();
	this.st.resetScore(app.getDungeonManager().getScoresFileName());
	PartyInventory.resetInventory();
	this.updateScoreText();
	this.updateInfo();
	this.redrawDungeon();
	this.resumeAnimator();
    }

    private boolean readSolution() {
	try {
	    final var activeLevel = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().getActiveLevel();
	    final var levelFile = Inconnuclear.getStuffBag().getDungeonManager().getLastUsedDungeon();
	    final var filename = levelFile + Strings.UNDERSCORE + activeLevel
		    + Strings.fileExtension(FileExtension.SOLUTION);
	    try (var file = DataIOFactory.createReader(DataMode.CUSTOM_XML, filename)) {
		this.gre = GameReplayEngine.readReplay(file);
	    }
	    return true;
	} catch (final IOException ioe) {
	    return false;
	}
    }

    public void redoLastMove() {
	final var a = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase();
	if (a.tryRedo()) {
	    this.moving = false;
	    this.shotActive = false;
	    a.redo();
	    final var laser = a.getWhatWas().wasSomething(HistoryStatus.WAS_LASER);
	    final var missile = a.getWhatWas().wasSomething(HistoryStatus.WAS_MISSILE);
	    final var stunner = a.getWhatWas().wasSomething(HistoryStatus.WAS_STUNNER);
	    final var boost = a.getWhatWas().wasSomething(HistoryStatus.WAS_BOOST);
	    final var magnet = a.getWhatWas().wasSomething(HistoryStatus.WAS_MAGNET);
	    final var blue = a.getWhatWas().wasSomething(HistoryStatus.WAS_BLUE_LASER);
	    final var disrupt = a.getWhatWas().wasSomething(HistoryStatus.WAS_DISRUPTOR);
	    final var bomb = a.getWhatWas().wasSomething(HistoryStatus.WAS_BOMB);
	    final var heatBomb = a.getWhatWas().wasSomething(HistoryStatus.WAS_HEAT_BOMB);
	    final var iceBomb = a.getWhatWas().wasSomething(HistoryStatus.WAS_ICE_BOMB);
	    final var other = missile || stunner || boost || magnet || blue || disrupt || bomb || heatBomb || iceBomb;
	    if (other) {
		this.updateScore(0, 0, -1);
		if (boost) {
		    PartyInventory.fireBoost();
		} else if (magnet) {
		    PartyInventory.fireMagnet();
		} else if (missile) {
		    PartyInventory.fireMissile();
		} else if (stunner) {
		    PartyInventory.fireStunner();
		} else if (blue) {
		    PartyInventory.fireBlueLaser();
		} else if (disrupt) {
		    PartyInventory.fireDisruptor();
		} else if (bomb) {
		    PartyInventory.fireBomb();
		} else if (heatBomb) {
		    PartyInventory.fireHeatBomb();
		} else if (iceBomb) {
		    PartyInventory.fireIceBomb();
		}
	    } else if (laser && !other) {
		this.updateScore(0, 1, 0);
	    } else {
		this.updateScore(1, 0, 0);
	    }
	    try {
		this.resetPlayerLocation();
	    } catch (final InvalidDungeonException iae) {
		CommonDialogs.showErrorDialog(Strings.error(ErrorString.PLAYER_LOCATION),
			Strings.untranslated(Untranslated.PROGRAM_NAME));
		this.exitGame();
		return;
	    }
	    this.updatePlayer();
	    Game.updateUndo(laser, missile, stunner, boost, magnet, blue, disrupt, bomb, heatBomb, iceBomb);
	}
	Game.checkMenus();
	this.updateScoreText();
	a.setDirtyFlags(this.plMgr.getPlayerLocationZ());
	this.redrawDungeon();
    }

    public void redrawDungeon() {
	this.gui.redrawDungeon();
    }

    public void remoteDelayedDecayTo(final GameObject obj) {
	this.delayedDecayActive = true;
	this.delayedDecayObject = obj;
	this.remoteDecay = true;
    }

    void replayDone() {
	this.replaying = false;
    }

    boolean replayLastMove() {
	if (this.gre.tryRedo()) {
	    this.gre.redo();
	    final var x = this.gre.getX();
	    final var y = this.gre.getY();
	    final var currDir = this.player.getDirection();
	    final var newDir = DirectionResolver.resolve(x, y);
	    if (currDir != newDir) {
		this.player.setDirection(newDir);
		this.redrawDungeon();
	    } else {
		this.updatePositionRelative(x, y);
	    }
	    return true;
	}
	return false;
    }

    public void replaySolution() {
	if (this.lpbLoaded) {
	    this.replaying = true;
	    // Turn recording off
	    this.recording = false;
	    this.disableRecording();
	    try {
		this.resetCurrentLevel(false);
	    } catch (final InvalidDungeonException iae) {
		CommonDialogs.showErrorDialog(Strings.error(ErrorString.PLAYER_LOCATION),
			Strings.untranslated(Untranslated.PROGRAM_NAME));
		this.exitGame();
		return;
	    }
	    final var rt = new ReplayTask();
	    rt.start();
	} else {
	    final var success = this.readSolution();
	    if (!success) {
		CommonDialogs.showErrorDialog(Strings.error(ErrorString.NO_SOLUTION_FILE),
			Strings.untranslated(Untranslated.PROGRAM_NAME));
	    } else {
		this.replaying = true;
		// Turn recording off
		this.recording = false;
		this.disableRecording();
		try {
		    this.resetCurrentLevel(false);
		} catch (final InvalidDungeonException iae) {
		    CommonDialogs.showErrorDialog(Strings.error(ErrorString.PLAYER_LOCATION),
			    Strings.untranslated(Untranslated.PROGRAM_NAME));
		    this.exitGame();
		    return;
		}
		final var rt = new ReplayTask();
		rt.start();
	    }
	}
    }

    public void resetCurrentLevel() throws InvalidDungeonException {
	this.resetLevel(true);
    }

    private void resetCurrentLevel(final boolean flag) throws InvalidDungeonException {
	this.resetLevel(flag);
    }

    public void resetGameState() {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	app.getDungeonManager().setDirty(false);
	m.restore();
	this.setSavedGameFlag(false);
	this.st.resetScore();
	final var playerExists = m.doesPlayerExist(this.plMgr.getActivePlayerNumber());
	if (playerExists) {
	    this.plMgr.setPlayerLocation(m.getStartColumn(0), m.getStartRow(0), m.getStartFloor(0));
	}
    }

    private void resetLevel(final boolean flag) throws InvalidDungeonException {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	if (flag) {
	    m.resetHistoryEngine();
	}
	app.getDungeonManager().setDirty(true);
	if (this.mlot != null && this.mlot.isAlive()) {
	    this.abortMovementLaserObjectLoop();
	}
	this.moving = false;
	this.shotActive = false;
	PartyInventory.resetInventory();
	m.restore();
	m.setDirtyFlags(this.plMgr.getPlayerLocationZ());
	final var playerExists = m.doesPlayerExist(this.plMgr.getActivePlayerNumber());
	if (playerExists) {
	    this.st.resetScore(app.getDungeonManager().getScoresFileName());
	    this.resetPlayerLocation();
	    this.updatePlayer();
	    m.clearVirtualGrid();
	    this.updateScore();
	    this.decay();
	    this.redrawDungeon();
	}
	Game.checkMenus();
    }

    public void resetPlayerLocation() throws InvalidDungeonException {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	final var found = m.findPlayer(1);
	if (found == null) {
	    throw new InvalidDungeonException(Strings.error(ErrorString.PLAYER_LOCATION));
	}
	this.plMgr.setPlayerLocation(found[0], found[1], found[2]);
    }

    public void resetViewingWindow() {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	if (m != null && this.vwMgr != null) {
	    this.vwMgr.setViewingWindowLocationX(m.getPlayerLocationY(0) - GameViewingWindowManager.getOffsetFactorX());
	    this.vwMgr.setViewingWindowLocationY(m.getPlayerLocationX(0) - GameViewingWindowManager.getOffsetFactorY());
	}
    }

    public void resetViewingWindowAndPlayerLocation() {
	Game.resetPlayerLocation(0);
	this.resetViewingWindow();
    }

    private void resumeAnimator() {
	if (this.animator == null) {
	    this.animator = new AnimationTask();
	    this.animator.start();
	}
    }

    public void saveGameHook(final DataIOWriter dungeonFile) throws IOException {
	final var app = Inconnuclear.getStuffBag();
	dungeonFile.writeString(app.getDungeonManager().getScoresFileName());
	dungeonFile.writeLong(this.st.getMoves());
	dungeonFile.writeLong(this.st.getShots());
	dungeonFile.writeLong(this.st.getOthers());
	PartyInventory.writeInventory(dungeonFile);
    }

    void scheduleAutoMove() {
	this.autoMove = true;
    }

    @Override
    public void setInitialState() {
	this.gui.setInitialState();
    }

    public void setSavedGameFlag(final boolean value) {
	this.savedGameFlag = value;
    }

    public void setStatusMessage(final String msg) {
	this.gui.setStatusMessage(msg);
    }

    public void showOutput() {
	Inconnuclear.getStuffBag().setMode(StuffBag.STATUS_GAME);
	this.gui.showOutput();
    }

    public void showOutputAndKeepMusic() {
	Inconnuclear.getStuffBag().setMode(StuffBag.STATUS_GAME);
	this.gui.showOutputAndKeepMusic();
    }

    public void showScoreTable() {
	this.st.showScoreTable();
    }

    private void solvedDungeon() {
	PartyInventory.resetInventory();
	this.exitGame();
    }

    public void solvedLevel(final boolean playSound) {
	if (playSound) {
	    SoundLoader.playSound(Sounds.VICTORY);
	}
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	if (playSound) {
	    if (this.recording) {
		this.writeSolution();
	    }
	    if (this.st.checkScore()) {
		this.st.commitScore();
	    }
	}
	m.resetHistoryEngine();
	this.gre = new GameReplayEngine();
	Game.checkMenus();
	this.suspendAnimator();
	m.restore();
	if (m.doesLevelExistOffset(1)) {
	    m.switchLevelOffset(1);
	    final var levelExists = m.switchToNextLevelWithDifficulty(GameGUI.getEnabledDifficulties());
	    if (levelExists) {
		m.setDirtyFlags(this.plMgr.getPlayerLocationZ());
		this.processLevelExists();
	    } else {
		this.solvedDungeon();
	    }
	} else {
	    this.solvedDungeon();
	}
    }

    public void stateChanged() {
	this.stateChanged = true;
    }

    public void stopMovement() {
	this.mt.stopMovement();
    }

    private void suspendAnimator() {
	if (this.animator != null) {
	    this.animator.stopAnimator();
	    try {
		this.animator.join();
	    } catch (final InterruptedException ie) {
		// Ignore
	    }
	    this.animator = null;
	}
    }

    public void toggleRecording() {
	this.recording = !this.recording;
    }

    public void undoLastMove() {
	final var app = Inconnuclear.getStuffBag();
	final var a = app.getDungeonManager().getDungeonBase();
	if (a.tryUndo()) {
	    this.moving = false;
	    this.shotActive = false;
	    a.undo();
	    final var laser = a.getWhatWas().wasSomething(HistoryStatus.WAS_LASER);
	    final var missile = a.getWhatWas().wasSomething(HistoryStatus.WAS_MISSILE);
	    final var stunner = a.getWhatWas().wasSomething(HistoryStatus.WAS_STUNNER);
	    final var boost = a.getWhatWas().wasSomething(HistoryStatus.WAS_BOOST);
	    final var magnet = a.getWhatWas().wasSomething(HistoryStatus.WAS_MAGNET);
	    final var blue = a.getWhatWas().wasSomething(HistoryStatus.WAS_BLUE_LASER);
	    final var disrupt = a.getWhatWas().wasSomething(HistoryStatus.WAS_DISRUPTOR);
	    final var bomb = a.getWhatWas().wasSomething(HistoryStatus.WAS_BOMB);
	    final var heatBomb = a.getWhatWas().wasSomething(HistoryStatus.WAS_HEAT_BOMB);
	    final var iceBomb = a.getWhatWas().wasSomething(HistoryStatus.WAS_ICE_BOMB);
	    final var other = missile || stunner || boost || magnet || blue || disrupt || bomb || heatBomb || iceBomb;
	    if (other) {
		this.updateScore(0, 0, -1);
		if (boost) {
		    PartyInventory.addOneBoost();
		} else if (magnet) {
		    PartyInventory.addOneMagnet();
		} else if (missile) {
		    PartyInventory.addOneMissile();
		} else if (stunner) {
		    PartyInventory.addOneStunner();
		} else if (blue) {
		    PartyInventory.addOneBlueLaser();
		} else if (disrupt) {
		    PartyInventory.addOneDisruptor();
		} else if (bomb) {
		    PartyInventory.addOneBomb();
		} else if (heatBomb) {
		    PartyInventory.addOneHeatBomb();
		} else if (iceBomb) {
		    PartyInventory.addOneIceBomb();
		}
	    } else if (laser) {
		this.updateScore(0, -1, 0);
	    } else {
		this.updateScore(-1, 0, 0);
	    }
	    try {
		this.resetPlayerLocation();
	    } catch (final InvalidDungeonException iae) {
		CommonDialogs.showErrorDialog(Strings.error(ErrorString.PLAYER_LOCATION),
			Strings.untranslated(Untranslated.PROGRAM_NAME));
		this.exitGame();
		return;
	    }
	    this.updatePlayer();
	    Game.updateRedo(laser, missile, stunner, boost, magnet, blue, disrupt, bomb, heatBomb, iceBomb);
	}
	Game.checkMenus();
	this.updateScoreText();
	a.setDirtyFlags(this.plMgr.getPlayerLocationZ());
	this.redrawDungeon();
    }

    void unscheduleAutoMove() {
	this.autoMove = false;
    }

    private void updateInfo() {
	final var a = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase();
	this.levelInfo.setText(LocaleUtils.subst(Strings.dialog(DialogString.CURRENT_LEVEL_INFO),
		Integer.toString(a.getActiveLevel() + 1), a.getName().trim(), a.getAuthor().trim(),
		Strings.difficulty(a.getDifficulty())));
    }

    void updatePlayer() {
	final var template = new Party();
	this.player = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().getCell(
		this.plMgr.getPlayerLocationX(), this.plMgr.getPlayerLocationY(), this.plMgr.getPlayerLocationZ(),
		template.getLayer());
    }

    public void updatePositionAbsolute(final int x, final int y) {
	this.mt.moveAbsolute(x, y);
    }

    public void updatePositionAbsoluteNoEvents(final int z) {
	final var x = this.plMgr.getPlayerLocationX();
	final var y = this.plMgr.getPlayerLocationY();
	this.updatePositionAbsoluteNoEvents(x, y, z);
    }

    public void updatePositionAbsoluteNoEvents(final int x, final int y, final int z) {
	final var template = new Party();
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	this.plMgr.savePlayerLocation();
	try {
	    if (!m.getCell(x, y, z, template.getLayer()).isSolid()) {
		if (z != 0) {
		    this.suspendAnimator();
		    m.setDirtyFlags(this.plMgr.getPlayerLocationZ());
		    m.setDirtyFlags(z);
		}
		m.setCell(this.player.getSavedObject(), this.plMgr.getPlayerLocationX(),
			this.plMgr.getPlayerLocationY(), this.plMgr.getPlayerLocationZ(), template.getLayer());
		this.plMgr.setPlayerLocation(x, y, z);
		this.player.setSavedObject(m.getCell(this.plMgr.getPlayerLocationX(), this.plMgr.getPlayerLocationY(),
			this.plMgr.getPlayerLocationZ(), template.getLayer()));
		m.setCell(this.player, this.plMgr.getPlayerLocationX(), this.plMgr.getPlayerLocationY(),
			this.plMgr.getPlayerLocationZ(), template.getLayer());
		app.getDungeonManager().setDirty(true);
		if (z != 0) {
		    this.resumeAnimator();
		}
	    }
	} catch (final ArrayIndexOutOfBoundsException | NullPointerException np) {
	    this.plMgr.restorePlayerLocation();
	    m.setCell(this.player, this.plMgr.getPlayerLocationX(), this.plMgr.getPlayerLocationY(),
		    this.plMgr.getPlayerLocationZ(), template.getLayer());
	    Inconnuclear.getStuffBag().showMessage(Strings.game(GameString.OUTSIDE_ARENA));
	}
    }

    void updatePositionRelative(final int x, final int y) {
	if (!this.moving) {
	    this.moving = true;
	    if (this.mlot == null || !this.mlot.isAlive()) {
		this.mlot = new MLOTask();
	    }
	    this.mlot.activateMovement(x, y);
	    if (!this.mlot.isAlive()) {
		this.mlot.start();
	    }
	    if (this.replaying) {
		// Wait
		while (this.moving) {
		    try {
			Thread.sleep(100);
		    } catch (final InterruptedException ie) {
			// Ignore
		    }
		}
	    }
	}
    }

    public void updatePushedIntoPositionAbsolute(final int x, final int y, final int z, final int x2, final int y2,
	    final int z2, final GameObject pushedInto, final GameObject source) {
	final var template = new Party();
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	var needsFixup1 = false;
	var needsFixup2 = false;
	try {
	    if (!m.getCell(x, y, z, pushedInto.getLayer()).isSolid()) {
		final var saved = m.getCell(x, y, z, pushedInto.getLayer());
		final var there = m.getCell(x2, y2, z2, pushedInto.getLayer());
		if (there.isPlayer()) {
		    needsFixup1 = true;
		}
		if (saved.isPlayer()) {
		    needsFixup2 = true;
		}
		if (needsFixup2) {
		    m.setCell(this.player, x, y, z, template.getLayer());
		    pushedInto.setSavedObject(saved.getSavedObject());
		    this.player.setSavedObject(pushedInto);
		} else {
		    m.setCell(pushedInto, x, y, z, pushedInto.getLayer());
		    pushedInto.setSavedObject(saved);
		}
		if (needsFixup1) {
		    m.setCell(this.player, x2, y2, z2, template.getLayer());
		    this.player.setSavedObject(source);
		} else {
		    m.setCell(source, x2, y2, z2, pushedInto.getLayer());
		}
		app.getDungeonManager().setDirty(true);
	    }
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    final var e = new GameObject(ObjectImageId.EMPTY);
	    m.setCell(e, x2, y2, z2, pushedInto.getLayer());
	}
    }

    public synchronized void updatePushedPosition(final int x, final int y, final int pushX, final int pushY,
	    final GameObject o) {
	if (this.mlot == null || !this.mlot.isAlive()) {
	    this.mlot = new MLOTask();
	}
	this.mlot.activateObjects(x, y, pushX, pushY, o);
	if (!this.mlot.isAlive()) {
	    this.mlot.start();
	}
    }

    void updateReplay(final boolean laser, final int x, final int y) {
	this.gre.updateUndoHistory(laser, x, y);
    }

    private void updateScore() {
	this.scoreMoves.setText(LocaleUtils.subst(Strings.game(GameString.MOVES), Long.toString(this.st.getMoves())));
	this.scoreShots.setText(LocaleUtils.subst(Strings.game(GameString.SHOTS), Long.toString(this.st.getShots())));
	this.scoreOthers
		.setText(LocaleUtils.subst(Strings.game(GameString.OTHERS), Long.toString(this.st.getOthers())));
	this.updateScoreText();
    }

    void updateScore(final int moves, final int shots, final int others) {
	if (moves > 0) {
	    this.st.incrementMoves();
	} else if (moves < 0) {
	    this.st.decrementMoves();
	}
	if (shots > 0) {
	    this.st.incrementShots();
	} else if (shots < 0) {
	    this.st.decrementShots();
	}
	if (others > 0) {
	    this.st.incrementOthers();
	} else if (others < 0) {
	    this.st.decrementOthers();
	}
	this.scoreMoves.setText(LocaleUtils.subst(Strings.game(GameString.MOVES), Long.toString(this.st.getMoves())));
	this.scoreShots.setText(LocaleUtils.subst(Strings.game(GameString.SHOTS), Long.toString(this.st.getShots())));
	this.scoreOthers
		.setText(LocaleUtils.subst(Strings.game(GameString.OTHERS), Long.toString(this.st.getOthers())));
	this.updateScoreText();
    }

    private void updateScoreText() {
	// Ammo
	switch (this.otherAmmoMode) {
	case Game.OTHER_AMMO_MODE_MISSILES:
	    if (this.getCheatStatus(Game.CHEAT_MISSILES)) {
		this.otherAmmoLeft.setText(
			LocaleUtils.subst(Strings.game(GameString.MISSILES), Strings.game(GameString.INFINITE)));
	    } else {
		this.otherAmmoLeft.setText(LocaleUtils.subst(Strings.game(GameString.MISSILES),
			Integer.toString(PartyInventory.getMissilesLeft())));
	    }
	    break;
	case Game.OTHER_AMMO_MODE_STUNNERS:
	    if (this.getCheatStatus(Game.CHEAT_STUNNERS)) {
		this.otherAmmoLeft.setText(
			LocaleUtils.subst(Strings.game(GameString.STUNNERS), Strings.game(GameString.INFINITE)));
	    } else {
		this.otherAmmoLeft.setText(LocaleUtils.subst(Strings.game(GameString.STUNNERS),
			Integer.toString(PartyInventory.getStunnersLeft())));
	    }
	    break;
	case Game.OTHER_AMMO_MODE_BLUE_LASERS:
	    if (this.getCheatStatus(Game.CHEAT_BLUE_LASERS)) {
		this.otherAmmoLeft.setText(
			LocaleUtils.subst(Strings.game(GameString.BLUE_LASERS), Strings.game(GameString.INFINITE)));
	    } else {
		this.otherAmmoLeft.setText(LocaleUtils.subst(Strings.game(GameString.BLUE_LASERS),
			Integer.toString(PartyInventory.getBlueLasersLeft())));
	    }
	    break;
	case Game.OTHER_AMMO_MODE_DISRUPTORS:
	    if (this.getCheatStatus(Game.CHEAT_DISRUPTORS)) {
		this.otherAmmoLeft.setText(
			LocaleUtils.subst(Strings.game(GameString.DISRUPTORS), Strings.game(GameString.INFINITE)));
	    } else {
		this.otherAmmoLeft.setText(LocaleUtils.subst(Strings.game(GameString.DISRUPTORS),
			Integer.toString(PartyInventory.getDisruptorsLeft())));
	    }
	    break;
	default:
	    break;
	}
	// Tools
	if (this.otherToolMode == Game.OTHER_TOOL_MODE_BOOSTS) {
	    if (this.getCheatStatus(Game.CHEAT_BOOSTS)) {
		this.otherToolsLeft
			.setText(LocaleUtils.subst(Strings.game(GameString.BOOSTS), Strings.game(GameString.INFINITE)));
	    } else {
		this.otherToolsLeft.setText(LocaleUtils.subst(Strings.game(GameString.BOOSTS),
			Integer.toString(PartyInventory.getBoostsLeft())));
	    }
	} else if (this.otherToolMode == Game.OTHER_TOOL_MODE_MAGNETS) {
	    if (this.getCheatStatus(Game.CHEAT_MAGNETS)) {
		this.otherToolsLeft.setText(
			LocaleUtils.subst(Strings.game(GameString.MAGNETS), Strings.game(GameString.INFINITE)));
	    } else {
		this.otherToolsLeft.setText(LocaleUtils.subst(Strings.game(GameString.MAGNETS),
			Integer.toString(PartyInventory.getMagnetsLeft())));
	    }
	}
	// Ranges
	switch (this.otherRangeMode) {
	case Game.OTHER_RANGE_MODE_BOMBS:
	    if (this.getCheatStatus(Game.CHEAT_BOMBS)) {
		this.otherRangesLeft
			.setText(LocaleUtils.subst(Strings.game(GameString.BOMBS), Strings.game(GameString.INFINITE)));
	    } else {
		this.otherRangesLeft.setText(LocaleUtils.subst(Strings.game(GameString.BOMBS),
			Integer.toString(PartyInventory.getBombsLeft())));
	    }
	    break;
	case Game.OTHER_RANGE_MODE_HEAT_BOMBS:
	    if (this.getCheatStatus(Game.CHEAT_HEAT_BOMBS)) {
		this.otherRangesLeft.setText(
			LocaleUtils.subst(Strings.game(GameString.HEAT_BOMBS), Strings.game(GameString.INFINITE)));
	    } else {
		this.otherRangesLeft.setText(LocaleUtils.subst(Strings.game(GameString.HEAT_BOMBS),
			Integer.toString(PartyInventory.getHeatBombsLeft())));
	    }
	    break;
	case Game.OTHER_RANGE_MODE_ICE_BOMBS:
	    if (this.getCheatStatus(Game.CHEAT_ICE_BOMBS)) {
		this.otherRangesLeft.setText(
			LocaleUtils.subst(Strings.game(GameString.ICE_BOMBS), Strings.game(GameString.INFINITE)));
	    } else {
		this.otherRangesLeft.setText(LocaleUtils.subst(Strings.game(GameString.ICE_BOMBS),
			Integer.toString(PartyInventory.getIceBombsLeft())));
	    }
	    break;
	default:
	    break;
	}
    }

    public void viewingWindowSizeChanged() {
	this.gui.viewingWindowSizeChanged();
	this.resetViewingWindow();
    }

    void waitForMLOLoop() {
	if (this.mlot != null && this.mlot.isAlive()) {
	    var waiting = true;
	    while (waiting) {
		try {
		    this.mlot.join();
		    waiting = false;
		} catch (final InterruptedException ie) {
		    // Ignore
		}
	    }
	}
    }

    private void writeSolution() {
	try {
	    final var activeLevel = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().getActiveLevel();
	    final var levelFile = Inconnuclear.getStuffBag().getDungeonManager().getLastUsedDungeon();
	    final var filename = levelFile + Strings.UNDERSCORE + activeLevel
		    + Strings.fileExtension(FileExtension.SOLUTION);
	    try (var file = DataIOFactory.createWriter(DataMode.CUSTOM_XML, filename)) {
		this.gre.writeReplay(file);
	    }
	} catch (final IOException ioe) {
	    // Ignore
	}
    }
}
