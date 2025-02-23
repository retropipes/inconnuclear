/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.music;

import java.io.IOException;

import org.retropipes.diane.asset.music.DianeMusicPlayer;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.locale.Music;

public class MusicLoader {
    public static boolean isMusicPlaying() {
	return DianeMusicPlayer.isPlaying();
    }

    public static void playMusic(final Music musicID) {
	// Play the music
	try {
	    DianeMusicPlayer.play(musicID);
	} catch (IOException e) {
	    Inconnuclear.logError(e);
	}
    }

    public static void stopMusic() {
	DianeMusicPlayer.stopPlaying();
    }

    // Constructors
    private MusicLoader() {
	// Do nothing
    }
}