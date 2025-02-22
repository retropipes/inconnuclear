/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;

import org.retropipes.diane.LocaleUtils;
import org.retropipes.diane.asset.image.ImageCompositor;
import org.retropipes.diane.drawgrid.DrawGrid;
import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.integration.Integration;
import org.retropipes.inconnuclear.Accelerators;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageLoader;
import org.retropipes.inconnuclear.loader.music.MusicLoader;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Difficulty;
import org.retropipes.inconnuclear.locale.GameString;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Music;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;
import org.retropipes.inconnuclear.settings.Settings;
import org.retropipes.inconnuclear.utility.RCLGenerator;

class GameGUI {
    private static GameObject DARK = new GameObject(ObjectImageId.DARKNESS);

    private static void checkMenus() {
	GameMenuGUI.checkMenus();
    }

    static int[] getEnabledDifficulties() {
	final var temp = new ArrayList<Integer>();
	if (Settings.isKidsDifficultyEnabled()) {
	    temp.add(Integer.valueOf(Difficulty.KIDS.ordinal()));
	}
	if (Settings.isEasyDifficultyEnabled()) {
	    temp.add(Integer.valueOf(Difficulty.EASY.ordinal()));
	}
	if (Settings.isMediumDifficultyEnabled()) {
	    temp.add(Integer.valueOf(Difficulty.MEDIUM.ordinal()));
	}
	if (Settings.isHardDifficultyEnabled()) {
	    temp.add(Integer.valueOf(Difficulty.HARD.ordinal()));
	}
	if (Settings.isDeadlyDifficultyEnabled()) {
	    temp.add(Integer.valueOf(Difficulty.DEADLY.ordinal()));
	}
	final var temp2 = temp.toArray(new Integer[temp.size()]);
	final var retVal = new int[temp2.length];
	for (var x = 0; x < temp2.length; x++) {
	    retVal[x] = temp2[x];
	}
	return retVal;
    }

    // Fields
    private MainWindow mainWindow;
    private GameMovementEventHandler handler;
    private GameWindowEventHandler whandler;
    private MainContent borderPane, scorePane, infoPane, outerOutputPane, difficultyPane;
    private JLabel messageLabel;
    private JLabel scoreMoves;
    private JLabel scoreShots;
    private JLabel scoreOthers;
    private JLabel otherAmmoLeft;
    private JLabel otherToolsLeft;
    private JLabel otherRangesLeft;
    private JLabel levelInfo;
    private JButton difficultyOK;
    private GameDifficultyEventHandler dhandler;
    private GameViewingWindowManager vwMgr = null;
    private JList<String> difficultyList;
    private final StatGUI sg;
    private DrawGrid drawGrid;
    private GameDraw outputPane;
    private boolean knm;
    private boolean deferredRedraw;
    boolean eventFlag;
    private boolean newGameResult;
    private GameMenuGUI menuGUI;

    // Constructors
    public GameGUI() {
	this.deferredRedraw = false;
	this.eventFlag = true;
	this.newGameResult = false;
	this.sg = new StatGUI();
	this.menuGUI = new GameMenuGUI();
    }

    public void activeLanguageChanged() {
	this.setUpDifficultyDialog();
    }

    public void attachAccelerators(final Accelerators accel) {
	this.menuGUI.attachAccelerators(accel);
    }

    public void attachMenus() {
	final var app = Inconnuclear.getStuffBag();
	Integration.integrate().setDefaultMenuBar(app.getMenus().getMainMenuBar());
	app.getMenus().checkFlags();
    }

    public JMenu createCommandsMenu() {
	return this.menuGUI.createCommandsMenu();
    }

    void difficultyDialogCancelButtonClicked() {
	this.hideDifficultyDialog();
	this.newGameResult = false;
    }

