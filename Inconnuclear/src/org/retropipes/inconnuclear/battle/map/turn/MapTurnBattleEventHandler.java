/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map.turn;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.battle.BattleAction;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.settings.Settings;

class MapTurnBattleEventHandler extends AbstractAction implements KeyListener {
    private final MapTurnBattleGUI gui;
    private static final long serialVersionUID = 20239525230523524L;

    public MapTurnBattleEventHandler(MapTurnBattleGUI mapTurnBattleGUI) {
	this.gui = mapTurnBattleGUI;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	try {
	    if (e.getSource() instanceof JButton) {
		SoundLoader.playSound(Sounds.CLICK);
	    }
	    final var cmd = e.getActionCommand();
	    final var b = Inconnuclear.getStuffBag().getBattle();
	    // Do Player Actions
	    if (cmd.equals("Cast Spell") || cmd.equals("c")) {
		// Cast Spell
		b.doPlayerActions(BattleAction.CAST_SPELL);
	    } else if (cmd.equals("Steal") || cmd.equals("t")) {
		// Steal Money
		b.doPlayerActions(BattleAction.STEAL);
	    } else if (cmd.equals("Drain") || cmd.equals("d")) {
		// Drain Enemy
		b.doPlayerActions(BattleAction.DRAIN);
	    } else if (cmd.equals("End Turn") || cmd.equals("e")) {
		// End Turn
		b.endTurn();
	    }
	} catch (final Throwable t) {
	    Inconnuclear.logError(t);
	}
    }

    private void handleMovement(final KeyEvent e) {
	try {
	    if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
		if (e.isMetaDown()) {
		    return;
		}
	    } else if (e.isControlDown()) {
		return;
	    }
	    final var bl = Inconnuclear.getStuffBag().getBattle();
	    final var bg = this.gui;
	    if (bg.eventHandlersOn) {
		final var keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_NUMPAD4:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
		    bl.updatePosition(-1, 0);
		    break;
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_X:
		    bl.updatePosition(0, 1);
		    break;
		case KeyEvent.VK_NUMPAD6:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
		    bl.updatePosition(1, 0);
		    break;
		case KeyEvent.VK_NUMPAD8:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
		    bl.updatePosition(0, -1);
		    break;
		case KeyEvent.VK_NUMPAD7:
		case KeyEvent.VK_Q:
		    bl.updatePosition(-1, -1);
		    break;
		case KeyEvent.VK_NUMPAD9:
		case KeyEvent.VK_E:
		    bl.updatePosition(1, -1);
		    break;
		case KeyEvent.VK_NUMPAD3:
		case KeyEvent.VK_C:
		    bl.updatePosition(1, 1);
		    break;
		case KeyEvent.VK_NUMPAD1:
		case KeyEvent.VK_Z:
		    bl.updatePosition(-1, 1);
		    break;
		case KeyEvent.VK_NUMPAD5:
		case KeyEvent.VK_S:
		    // Confirm before attacking self
		    final var res = CommonDialogs.showConfirmDialog("Are you sure you want to attack yourself?",
			    "Battle");
		    if (res == CommonDialogs.YES_OPTION) {
			bl.updatePosition(0, 0);
		    }
		    break;
		default:
		    break;
		}
	    }
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
    }

    @Override
    public void keyPressed(final KeyEvent e) {
	if (!Settings.oneMove()) {
	    this.handleMovement(e);
	}
    }

    @Override
    public void keyReleased(final KeyEvent e) {
	if (Settings.oneMove()) {
	    this.handleMovement(e);
	}
    }

    @Override
    public void keyTyped(final KeyEvent e) {
	// Do nothing
    }
}