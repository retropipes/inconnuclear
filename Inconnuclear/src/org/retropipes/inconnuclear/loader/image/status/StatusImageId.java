/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.status;

import java.net.URL;

import org.retropipes.diane.asset.image.DianeImageIndex;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

public enum StatusImageId implements DianeImageIndex {
    ACTIONS_00, ACTIONS_01, ACTIONS_02, ACTIONS_03, ACTIONS_04, ACTIONS_05, ACTIONS_06, ACTIONS_07, ACTIONS_08,
    ACTIONS_09, ACTIONS_10, ACTIONS_11, ACTIONS_12, ACTIONS_13, ACTIONS_14, ACTIONS_15, ACTIONS_16, ACTIONS_17,
    ACTIONS_18, ACTIONS_19, ATTACKS_LEFT, CASTS_LEFT, CREATURE_ID, CREATURE_LEVEL, CREATURE_TEAM, DEFENSE, DEPTH,
    EXPERIENCE, HEALTH, ITEMS_LEFT, MAGIC, MAGIC_DEFENSE, MELEE_ATTACK, MONEY, MOVES_LEFT, RANGED_ATTACK, START,
    THEFTS_LEFT, _NONE;

    @Override
    public String getName() {
	return StatusImageCatalogLoader.getFilename(this.ordinal());
    }

    @Override
    public URL getURL() {
	return this.getClass().getResource(Strings.untranslated(Untranslated.STATUS_IMAGE_LOAD_PATH) + this.getName()
		+ Strings.fileExtension(FileExtension.IMAGE));
    }
}
