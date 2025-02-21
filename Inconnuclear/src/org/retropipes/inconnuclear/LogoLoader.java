/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.inconnuclear.loader.image.ui.UiImageId;
import org.retropipes.inconnuclear.loader.image.ui.UiImageLoader;

public class LogoLoader {
    public static BufferedImageIcon getIconLogo() {
	return UiImageLoader.load(UiImageId.ICONLOGO);
    }

    public static BufferedImageIcon getLogo() {
	return UiImageLoader.load(UiImageId.LOGO);
    }

    public static BufferedImageIcon getMicroLogo() {
	return UiImageLoader.load(UiImageId.MICROLOGO);
    }

    public static BufferedImageIcon getMiniatureLogo() {
	return UiImageLoader.load(UiImageId.MINILOGO);
    }

    private LogoLoader() {
	// Do nothing
    }
}
