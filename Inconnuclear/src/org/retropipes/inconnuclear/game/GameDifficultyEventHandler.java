/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Strings;

class GameDifficultyEventHandler implements ActionListener, WindowListener {
    /**
     * 
     */
    private final GameGUI gui;

    public GameDifficultyEventHandler(GameGUI gameGUI) {
	this.gui = gameGUI;
	// Do nothing
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	final var cmd = e.getActionCommand();
	final var gm = this.gui;
	if (cmd.equals(Strings.dialog(DialogString.OK_BUTTON))) {
	    gm.difficultyDialogOKButtonClicked();
	} else {
	    gm.difficultyDialogCancelButtonClicked();
	}
    }

    @Override
    public void windowActivated(final WindowEvent e) {
	// Ignore
    }

    @Override
    public void windowClosed(final WindowEvent e) {
	// Ignore
    }

    @Override
    public void windowClosing(final WindowEvent e) {
	this.gui.difficultyDialogCancelButtonClicked();
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
	// Ignore
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
	// Ignore
    }

    @Override
    public void windowIconified(final WindowEvent e) {
	// Ignore
    }

    @Override
    public void windowOpened(final WindowEvent e) {
	// Ignore
    }
}