/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.utility;

public class Zones {
    public static final String getZoneNumber(final int ID) {
	final var tempZoneID = Integer.toString(ID);
	String zoneID;
	if (tempZoneID.length() == 1) {
	    zoneID = "0" + tempZoneID;
	} else {
	    zoneID = tempZoneID;
	}
	return zoneID;
    }

    // Private constructor
    private Zones() {
	// Do nothing
    }
}