/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.halo;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.diane.asset.image.DianeImageLoader;

public class HaloImageLoader {
    public static BufferedImageIcon load(final HaloImageId baseId) {
	return DianeImageLoader.load(baseId);
    }
}
