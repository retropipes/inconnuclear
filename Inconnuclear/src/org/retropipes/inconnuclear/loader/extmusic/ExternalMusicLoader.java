/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */

package org.retropipes.inconnuclear.loader.extmusic;

import java.io.File;

import org.retropipes.diane.asset.ogg.DianeOggPlayer;

public class ExternalMusicLoader {
    // Fields
    private static String EXTERNAL_LOAD_PATH = null;
    private static DianeOggPlayer EXTERNAL_MUSIC_PLAYER;
    private static ExternalMusic currentExternalMusic;

    public static void deleteExternalMusicFile(final String folder, final String filename) {
	final var file = new File(folder + filename);
	file.delete();
    }

    public static void dungeonChanged() {
	ExternalMusicLoader.EXTERNAL_LOAD_PATH = null;
    }

    public static ExternalMusic getExternalMusic(final String folder, final String filename) {
	if (ExternalMusicLoader.currentExternalMusic == null) {
	    ExternalMusicLoader.loadExternalMusic(folder, filename);
	}
	return ExternalMusicLoader.currentExternalMusic;
    }

    private static DianeOggPlayer getExternalMusicInternal(final String folder, final String filename) {
	if (ExternalMusicLoader.EXTERNAL_LOAD_PATH == null) {
	    ExternalMusicLoader.EXTERNAL_LOAD_PATH = folder;
	}
	final var extm = DianeOggPlayer.loadLoopedFile(ExternalMusicLoader.EXTERNAL_LOAD_PATH + filename);
	ExternalMusicLoader.EXTERNAL_MUSIC_PLAYER = extm;
	return extm;
    }

    public static boolean isExternalMusicPlaying() {
	if (ExternalMusicLoader.EXTERNAL_MUSIC_PLAYER != null
		&& ExternalMusicLoader.EXTERNAL_MUSIC_PLAYER.isPlaying()) {
	    return true;
	}
	return false;
    }

    public static void loadExternalMusic(final String folder, final String filename) {
	final var ellt = new ExternalMusicLoadTask(folder, folder + filename);
	ellt.start();
	// Wait
	if (ellt.isAlive()) {
	    var waiting = true;
	    while (waiting) {
		try {
		    ellt.join();
		    waiting = false;
		} catch (final InterruptedException ie) {
		    // Ignore
		}
	    }
	}
    }

    public static void playExternalMusic(final String folder, final String filename) {
	final var extm = ExternalMusicLoader.getExternalMusicInternal(folder, filename);
	if (extm != null) {
	    ExternalMusicLoader.EXTERNAL_MUSIC_PLAYER = extm;
	    extm.play();
	}
    }

    public static boolean saveExternalMusic(final String folder) {
	// Write external music
	final var extMusicDir = new File(folder);
	if (!extMusicDir.exists()) {
	    final var res = extMusicDir.mkdirs();
	    if (!res) {
		return false;
	    }
	}
	final var filename = ExternalMusicLoader.currentExternalMusic.getName();
	final var filepath = ExternalMusicLoader.currentExternalMusic.getPath();
	final var esst = new ExternalMusicSaveTask(ExternalMusicImporter.getMusicBasePath(), filepath, filename);
	esst.start();
	return true;
    }

    public static void setExternalMusic(final ExternalMusic newExternalMusic) {
	ExternalMusicLoader.currentExternalMusic = newExternalMusic;
    }

    public static void stopExternalMusic() {
	if (ExternalMusicLoader.isExternalMusicPlaying()) {
	    DianeOggPlayer.stopPlaying();
	}
    }

    // Constructors
    private ExternalMusicLoader() {
	// Do nothing
    }
}