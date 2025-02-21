/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.ui;

import java.net.URL;

import org.retropipes.diane.asset.image.DianeImageIndex;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

public enum UiImageId implements DianeImageIndex {
    ICONLOGO, LOGO, MICROLOGO, MINILOGO, _NONE;

    @Override
    public String getName() {
	return UiImageCatalogLoader.getFilename(this.ordinal());
    }

    @Override
    public URL getURL() {
	var path = Strings.untranslated(Untranslated.UI_IMAGE_LOAD_PATH);
	var name = this.getName();
	var ext = Strings.fileExtension(FileExtension.IMAGE);
	return this.getClass().getResource(path + name + ext);
    }
}
