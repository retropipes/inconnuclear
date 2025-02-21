/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import org.retropipes.diane.direction.Direction;
import org.retropipes.diane.direction.DirectionResolver;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.dungeon.DungeonData;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;
import org.retropipes.inconnuclear.settings.Settings;
import org.retropipes.inconnuclear.utility.AlreadyDeadException;
import org.retropipes.inconnuclear.utility.GameActions;

final class MLOTask extends Thread {
    static void activateAutomaticMovement() {
	Inconnuclear.getStuffBag().getGame().scheduleAutoMove();
    }

    private static boolean checkSolid(final GameObject next) {
	final var gm = Inconnuclear.getStuffBag().getGame();
	// Check cheats
	if (gm.getCheatStatus(Game.CHEAT_GHOSTLY)) {
	    return true;
	}
	return !next.isSolid();
    }

    static boolean checkSolid(final int zx, final int zy) {
	final var gm = Inconnuclear.getStuffBag().getGame();
	final var next = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().getCell(zx, zy,
		gm.getPlayerManager().getPlayerLocationZ(), Layer.STATUS.ordinal());
	// Check cheats
	if (gm.getCheatStatus(Game.CHEAT_GHOSTLY)) {
	    return true;
	}
	return !next.isSolid();
    }

    private static int normalizeColumn(final int column, final int columns) {
	var fC = column;
	if (fC < 0) {
	    fC += columns;
	    while (fC < 0) {
		fC += columns;
	    }
	} else if (fC > columns - 1) {
	    fC -= columns;
	    while (fC > columns - 1) {
		fC -= columns;
	    }
	}
	return fC;
    }

    private static int normalizeRow(final int row, final int rows) {
	var fR = row;
	if (fR < 0) {
	    fR += rows;
	    while (fR < 0) {
		fR += rows;
	    }
	} else if (fR > rows - 1) {
	    fR -= rows;
	    while (fR > rows - 1) {
		fR -= rows;
	    }
	}
	return fR;
    }

    // Fields
    private int sx, sy;
    private boolean mover;
    private boolean move;
    private boolean proceed;
    private boolean abort;
    private boolean frozen;
    private boolean magnet;
    private boolean loopCheck;
    private final ArrayList<MovingObjectTracker> objectTrackers;

    // Constructors
    public MLOTask() {
	this.setName(Strings.untranslated(Untranslated.MLOH_NAME));
	this.setPriority(Thread.MIN_PRIORITY);
	this.abort = false;
	this.objectTrackers = new ArrayList<>();
	this.frozen = false;
	this.magnet = false;
    }

    void abortLoop() {
	this.abort = true;
    }

    void activateMovement(final int zx, final int zy) {
	final var gm = Inconnuclear.getStuffBag().getGame();
	if (zx == 2 || zy == 2 || zx == -2 || zy == -2) {
	    // Boosting
	    gm.updateScore(0, 0, 1);
	    this.sx = zx;
	    this.sy = zy;
	    this.magnet = false;
	    Game.updateUndo(false, false, false, true, false, false, false, false, false, false);
	} else if (zx == 3 || zy == 3 || zx == -3 || zy == -3) {
	    // Using a Magnet
	    gm.updateScore(0, 0, 1);
	    final var a = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase();
	    final var px = gm.getPlayerManager().getPlayerLocationX();
	    final var py = gm.getPlayerManager().getPlayerLocationY();
	    final var pz = gm.getPlayerManager().getPlayerLocationZ();
	    if (zx == 3) {
		this.sx = a.checkForMagnetic(pz, px, py, Direction.EAST);
		this.sy = 0;
	    } else if (zx == -3) {
		this.sx = -a.checkForMagnetic(pz, px, py, Direction.WEST);
		this.sy = 0;
	    }
	    if (zy == 3) {
		this.sx = 0;
		this.sy = a.checkForMagnetic(pz, px, py, Direction.SOUTH);
	    } else if (zy == -3) {
		this.sx = 0;
		this.sy = -a.checkForMagnetic(pz, px, py, Direction.NORTH);
	    }
	    this.magnet = true;
	    if (this.sx == 0 && this.sy == 0) {
		// Failure
		SoundLoader.playSound(Sounds.STEP_FAIL);
	    } else {
		// Success
	    }
	    Game.updateUndo(false, false, false, false, true, false, false, false, false, false);
	} else {
	    // Moving normally
	    SoundLoader.playSound(Sounds.STEP_PARTY);
	    gm.updateScore(1, 0, 0);
	    this.sx = zx;
	    this.sy = zy;
	    this.magnet = false;
	    Game.updateUndo(false, false, false, false, false, false, false, false, false, false);
	}
	this.move = true;
	this.loopCheck = true;
	if (!gm.isReplaying()) {
	    gm.updateReplay(false, zx, zy);
	}
    }

