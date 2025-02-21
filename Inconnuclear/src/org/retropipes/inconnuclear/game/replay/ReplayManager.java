/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game.replay;

import java.io.File;

import org.retropipes.diane.fileio.utility.FilenameChecker;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.settings.Settings;

public class ReplayManager {
    private static String getExtension(final String s) {
	String ext = null;
	final var i = s.lastIndexOf('.');
	if (i > 0 && i < s.length() - 1) {
	    ext = s.substring(i + 1).toLowerCase();
	}
	return ext;
    }

    private static String getFileNameOnly(final String s) {
	String fno = null;
	final var i = s.lastIndexOf(File.separatorChar);
	if (i > 0 && i < s.length() - 1) {
	    fno = s.substring(i + 1);
	} else {
	    fno = s;
	}
	return fno;
    }

    private static String getNameWithoutExtension(final String s) {
	String ext = null;
	final var i = s.lastIndexOf('.');
	if (i > 0 && i < s.length() - 1) {
	    ext = s.substring(0, i);
	} else {
	    ext = s;
	}
	return ext;
    }

    public static void loadFile(final String filename) {
	if (!FilenameChecker
		.isFilenameOK(ReplayManager.getNameWithoutExtension(ReplayManager.getFileNameOnly(filename)))) {
	    CommonDialogs.showErrorDialog(Strings.dialog(DialogString.ILLEGAL_CHARACTERS),
		    Strings.dialog(DialogString.LOAD));
	} else {
	    final var lpblt = new ReplayFileLoadTask(filename);
	    lpblt.start();
	}
    }

    public static void loadReplay() {
	String filename, extension;
	final var lastOpen = Settings.getLastDirOpen();
	File file = CommonDialogs.showFileOpenDialog(new File(lastOpen), null, Strings.dialog(DialogString.LOAD));
	if (file != null) {
	    filename = file.getAbsolutePath();
	    extension = ReplayManager.getExtension(filename);
	    if (extension.equals(Strings.fileExtension(FileExtension.REPLAY))) {
		ReplayManager.loadFile(filename);
	    } else {
		CommonDialogs.showDialog(Strings.dialog(DialogString.NON_PLAYBACK_FILE));
	    }
	}
    }

    // Constructors
    private ReplayManager() {
	// Do nothing
    }
}
