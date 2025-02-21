/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.attribute;

import java.net.URL;

import org.retropipes.diane.asset.image.DianeImageIndex;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

public enum AttributeImageId implements DianeImageIndex {
    BOMBABLE, BREAKABLE_HORIZONTAL, BREAKABLE_VERTICAL, CLOAKED, CRACKED, CRUMBLING, DAMAGED, EXPLODING, FADING, FAKE,
    FLIP, INVISIBLE, LARGE_ALL, LARGE_ANY, LARGE_NUMBER_0, LARGE_NUMBER_1, LARGE_NUMBER_2, LARGE_NUMBER_3,
    LARGE_NUMBER_4, LARGE_NUMBER_5, LARGE_NUMBER_6, LARGE_NUMBER_7, LARGE_NUMBER_8, LARGE_NUMBER_9, LARGE_NUMBER_10,
    LARGE_NUMBER_11, LARGE_NUMBER_12, LARGE_NUMBER_13, LARGE_NUMBER_14, LARGE_NUMBER_15, LARGE_NUMBER_16,
    LARGE_NUMBER_17, LARGE_NUMBER_18, LARGE_NUMBER_19, MOVE, NO, ONE_WAY_EAST, ONE_WAY_NORTH, ONE_WAY_SOUTH,
    ONE_WAY_WEST, PRESSURE, PULLABLE, PUSHABLE, QUESTION, ROTATING_CLOCKWISE, ROTATING_COUNTERCLOCKWISE, SIGN,
    SMALL_ALL, SMALL_ANY, SMALL_NUMBER_0, SMALL_NUMBER_1, SMALL_NUMBER_2, SMALL_NUMBER_3, SMALL_NUMBER_4,
    SMALL_NUMBER_5, SMALL_NUMBER_6, SMALL_NUMBER_7, SMALL_NUMBER_8, SMALL_NUMBER_9, SMALL_NUMBER_10, SMALL_NUMBER_11,
    SMALL_NUMBER_12, SMALL_NUMBER_13, SMALL_NUMBER_14, SMALL_NUMBER_15, SMALL_NUMBER_16, SMALL_NUMBER_17,
    SMALL_NUMBER_18, SMALL_NUMBER_19, TRAP, WEAKENED, _NONE;

    @Override
    public String getName() {
	return AttributeImageCatalogLoader.getFilename(this.ordinal());
    }

    @Override
    public URL getURL() {
	return this.getClass().getResource(Strings.untranslated(Untranslated.ATTRIBUTE_IMAGE_LOAD_PATH) + this.getName()
		+ Strings.fileExtension(FileExtension.IMAGE));
    }
}
