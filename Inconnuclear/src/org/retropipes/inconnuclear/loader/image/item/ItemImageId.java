/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.item;

import java.net.URL;

import org.retropipes.diane.asset.image.DianeImageIndex;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

public enum ItemImageId implements DianeImageIndex {
    AMULET,
    APPLE,
    AXE,
    BOOTS,
    BOW,
    BRACER,
    BREAD,
    CAP,
    CRYSTAL,
    DAGGER,
    GLOVES,
    HAMMER,
    POTION,
    ROCK,
    STAFF,
    SWORD,
    TORSO,
    WAND,
    _NONE;

    @Override
    public String getName() {
	if (this == ItemImageId._NONE) {
	    return null;
	}
	return this.toString().toLowerCase();
    }

    @Override
    public URL getURL() {
	if (this == ItemImageId._NONE) {
	    return null;
	}
	return this.getClass().getResource(Strings.untranslated(Untranslated.ITEM_IMAGE_LOAD_PATH) + this.getName()
		+ Strings.fileExtension(FileExtension.IMAGE));
    }
}
