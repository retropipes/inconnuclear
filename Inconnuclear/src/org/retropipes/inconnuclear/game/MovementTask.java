/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.locale.Layer;

final class MovementTask extends Thread {
    private static void checkGameOver() {
	if (!PartyManager.getParty().isAlive()) {
	    SoundLoader.playSound(Sounds.GAME_OVER);
	    CommonDialogs.showDialog(
		    "You have died! You lose 10% of your experience and all your Gold, but you are healed fully.");
	    PartyManager.getParty().getLeader().onDeath(-10);
	}
    }

    private static boolean checkSolid(final GameObject inside, final GameObject below, final GameObject nextBelow,
	    final GameObject nextAbove) {
	final var insideSolid = inside.isSolid();
	final var belowSolid = below.isSolid();
	final var nextBelowSolid = nextBelow.isSolid();
	final var nextAboveSolid = nextAbove.isSolid();
	if (insideSolid || belowSolid || nextBelowSolid || nextAboveSolid) {
	    return false;
	}
	return true;
    }

    private static void fireMoveFailedActions(final int x, final int y, final GameObject inside, final GameObject below,
	    final GameObject nextBelow, final GameObject nextAbove) {
	final var insideSolid = inside.isSolid();
	final var belowSolid = below.isSolid();
	final var nextBelowSolid = nextBelow.isSolid();
	final var nextAboveSolid = nextAbove.isSolid();
	final var z = 0;
	if (insideSolid) {
	    inside.moveFailedAction(x, y, z);
	}
	if (belowSolid) {
	    below.moveFailedAction(x, y, z);
	}
	if (nextBelowSolid) {
	    nextBelow.moveFailedAction(x, y, z);
	}
	if (nextAboveSolid) {
	    nextAbove.moveFailedAction(x, y, z);
	}
    }

    // Fields
    private final GameViewingWindowManager vwMgr;
    private final GameGUI gui;
    private GameObject saved;
    private boolean proceed;
    private boolean relative;
    private int moveX, moveY;

    // Constructors
    public MovementTask(final GameViewingWindowManager view, final GameGUI gameGUI) {
	this.setName("Movement Handler");
	this.vwMgr = view;
	this.gui = gameGUI;
	this.saved = new GameObject(ObjectImageId.EMPTY);
    }

    private boolean checkLoopCondition(final GameObject below, final GameObject nextBelow, final GameObject nextAbove) {
	return this.proceed && !nextBelow.hasFriction()
		&& MovementTask.checkSolid(this.saved, below, nextBelow, nextAbove);
    }

    void fireStepActions() {
	final var m = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase();
	final var px = m.getPlayerLocationX(0);
	final var py = m.getPlayerLocationY(0);
	m.updateVisibleSquares(px, py, 0);
	m.tickTimers();
	this.gui.updateStats();
	MovementTask.checkGameOver();
    }

    public synchronized void moveAbsolute(final int x, final int y) {
	this.moveX = x;
	this.moveY = y;
	this.relative = false;
	this.notify();
    }

    public synchronized void moveRelative(final int x, final int y) {
	this.moveX = x;
	this.moveY = y;
	this.relative = true;
	this.notify();
    }

    private void redrawDungeon() {
	this.gui.redrawDungeon();
    }

    @Override
    public void run() {
	try {
	    while (true) {
		this.waitForWork();
		if (this.relative) {
		    this.updatePositionRelative(this.moveX, this.moveY);
		}
		if (!this.relative) {
		    this.updatePositionAbsolute(this.moveX, this.moveY);
		}
	    }
	} catch (final Throwable t) {
	    Inconnuclear.logError(t);
	}
    }

    public void stopMovement() {
	this.proceed = false;
    }

