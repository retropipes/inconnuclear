/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.settings.Settings;

class ReplayTask extends Thread {
    // Constructors
    public ReplayTask() {
	// Do nothing
    }

    @Override
    public void run() {
	final var gm = Inconnuclear.getStuffBag().getGame();
	var result = true;
	while (result) {
	    result = gm.replayLastMove();
	    // Delay, for animation purposes
	    try {
		Thread.sleep(Settings.getReplaySpeed());
	    } catch (final InterruptedException ie) {
		// Ignore
	    }
	}
	gm.replayDone();
    }
}
