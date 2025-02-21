/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.utility;

import org.retropipes.inconnuclear.dungeon.gameobject.Material;

public class RangeTypes {
    public static final int BOMB = 0;
    public static final int HEAT_BOMB = 1;
    public static final int ICE_BOMB = 2;

    public static final Material getMaterialForRangeType(final int rt) {
	return switch (rt) {
	case BOMB -> Material.METALLIC;
	case HEAT_BOMB -> Material.FIRE;
	case ICE_BOMB -> Material.ICE;
	default -> Material.DEFAULT;
	};
    }

    private RangeTypes() {
	// Do nothing
    }
}
