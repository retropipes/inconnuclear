/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.window.time;

import org.retropipes.inconnuclear.settings.Settings;

class WindowTimeBattleSpeed {
    // Method
    static int getSpeed() {
	return Settings.getBattleSpeed() / 10;
    }

    // Constructor
    private WindowTimeBattleSpeed() {
	// Do nothing
    }
}