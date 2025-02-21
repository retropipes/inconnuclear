/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.utility;

import java.io.File;

import org.retropipes.diane.fileio.utility.DirectoryUtilities;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;

public class CleanupTask {
    public static void cleanUp() {
	try {
	    final var dirToDelete = new File(DungeonBase.getDungeonTempFolder());
	    DirectoryUtilities.removeDirectory(dirToDelete);
	} catch (final Throwable t) {
	    // Ignore
	}
    }

    private CleanupTask() {
	// Do nothing
    }
}
