/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.io.IOException;

import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.inconnuclear.creature.GameDifficulty;
import org.retropipes.inconnuclear.creature.party.PartyManager;

public class FileHooks {
    public static void loadGameHook(final DataIOReader mapFile, final GameDifficulty diff) throws IOException {
	PartyManager.loadGameHook(mapFile, diff);
    }

    public static void saveGameHook(final DataIOWriter mapFile) throws IOException {
	PartyManager.saveGameHook(mapFile);
    }

    private FileHooks() {
	// Do nothing
    }
}
