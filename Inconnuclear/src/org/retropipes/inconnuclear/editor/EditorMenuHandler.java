/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.StuffBag;
import org.retropipes.inconnuclear.locale.EditorString;
import org.retropipes.inconnuclear.locale.Menu;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.TimeTravel;
import org.retropipes.inconnuclear.utility.DungeonConstants;

class EditorMenuHandler implements ActionListener {
    private final Editor editor;

    public EditorMenuHandler(Editor theEditor) {
	this.editor = theEditor;
    }

    // Handle menus
    @Override
    public void actionPerformed(final ActionEvent e) {
	try {
	    final var app = Inconnuclear.getStuffBag();
	    final var cmd = e.getActionCommand();
	    if (cmd.equals(Strings.menu(Menu.UNDO))) {
		// Undo most recent action
		if (app.getMode() == StuffBag.STATUS_EDITOR) {
		    this.editor.undo();
		} else if (app.getMode() == StuffBag.STATUS_GAME) {
		    app.getGame().abortAndWaitForMLOLoop();
		    app.getGame().undoLastMove();
		}
	    } else if (cmd.equals(Strings.menu(Menu.REDO))) {
		// Redo most recent undone action
		if (app.getMode() == StuffBag.STATUS_EDITOR) {
		    this.editor.redo();
		} else if (app.getMode() == StuffBag.STATUS_GAME) {
		    app.getGame().abortAndWaitForMLOLoop();
		    app.getGame().redoLastMove();
		}
	    } else if (cmd.equals(Strings.menu(Menu.CUT_LEVEL))) {
		// Cut Level
		final var level = this.editor.getLocationManager().getEditorLocationU();
		app.getDungeonManager().getDungeonBase().cutLevel();
		this.editor.fixLimits();
		this.editor.updateEditorLevelAbsolute(level);
	    } else if (cmd.equals(Strings.menu(Menu.COPY_LEVEL))) {
		// Copy Level
		app.getDungeonManager().getDungeonBase().copyLevel();
	    } else if (cmd.equals(Strings.menu(Menu.PASTE_LEVEL))) {
		// Paste Level
		app.getDungeonManager().getDungeonBase().pasteLevel();
		this.editor.fixLimits();
		this.editor.redrawEditor();
	    } else if (cmd.equals(Strings.menu(Menu.INSERT_LEVEL_FROM_CLIPBOARD))) {
		// Insert Level From Clipboard
		app.getDungeonManager().getDungeonBase().insertLevelFromClipboard();
		this.editor.fixLimits();
	    } else if (cmd.equals(Strings.menu(Menu.CLEAR_HISTORY))) {
		// Clear undo/redo history, confirm first
		final var res = CommonDialogs.showConfirmDialog(Strings.menu(Menu.CONFIRM_CLEAR_HISTORY),
			Strings.editor(EditorString.EDITOR));
		if (res == CommonDialogs.YES_OPTION) {
		    this.editor.clearHistory();
		}
	    } else if (cmd.equals(Strings.menu(Menu.GO_TO_LEVEL))) {
		// Go To Level
		this.editor.goToLevelHandler();
	    } else if (cmd.equals(Strings.menu(Menu.UP_ONE_FLOOR))) {
		// Go up one floor
		this.editor.updateEditorPosition(1, 0);
	    } else if (cmd.equals(Strings.menu(Menu.DOWN_ONE_FLOOR))) {
		// Go down one floor
		this.editor.updateEditorPosition(-1, 0);
	    } else if (cmd.equals(Strings.menu(Menu.UP_ONE_LEVEL))) {
		// Go up one level
		this.editor.updateEditorPosition(0, 1);
	    } else if (cmd.equals(Strings.menu(Menu.DOWN_ONE_LEVEL))) {
		// Go down one level
		this.editor.updateEditorPosition(0, -1);
	    } else if (cmd.equals(Strings.menu(Menu.ADD_A_LEVEL))) {
		// Add a level
		this.editor.addLevel();
	    } else if (cmd.equals(Strings.menu(Menu.REMOVE_A_LEVEL))) {
		// Remove a level
		this.editor.removeLevel();
	    } else if (cmd.equals(Strings.menu(Menu.FILL_CURRENT_LEVEL))) {
		// Fill level
		this.editor.fillLevel();
	    } else if (cmd.equals(Strings.menu(Menu.RESIZE_CURRENT_LEVEL))) {
		// Resize level
		this.editor.resizeLevel();
	    } else if (cmd.equals(Strings.menu(Menu.LEVEL_PREFERENCES))) {
		// Set Level Preferences
		this.editor.setLevelPrefs();
	    } else if (cmd.equals(Strings.menu(Menu.SET_START_POINT))) {
		// Set Start Point
		this.editor.editPlayerLocation();
	    } else if (cmd.equals(Strings.menu(Menu.SET_MUSIC))) {
		// Set Music
		this.editor.defineDungeonMusic();
	    } else if (cmd.equals(Strings.menu(Menu.CHANGE_LAYER))) {
		// Change Layer
		this.editor.changeLayer();
	    } else if (cmd.equals(Strings.menu(Menu.ENABLE_GLOBAL_MOVE_SHOOT))) {
		// Enable Global Move-Shoot
		this.editor.enableGlobalMoveShoot();
	    } else if (cmd.equals(Strings.menu(Menu.DISABLE_GLOBAL_MOVE_SHOOT))) {
		// Disable Global Move-Shoot
		this.editor.disableGlobalMoveShoot();
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.FAR_PAST))) {
		// Time Travel: Distant Past
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_DISTANT_PAST);
		this.editor.editorEraDistantPast.setSelected(true);
		this.editor.editorEraPast.setSelected(false);
		this.editor.editorEraPresent.setSelected(false);
		this.editor.editorEraFuture.setSelected(false);
		this.editor.editorEraDistantFuture.setSelected(false);
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.PAST))) {
		// Time Travel: Past
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_PAST);
		this.editor.editorEraDistantPast.setSelected(false);
		this.editor.editorEraPast.setSelected(true);
		this.editor.editorEraPresent.setSelected(false);
		this.editor.editorEraFuture.setSelected(false);
		this.editor.editorEraDistantFuture.setSelected(false);
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.PRESENT))) {
		// Time Travel: Present
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_PRESENT);
		this.editor.editorEraDistantPast.setSelected(false);
		this.editor.editorEraPast.setSelected(false);
		this.editor.editorEraPresent.setSelected(true);
		this.editor.editorEraFuture.setSelected(false);
		this.editor.editorEraDistantFuture.setSelected(false);
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.FUTURE))) {
		// Time Travel: Future
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_FUTURE);
		this.editor.editorEraDistantPast.setSelected(false);
		this.editor.editorEraPast.setSelected(false);
		this.editor.editorEraPresent.setSelected(false);
		this.editor.editorEraFuture.setSelected(true);
		this.editor.editorEraDistantFuture.setSelected(false);
	    } else if (cmd.equals(Strings.timeTravel(TimeTravel.FAR_FUTURE))) {
		// Time Travel: Distant Future
		app.getDungeonManager().getDungeonBase().switchEra(DungeonConstants.ERA_DISTANT_FUTURE);
		this.editor.editorEraDistantPast.setSelected(false);
		this.editor.editorEraPast.setSelected(false);
		this.editor.editorEraPresent.setSelected(false);
		this.editor.editorEraFuture.setSelected(false);
		this.editor.editorEraDistantFuture.setSelected(true);
	    }
	    app.getMenus().checkFlags();
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
    }
}