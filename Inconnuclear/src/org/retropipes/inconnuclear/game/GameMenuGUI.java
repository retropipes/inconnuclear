/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.retropipes.diane.integration.Integration;
import org.retropipes.inconnuclear.Accelerators;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.locale.Menu;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.TimeTravel;

class GameMenuGUI {
    static void checkMenus() {
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

    // Fields
    private JMenu gameTimeTravelSubMenu;
    JCheckBoxMenuItem gameEraDistantPast, gameEraPast, gameEraPresent, gameEraFuture, gameEraDistantFuture;
    private JMenuItem gameReset, gameShowTable, gameReplaySolution, gameLoadLPB, gamePreviousLevel, gameSkipLevel,
	    gameLoadLevel, gameShowHint, gameCheats, gameChangeOtherAmmoMode, gameChangeOtherToolMode,
	    gameChangeOtherRangeMode, gameNewGame, gameEquipment, gameRegisterCharacter, gameUnregisterCharacter,
	    gameRemoveCharacter, gameViewStats;
    private JCheckBoxMenuItem gameRecordSolution;

    // Constructors
    public GameMenuGUI() {
	// Do nothing
    }

    public void attachAccelerators(final Accelerators accel) {
	this.gameReset.setAccelerator(accel.gameResetAccel);
	this.gameShowTable.setAccelerator(accel.gameShowTableAccel);
    }

    public void attachMenus() {
	final var app = Inconnuclear.getStuffBag();
	Integration.integrate().setDefaultMenuBar(app.getMenus().getMainMenuBar());
	app.getMenus().checkFlags();
    }

    public JMenu createCommandsMenu() {
	final var mhandler = new GameMenuEventHandler(this);
	final var gameMenu = new JMenu(Strings.menu(Menu.GAME));
	this.gameTimeTravelSubMenu = new JMenu(Strings.menu(Menu.TIME_TRAVEL));
	this.gameNewGame = new JMenuItem(Strings.menu(Menu.NEW_GAME));
	this.gameReset = new JMenuItem(Strings.menu(Menu.RESET_CURRENT_LEVEL));
	this.gameShowTable = new JMenuItem(Strings.menu(Menu.SHOW_SCORE_TABLE));
	this.gameReplaySolution = new JMenuItem(Strings.menu(Menu.REPLAY_SOLUTION));
	this.gameRecordSolution = new JCheckBoxMenuItem(Strings.menu(Menu.RECORD_SOLUTION));
	this.gameLoadLPB = new JMenuItem(Strings.menu(Menu.LOAD_REPLAY_FILE));
	this.gamePreviousLevel = new JMenuItem(Strings.menu(Menu.PREVIOUS_LEVEL));
	this.gameSkipLevel = new JMenuItem(Strings.menu(Menu.SKIP_LEVEL));
	this.gameLoadLevel = new JMenuItem(Strings.menu(Menu.LOAD_LEVEL));
	this.gameShowHint = new JMenuItem(Strings.menu(Menu.SHOW_HINT));
	this.gameCheats = new JMenuItem(Strings.menu(Menu.CHEATS));
	this.gameChangeOtherAmmoMode = new JMenuItem(Strings.menu(Menu.CHANGE_OTHER_AMMO));
	this.gameChangeOtherToolMode = new JMenuItem(Strings.menu(Menu.CHANGE_OTHER_TOOL));
	this.gameChangeOtherRangeMode = new JMenuItem(Strings.menu(Menu.CHANGE_OTHER_RANGE));
	this.gameEraDistantPast = new JCheckBoxMenuItem(Strings.timeTravel(TimeTravel.FAR_PAST), false);
	this.gameEraPast = new JCheckBoxMenuItem(Strings.timeTravel(TimeTravel.PAST), false);
	this.gameEraPresent = new JCheckBoxMenuItem(Strings.timeTravel(TimeTravel.PRESENT), true);
	this.gameEraFuture = new JCheckBoxMenuItem(Strings.timeTravel(TimeTravel.FUTURE), false);
	this.gameEraDistantFuture = new JCheckBoxMenuItem(Strings.timeTravel(TimeTravel.FAR_FUTURE), false);
	this.gameRegisterCharacter = new JMenuItem(Strings.menu(Menu.REGISTER_CHARACTER));
	this.gameUnregisterCharacter = new JMenuItem(Strings.menu(Menu.UNREGISTER_CHARACTER));
	this.gameRemoveCharacter = new JMenuItem(Strings.menu(Menu.REMOVE_CHARACTER));
	this.gameEquipment = new JMenuItem(Strings.menu(Menu.SHOW_EQUIPMENT));
	this.gameViewStats = new JMenuItem(Strings.menu(Menu.VIEW_STATISTICS));
	this.gameNewGame.addActionListener(mhandler);
	this.gameReset.addActionListener(mhandler);
	this.gameShowTable.addActionListener(mhandler);
	this.gameReplaySolution.addActionListener(mhandler);
	this.gameRecordSolution.addActionListener(mhandler);
	this.gameLoadLPB.addActionListener(mhandler);
	this.gamePreviousLevel.addActionListener(mhandler);
	this.gameSkipLevel.addActionListener(mhandler);
	this.gameLoadLevel.addActionListener(mhandler);
	this.gameShowHint.addActionListener(mhandler);
	this.gameCheats.addActionListener(mhandler);
	this.gameChangeOtherAmmoMode.addActionListener(mhandler);
	this.gameChangeOtherToolMode.addActionListener(mhandler);
	this.gameChangeOtherRangeMode.addActionListener(mhandler);
	this.gameEraDistantPast.addActionListener(mhandler);
	this.gameEraPast.addActionListener(mhandler);
	this.gameEraPresent.addActionListener(mhandler);
	this.gameEraFuture.addActionListener(mhandler);
	this.gameEraDistantFuture.addActionListener(mhandler);
	this.gameRegisterCharacter.addActionListener(mhandler);
	this.gameUnregisterCharacter.addActionListener(mhandler);
	this.gameRemoveCharacter.addActionListener(mhandler);
	this.gameEquipment.addActionListener(mhandler);
	this.gameViewStats.addActionListener(mhandler);
	this.gameTimeTravelSubMenu.add(this.gameEraDistantPast);
	this.gameTimeTravelSubMenu.add(this.gameEraPast);
	this.gameTimeTravelSubMenu.add(this.gameEraPresent);
	this.gameTimeTravelSubMenu.add(this.gameEraFuture);
	this.gameTimeTravelSubMenu.add(this.gameEraDistantFuture);
	gameMenu.add(this.gameNewGame);
	gameMenu.add(this.gameReset);
	gameMenu.add(this.gameShowTable);
	gameMenu.add(this.gameReplaySolution);
	gameMenu.add(this.gameRecordSolution);
	gameMenu.add(this.gameLoadLPB);
	gameMenu.add(this.gamePreviousLevel);
	gameMenu.add(this.gameSkipLevel);
	gameMenu.add(this.gameLoadLevel);
	gameMenu.add(this.gameShowHint);
	gameMenu.add(this.gameCheats);
	gameMenu.add(this.gameChangeOtherAmmoMode);
	gameMenu.add(this.gameChangeOtherToolMode);
	gameMenu.add(this.gameChangeOtherRangeMode);
	gameMenu.add(this.gameTimeTravelSubMenu);
	gameMenu.add(this.gameEquipment);
	gameMenu.add(this.gameRegisterCharacter);
	gameMenu.add(this.gameUnregisterCharacter);
	gameMenu.add(this.gameRemoveCharacter);
	gameMenu.add(this.gameViewStats);
	return gameMenu;
    }

    public void disableDirtyCommands() {
	// Do nothing
    }

    public void disableLoadedCommands() {
	// Do nothing
    }

    public void disableModeCommands() {
	this.gameNewGame.setEnabled(true);
	this.gameReset.setEnabled(false);
	this.gameShowTable.setEnabled(false);
	this.gameReplaySolution.setEnabled(false);
	this.gameRecordSolution.setEnabled(false);
	this.gameLoadLPB.setEnabled(false);
	this.gamePreviousLevel.setEnabled(false);
	this.gameSkipLevel.setEnabled(false);
	this.gameLoadLevel.setEnabled(false);
	this.gameShowHint.setEnabled(false);
	this.gameCheats.setEnabled(false);
	this.gameChangeOtherAmmoMode.setEnabled(false);
	this.gameChangeOtherToolMode.setEnabled(false);
	this.gameChangeOtherRangeMode.setEnabled(false);
	this.gameEraDistantPast.setEnabled(false);
	this.gameEraPast.setEnabled(false);
	this.gameEraPresent.setEnabled(false);
	this.gameEraFuture.setEnabled(false);
	this.gameEraDistantFuture.setEnabled(false);
	this.gameRegisterCharacter.setEnabled(true);
	this.gameUnregisterCharacter.setEnabled(true);
	this.gameRemoveCharacter.setEnabled(true);
	this.gameEquipment.setEnabled(false);
	this.gameViewStats.setEnabled(false);
    }

    void disableRecording() {
	this.gameRecordSolution.setSelected(false);
    }

    public void enableDirtyCommands() {
	// Do nothing
    }

    public void enableLoadedCommands() {
	// Do nothing
    }

    public void enableModeCommands() {
	this.gameNewGame.setEnabled(false);
	this.gameReset.setEnabled(true);
	this.gameShowTable.setEnabled(true);
	this.gameReplaySolution.setEnabled(true);
	this.gameRecordSolution.setEnabled(true);
	this.gameLoadLPB.setEnabled(true);
	this.gamePreviousLevel.setEnabled(true);
	this.gameSkipLevel.setEnabled(true);
	this.gameLoadLevel.setEnabled(true);
	this.gameShowHint.setEnabled(true);
	this.gameCheats.setEnabled(true);
	this.gameChangeOtherAmmoMode.setEnabled(true);
	this.gameChangeOtherToolMode.setEnabled(true);
	this.gameChangeOtherRangeMode.setEnabled(true);
	this.gameEraDistantPast.setEnabled(true);
	this.gameEraPast.setEnabled(true);
	this.gameEraPresent.setEnabled(true);
	this.gameEraFuture.setEnabled(true);
	this.gameEraDistantFuture.setEnabled(true);
	this.gameRegisterCharacter.setEnabled(false);
	this.gameUnregisterCharacter.setEnabled(false);
	this.gameRemoveCharacter.setEnabled(false);
	this.gameEquipment.setEnabled(true);
	this.gameViewStats.setEnabled(true);
    }

    public void setInitialState() {
	this.disableModeCommands();
    }
}
