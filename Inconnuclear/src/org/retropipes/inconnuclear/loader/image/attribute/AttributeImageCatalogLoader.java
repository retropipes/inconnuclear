/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.attribute;

import java.io.IOException;
import java.util.ArrayList;

import org.retropipes.diane.Diane;
import org.retropipes.diane.fileio.utility.ResourceStreamReader;
import org.retropipes.inconnuclear.locale.Strings;

class AttributeImageCatalogLoader {
    private static ArrayList<String> FILENAME_CACHE = null;
    private static String CATALOG_PATH = "/asset/catalog/image/attribute.catalog"; //$NON-NLS-1$

    static String getFilename(final int index) {
	if (FILENAME_CACHE == null) {
	    try (final var rsr = new ResourceStreamReader(
		    AttributeImageCatalogLoader.class.getResourceAsStream(CATALOG_PATH))) {
		// Fetch data
		final var rawData = new ArrayList<String>();
		var line = Strings.EMPTY;
		while (line != null) {
		    line = rsr.readString();
		    if (line != null) {
			rawData.add(line);
		    }
		}
		FILENAME_CACHE = rawData;
	    } catch (final IOException e) {
		Diane.handleError(e);
		return null;
	    }
	}
	if (FILENAME_CACHE == null) {
	    return null;
	}
	return FILENAME_CACHE.get(index);
    }

    private AttributeImageCatalogLoader() {
	// Do nothing
    }
}
