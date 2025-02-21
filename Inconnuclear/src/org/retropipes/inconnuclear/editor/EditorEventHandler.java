/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.retropipes.inconnuclear.Inconnuclear;

class EditorEventHandler implements MouseListener, MouseMotionListener, WindowListener {
    /**
     * 
     */
    private final Editor editor;

    // handle scroll bars
    public EditorEventHandler(Editor theEditor) {
	this.editor = theEditor;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
	try {
	    final var x = e.getX();
	    final var y = e.getY();
	    if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown()) {
		this.editor.editObjectProperties(x, y);
	    } else if (e.isShiftDown()) {
		this.editor.probeObjectProperties(x, y);
	    } else {
		this.editor.editObject(x, y);
	    }
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
	try {
	    final var x = e.getX();
	    final var y = e.getY();
	    this.editor.editObject(x, y);
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
	// Do nothing
    }

    @Override
    public void mouseExited(final MouseEvent e) {
	// Do nothing
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
	// Do nothing
    }

    // handle mouse
    @Override
    public void mousePressed(final MouseEvent e) {
	// Do nothing
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
	// Do nothing
    }

    // Handle windows
    @Override
    public void windowActivated(final WindowEvent we) {
	// Do nothing
    }

    @Override
    public void windowClosed(final WindowEvent we) {
	// Do nothing
    }

    @Override
    public void windowClosing(final WindowEvent we) {
	this.editor.handleCloseWindow();
	Inconnuclear.getStuffBag().getGUIManager().showGUI();
    }

    @Override
    public void windowDeactivated(final WindowEvent we) {
	// Do nothing
    }

    @Override
    public void windowDeiconified(final WindowEvent we) {
	// Do nothing
    }

    @Override
    public void windowIconified(final WindowEvent we) {
	// Do nothing
    }

    @Override
    public void windowOpened(final WindowEvent we) {
	// Do nothing
    }
}