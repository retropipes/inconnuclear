/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.gameobject;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.diane.asset.image.DianeImageLoader;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;

public class ObjectImageLoader {
    public static BufferedImageIcon load(final ObjectImageId baseId) {
	return DianeImageLoader.load(baseId);
    }

    public static BufferedImageIcon load(final String name, final int baseId) {
	var filename = Integer.toString(baseId);
	return DianeImageLoader.load(name, ObjectImageLoader.class
		.getResource("/asset/image/object/" + filename + Strings.fileExtension(FileExtension.IMAGE))); //$NON-NLS-1$
    }
}
