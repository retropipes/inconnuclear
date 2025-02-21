/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.music;

import java.io.IOException;
import java.util.ArrayList;

import org.retropipes.diane.Diane;
import org.retropipes.diane.fileio.utility.ResourceStreamReader;
import org.retropipes.inconnuclear.locale.Music;
import org.retropipes.inconnuclear.locale.Strings;

class MusicCatalogLoader {
    private static ArrayList<String> MUSIC_FILENAMES = null;

    public static String getMusicFilename(final Music music) {
	if (MusicCatalogLoader.MUSIC_FILENAMES == null) {
	    try (final var rsr = new ResourceStreamReader(
		    MusicCatalogLoader.class.getResourceAsStream("/asset/catalog/music.catalog"))) { //$NON-NLS-1$
		// Fetch data
		final var rawData = new ArrayList<String>();
		var line = Strings.EMPTY;
		while (line != null) {
		    line = rsr.readString();
		    if (line != null) {
			rawData.add(line);
		    }
		}
		MusicCatalogLoader.MUSIC_FILENAMES = rawData;
	    } catch (final IOException e) {
		Diane.handleError(e);
		return null;
	    }
	}
	if (MusicCatalogLoader.MUSIC_FILENAMES == null) {
	    return null;
	}
	return MusicCatalogLoader.MUSIC_FILENAMES.get(music.ordinal());
    }

    private MusicCatalogLoader() {
	// Do nothing
    }
}
