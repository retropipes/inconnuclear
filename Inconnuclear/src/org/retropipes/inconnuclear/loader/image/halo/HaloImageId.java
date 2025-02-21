/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.halo;

import java.net.URL;

import org.retropipes.diane.asset.image.DianeImageIndex;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

public enum HaloImageId implements DianeImageIndex {
    BLESSED, BLESSMORE, BLESSMOST, CURSED, CURSEMORE, CURSEMOST, _NONE;

    @Override
    public String getName() {
	return HaloImageCatalogLoader.getFilename(this.ordinal());
    }

    @Override
    public URL getURL() {
	return this.getClass().getResource(Strings.untranslated(Untranslated.HALO_IMAGE_LOAD_PATH) + this.getName()
		+ Strings.fileExtension(FileExtension.IMAGE));
    }
}
