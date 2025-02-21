/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.creature.characterfile.CharacterRegistration;
import org.retropipes.inconnuclear.dungeon.GenerateDungeonTask;
import org.retropipes.inconnuclear.game.replay.ReplayManager;
import org.retropipes.inconnuclear.locale.Menu;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.TimeTravel;
import org.retropipes.inconnuclear.locale.Untranslated;
import org.retropipes.inconnuclear.utility.DungeonConstants;

class GameMenuEventHandler implements ActionListener {
    /**
     * 
     */
    private final GameMenuGUI gui;

    public GameMenuEventHandler(GameMenuGUI theGUI) {
	this.gui = theGUI;
	// Do nothing
    }

    // Handle menus
    @Override
    public void actionPerformed(final ActionEvent e) {
	try {
	    final var app = Inconnuclear.getStuffBag();
	    final var cmd = e.getActionCommand();
	    final var game = app.getGame();
	    if (cmd.equals(Strings.menu(Menu.RESET_CURRENT_LEVEL))) {
		final var result = CommonDialogs.showConfirmDialog(Strings.menu(Menu.CONFIRM_RESET_CURRENT_LEVEL),
			Strings.untranslated(Untranslated.PROGRAM_NAME));
		if (result == CommonDialogs.YES_OPTION) {
		    game.abortAndWaitForMLOLoop();
		    game.resetCurrentLevel();
		}
	    } else if (cmd.equals(Strings.menu(Menu.SHOW_SCORE_TABLE))) {
		game.showScoreTable();
	    } else if (cmd.equals(Strings.menu(Menu.REPLAY_SOLUTION))) {
		game.abortAndWaitForMLOLoop();
		game.replaySolution();
	    } else if (cmd.equals(Strings.menu(Menu.RECORD_SOLUTION))) {
		game.toggleRecording();
	    } else if (cmd.equals(Strings.menu(Menu.LOAD_REPLAY_FILE))) {
		game.abortAndWaitForMLOLoop();
		ReplayManager.loadReplay();
	    } else if (cmd.equals(Strings.menu(Menu.PREVIOUS_LEVEL))) {
		game.abortAndWaitForMLOLoop();
		game.previousLevel();
	    } else if (cmd.equals(Strings.menu(Menu.SKIP_LEVEL))) {
		game.abortAndWaitForMLOLoop();
		game.solvedLevel(false);
	    } else if (cmd.equals(Strings.menu(Menu.LOAD_LEVEL))) {
		game.abortAndWaitForMLOLoop();
		game.loadLevel();
	    } else if (cmd.equals(Strings.menu(Menu.SHOW_HINT))) {
		CommonDialogs.showDialog(app.getDungeonManager().getDungeonBase().getHint().trim());
	    } else if (cmd.equals(Strings.menu(Menu.CHEATS))) {
		game.enterCheatCode();
	    } else if (cmd.equals(Strings.menu(Menu.CHANGE_OTHER_AMMO))) {
		game.changeOtherAmmoMode();
	    } else if (cmd.equals(Strings.menu(Menu.CHANGE_OTHER_TOOL))) {
		game.changeOtherToolMode();
	    } else if (cmd.equals(Strings.menu(Menu.CHANGE_OTHER_RANGE))) {
		game.changeOtherRangeMode();
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.FAR_PAST))) {
		// Time Travel: Distant Past
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_DISTANT_PAST);
		this.gui.gameEraDistantPast.setSelected(true);
		this.gui.gameEraPast.setSelected(false);
		this.gui.gameEraPresent.setSelected(false);
		this.gui.gameEraFuture.setSelected(false);
		this.gui.gameEraDistantFuture.setSelected(false);
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.PAST))) {
		// Time Travel: Past
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_PAST);
		this.gui.gameEraDistantPast.setSelected(false);
		this.gui.gameEraPast.setSelected(true);
		this.gui.gameEraPresent.setSelected(false);
		this.gui.gameEraFuture.setSelected(false);
		this.gui.gameEraDistantFuture.setSelected(false);
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.PRESENT))) {
		// Time Travel: Present
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_PRESENT);
		this.gui.gameEraDistantPast.setSelected(false);
		this.gui.gameEraPast.setSelected(false);
		this.gui.gameEraPresent.setSelected(true);
		this.gui.gameEraFuture.setSelected(false);
		this.gui.gameEraDistantFuture.setSelected(false);
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.FUTURE))) {
		// Time Travel: Future
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_FUTURE);
		this.gui.gameEraDistantPast.setSelected(false);
		this.gui.gameEraPast.setSelected(false);
		this.gui.gameEraPresent.setSelected(false);
		this.gui.gameEraFuture.setSelected(true);
		this.gui.gameEraDistantFuture.setSelected(false);
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.FAR_FUTURE))) {
		// Time Travel: Distant Future
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_DISTANT_FUTURE);
		this.gui.gameEraDistantPast.setSelected(false);
		this.gui.gameEraPast.setSelected(false);
		this.gui.gameEraPresent.setSelected(false);
		this.gui.gameEraFuture.setSelected(false);
		this.gui.gameEraDistantFuture.setSelected(true);
	    } else if (cmd.equals(Strings.menu(Menu.NEW_GAME))) {
		// Start a new game
		final var proceed = app.getGame().newGame();
		if (proceed) {
		    new GenerateDungeonTask(true).start();
		}
	    } else if (cmd.equals(Strings.menu(Menu.REGISTER_CHARACTER))) {
		// Register Character
		CharacterRegistration.registerCharacter();
	    } else if (cmd.equals(Strings.menu(Menu.UNREGISTER_CHARACTER))) {
		// Unregister Character
		CharacterRegistration.unregisterCharacter();
	    } else if (cmd.equals(Strings.menu(Menu.REMOVE_CHARACTER))) {
		// Confirm
		final var confirm = CommonDialogs
			.showConfirmDialog("WARNING: This will DELETE the character from disk,\n"
				+ "and CANNOT be undone! Proceed anyway?", "Remove Character");
		if (confirm == CommonDialogs.YES_OPTION) {
		    // Remove Character
		    CharacterRegistration.removeCharacter();
		}
	    } else if (cmd.equals(Strings.menu(Menu.SHOW_EQUIPMENT))) {
		InventoryViewer.showEquipmentDialog();
	    } else if (cmd.equals(Strings.menu(Menu.VIEW_STATISTICS))) {
		// View Statistics
		StatisticsViewer.viewStatistics();
	    }
	    app.getMenus().checkFlags();
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
    }
}