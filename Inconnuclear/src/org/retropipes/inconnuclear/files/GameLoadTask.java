/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JProgressBar;

import org.retropipes.diane.fileio.utility.ZipUtilities;
import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.VersionException;
import org.retropipes.inconnuclear.dungeon.Dungeon;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Strings;

public class GameLoadTask extends Thread {
    // Fields
    private final String filename;
    private final MainWindow mainWindow;
    private final MainContent loadContent;

    // Constructors
    public GameLoadTask(final String file) {
	this.filename = file;
	this.setName("Game Loader");
	this.mainWindow = MainWindow.mainWindow();
	final var loadBar = new JProgressBar();
	loadBar.setIndeterminate(true);
	this.loadContent = MainWindow.createContent();
	this.loadContent.add(loadBar);
    }

    @Override
    public void run() {
	final var sg = "Game";
	final var mazeFile = new File(this.filename);
	try {
	    this.mainWindow.setAndSave(this.loadContent, Strings.dialog(DialogString.LOADING));
	    final var app = Inconnuclear.getStuffBag();
	    int startW;
	    app.getGame().setSavedGameFlag(false);
	    final var tempLock = new File(DungeonBase.getDungeonTempFolder() + "lock.tmp");
	    var gameDungeon = new Dungeon();
	    // Unlock the file
	    GameFileManager.load(mazeFile, tempLock);
	    ZipUtilities.unzipDirectory(tempLock, new File(gameDungeon.getBasePath()));
	    final var success = tempLock.delete();
	    if (!success) {
		throw new IOException("Failed to delete temporary file!");
	    }
	    // Set prefix handler
	    gameDungeon.setPrefixHandler(new PrefixHandler());
	    // Set suffix handler
	    gameDungeon.setSuffixHandler(new SuffixHandler());
	    gameDungeon = gameDungeon.readDungeonBase();
	    if (gameDungeon == null) {
		throw new IOException("Unknown object encountered.");
	    }
	    app.getDungeonManager().setDungeonBase(gameDungeon);
	    startW = gameDungeon.getStartLevel(0);
	    gameDungeon.switchLevel(startW);
	    final var playerExists = gameDungeon.doesPlayerExist(0);
	    if (playerExists) {
		app.getDungeonManager().getDungeonBase().setPlayerToStart();
		app.getGame().resetViewingWindow();
	    }
	    gameDungeon.save();
	    // Final cleanup
	    app.getGame().stateChanged();
	    app.getDungeonManager().setLoaded(true);
	    CommonDialogs.showDialog(sg + " loaded.");
	    app.getGame().playDungeon();
	    app.getDungeonManager().handleDeferredSuccess(true, false, null);
	} catch (final VersionException ve) {
	    CommonDialogs.showDialog(
		    "Loading the " + sg.toLowerCase() + " failed, due to the format version being unsupported.");
	    Inconnuclear.getStuffBag().getDungeonManager().handleDeferredSuccess(false, true, mazeFile);
	} catch (final FileNotFoundException fnfe) {
	    CommonDialogs.showDialog("Loading the " + sg.toLowerCase()
		    + " failed, probably due to illegal characters in the file name.");
	    Inconnuclear.getStuffBag().getDungeonManager().handleDeferredSuccess(false, false, null);
	} catch (final IOException ie) {
	    CommonDialogs
		    .showDialog("Loading the " + sg.toLowerCase() + " failed, due to some other type of I/O error.");
	    Inconnuclear.getStuffBag().getDungeonManager().handleDeferredSuccess(false, false, null);
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	} finally {
	    this.mainWindow.restoreSaved();
	}
    }
}
