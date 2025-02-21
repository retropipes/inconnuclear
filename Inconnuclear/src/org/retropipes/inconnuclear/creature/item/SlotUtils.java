/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.item;

import org.retropipes.inconnuclear.locale.Slot;

public class SlotUtils {
    public static Slot getArmorSlotForType(final int armorType) {
	if (armorType >= Slot.WEAPON.ordinal()) {
	    return Slot.values()[armorType + 1];
	}
	return Slot.values()[armorType];
    }
}
