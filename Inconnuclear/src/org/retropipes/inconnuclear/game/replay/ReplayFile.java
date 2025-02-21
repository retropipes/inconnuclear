/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game.replay;

import java.io.FileInputStream;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.locale.ErrorString;
import org.retropipes.inconnuclear.locale.GameString;
import org.retropipes.inconnuclear.locale.Strings;

class ReplayFile {
    private static void decodeData(final byte d) {
	final var gm = Inconnuclear.getStuffBag().getGame();
	switch (d) {
	case 0x20:
	    gm.loadReplay(true, 0, 0);
	    break;
	case 0x25:
	    gm.loadReplay(false, -1, 0);
	    break;
	case 0x26:
	    gm.loadReplay(false, 0, -1);
	    break;
	case 0x27:
	    gm.loadReplay(false, 1, 0);
	    break;
	case 0x28:
	    gm.loadReplay(false, 0, 1);
	    break;
	default:
	    break;
	}
    }

    static void loadLPB(final FileInputStream file) {
	// Load LPB
	final var success = ReplayFileLoader.loadLPB(file);
	if (!success) {
	    CommonDialogs.showErrorDialog(Strings.error(ErrorString.REPLAY_LOAD_FAILURE),
		    Strings.game(GameString.LOAD_PLAYBACK));
	} else {
	    final var gm = Inconnuclear.getStuffBag().getGame();
	    gm.clearReplay();
	    final var data = ReplayFileLoader.getData();
	    for (var x = data.length - 1; x >= 0; x--) {
		ReplayFile.decodeData(data[x]);
	    }
	    CommonDialogs.showTitledDialog(Strings.game(GameString.PLAYBACK_LOADED),
		    Strings.game(GameString.LOAD_PLAYBACK));
	}
    }

    private ReplayFile() {
	// Do nothing
    }
}