    void activateObjects(final int zx, final int zy, final int pushX, final int pushY, final GameObject gmo) {
	final var tracker = new MovingObjectTracker();
	tracker.activateObject(zx, zy, pushX, pushY, gmo);
	this.objectTrackers.add(tracker);
    }

    private boolean areObjectTrackersChecking() {
	var result = false;
	for (final MovingObjectTracker tracker : this.objectTrackers) {
	    if (tracker.isChecking()) {
		result = true;
	    }
	}
	return result;
    }

    private boolean canMoveThere() {
	final var app = Inconnuclear.getStuffBag();
	final var gm = app.getGame();
	final var plMgr = gm.getPlayerManager();
	final var px = plMgr.getPlayerLocationX();
	final var py = plMgr.getPlayerLocationY();
	final var pz = plMgr.getPlayerLocationZ();
	final var pw = Layer.MARKER.ordinal();
	final var m = app.getDungeonManager().getDungeonBase();
	GameObject lgo = null;
	GameObject ugo = null;
	GameObject loo = null;
	GameObject uoo = null;
	try {
	    lgo = m.getCell(px + this.sx, py + this.sy, pz, Layer.GROUND.ordinal());
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    lgo = new GameObject(ObjectImageId.WALL);
	}
	try {
	    ugo = m.getCell(px + this.sx, py + this.sy, pz, Layer.OBJECT.ordinal());
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    ugo = new GameObject(ObjectImageId.WALL);
	}
	try {
	    loo = m.getCell(px + this.sx, py + this.sy, pz, Layer.STATUS.ordinal());
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    loo = new GameObject(ObjectImageId.WALL);
	}
	try {
	    uoo = m.getCell(px + this.sx, py + this.sy, pz, pw);
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    uoo = new GameObject(ObjectImageId.WALL);
	}
	return MLOTask.checkSolid(lgo) && MLOTask.checkSolid(ugo) && MLOTask.checkSolid(loo) && MLOTask.checkSolid(uoo);
    }

    private boolean checkLoopCondition(final boolean zproceed) {
	final var app = Inconnuclear.getStuffBag();
	final var gm = app.getGame();
	final var plMgr = gm.getPlayerManager();
	final var px = plMgr.getPlayerLocationX();
	final var py = plMgr.getPlayerLocationY();
	final var pz = plMgr.getPlayerLocationZ();
	final var m = app.getDungeonManager().getDungeonBase();
	GameObject lgo = null;
	GameObject ugo = null;
	try {
	    lgo = m.getCell(px + this.sx, py + this.sy, pz, Layer.GROUND.ordinal());
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    lgo = new GameObject(ObjectImageId.WALL);
	}
	try {
	    ugo = m.getCell(px + this.sx, py + this.sy, pz, Layer.OBJECT.ordinal());
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    ugo = new GameObject(ObjectImageId.WALL);
	}
	return zproceed && (!lgo.hasFriction() || !ugo.hasFriction() || this.mover || this.frozen)
		&& this.canMoveThere();
    }

    private void cullTrackers() {
	final var tempArray1 = this.objectTrackers.toArray(new MovingObjectTracker[this.objectTrackers.size()]);
	this.objectTrackers.clear();
	for (final MovingObjectTracker tracker : tempArray1) {
	    if (tracker != null && tracker.isTracking()) {
		this.objectTrackers.add(tracker);
	    }
	}
    }

