/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.extmusic;

import java.io.File;
import java.io.IOException;

import org.retropipes.diane.fileio.utility.FileUtilities;

public class ExternalMusicImporter {
    private static final String MAC_PREFIX = "HOME"; //$NON-NLS-1$
    private static final String WIN_PREFIX = "APPDATA"; //$NON-NLS-1$
    private static final String UNIX_PREFIX = "HOME"; //$NON-NLS-1$
    private static final String MAC_SOUND_DIR = "/Library/Application Support/Putty Software/inconnuclear/TempMusic"; //$NON-NLS-1$
    private static final String WIN_SOUND_DIR = "\\Putty Software\\inconnuclear\\TempMusic"; //$NON-NLS-1$
    private static final String UNIX_SOUND_DIR = "/.puttysoftware/inconnuclear/tempmusic"; //$NON-NLS-1$
    private static File destFile;

    public static File getDestinationFile() {
	return ExternalMusicImporter.destFile;
    }

    private static String getDirPrefix() {
	final var osName = System.getProperty("os.name"); //$NON-NLS-1$
	if (osName.indexOf("Mac OS X") != -1) { //$NON-NLS-1$
	    // Mac OS X
	    return System.getenv(ExternalMusicImporter.MAC_PREFIX);
	}
	if (osName.indexOf("Windows") != -1) { //$NON-NLS-1$
	    // Windows
	    return System.getenv(ExternalMusicImporter.WIN_PREFIX);
	}
	// Other - assume UNIX-like
	return System.getenv(ExternalMusicImporter.UNIX_PREFIX);
    }

    private static String getExtension(final File f) {
	String ext = null;
	final var s = f.getName();
	final var i = s.lastIndexOf('.');
	if (i > 0 && i < s.length() - 1) {
	    ext = s.substring(i).toLowerCase();
	}
	return ext;
    }

    public static String getMusicBasePath() {
	final var b = new StringBuilder();
	b.append(ExternalMusicImporter.getDirPrefix());
	b.append(ExternalMusicImporter.getMusicDirectory());
	return b.toString();
    }

    private static String getMusicDirectory() {
	final var osName = System.getProperty("os.name"); //$NON-NLS-1$
	if (osName.indexOf("Mac OS X") != -1) { //$NON-NLS-1$
	    // Mac OS X
	    return ExternalMusicImporter.MAC_SOUND_DIR;
	}
	if (osName.indexOf("Windows") != -1) { //$NON-NLS-1$
	    // Windows
	    return ExternalMusicImporter.WIN_SOUND_DIR;
	}
	// Other - assume UNIX-like
	return ExternalMusicImporter.UNIX_SOUND_DIR;
    }

    public static String importMusic(final File source) {
	final var basePath = ExternalMusicImporter.getMusicBasePath();
	final var musicfilename = ExternalMusicIDGenerator.generateRandomFilename()
		+ ExternalMusicImporter.getExtension(source);
	final var dest = new File(basePath + File.separator + musicfilename);
	ExternalMusicImporter.destFile = dest;
	try {
	    if (!dest.getParentFile().exists()) {
		dest.getParentFile().mkdirs();
	    }
	    ExternalMusicLoader.deleteExternalMusicFile(basePath, musicfilename);
	    FileUtilities.copyFile(source, dest);
	} catch (final IOException io) {
	    // Ignore
	}
	return musicfilename;
    }
}
