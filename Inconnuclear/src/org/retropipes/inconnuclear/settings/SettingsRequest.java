/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.
All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.settings;

import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;

public class SettingsRequest implements PreferencesHandler {
    public SettingsRequest() {
	// Do nothing
    }

    @Override
    public void handlePreferences(final PreferencesEvent pe) {
	Settings.showPrefs();
    }
}