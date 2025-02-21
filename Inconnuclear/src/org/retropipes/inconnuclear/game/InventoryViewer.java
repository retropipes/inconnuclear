/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.creature.party.PartyManager;

public final class InventoryViewer {
    public static void showEquipmentDialog() {
	final var title = "Equipment";
	final var member = PartyManager.getParty().getLeader();
	if (member != null) {
	    final var equipString = member.getItems().generateEquipmentStringArray();
	    CommonDialogs.showInputDialog("Equipment", title, equipString, equipString[0]);
	}
    }

    private InventoryViewer() {
	// Do nothing
    }
}
