/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.files;

import java.io.IOException;

import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.game.FileHooks;
import org.retropipes.inconnuclear.settings.Settings;

public class SuffixHandler implements AbstractSuffixIO {
    @Override
    public void readSuffix(final DataIOReader reader, final int formatVersion) throws IOException {
	Inconnuclear.getStuffBag().getGame();
	FileHooks.loadGameHook(reader, Settings.getGameDifficulty());
    }

    @Override
    public void writeSuffix(final DataIOWriter writer) throws IOException {
	Inconnuclear.getStuffBag().getGame();
	FileHooks.saveGameHook(writer);
    }
}
