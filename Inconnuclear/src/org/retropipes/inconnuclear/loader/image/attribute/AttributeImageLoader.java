/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.attribute;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.diane.asset.image.DianeImageLoader;

public class AttributeImageLoader {
    public static BufferedImageIcon load(final AttributeImageId baseId) {
	return DianeImageLoader.load(baseId);
    }
}
