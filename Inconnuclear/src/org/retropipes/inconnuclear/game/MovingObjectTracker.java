/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import org.retropipes.diane.direction.DirectionResolver;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.locale.Layer;

final class MovingObjectTracker {
    private static boolean checkSolid(final GameObject next) {
	final var nextSolid = next.isSolid();
	if (!nextSolid || next.isPlayer()) {
	    return true;
	}
	return false;
    }

    // Fields
    private boolean objectMoving;
    private int objCumX, objCumY, objIncX, objIncY;
    private int objMultX, objMultY;
    private GameObject belowUpper;
    private GameObject belowLower;
    private GameObject movingObj;
    private boolean objectCheck;
    private boolean objectNewlyActivated;

    // Constructors
    public MovingObjectTracker() {
	this.resetTracker();
    }

    void activateObject(final int zx, final int zy, final int pushX, final int pushY, final GameObject gmo) {
	final var gm = Inconnuclear.getStuffBag().getGame();
	final var plMgr = gm.getPlayerManager();
	final var pz = plMgr.getPlayerLocationZ();
	this.objIncX = pushX - zx;
	this.objIncY = pushY - zy;
	this.objCumX = zx;
	this.objCumY = zy;
	this.movingObj = gmo;
	this.belowUpper = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().getCell(
		this.objCumX + this.objIncX * this.objMultX, this.objCumY + this.objIncY * this.objMultY, pz,
		Layer.OBJECT.ordinal());
	this.belowLower = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().getCell(
		this.objCumX + this.objIncX * this.objMultX, this.objCumY + this.objIncY * this.objMultY, pz,
		Layer.GROUND.ordinal());
	this.objectMoving = true;
	this.objectCheck = true;
	this.objectNewlyActivated = true;
    }

    private void doObjectOnce() {
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	final var gm = app.getGame();
	final var pz = gm.getPlayerManager().getPlayerLocationZ();
	try {
	    if (gm.isDelayedDecayActive() && gm.isRemoteDecayActive()) {
		gm.doRemoteDelayedDecay(this.movingObj);
	    }
	    final var oldSave = this.movingObj.getSavedObject();
	    final var saved = m.getCell(this.objCumX + this.objIncX * this.objMultX,
		    this.objCumY + this.objIncY * this.objMultY, pz, this.movingObj.getLayer());
	    this.belowUpper = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().getCell(
		    this.objCumX + this.objIncX * this.objMultX, this.objCumY + this.objIncY * this.objMultY, pz,
		    Layer.OBJECT.ordinal());
	    this.belowLower = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().getCell(
		    this.objCumX + this.objIncX * this.objMultX, this.objCumY + this.objIncY * this.objMultY, pz,
		    Layer.GROUND.ordinal());
	    if (MovingObjectTracker.checkSolid(saved)) {
		this.belowLower.pushOutAction(this.movingObj, this.objCumX, this.objCumY, pz);
		this.belowUpper.pushOutAction(this.movingObj, this.objCumX, this.objCumY, pz);
		oldSave.pushOutAction(this.movingObj, this.objCumX, this.objCumY, pz);
		m.setCell(oldSave, this.objCumX, this.objCumY, pz, this.movingObj.getLayer());
		this.movingObj.setSavedObject(saved);
		m.setCell(this.movingObj, this.objCumX + this.objIncX * this.objMultX,
			this.objCumY + this.objIncY * this.objMultY, pz, this.movingObj.getLayer());
		var stopObj = this.belowLower.pushIntoAction(this.movingObj,
			this.objCumX + this.objIncX * this.objMultX, this.objCumY + this.objIncY * this.objMultY, pz);
		final var temp1 = this.belowUpper.pushIntoAction(this.movingObj,
			this.objCumX + this.objIncX * this.objMultX, this.objCumY + this.objIncY * this.objMultY, pz);
		if (!temp1) {
		    stopObj = false;
		}
		final var temp2 = saved.pushIntoAction(this.movingObj, this.objCumX + this.objIncX * this.objMultX,
			this.objCumY + this.objIncY * this.objMultY, pz);
		if (!temp2) {
		    stopObj = false;
		}
		this.objectMoving = stopObj;
		this.objectCheck = stopObj;
		final var oldObjIncX = this.objIncX;
		final var oldObjIncY = this.objIncY;
		if (this.belowUpper == null || this.belowLower == null) {
		    this.objectCheck = false;
		} else if (!this.movingObj.hasFriction()) {
		    // Handle icy objects
		    this.objectCheck = true;
		} else if (this.belowUpper.canMoveBoxes() && this.movingObj.canMove()) {
		    // Handle box on box mover
		    final var dir = this.belowUpper.getDirection();
		    final var unres = DirectionResolver.unresolve(dir);
		    this.objIncX = unres[0];
		    this.objIncY = unres[1];
		    this.objectCheck = true;
		} else if (this.belowUpper.canMoveMirrors() && this.movingObj.canMove()) {
		    // Handle mirror on mirror mover
		    final var dir = this.belowUpper.getDirection();
		    final var unres = DirectionResolver.unresolve(dir);
		    this.objIncX = unres[0];
		    this.objIncY = unres[1];
		    this.objectCheck = true;
		} else {
		    this.objectCheck = !this.belowLower.hasFriction() || !this.belowUpper.hasFriction();
		}
		if (this.objIncX != oldObjIncX || this.objIncY != oldObjIncY) {
		    this.objCumX += oldObjIncX;
		    this.objCumY += oldObjIncY;
		} else {
		    this.objCumX += this.objIncX;
		    this.objCumY += this.objIncY;
		}
		app.getDungeonManager().setDirty(true);
	    } else {
		// Movement failed
		this.belowLower.pushIntoAction(this.movingObj, this.objCumX, this.objCumY, pz);
		this.belowUpper.pushIntoAction(this.movingObj, this.objCumX, this.objCumY, pz);
		oldSave.pushIntoAction(this.movingObj, this.objCumX, this.objCumY, pz);
		this.movingObj.pushCollideAction(this.movingObj, this.objCumX, this.objCumY, pz);
		saved.pushCollideAction(this.movingObj, this.objCumX + this.objIncX * this.objMultX,
			this.objCumY + this.objIncY * this.objMultY, pz);
		this.objectMoving = false;
		this.objectCheck = false;
	    }
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    this.objectMoving = false;
	    this.objectCheck = false;
	}
	gm.redrawDungeon();
    }

