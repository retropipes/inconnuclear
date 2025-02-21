/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game.replay;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JProgressBar;

import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.GameString;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

class ReplayFileLoadTask extends Thread {
    // Fields
    private final String filename;
    private final MainWindow mainWindow;
    private final MainContent loadContent;

    // Constructors
    ReplayFileLoadTask(final String file) {
	JProgressBar loadBar;
	this.filename = file;
	this.setName(Strings.untranslated(Untranslated.REPLAY_LOADER_NAME));
	this.mainWindow = MainWindow.mainWindow();
	loadBar = new JProgressBar();
	loadBar.setIndeterminate(true);
	this.loadContent = MainWindow.createContent();
	this.loadContent.add(loadBar);
    }

    @Override
    public void run() {
	this.mainWindow.setAndSave(this.loadContent, Strings.dialog(DialogString.LOADING));
	final var app = Inconnuclear.getStuffBag();
	app.getGame().setSavedGameFlag(false);
	try (var dungeonFile = new FileInputStream(this.filename)) {
	    ReplayFile.loadLPB(dungeonFile);
	    dungeonFile.close();
	} catch (final FileNotFoundException fnfe) {
	    CommonDialogs.showDialog(Strings.game(GameString.PLAYBACK_LOAD_FAILED));
	} catch (final IOException ie) {
	    CommonDialogs.showDialog(ie.getMessage());
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	} finally {
	    this.mainWindow.restoreSaved();
	}
    }
}
