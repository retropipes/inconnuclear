/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map;

import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.battle.Battle;
import org.retropipes.inconnuclear.settings.Settings;

public class MapBattleAITask extends Thread {
    // Fields
    private final Battle b;

    // Constructors
    public MapBattleAITask(final Battle battle) {
	this.setName("Map AI Runner");
	this.b = battle;
    }

    public synchronized void aiRun() {
	this.notify();
    }

    public synchronized void aiWait() {
	try {
	    this.wait();
	} catch (final InterruptedException e) {
	    // Ignore
	}
    }

    @Override
    public void run() {
	try {
	    this.aiWait();
	    while (true) {
		this.b.executeNextAIAction();
		if (this.b.getLastAIActionResult()) {
		    // Delay, for animation purposes
		    try {
			final var battleSpeed = Settings.getBattleSpeed();
			Thread.sleep(battleSpeed);
		    } catch (final InterruptedException i) {
			// Ignore
		    }
		}
	    }
	} catch (final Throwable t) {
	    Inconnuclear.logError(t);
	}
    }
}
