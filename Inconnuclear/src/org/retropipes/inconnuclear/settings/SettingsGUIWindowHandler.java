/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.settings;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class SettingsGUIWindowHandler extends WindowAdapter {
    private final SettingsGUI pm;

    public SettingsGUIWindowHandler(final SettingsGUI settingsGUI) {
	this.pm = settingsGUI;
    }

    @Override
    public void windowClosing(final WindowEvent e) {
	this.pm.hidePrefs();
    }
}