    private void doMovementLasersObjects() {
	synchronized (DungeonData.LOCK_OBJECT) {
	    final var gm = Inconnuclear.getStuffBag().getGame();
	    final var plMgr = gm.getPlayerManager();
	    final var pz = plMgr.getPlayerLocationZ();
	    this.loopCheck = true;
	    var objs = new GameObject[4];
	    objs[Layer.GROUND.ordinal()] = new GameObject(ObjectImageId.WALL);
	    objs[Layer.OBJECT.ordinal()] = new GameObject(ObjectImageId.WALL);
	    objs[Layer.STATUS.ordinal()] = new GameObject(ObjectImageId.WALL);
	    objs[Layer.MARKER.ordinal()] = new GameObject(ObjectImageId.WALL);
	    do {
		try {
		    if (this.move && this.loopCheck) {
			objs = this.doMovementOnce();
		    }
		    // Abort check 1
		    if (this.abort) {
			break;
		    }
		    for (final MovingObjectTracker tracker : this.objectTrackers) {
			if (tracker.isTracking()) {
			    tracker.trackPart1();
			}
		    }
		    // Abort check 3
		    if (this.abort) {
			break;
		    }
		    var actionType = 0;
		    if (this.move && !this.magnet && Math.abs(this.sx) <= 1 && Math.abs(this.sy) <= 1) {
			actionType = GameActions.MOVE;
		    } else {
			actionType = GameActions.NON_MOVE;
		    }
		    for (final MovingObjectTracker tracker : this.objectTrackers) {
			if (tracker.isTracking()) {
			    tracker.trackPart2();
			}
		    }
		    if (this.move) {
			this.loopCheck = this.checkLoopCondition(this.proceed);
			if (this.mover && !this.canMoveThere()) {
			    MLOTask.activateAutomaticMovement();
			}
			if (objs[Layer.STATUS.ordinal()].solvesOnMove()) {
			    this.abort = true;
			    if (this.move) {
				Inconnuclear.getStuffBag().getDungeonManager().setDirty(true);
				gm.moveLoopDone();
				this.move = false;
			    }
			    gm.solvedLevel(true);
			    return;
			}
		    } else {
			this.loopCheck = false;
		    }
		    if (this.move && !this.loopCheck) {
			Inconnuclear.getStuffBag().getDungeonManager().setDirty(true);
			gm.moveLoopDone();
			this.move = false;
		    }
		    for (final MovingObjectTracker tracker : this.objectTrackers) {
			if (tracker.isTracking()) {
			    tracker.trackPart3();
			}
		    }
		    // Check auto-move flag
		    if (gm.isAutoMoveScheduled() && this.canMoveThere()) {
			gm.unscheduleAutoMove();
			this.move = true;
			this.loopCheck = true;
		    }
		    Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().tickTimers(pz, actionType);
		    // Delay
		    try {
			Thread.sleep(Settings.getActionSpeed());
		    } catch (final InterruptedException ie) {
			// Ignore
		    }
		    for (final MovingObjectTracker tracker : this.objectTrackers) {
			if (tracker.isTracking()) {
			    tracker.trackPart4();
			}
		    }
		    this.cullTrackers();
		} catch (final ConcurrentModificationException cme) {
		    // Ignore
		}
	    } while (!this.abort && (this.loopCheck || this.areObjectTrackersChecking()));
	    // Check cheats
	    if (objs[Layer.GROUND.ordinal()].killsOnMove() && !gm.getCheatStatus(Game.CHEAT_SWIMMING)) {
		gm.gameOver();
	    }
	}
    }

