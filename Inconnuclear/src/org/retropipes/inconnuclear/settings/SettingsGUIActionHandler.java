/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Strings;

class SettingsGUIActionHandler implements ActionListener {
    private final SettingsGUI pm;

    public SettingsGUIActionHandler(final SettingsGUI settingsGUI) {
	this.pm = settingsGUI;
    }

    // Handle buttons
    @Override
    public void actionPerformed(final ActionEvent e) {
	try {
	    final var cmd = e.getActionCommand();
	    if (cmd.equals(Strings.dialog(DialogString.OK_BUTTON))) {
		this.pm.setPrefs();
	    } else if (cmd.equals(Strings.dialog(DialogString.CANCEL_BUTTON))) {
		this.pm.hidePrefs();
	    }
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
    }
}