    private void updatePositionAbsolute(final int x, final int y) {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	try {
	    m.getCell(x, y, 0, Layer.STATUS.ordinal()).preMoveAction(true, x, y);
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    // Ignore
	}
	m.savePlayerLocation();
	this.vwMgr.saveViewingWindow();
	try {
	    if (!m.getCell(x, y, 0, Layer.STATUS.ordinal()).isSolid()) {
		m.setPlayerLocationX(x, 0);
		m.setPlayerLocationY(y, 0);
		this.vwMgr.setViewingWindowLocationX(
			m.getPlayerLocationY(0) - GameViewingWindowManager.getOffsetFactorX());
		this.vwMgr.setViewingWindowLocationY(
			m.getPlayerLocationX(0) - GameViewingWindowManager.getOffsetFactorY());
		this.saved = m.getCell(m.getPlayerLocationX(0), m.getPlayerLocationY(0), 0, Layer.STATUS.ordinal());
		app.getDungeonManager().setDirty(true);
		this.saved.postMoveAction(x, y, 0);
		final var px = m.getPlayerLocationX(0);
		final var py = m.getPlayerLocationY(0);
		m.updateVisibleSquares(px, py, 0);
		this.redrawDungeon();
	    }
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    m.restorePlayerLocation();
	    this.vwMgr.restoreViewingWindow();
	    app.showMessage("Can't go outside the maze");
	}
    }

    private void updatePositionRelative(final int dirX, final int dirY) {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	var px = m.getPlayerLocationX(0);
	var py = m.getPlayerLocationY(0);
	final var pz = 0;
	final var fX = dirX;
	final var fY = dirY;
	this.proceed = false;
	GameObject below = null;
	GameObject nextBelow = null;
	GameObject nextAbove = new GameObject(ObjectImageId.WALL);
	do {
	    try {
		below = m.getCell(px, py, 0, Layer.GROUND.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException ae) {
		below = new GameObject(ObjectImageId.EMPTY);
	    }
	    try {
		nextBelow = m.getCell(px + fX, py + fY, 0, Layer.GROUND.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException ae) {
		nextBelow = new GameObject(ObjectImageId.EMPTY);
	    }
	    try {
		nextAbove = m.getCell(px + fX, py + fY, 0, Layer.STATUS.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException ae) {
		nextAbove = new GameObject(ObjectImageId.WALL);
	    }
	    try {
		this.proceed = nextAbove.preMoveAction(true, px + fX, py + fY);
	    } catch (final ArrayIndexOutOfBoundsException ae) {
		this.proceed = true;
	    }
	    if (this.proceed) {
		m.savePlayerLocation();
		this.vwMgr.saveViewingWindow();
		try {
		    if (MovementTask.checkSolid(this.saved, below, nextBelow, nextAbove)) {
			m.offsetPlayerLocationX(fX, 0);
			m.offsetPlayerLocationY(fY, 0);
			px += fX;
			py += fY;
			this.vwMgr.offsetViewingWindowLocationX(fY);
			this.vwMgr.offsetViewingWindowLocationY(fX);
			app.getDungeonManager().setDirty(true);
			app.saveFormerMode();
			this.fireStepActions();
			this.redrawDungeon();
			if (app.modeChanged()) {
			    this.proceed = false;
			}
			if (this.proceed) {
			    this.saved = m.getCell(px, py, 0, Layer.STATUS.ordinal());
			}
		    } else {
			// Move failed - object is solid in that direction
			MovementTask.fireMoveFailedActions(px + fX, py + fY, this.saved, below, nextBelow, nextAbove);
			this.fireStepActions();
		    }
		} catch (final ArrayIndexOutOfBoundsException ae) {
		    this.vwMgr.restoreViewingWindow();
		    m.restorePlayerLocation();
		    // Move failed - attempted to go outside the maze
		    nextAbove.moveFailedAction(px, py, pz);
		    app.showMessage("Can't go that way");
		    nextAbove = new GameObject(ObjectImageId.EMPTY);
		    this.proceed = false;
		}
		this.fireStepActions();
	    } else {
		// Move failed - pre-move check failed
		nextAbove.moveFailedAction(px + fX, py + fY, pz);
		this.fireStepActions();
		this.proceed = false;
	    }
	    px = m.getPlayerLocationX(0);
	    py = m.getPlayerLocationY(0);
	} while (this.checkLoopCondition(below, nextBelow, nextAbove));
    }

    private synchronized void waitForWork() {
	try {
	    this.wait();
	} catch (final InterruptedException e) {
	    // Ignore
	}
    }
}
