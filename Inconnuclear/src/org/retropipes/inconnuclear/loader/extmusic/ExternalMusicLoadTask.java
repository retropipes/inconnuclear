/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.extmusic;

import java.io.File;

import org.retropipes.diane.Diane;

public class ExternalMusicLoadTask extends Thread {
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

    // Fields
    private ExternalMusic gameExternalMusic;
    private final String folder;
    private final String filename;

    // Constructors
    public ExternalMusicLoadTask(final String loadFolder, final String loadFilename) {
	this.folder = loadFolder;
	this.filename = loadFilename;
	this.setName("External Music Loader");
    }

    @Override
    public void run() {
	try {
	    this.gameExternalMusic = new ExternalMusic();
	    this.gameExternalMusic.setName(ExternalMusicLoadTask.getFileNameOnly(this.filename));
	    this.gameExternalMusic.setPath(this.folder);
	    ExternalMusicLoader.setExternalMusic(this.gameExternalMusic);
	} catch (final Exception ex) {
	    Diane.handleError(ex);
	}
    }
}
