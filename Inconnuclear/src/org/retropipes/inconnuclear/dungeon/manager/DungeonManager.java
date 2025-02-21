/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.dungeon.manager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.retropipes.diane.fileio.utility.FilenameChecker;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.StuffBag;
import org.retropipes.inconnuclear.dungeon.Dungeon;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.files.DungeonLoadTask;
import org.retropipes.inconnuclear.files.DungeonSaveTask;
import org.retropipes.inconnuclear.files.GameFinder;
import org.retropipes.inconnuclear.files.GameLoadTask;
import org.retropipes.inconnuclear.files.GameSaveTask;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.EditorString;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;
import org.retropipes.inconnuclear.settings.Settings;
import org.retropipes.inconnuclear.utility.CleanupTask;

public final class DungeonManager {
    private static final String MAC_PREFIX = "HOME";
    private static final String WIN_PREFIX = "APPDATA";
    private static final String UNIX_PREFIX = "HOME";
    private static final String MAC_DIR = "/Library/Application Support/Ignition Igloo Games/Chrystalz/Games/";
    private static final String WIN_DIR = "\\Ignition Igloo Games\\Chrystalz\\Games\\";
    private static final String UNIX_DIR = "/.ignitionigloogames/chrystalz/games/";

    public static DungeonBase createDungeonBase() throws IOException {
	return new Dungeon();
    }

    private static String getExtension(final String s) {
	String ext = null;
	final var i = s.lastIndexOf('.');
	if (i > 0 && i < s.length() - 1) {
	    ext = s.substring(i).toLowerCase();
	}
	return ext;
    }

    private static String getFileNameOnly(final String s) {
	String fno = null;
	final var i = s.lastIndexOf(File.pathSeparatorChar);
	if (i > 0 && i < s.length() - 1) {
	    fno = s.substring(i + 1);
	} else {
	    fno = s;
	}
	return fno;
    }

    private static String getGameDirectory() {
	final var b = new StringBuilder();
	b.append(DungeonManager.getGameDirectoryPrefix());
	b.append(DungeonManager.getGameDirectoryName());
	return b.toString();
    }

    private static String getGameDirectoryName() {
	final var osName = System.getProperty("os.name");
	if (osName.indexOf("Mac OS X") != -1) {
	    // Mac OS X
	    return DungeonManager.MAC_DIR;
	}
	if (osName.indexOf("Windows") != -1) {
	    // Windows
	    return DungeonManager.WIN_DIR;
	}
	// Other - assume UNIX-like
	return DungeonManager.UNIX_DIR;
    }

