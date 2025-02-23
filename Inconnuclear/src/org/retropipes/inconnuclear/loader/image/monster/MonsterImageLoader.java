/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.monster;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.diane.asset.image.DianeImageLoader;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;

public class MonsterImageLoader {
    public static BufferedImageIcon load(final int index) {
	var filename = Integer.toString(index);
	return DianeImageLoader.load("monster-" + filename, MonsterImageLoader.class // $NON-NLS-1$
		.getResource("/asset/image/monster/" + filename + Strings.fileExtension(FileExtension.IMAGE))); //$NON-NLS-1$
    }

    public static BufferedImageIcon loadBoss(final int index) {
	var filename = Integer.toString(index);
	return DianeImageLoader.load("boss-" + filename, MonsterImageLoader.class // $NON-NLS-1$
		.getResource("/asset/image/boss/" + filename + Strings.fileExtension(FileExtension.IMAGE))); //$NON-NLS-1$
    }

    public static BufferedImageIcon loadFinalBoss() {
	return DianeImageLoader.load("boss-final", MonsterImageLoader.class //$NON-NLS-1$
		.getResource("/asset/image/boss/final" + Strings.fileExtension(FileExtension.IMAGE))); //$NON-NLS-1$
    }
}
