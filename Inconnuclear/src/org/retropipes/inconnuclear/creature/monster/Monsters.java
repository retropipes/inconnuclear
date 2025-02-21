/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.monster;

import org.retropipes.inconnuclear.locale.Strings;

public class Monsters {
    public static final String getImageFilename(final int ID) {
	final var tempMonID = Integer.toString(ID);
	String monID;
	if (tempMonID.length() == 1) {
	    monID = "0" + tempMonID;
	} else {
	    monID = tempMonID;
	}
	return monID;
    }

    public static final String getType(final int zoneID, final int ID) {
	return Strings.monsterzone(zoneID, ID);
    }

    // Private constructor
    private Monsters() {
	// Do nothing
    }
}