    private static String getGameDirectoryPrefix() {
	final var osName = System.getProperty("os.name");
	if (osName.indexOf("Mac OS X") != -1) {
	    // Mac OS X
	    return System.getenv(DungeonManager.MAC_PREFIX);
	}
	if (osName.indexOf("Windows") != -1) {
	    // Windows
	    return System.getenv(DungeonManager.WIN_PREFIX);
	}
	// Other - assume UNIX-like
	return System.getenv(DungeonManager.UNIX_PREFIX);
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

    private static void loadDungeonFile(final String filename, final boolean isSavedGame, final boolean protect) {
	if (!FilenameChecker
		.isFilenameOK(DungeonManager.getNameWithoutExtension(DungeonManager.getFileNameOnly(filename)))) {
	    CommonDialogs.showErrorDialog(Strings.dialog(DialogString.ILLEGAL_CHARACTERS),
		    Strings.dialog(DialogString.LOAD));
	} else {
	    // Run cleanup task
	    CleanupTask.cleanUp();
	    // Load file
	    final var xlt = new DungeonLoadTask(filename, isSavedGame, protect);
	    xlt.start();
	}
    }

    private static void loadGameFile(final String filename) {
	if (!FilenameChecker
		.isFilenameOK(DungeonManager.getNameWithoutExtension(DungeonManager.getFileNameOnly(filename)))) {
	    CommonDialogs.showErrorDialog("""
	    	The file you selected contains illegal characters in its
	    	name. These characters are not allowed: /?<>\\:|"
	    	Files named con, nul, or prn are illegal, as are files
	    	named com1 through com9 and lpt1 through lpt9.""", "Load");
	} else {
	    final var llt = new GameLoadTask(filename);
	    llt.start();
	}
    }

    private static void saveDungeonFile(final String filename, final boolean isSavedGame, final boolean protect) {
	if (isSavedGame) {
	    Inconnuclear.getStuffBag().showMessage(Strings.dialog(DialogString.SUSPENDING_GAME));
	} else {
	    Inconnuclear.getStuffBag().showMessage(Strings.dialog(DialogString.SAVING_DUNGEON));
	}
	final var xst = new DungeonSaveTask(filename, isSavedGame, protect);
	xst.start();
    }

    public static boolean saveGame() {
	var filename = "";
	String extension;
	var returnVal = "\\";
	while (!FilenameChecker.isFilenameOK(returnVal)) {
	    returnVal = CommonDialogs.showTextInputDialog("Name?", "Save Game");
	    if (returnVal == null) {
		break;
	    }
	    extension = Strings.fileExtension(FileExtension.SUSPEND);
	    final var file = new File(DungeonManager.getGameDirectory() + returnVal + extension);
	    filename = file.getAbsolutePath();
	    if (!FilenameChecker.isFilenameOK(returnVal)) {
		CommonDialogs.showErrorDialog("""
			The file name you entered contains illegal characters.
			These characters are not allowed: /?<>\\:|"
			Files named con, nul, or prn are illegal, as are files
			named com1 through com9 and lpt1 through lpt9.""", "Save Game");
	    } else {
		// Make sure folder exists
		if (!file.getParentFile().exists()) {
		    final var okay = file.getParentFile().mkdirs();
		    if (!okay) {
			Inconnuclear.logError(new IOException("Cannot create game folder!"));
		    }
		}
		DungeonManager.saveGameFile(filename);
	    }
	}
	return false;
    }

    private static void saveGameFile(final String filename) {
	final var sg = "Saved Game";
	Inconnuclear.getStuffBag().showMessage("Saving " + sg + " file...");
	final var lst = new GameSaveTask(filename);
	lst.start();
    }

    public static int showSaveDialog() {
	String type, source;
	final var app = Inconnuclear.getStuffBag();
	final var mode = app.getMode();
	if (mode == StuffBag.STATUS_EDITOR) {
	    type = Strings.dialog(DialogString.PROMPT_SAVE_ARENA);
	    source = Strings.editor(EditorString.EDITOR);
	} else if (mode == StuffBag.STATUS_GAME) {
	    type = Strings.dialog(DialogString.PROMPT_SAVE_GAME);
	    source = Strings.untranslated(Untranslated.PROGRAM_NAME);
	} else {
	    // Not in the game or editor, so abort
	    return CommonDialogs.NO_OPTION;
	}
	return CommonDialogs.showYNCConfirmDialog(type, source);
    }

    // Fields
    private DungeonBase gameDungeonBase;
    private boolean loaded, isDirty;
    private String scoresFileName;
    private String lastUsedDungeonFile;
    private String lastUsedGameFile;
    private boolean dungeonProtected;

    // Constructors
    public DungeonManager() {
	this.loaded = false;
	this.isDirty = false;
	this.gameDungeonBase = null;
	this.lastUsedDungeonFile = Strings.EMPTY;
	this.lastUsedGameFile = Strings.EMPTY;
	this.scoresFileName = Strings.EMPTY;
    }

    public void clearLastUsedFilenames() {
	this.lastUsedDungeonFile = Strings.EMPTY;
	this.lastUsedGameFile = Strings.EMPTY;
    }

    public boolean getDirty() {
	return this.isDirty;
    }

    public DungeonBase getDungeonBase() {
	return this.gameDungeonBase;
    }

    public String getLastUsedDungeon() {
	return this.lastUsedDungeonFile;
    }

    public String getLastUsedGame() {
	return this.lastUsedGameFile;
    }

    public boolean getLoaded() {
	return this.loaded;
    }

    public String getScoresFileName() {
	return this.scoresFileName;
    }

    public void handleDeferredSuccess(final boolean value, final boolean versionError, final File triedToLoad) {
	if (value) {
	    this.setLoaded(true);
	}
	if (versionError) {
	    triedToLoad.delete();
	}
	this.setDirty(false);
	Inconnuclear.getStuffBag().getGame().stateChanged();
	Inconnuclear.getStuffBag().getMenus().checkFlags();
    }

    public boolean isDungeonProtected() {
	return this.dungeonProtected;
    }

    public boolean loadDungeon() {
	return this.loadDungeonImpl(Settings.getLastDirOpen());
    }

    public boolean loadDungeonDefault() {
	try {
	    return this.loadDungeonImpl(
		    DungeonManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
			    + File.separator + "Common" + File.separator + "Levels");
	} catch (final URISyntaxException e) {
	    return this.loadDungeon();
	}
    }

    private boolean loadDungeonImpl(final String initialDirectory) {
	var status = 0;
	var saved = true;
	var fileOnly = Strings.EMPTY;
	String filename, extension;
	final String dir = initialDirectory;
	if (this.getDirty()) {
	    status = DungeonManager.showSaveDialog();
	    if (status == CommonDialogs.YES_OPTION) {
		saved = this.saveDungeon(this.isDungeonProtected());
	    } else if (status == CommonDialogs.CANCEL_OPTION) {
		saved = false;
	    } else {
		this.setDirty(false);
	    }
	}
	if (saved) {
	    File file = CommonDialogs.showFileOpenDialog(new File(dir), null, Strings.dialog(DialogString.LOAD));
	    if (file != null) {
		filename = file.getAbsolutePath();
		fileOnly = filename.substring(dir.length() + 1);
		extension = DungeonManager.getExtension(fileOnly);
		if (extension.equals(Strings.fileExtension(FileExtension.DUNGEON))) {
		    this.lastUsedDungeonFile = filename;
		    this.scoresFileName = DungeonManager.getNameWithoutExtension(fileOnly);
		    DungeonManager.loadDungeonFile(filename, false, false);
		} else if (extension.equals(Strings.fileExtension(FileExtension.PROTECTED_DUNGEON))) {
		    this.lastUsedDungeonFile = filename;
		    this.scoresFileName = DungeonManager.getNameWithoutExtension(fileOnly);
		    DungeonManager.loadDungeonFile(filename, false, true);
		} else if (extension.equals(Strings.fileExtension(FileExtension.SUSPEND))) {
		    this.lastUsedGameFile = filename;
		    DungeonManager.loadDungeonFile(filename, true, false);
		} else {
		    CommonDialogs.showDialog(Strings.dialog(DialogString.NON_DUNGEON_FILE));
		}
	    } else // User cancelled
	    if (this.loaded) {
		return true;
	    }
	}
	return false;
    }

    public boolean loadGame() {
	var status = 0;
	var saved = true;
	String filename;
	final var gf = new GameFinder();
	if (this.getDirty()) {
	    status = DungeonManager.showSaveDialog();
	    if (status == CommonDialogs.YES_OPTION) {
		saved = DungeonManager.saveGame();
	    } else if (status == CommonDialogs.CANCEL_OPTION) {
		saved = false;
	    } else {
		this.setDirty(false);
	    }
	}
	if (saved) {
	    final var gameDir = DungeonManager.getGameDirectory();
	    final var rawChoices = new File(gameDir).list(gf);
	    if (rawChoices != null && rawChoices.length > 0) {
		final var choices = new String[rawChoices.length];
		// Strip extension
		for (var x = 0; x < choices.length; x++) {
		    choices[x] = DungeonManager.getNameWithoutExtension(rawChoices[x]);
		}
		final var returnVal = CommonDialogs.showInputDialog("Select a Game", "Load Game", choices, choices[0]);
		if (returnVal != null) {
		    var index = -1;
		    for (var x = 0; x < choices.length; x++) {
			if (returnVal.equals(choices[x])) {
			    index = x;
			    break;
			}
		    }
		    if (index != -1) {
			final var file = new File(gameDir + rawChoices[index]);
			filename = file.getAbsolutePath();
			DungeonManager.loadGameFile(filename);
		    } else // Result not found
		    if (this.loaded) {
			return true;
		    }
		} else // User cancelled
		if (this.loaded) {
		    return true;
		}
	    } else {
		CommonDialogs.showErrorDialog("No Games Found!", "Load Game");
		if (this.loaded) {
		    return true;
		}
	    }
	}
	return false;
    }

    public boolean saveDungeon(final boolean protect) {
	final var app = Inconnuclear.getStuffBag();
	if (app.getMode() == StuffBag.STATUS_GAME) {
	    if (this.lastUsedGameFile == null || this.lastUsedGameFile.equals(Strings.EMPTY)) {
		return this.saveDungeonAs(protect);
	    }
	    final var extension = DungeonManager.getExtension(this.lastUsedGameFile);
	    if (extension != null) {
		if (!extension.equals(Strings.fileExtension(FileExtension.SUSPEND))) {
		    this.lastUsedGameFile = DungeonManager.getNameWithoutExtension(this.lastUsedGameFile)
			    + Strings.fileExtension(FileExtension.SUSPEND);
		}
	    } else {
		this.lastUsedGameFile += Strings.fileExtension(FileExtension.SUSPEND);
	    }
	    DungeonManager.saveDungeonFile(this.lastUsedGameFile, true, false);
	} else {
	    if (protect) {
		if (this.lastUsedDungeonFile == null || this.lastUsedDungeonFile.equals(Strings.EMPTY)) {
		    return this.saveDungeonAs(protect);
		}
		final var extension = DungeonManager.getExtension(this.lastUsedDungeonFile);
		if (extension != null) {
		    if (!extension.equals(Strings.fileExtension(FileExtension.PROTECTED_DUNGEON))) {
			this.lastUsedDungeonFile = DungeonManager.getNameWithoutExtension(this.lastUsedDungeonFile)
				+ Strings.fileExtension(FileExtension.PROTECTED_DUNGEON);
		    }
		} else {
		    this.lastUsedDungeonFile += Strings.fileExtension(FileExtension.PROTECTED_DUNGEON);
		}
	    } else {
		if (this.lastUsedDungeonFile == null || this.lastUsedDungeonFile.equals(Strings.EMPTY)) {
		    return this.saveDungeonAs(protect);
		}
		final var extension = DungeonManager.getExtension(this.lastUsedDungeonFile);
		if (extension != null) {
		    if (!extension.equals(Strings.fileExtension(FileExtension.DUNGEON))) {
			this.lastUsedDungeonFile = DungeonManager.getNameWithoutExtension(this.lastUsedDungeonFile)
				+ Strings.fileExtension(FileExtension.DUNGEON);
		    }
		} else {
		    this.lastUsedDungeonFile += Strings.fileExtension(FileExtension.DUNGEON);
		}
	    }
	    DungeonManager.saveDungeonFile(this.lastUsedDungeonFile, false, protect);
	}
	return false;
    }

    public boolean saveDungeonAs(final boolean protect) {
	final var app = Inconnuclear.getStuffBag();
	var filename = Strings.EMPTY;
	var fileOnly = Strings.EMPTY;
	String extension;
	final var dir = Settings.getLastDirSave();
	while (!FilenameChecker.isFilenameOK(fileOnly)) {
	    File file = CommonDialogs.showFileSaveDialog(new File(dir), Strings.dialog(DialogString.LOAD));
	    if (file == null) {
		break;
	    }
	    filename = file.getAbsolutePath();
	    extension = DungeonManager.getExtension(filename);
	    fileOnly = filename.substring(dir.length() + 1);
	    if (!FilenameChecker.isFilenameOK(fileOnly)) {
		CommonDialogs.showErrorDialog(Strings.dialog(DialogString.ILLEGAL_CHARACTERS),
			Strings.dialog(DialogString.SAVE));
	    } else {
		Settings.setLastDirSave(dir);
		if (app.getMode() == StuffBag.STATUS_GAME) {
		    if (extension != null) {
			if (!extension.equals(Strings.fileExtension(FileExtension.SUSPEND))) {
			    filename = DungeonManager.getNameWithoutExtension(fileOnly)
				    + Strings.fileExtension(FileExtension.SUSPEND);
			}
		    } else {
			filename += Strings.fileExtension(FileExtension.SUSPEND);
		    }
		    this.lastUsedGameFile = filename;
		    DungeonManager.saveDungeonFile(filename, true, false);
		} else {
		    if (protect) {
			if (extension != null) {
			    if (!extension.equals(Strings.fileExtension(FileExtension.PROTECTED_DUNGEON))) {
				filename = DungeonManager.getNameWithoutExtension(fileOnly)
					+ Strings.fileExtension(FileExtension.PROTECTED_DUNGEON);
			    }
			} else {
			    filename += Strings.fileExtension(FileExtension.PROTECTED_DUNGEON);
			}
		    } else if (extension != null) {
			if (!extension.equals(Strings.fileExtension(FileExtension.DUNGEON))) {
			    filename = DungeonManager.getNameWithoutExtension(fileOnly)
				    + Strings.fileExtension(FileExtension.DUNGEON);
			}
		    } else {
			filename += Strings.fileExtension(FileExtension.DUNGEON);
		    }
		    this.lastUsedDungeonFile = filename;
		    this.scoresFileName = DungeonManager.getNameWithoutExtension(fileOnly);
		    DungeonManager.saveDungeonFile(filename, false, protect);
		}
	    }
	}
	return false;
    }

    public void setDirty(final boolean newDirty) {
	final var app = Inconnuclear.getStuffBag();
	this.isDirty = newDirty;
	final var frame = MainWindow.mainWindow();
	if (frame != null) {
	    frame.setDirty(newDirty);
	}
	app.getMenus().checkFlags();
    }

    public void setDungeonBase(final DungeonBase newDungeonBase) {
	this.gameDungeonBase = newDungeonBase;
    }

    public void setDungeonProtected(final boolean value) {
	this.dungeonProtected = value;
    }

    public void setLastUsedDungeon(final String newFile) {
	this.lastUsedDungeonFile = newFile;
    }

    public void setLastUsedGame(final String newFile) {
	this.lastUsedGameFile = newFile;
    }

    public void setLoaded(final boolean status) {
	final var app = Inconnuclear.getStuffBag();
	this.loaded = status;
	app.getMenus().checkFlags();
    }

    public void setScoresFileName(final String filename) {
	this.scoresFileName = filename;
    }
}