    void difficultyDialogOKButtonClicked() {
	this.hideDifficultyDialog();
	if (this.difficultyList.isSelectedIndex(Difficulty.KIDS.ordinal())) {
	    Settings.setKidsDifficultyEnabled(true);
	} else {
	    Settings.setKidsDifficultyEnabled(false);
	}
	if (this.difficultyList.isSelectedIndex(Difficulty.EASY.ordinal())) {
	    Settings.setEasyDifficultyEnabled(true);
	} else {
	    Settings.setEasyDifficultyEnabled(false);
	}
	if (this.difficultyList.isSelectedIndex(Difficulty.MEDIUM.ordinal())) {
	    Settings.setMediumDifficultyEnabled(true);
	} else {
	    Settings.setMediumDifficultyEnabled(false);
	}
	if (this.difficultyList.isSelectedIndex(Difficulty.HARD.ordinal())) {
	    Settings.setHardDifficultyEnabled(true);
	} else {
	    Settings.setHardDifficultyEnabled(false);
	}
	if (this.difficultyList.isSelectedIndex(Difficulty.DEADLY.ordinal())) {
	    Settings.setDeadlyDifficultyEnabled(true);
	} else {
	    Settings.setDeadlyDifficultyEnabled(false);
	}
	this.newGameResult = true;
    }

    public void disableDirtyCommands() {
	this.menuGUI.disableDirtyCommands();
    }

    public void disableEvents() {
	this.mainWindow.setEnabled(false);
	this.eventFlag = false;
    }

    public void disableLoadedCommands() {
	this.menuGUI.disableLoadedCommands();
    }

    public void disableModeCommands() {
	this.menuGUI.disableModeCommands();
    }

    void disableRecording() {
	this.menuGUI.disableRecording();
    }

    public void enableDirtyCommands() {
	this.menuGUI.enableDirtyCommands();
    }

    public void enableEvents() {
	this.mainWindow.setEnabled(true);
	this.eventFlag = true;
    }

    public void enableLoadedCommands() {
	this.menuGUI.enableLoadedCommands();
    }

    public void enableModeCommands() {
	this.menuGUI.enableModeCommands();
    }

    public void hideOutput() {
	if (this.mainWindow != null) {
	    this.mainWindow.removeKeyListener(this.handler);
	    this.outputPane.removeMouseListener(this.handler);
	    this.mainWindow.removeWindowListener(this.whandler);
	    this.mainWindow.restoreSaved();
	}
    }

    void initViewManager() {
	if (this.vwMgr == null) {
	    this.vwMgr = Inconnuclear.getStuffBag().getGame().getViewManager();
	    this.setUpGUI();
	}
    }

    public void keepNextMessage() {
	this.knm = true;
    }

    boolean newGame() {
	this.difficultyList.clearSelection();
	final var retVal = GameGUI.getEnabledDifficulties();
	this.difficultyList.setSelectedIndices(retVal);
	this.showDifficultyDialog();
	return this.newGameResult;
    }

    void newGameResultFailure() {
	this.newGameResult = false;
    }

    void newGameResultSuccess() {
	this.newGameResult = true;
    }

    public void redrawDungeon() {
	// Draw the maze
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	int x, y, u, v;
	int xFix, yFix;
	boolean visible;
	u = m.getPlayerLocationX(0);
	v = m.getPlayerLocationY(0);
	final GameObject wall = new GameObject(ObjectImageId.WALL);
	for (x = this.vwMgr.getViewingWindowLocationX(); x <= this.vwMgr.getLowerRightViewingWindowLocationX(); x++) {
	    for (y = this.vwMgr.getViewingWindowLocationY(); y <= this.vwMgr
		    .getLowerRightViewingWindowLocationY(); y++) {
		xFix = x - this.vwMgr.getViewingWindowLocationX();
		yFix = y - this.vwMgr.getViewingWindowLocationY();
		visible = app.getDungeonManager().getDungeonBase().isSquareVisible(u, v, y, x, 0);
		try {
		    if (visible) {
			final var obj1 = m.getCell(y, x, 0, Layer.GROUND.ordinal());
			final var obj2 = m.getCell(y, x, 0, Layer.STATUS.ordinal());
			final var img1 = obj1.getImage();
			final var img2 = obj2.getImage();
			if (u == y && v == x) {
			    final GameObject obj3 = new GameObject(ObjectImageId.PARTY);
			    final var img3 = obj3.getImage();
			    final var cacheName = Strings.compositeCacheName(obj1.getCacheName(), obj2.getCacheName(),
				    obj3.getCacheName());
			    final var img = ImageCompositor.composite(cacheName, img1, img2, img3);
			    this.drawGrid.setImageCell(img, xFix, yFix);
			} else {
			    final var cacheName = Strings.compositeCacheName(obj1.getCacheName(), obj2.getCacheName());
			    final var img = ImageCompositor.composite(cacheName, img1, img2);
			    this.drawGrid.setImageCell(img, xFix, yFix);
			}
		    } else {
			this.drawGrid.setImageCell(
				ObjectImageLoader.load(GameGUI.DARK.getCacheName(), GameGUI.DARK.getIdValue()), xFix,
				yFix);
		    }
		} catch (final ArrayIndexOutOfBoundsException ae) {
		    this.drawGrid.setImageCell(wall.getImage(), xFix, yFix);
		}
	    }
	}
	if (this.knm) {
	    this.knm = false;
	} else {
	    this.setStatusMessage(" ");
	}
	this.outputPane.repaint();
	this.showOutputAndKeepMusic();
    }

