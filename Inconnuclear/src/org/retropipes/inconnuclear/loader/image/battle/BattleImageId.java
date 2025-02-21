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
    TEAM0, TEAM0_NOACTIONS, TEAM1, TEAM1_NOACTIONS, TEAM2, TEAM2_NOACTIONS, TEAM3, TEAM3_NOACTIONS, TEAM4,
    TEAM4_NOACTIONS, TEAM5, TEAM5_NOACTIONS, TEAM6, TEAM6_NOACTIONS, TEAM7, TEAM7_NOACTIONS, _NONE;

    @Override
    public String getName() {
	return BattleImageCatalogLoader.getFilename(this.ordinal());
    }

    @Override
    public URL getURL() {
	return this.getClass().getResource(Strings.untranslated(Untranslated.BATTLE_IMAGE_LOAD_PATH) + this.getName()
		+ Strings.fileExtension(FileExtension.IMAGE));
    }
}
