/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipException;

import javax.swing.JProgressBar;

import org.retropipes.diane.fileio.utility.ZipUtilities;
import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.dungeon.manager.DungeonManager;
import org.retropipes.inconnuclear.loader.extmusic.ExternalMusicLoader;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.ErrorString;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;
import org.retropipes.inconnuclear.utility.InvalidDungeonException;

public class DungeonLoadTask extends Thread {
    // Fields
    private final String filename;
    private final MainContent loadContent;
    private final boolean isSavedGame;
    private final MainWindow mainWindow;
    private final boolean dungeonProtected;

    // Constructors
    public DungeonLoadTask(final String file, final boolean saved, final boolean protect) {
	JProgressBar loadBar;
	this.filename = file;
	this.isSavedGame = saved;
	this.dungeonProtected = protect;
	this.setName(Strings.untranslated(Untranslated.FILE_LOADER_NAME));
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
	if (this.isSavedGame) {
	    app.getGame().setSavedGameFlag(true);
	} else {
	    app.getGame().setSavedGameFlag(false);
	}
	try {
	    final var dungeonFile = new File(this.filename);
	    final var tempLock = new File(DungeonBase.getDungeonTempFolder() + "lock.tmp");
	    var gameDungeon = DungeonManager.createDungeonBase();
	    if (this.dungeonProtected) {
		// Attempt to unprotect the file
		DungeonProtectionWrapper.unprotect(dungeonFile, tempLock);
		try {
		    ZipUtilities.unzipDirectory(tempLock, new File(gameDungeon.getBasePath()));
		    app.getDungeonManager().setDungeonProtected(true);
		} catch (final ZipException ze) {
		    CommonDialogs.showErrorDialog(Strings.error(ErrorString.BAD_PROTECTION_KEY),
			    Strings.dialog(DialogString.PROTECTION_TITLE));
		    app.getDungeonManager().handleDeferredSuccess(false, false, null);
		    return;
		} finally {
		    tempLock.delete();
		}
	    } else {
		ZipUtilities.unzipDirectory(dungeonFile, new File(gameDungeon.getBasePath()));
		app.getDungeonManager().setDungeonProtected(false);
	    }
	    // Set prefix handler
	    gameDungeon.setPrefixHandler(new DungeonFilePrefixHandler());
	    // Set suffix handler
	    if (this.isSavedGame) {
		gameDungeon.setSuffixHandler(new DungeonFileSuffixHandler());
	    } else {
		gameDungeon.setSuffixHandler(null);
	    }
	    gameDungeon = gameDungeon.readDungeonBase();
	    if (gameDungeon == null) {
		throw new InvalidDungeonException(Strings.error(ErrorString.UNKNOWN_OBJECT));
	    }
	    app.getDungeonManager().setDungeonBase(gameDungeon);
	    final var playerExists = gameDungeon.doesPlayerExist(0);
	    if (playerExists) {
		app.getGame().getPlayerManager().resetPlayerLocation();
	    }
	    if (!this.isSavedGame) {
		gameDungeon.save();
	    }
	    // Final cleanup
	    final var lum = app.getDungeonManager().getLastUsedDungeon();
	    final var lug = app.getDungeonManager().getLastUsedGame();
	    app.getDungeonManager().clearLastUsedFilenames();
	    if (this.isSavedGame) {
		app.getDungeonManager().setLastUsedGame(lug);
	    } else {
		app.getDungeonManager().setLastUsedDungeon(lum);
	    }
	    app.getEditor().dungeonChanged();
	    ExternalMusicLoader.dungeonChanged();
	    if (this.isSavedGame) {
		CommonDialogs.showDialog(Strings.dialog(DialogString.GAME_LOADING_SUCCESS));
	    } else {
		CommonDialogs.showDialog(Strings.dialog(DialogString.DUNGEON_LOADING_SUCCESS));
	    }
	    app.getDungeonManager().handleDeferredSuccess(true, false, null);
	} catch (final FileNotFoundException fnfe) {
	    if (this.isSavedGame) {
		CommonDialogs.showDialog(Strings.dialog(DialogString.GAME_LOADING_FAILED));
	    } else {
		CommonDialogs.showDialog(Strings.dialog(DialogString.DUNGEON_LOADING_FAILED));
	    }
	    app.getDungeonManager().handleDeferredSuccess(false, false, null);
	} catch (final ProtectionCancelException pce) {
	    app.getDungeonManager().handleDeferredSuccess(false, false, null);
	} catch (final IOException ie) {
	    if (this.isSavedGame) {
		CommonDialogs.showDialog(Strings.dialog(DialogString.GAME_LOADING_FAILED));
	    } else {
		CommonDialogs.showDialog(Strings.dialog(DialogString.DUNGEON_LOADING_FAILED));
	    }
	    Inconnuclear.logWarningDirectly(ie);
	    app.getDungeonManager().handleDeferredSuccess(false, false, null);
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	} finally {
	    this.mainWindow.restoreSaved();
	}
    }
}
