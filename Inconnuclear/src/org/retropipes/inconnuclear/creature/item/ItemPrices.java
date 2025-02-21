/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.item;

public class ItemPrices {
    public static int getEquipmentCost(final int x) {
	return 10 * x * x * x + 10 * x * x + 10 * x + 10;
    }
}