    private void resetBorderPane() {
	this.borderPane.removeAll();
	this.borderPane.add(this.outputPane, BorderLayout.CENTER);
	this.borderPane.add(this.messageLabel, BorderLayout.NORTH);
	this.borderPane.add(this.sg.getStatsPane(), BorderLayout.EAST);
    }

    public void setInitialState() {
	this.disableModeCommands();
    }

    public void setStatusMessage(final String msg) {
	this.messageLabel.setText(msg);
    }

    private void setUpDifficultyDialog() {
	// Set up Difficulty Dialog
	this.dhandler = new GameDifficultyEventHandler(this);
	this.difficultyPane = MainWindow.createContent();
	final var listPane = MainWindow.createContent();
	final var buttonPane = MainWindow.createContent();
	this.difficultyList = new JList<>(Strings.allDifficulties());
	this.difficultyOK = new JButton(Strings.dialog(DialogString.OK_BUTTON));
	final var cancelButton = new JButton(Strings.dialog(DialogString.CANCEL_BUTTON));
	buttonPane.setLayout(new FlowLayout());
	buttonPane.add(this.difficultyOK);
	buttonPane.add(cancelButton);
	listPane.setLayout(new FlowLayout());
	listPane.add(this.difficultyList);
	this.difficultyPane.setLayout(new BorderLayout());
	this.difficultyPane.add(listPane, BorderLayout.CENTER);
	this.difficultyPane.add(buttonPane, BorderLayout.SOUTH);
	this.difficultyOK.setDefaultCapable(true);
	cancelButton.setDefaultCapable(false);
	this.difficultyOK.addActionListener(this.dhandler);
	cancelButton.addActionListener(this.dhandler);
    }

    private void showDifficultyDialog() {
	this.mainWindow.setAndSave(this.difficultyPane, Strings.game(GameString.SELECT_DIFFICULTY), this.difficultyOK);
	this.mainWindow.pack();
	this.mainWindow.addWindowListener(this.dhandler);
    }

    void hideDifficultyDialog() {
	this.mainWindow.removeWindowListener(this.dhandler);
	this.mainWindow.restoreSaved();
	this.mainWindow.pack();
    }

