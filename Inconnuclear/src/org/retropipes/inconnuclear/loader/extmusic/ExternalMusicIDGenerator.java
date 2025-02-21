/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.extmusic;

import org.retropipes.diane.random.RandomLongRange;

public class ExternalMusicIDGenerator {
    public static String generateRandomFilename() {
	return Long.toString(RandomLongRange.generateRaw(), 36).toLowerCase();
    }

    // Constructor
    private ExternalMusicIDGenerator() {
	// Do nothing
    }
}