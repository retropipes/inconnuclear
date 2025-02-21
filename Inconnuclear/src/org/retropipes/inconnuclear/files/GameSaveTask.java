/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.retropipes.diane.fileio.utility.ZipUtilities;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;

public class GameSaveTask extends Thread {
    private static boolean hasExtension(final String s) {
	String ext = null;
	final var i = s.lastIndexOf('.');
	if (i > 0 && i < s.length() - 1) {
	    ext = s.substring(i + 1).toLowerCase();
	}
	if (ext == null) {
	    return false;
	}
	return true;
    }

    // Fields
    private String filename;

    // Constructors
    public GameSaveTask(final String file) {
	this.filename = file;
	this.setName("Game Writer");
    }

    @Override
    public void run() {
	var success = true;
	final var sg = "Game";
	try {
	    final var app = Inconnuclear.getStuffBag();
	    // filename check
	    final var hasExtension = GameSaveTask.hasExtension(this.filename);
	    if (!hasExtension) {
		this.filename += Strings.fileExtension(FileExtension.SUSPEND);
	    }
	    final var mazeFile = new File(this.filename);
	    final var tempLock = new File(DungeonBase.getDungeonTempFolder() + "lock.tmp");
	    // Set prefix handler
	    app.getDungeonManager().getDungeonBase().setPrefixHandler(new PrefixHandler());
	    // Set suffix handler
	    app.getDungeonManager().getDungeonBase().setSuffixHandler(new SuffixHandler());
	    app.getDungeonManager().getDungeonBase().writeDungeon();
	    ZipUtilities.zipDirectory(new File(app.getDungeonManager().getDungeonBase().getBasePath()), tempLock);
	    // Lock the file
	    GameFileManager.save(tempLock, mazeFile);
	    final var delSuccess = tempLock.delete();
	    if (!delSuccess) {
		throw new IOException("Failed to delete temporary file!");
	    }
	    app.showMessage(sg + " saved.");
	} catch (final FileNotFoundException fnfe) {
	    CommonDialogs.showDialog("Writing the " + sg.toLowerCase()
		    + " failed, probably due to illegal characters in the file name.");
	    success = false;
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
	Inconnuclear.getStuffBag().getDungeonManager().handleDeferredSuccess(success, false, null);
    }
}
