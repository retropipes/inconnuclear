/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map.time;

import java.util.TimerTask;

import org.retropipes.diane.Diane;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.StuffBag;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;

class MapTimeBattlePlayerTask extends TimerTask {
    private final MapTimeBattleLogic logic;

    public MapTimeBattlePlayerTask(MapTimeBattleLogic mapTimeBattleLogic) {
	this.logic = mapTimeBattleLogic;
    }

    @Override
    public void run() {
	try {
	    final var app = Inconnuclear.getStuffBag();
	    final var b = app.getBattle();
	    if (app.getMode() == StuffBag.STATUS_BATTLE && b instanceof MapTimeBattleLogic) {
		final var gui = this.logic.battleGUI;
		if (!gui.isPlayerActionBarFull()) {
		    gui.turnEventHandlersOff();
		    gui.updatePlayerActionBarValue();
		    if (gui.isPlayerActionBarFull()) {
			SoundLoader.playSound(Sounds.PLAYER_UP);
			gui.turnEventHandlersOn();
		    }
		} else {
		    gui.turnEventHandlersOn();
		}
	    }
	} catch (final Throwable t) {
	    Diane.handleError(t);
	}
    }
}