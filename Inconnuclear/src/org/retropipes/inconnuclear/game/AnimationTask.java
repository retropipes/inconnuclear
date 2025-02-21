/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.dungeon.DungeonData;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

class AnimationTask extends Thread {
    // Fields
    private boolean stop = false;

    // Constructors
    public AnimationTask() {
	this.setName(Strings.untranslated(Untranslated.ANIMATOR_NAME));
	this.setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run() {
	try {
	    final var a = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase();
	    while (!this.stop) {
		final var pz = Inconnuclear.getStuffBag().getGame().getPlayerManager().getPlayerLocationZ();
		final var maxX = a.getRows();
		final var maxY = a.getColumns();
		final var maxW = Layer.values().length;
		for (var x = 0; x < maxX; x++) {
		    for (var y = 0; y < maxY; y++) {
			for (var w = 0; w < maxW; w++) {
			    synchronized (DungeonData.LOCK_OBJECT) {
				final var oldFN = a.getCell(x, y, pz, w).getFrameNumber();
				a.getCell(x, y, pz, w).toggleFrameNumber();
				final var newFN = a.getCell(x, y, pz, w).getFrameNumber();
				if (oldFN != newFN) {
				    a.markAsDirty(x, y, pz);
				}
			    }
			}
		    }
		}
		Inconnuclear.getStuffBag().getGame().redrawDungeon();
		try {
		    Thread.sleep(200);
		} catch (final InterruptedException ie) {
		    // Ignore
		}
	    }
	} catch (final Throwable t) {
	    Inconnuclear.logError(t);
	}
    }

    void stopAnimator() {
	this.stop = true;
    }
}