    void haltMovingObject() {
	this.objectMoving = false;
	this.objectCheck = false;
    }

    boolean isChecking() {
	return this.objectCheck;
    }

    boolean isTracking() {
	return this.objectMoving;
    }

    void resetTracker() {
	this.objectCheck = true;
	this.objectNewlyActivated = false;
	this.objMultX = 1;
	this.objMultY = 1;
    }

    void trackPart1() {
	if (this.objectMoving && this.objectCheck) {
	    this.doObjectOnce();
	}
    }

    void trackPart2() {
	try {
	    final var gm = Inconnuclear.getStuffBag().getGame();
	    final var plMgr = gm.getPlayerManager();
	    final var pz = plMgr.getPlayerLocationZ();
	    if (this.objectMoving) {
		// Make objects pushed into ice move 2 squares first time
		if (this.objectCheck && this.objectNewlyActivated
			&& (!this.belowLower.hasFriction() || !this.belowUpper.hasFriction())) {
		    this.doObjectOnce();
		    this.objectCheck = !this.belowLower.hasFriction() || !this.belowUpper.hasFriction();
		}
	    } else {
		this.objectCheck = false;
		// Check for moving object stopped on thin ice
		if (this.movingObj != null && gm.isDelayedDecayActive() && gm.isRemoteDecayActive()) {
		    gm.doRemoteDelayedDecay(this.movingObj);
		    this.belowUpper.pushIntoAction(this.movingObj, this.objCumX, this.objCumY, pz);
		    this.belowLower.pushIntoAction(this.movingObj, this.objCumX, this.objCumY, pz);
		}
	    }
	} catch (final ArrayIndexOutOfBoundsException aioobe) {
	    // Stop object
	    this.objectMoving = false;
	    this.objectCheck = false;
	}
    }

    void trackPart3() {
	if (!this.objectCheck) {
	    this.objectMoving = false;
	}
    }

    void trackPart4() {
	this.objectNewlyActivated = false;
    }
}