    private void setUpGUI() {
	this.handler = new GameMovementEventHandler(this);
	this.whandler = new GameWindowEventHandler();
	this.mainWindow = MainWindow.mainWindow();
	this.borderPane = MainWindow.createContent();
	this.borderPane.setLayout(new BorderLayout());
	this.messageLabel = new JLabel(" ");
	this.messageLabel.setOpaque(true);
	this.drawGrid = new DrawGrid(Settings.getViewingWindowSize());
	this.outputPane = new GameDraw(this.drawGrid);
	// Pasted code
	this.borderPane = MainWindow.createContent();
	this.borderPane.setLayout(new BorderLayout());
	this.mainWindow = MainWindow.mainWindow();
	this.outerOutputPane = RCLGenerator.generateRowColumnLabels();
	this.outputPane = new GameDraw();
	this.outputPane.setLayout(new GridLayout(GameViewingWindowManager.getFixedViewingWindowSizeX(),
		GameViewingWindowManager.getFixedViewingWindowSizeY()));
	this.scoreMoves = new JLabel(LocaleUtils.subst(Strings.game(GameString.MOVES), Integer.toString(0)));
	this.scoreShots = new JLabel(LocaleUtils.subst(Strings.game(GameString.SHOTS), Integer.toString(0)));
	this.scoreOthers = new JLabel(LocaleUtils.subst(Strings.game(GameString.OTHERS), Integer.toString(0)));
	this.otherAmmoLeft = new JLabel(LocaleUtils.subst(Strings.game(GameString.MISSILES), Integer.toString(0)));
	this.otherToolsLeft = new JLabel(LocaleUtils.subst(Strings.game(GameString.BOOSTS), Integer.toString(0)));
	this.otherRangesLeft = new JLabel(LocaleUtils.subst(Strings.game(GameString.BOMBS), Integer.toString(0)));
	this.scorePane = MainWindow.createContent();
	this.scorePane.setLayout(new FlowLayout());
	this.scorePane.add(this.scoreMoves);
	this.scorePane.add(this.scoreShots);
	this.scorePane.add(this.scoreOthers);
	this.scorePane.add(this.otherAmmoLeft);
	this.scorePane.add(this.otherToolsLeft);
	this.scorePane.add(this.otherRangesLeft);
	this.levelInfo = new JLabel(Strings.SPACE);
	this.infoPane = MainWindow.createContent();
	this.infoPane.setLayout(new FlowLayout());
	this.infoPane.add(this.levelInfo);
	this.scoreMoves.setLabelFor(this.outputPane);
	this.scoreShots.setLabelFor(this.outputPane);
	this.scoreOthers.setLabelFor(this.outputPane);
	this.otherAmmoLeft.setLabelFor(this.outputPane);
	this.otherToolsLeft.setLabelFor(this.outputPane);
	this.otherRangesLeft.setLabelFor(this.outputPane);
	this.levelInfo.setLabelFor(this.outputPane);
	this.outerOutputPane.add(this.outputPane, BorderLayout.CENTER);
	this.borderPane.add(this.outerOutputPane, BorderLayout.CENTER);
	this.borderPane.add(this.scorePane, BorderLayout.NORTH);
	this.borderPane.add(this.infoPane, BorderLayout.SOUTH);
	this.setUpDifficultyDialog();
    }

    public void showOutput() {
	if (MusicLoader.isMusicPlaying()) {
	    MusicLoader.stopMusic();
	}
	final var zoneID = PartyManager.getParty().getZone();
	if (zoneID == DungeonBase.getMaxLevels() - 1) {
	    MusicLoader.playMusic(Music.DEVILS_DANCE_1);
	} else {
	    MusicLoader.playMusic(Music.DUNGEON_00);
	}
	this.showOutputCommon();
    }

    public void showOutputAndKeepMusic() {
	this.showOutputCommon();
    }

    private void showOutputCommon() {
	final var app = Inconnuclear.getStuffBag();
	this.mainWindow.setAndSave(this.borderPane, Strings.untranslated(Untranslated.PROGRAM_NAME));
	this.mainWindow.addKeyListener(this.handler);
	this.outputPane.addMouseListener(this.handler);
	this.mainWindow.addWindowListener(this.whandler);
	Integration.integrate().setDefaultMenuBar(app.getMenus().getMainMenuBar());
	if (this.deferredRedraw) {
	    this.deferredRedraw = false;
	    this.redrawDungeon();
	}
	app.getMenus().checkFlags();
	GameGUI.checkMenus();
	this.updateStats();
    }

    void updateGameGUI() {
	this.resetBorderPane();
	this.sg.updateImages();
	this.sg.updateStats();
    }

    public void updateStats() {
	this.sg.updateStats();
    }

    void viewingWindowSizeChanged() {
	this.setUpGUI();
	this.updateGameGUI();
	this.deferredRedraw = true;
    }
}