    private GameObject[] doMovementOnce() {
	final var gm = Inconnuclear.getStuffBag().getGame();
	final var plMgr = gm.getPlayerManager();
	var px = plMgr.getPlayerLocationX();
	var py = plMgr.getPlayerLocationY();
	final var pz = plMgr.getPlayerLocationZ();
	final var pw = Layer.MARKER.ordinal();
	final var app = Inconnuclear.getStuffBag();
	final var m = app.getDungeonManager().getDungeonBase();
	this.proceed = true;
	this.mover = false;
	GameObject lgo = null;
	GameObject ugo = null;
	GameObject loo = null;
	GameObject uoo = null;
	try {
	    lgo = m.getCell(px + this.sx, py + this.sy, pz, Layer.GROUND.ordinal());
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    lgo = new GameObject(ObjectImageId.WALL);
	}
	try {
	    ugo = m.getCell(px + this.sx, py + this.sy, pz, Layer.OBJECT.ordinal());
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    ugo = new GameObject(ObjectImageId.WALL);
	}
	try {
	    loo = m.getCell(px + this.sx, py + this.sy, pz, Layer.STATUS.ordinal());
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    loo = new GameObject(ObjectImageId.WALL);
	}
	try {
	    uoo = m.getCell(px + this.sx, py + this.sy, pz, pw);
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    uoo = new GameObject(ObjectImageId.WALL);
	}
	if (this.proceed) {
	    plMgr.savePlayerLocation();
	    try {
		if (this.canMoveThere()) {
		    if (gm.isDelayedDecayActive()) {
			gm.doDelayedDecay();
		    }
		    // Preserve other objects
		    if (m.getCell(px, py, pz, pw).canMove()) {
			gm.getPlayer().setSavedObject(m.getCell(px, py, pz, pw));
		    }
		    m.setCell(gm.getPlayer().getSavedObject(), px, py, pz, pw);
		    plMgr.offsetPlayerLocationX(this.sx);
		    plMgr.offsetPlayerLocationY(this.sy);
		    px = MLOTask.normalizeColumn(px + this.sx, DungeonBase.getMinColumns());
		    py = MLOTask.normalizeRow(py + this.sy, DungeonBase.getMinRows());
		    gm.getPlayer().setSavedObject(m.getCell(px, py, pz, pw));
		    m.setCell(gm.getPlayer(), px, py, pz, pw);
		    lgo.postMoveAction(px, py, pz);
		    ugo.postMoveAction(px, py, pz);
		    loo.postMoveAction(px, py, pz);
		    uoo.postMoveAction(px, py, pz);
		    if (ugo.canMoveParty()) {
			final var dir = ugo.getDirection();
			final var unres = DirectionResolver.unresolve(dir);
			this.sx = unres[0];
			this.sy = unres[1];
			this.mover = true;
		    } else {
			this.mover = false;
		    }
		} else {
		    // Move failed - object is solid in that direction
		    if (gm.isDelayedDecayActive()) {
			gm.doDelayedDecay();
		    }
		    if (lgo == null) {
			lgo = new GameObject(ObjectImageId.GRASS);
		    }
		    lgo.moveFailedAction(plMgr.getPlayerLocationX() + this.sx, plMgr.getPlayerLocationY() + this.sy,
			    plMgr.getPlayerLocationZ());
		    if (ugo == null) {
			ugo = new GameObject(ObjectImageId.GRASS);
		    }
		    ugo.moveFailedAction(plMgr.getPlayerLocationX() + this.sx, plMgr.getPlayerLocationY() + this.sy,
			    plMgr.getPlayerLocationZ());
		    if (loo == null) {
			loo = new GameObject(ObjectImageId.GRASS);
		    }
		    loo.moveFailedAction(plMgr.getPlayerLocationX() + this.sx, plMgr.getPlayerLocationY() + this.sy,
			    plMgr.getPlayerLocationZ());
		    if (uoo == null) {
			uoo = new GameObject(ObjectImageId.GRASS);
		    }
		    uoo.moveFailedAction(plMgr.getPlayerLocationX() + this.sx, plMgr.getPlayerLocationY() + this.sy,
			    plMgr.getPlayerLocationZ());
		    if (gm.getPlayer().getSavedObject().canMoveParty()) {
			final var dir = gm.getPlayer().getSavedObject().getDirection();
			final var unres = DirectionResolver.unresolve(dir);
			this.sx = unres[0];
			this.sy = unres[1];
			this.mover = true;
		    } else {
			this.mover = false;
		    }
		    this.proceed = false;
		}
	    } catch (final ArrayIndexOutOfBoundsException ae) {
		plMgr.restorePlayerLocation();
		m.setCell(gm.getPlayer(), plMgr.getPlayerLocationX(), plMgr.getPlayerLocationY(),
			plMgr.getPlayerLocationZ(), pw);
		// Move failed - attempted to go outside the dungeon
		if (lgo == null) {
		    lgo = new GameObject(ObjectImageId.GRASS);
		}
		lgo.moveFailedAction(plMgr.getPlayerLocationX() + this.sx, plMgr.getPlayerLocationY() + this.sy,
			plMgr.getPlayerLocationZ());
		if (ugo == null) {
		    ugo = new GameObject(ObjectImageId.GRASS);
		}
		ugo.moveFailedAction(plMgr.getPlayerLocationX() + this.sx, plMgr.getPlayerLocationY() + this.sy,
			plMgr.getPlayerLocationZ());
		if (loo == null) {
		    loo = new GameObject(ObjectImageId.GRASS);
		}
		loo.moveFailedAction(plMgr.getPlayerLocationX() + this.sx, plMgr.getPlayerLocationY() + this.sy,
			plMgr.getPlayerLocationZ());
		if (uoo == null) {
		    uoo = new GameObject(ObjectImageId.GRASS);
		}
		uoo.moveFailedAction(plMgr.getPlayerLocationX() + this.sx, plMgr.getPlayerLocationY() + this.sy,
			plMgr.getPlayerLocationZ());
		this.proceed = false;
	    }
	} else {
	    // Move failed - pre-move check failed
	    lgo.moveFailedAction(px + this.sx, py + this.sy, pz);
	    ugo.moveFailedAction(px + this.sx, py + this.sy, pz);
	    loo.moveFailedAction(px + this.sx, py + this.sy, pz);
	    uoo.moveFailedAction(px + this.sx, py + this.sy, pz);
	    this.proceed = false;
	}
	gm.redrawDungeon();
	return new GameObject[] { lgo, ugo, loo, uoo };
    }

    void haltMovingObjects() {
	for (final MovingObjectTracker tracker : this.objectTrackers) {
	    if (tracker.isTracking()) {
		tracker.haltMovingObject();
	    }
	}
    }

    @Override
    public void run() {
	try {
	    final var gm = Inconnuclear.getStuffBag().getGame();
	    gm.clearDead();
	    this.doMovementLasersObjects();
	    // Check auto-move flag
	    if (gm.isAutoMoveScheduled() && this.canMoveThere()) {
		gm.unscheduleAutoMove();
		this.doMovementLasersObjects();
	    }
	} catch (final AlreadyDeadException ade) {
	    // Ignore
	} catch (final Throwable t) {
	    Inconnuclear.logError(t);
	}
    }
}
