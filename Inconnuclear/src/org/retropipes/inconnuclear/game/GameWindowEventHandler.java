/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.dungeon.manager.DungeonManager;

class GameWindowEventHandler extends WindowAdapter {
    GameWindowEventHandler() {
	// Do nothing
    }

    @Override
    public void windowClosing(final WindowEvent we) {
	try {
	    final var app = Inconnuclear.getStuffBag();
	    var success = false;
	    var status = 0;
	    if (app.getDungeonManager().getDirty()) {
		app.getDungeonManager();
		status = DungeonManager.showSaveDialog();
		if (status == CommonDialogs.YES_OPTION) {
		    app.getDungeonManager();
		    success = DungeonManager.saveGame();
		    if (success) {
			app.getGame().exitGame();
		    }
		} else if (status == CommonDialogs.NO_OPTION) {
		    app.getGame().exitGame();
		}
	    } else {
		app.getGame().exitGame();
	    }
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
    }
}