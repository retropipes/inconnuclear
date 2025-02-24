/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.battle;

import java.net.URL;

import org.retropipes.diane.asset.image.DianeImageIndex;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

public enum BattleImageId implements DianeImageIndex {
    TEAM0,
    TEAM0_NOACTIONS,
    TEAM0_PARALYZED,
    TEAM1,
    TEAM1_NOACTIONS,
    TEAM1_PARALYZED,
    TEAM2,
    TEAM2_NOACTIONS,
    TEAM2_PARALYZED,
    TEAM3,
    TEAM3_NOACTIONS,
    TEAM3_PARALYZED,
    TEAM4,
    TEAM4_NOACTIONS,
    TEAM4_PARALYZED,
    TEAM5,
    TEAM5_NOACTIONS,
    TEAM5_PARALYZED,
    TEAM6,
    TEAM6_NOACTIONS,
    TEAM6_PARALYZED,
    TEAM7,
    TEAM7_NOACTIONS,
    TEAM7_PARALYZED,
    TEAM8,
    TEAM8_NOACTIONS,
    TEAM8_PARALYZED,
    TEAM9,
    TEAM9_NOACTIONS,
    TEAM9_PARALYZED,
    DAMAGE,
    DEAD,
    _NONE;

    @Override
    public String getName() {
	if (this == BattleImageId._NONE) {
	    return null;
	}
	return BattleImageCatalogLoader.getFilename(this.ordinal());
    }

    @Override
    public URL getURL() {
	if (this == BattleImageId._NONE) {
	    return null;
	}
	return this.getClass().getResource(Strings.untranslated(Untranslated.BATTLE_IMAGE_LOAD_PATH) + this.getName()
		+ Strings.fileExtension(FileExtension.IMAGE));
    }
}
