/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear;

import java.awt.GridLayout;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.retropipes.diane.gui.GUIPrinter;
import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.diane.integration.Integration;
import org.retropipes.inconnuclear.dungeon.manager.DungeonManager;
import org.retropipes.inconnuclear.loader.music.MusicLoader;
import org.retropipes.inconnuclear.locale.Menu;
import org.retropipes.inconnuclear.locale.Music;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;
import org.retropipes.inconnuclear.settings.Settings;
import org.retropipes.inconnuclear.utility.CleanupTask;

public class GUIManager implements MenuSection, QuitHandler {
    private class CloseHandler implements WindowListener {
	public CloseHandler() {
	    // Do nothing
	}

	@Override
	public void windowActivated(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowClosed(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowClosing(final WindowEvent arg0) {
	    if (GUIManager.this.quitHandler()) {
		System.exit(0);
	    }
	}

	@Override
	public void windowDeactivated(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowDeiconified(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowIconified(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowOpened(final WindowEvent arg0) {
	    // Do nothing
	}
    }

    private static class MenuHandler implements ActionListener {
	public MenuHandler() {
	    // Do nothing
	}

	// Handle menus
	@Override
	public void actionPerformed(final ActionEvent e) {
	    try {
		final var app = Inconnuclear.getStuffBag();
		var loaded = false;
		final var cmd = e.getActionCommand();
		if (cmd.equals(Strings.menu(Menu.NEW))) {
		    loaded = app.getEditor().newDungeon();
		    app.getDungeonManager().setLoaded(loaded);
		} else if (cmd.equals(Strings.menu(Menu.OPEN))) {
		    loaded = app.getDungeonManager().loadDungeon();
		    app.getDungeonManager().setLoaded(loaded);
		} else if (cmd.equals(Strings.menu(Menu.OPEN_DEFAULT))) {
		    loaded = app.getDungeonManager().loadDungeonDefault();
		    app.getDungeonManager().setLoaded(loaded);
		} else if (cmd.equals(Strings.menu(Menu.CLOSE))) {
		    // Close the window
		    if (app.getMode() == StuffBag.STATUS_EDITOR) {
			app.getEditor().handleCloseWindow();
		    } else if (app.getMode() == StuffBag.STATUS_GAME) {
			var saved = true;
			var status = 0;
			if (app.getDungeonManager().getDirty()) {
			    status = DungeonManager.showSaveDialog();
			    if (status == CommonDialogs.YES_OPTION) {
				saved = app.getDungeonManager()
					.saveDungeon(app.getDungeonManager().isDungeonProtected());
			    } else if (status == CommonDialogs.CANCEL_OPTION) {
				saved = false;
			    } else {
				app.getDungeonManager().setDirty(false);
			    }
			}
			if (saved) {
			    app.getGame().exitGame();
			}
		    }
		    app.getGUIManager().showGUI();
		} else if (cmd.equals(Strings.menu(Menu.SAVE))) {
		    if (app.getDungeonManager().getLoaded()) {
			app.getDungeonManager().saveDungeon(app.getDungeonManager().isDungeonProtected());
		    } else {
			CommonDialogs.showDialog(Strings.menu(Menu.ERROR_NO_DUNGEON_OPENED));
		    }
		} else if (cmd.equals(Strings.menu(Menu.SAVE_AS))) {
		    if (app.getDungeonManager().getLoaded()) {
			app.getDungeonManager().saveDungeonAs(false);
		    } else {
			CommonDialogs.showDialog(Strings.menu(Menu.ERROR_NO_DUNGEON_OPENED));
		    }
		} else if (cmd.equals(Strings.menu(Menu.SAVE_AS_PROTECTED))) {
		    if (app.getDungeonManager().getLoaded()) {
			app.getDungeonManager().saveDungeonAs(true);
		    } else {
			CommonDialogs.showDialog(Strings.menu(Menu.ERROR_NO_DUNGEON_OPENED));
		    }
		} else if (cmd.equals(Strings.menu(Menu.PREFERENCES))) {
		    // Show preferences dialog
		    Settings.showPrefs();
		} else if (cmd.equals(Strings.menu(Menu.PRINT_GAME_WINDOW))) {
		    GUIPrinter.printScreen();
		} else if (cmd.equals(Strings.menu(Menu.EXIT))) {
		    // Exit program
		    if (app.getGUIManager().quitHandler()) {
			System.exit(0);
		    }
		} else // Quit program
		if (cmd.equals(Strings.menu(Menu.QUIT)) && app.getGUIManager().quitHandler()) {
		    System.exit(0);
		}
		app.getMenus().checkFlags();
	    } catch (final Exception ex) {
		Inconnuclear.logError(ex);
	    }
	}
    }

    // Fields
    private final MainWindow mainWindow;
    private final CloseHandler cHandler;
    private final MainContent guiPane;
    private final JLabel logoLabel;
    private JMenuItem fileNew, fileOpen, fileOpenDefault, fileClose, fileSave, fileSaveAs, fileSaveAsProtected,
	    filePrint, filePreferences, fileExit;

    // Constructors
    public GUIManager() {
	this.cHandler = new CloseHandler();
	this.mainWindow = MainWindow.mainWindow();
	this.guiPane = MainWindow.createContent();
	this.guiPane.setLayout(new GridLayout(1, 1));
	this.logoLabel = new JLabel(Strings.EMPTY, null, SwingConstants.CENTER);
	this.logoLabel.setLabelFor(null);
	this.logoLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
	this.guiPane.add(this.logoLabel);
    }

    @Override
    public void attachAccelerators(final Accelerators accel) {
	this.fileNew.setAccelerator(accel.fileNewAccel);
	this.fileOpen.setAccelerator(accel.fileOpenAccel);
	this.fileClose.setAccelerator(accel.fileCloseAccel);
	this.fileSave.setAccelerator(accel.fileSaveAccel);
	this.fileSaveAs.setAccelerator(accel.fileSaveAsAccel);
	this.filePreferences.setAccelerator(accel.filePreferencesAccel);
	this.filePrint.setAccelerator(accel.filePrintAccel);
	if (System.getProperty(Strings.untranslated(Untranslated.OS_NAME))
		.contains(Strings.untranslated(Untranslated.WINDOWS))) {
	    this.fileExit.setAccelerator(null);
	} else {
	    this.fileExit.setAccelerator(accel.fileExitAccel);
	}
    }

    public void attachMenus() {
	final var app = Inconnuclear.getStuffBag();
	Integration.integrate().setDefaultMenuBar(app.getMenus().getMainMenuBar());
	app.getMenus().checkFlags();
    }

    @Override
    public JMenu createCommandsMenu() {
	final var mhandler = new MenuHandler();
	final var fileMenu = new JMenu(Strings.menu(Menu.FILE));
	this.fileNew = new JMenuItem(Strings.menu(Menu.NEW));
	this.fileOpen = new JMenuItem(Strings.menu(Menu.OPEN));
	this.fileOpenDefault = new JMenuItem(Strings.menu(Menu.OPEN_DEFAULT));
	this.fileClose = new JMenuItem(Strings.menu(Menu.CLOSE));
	this.fileSave = new JMenuItem(Strings.menu(Menu.SAVE));
	this.fileSaveAs = new JMenuItem(Strings.menu(Menu.SAVE_AS));
	this.fileSaveAsProtected = new JMenuItem(Strings.menu(Menu.SAVE_AS_PROTECTED));
	this.filePreferences = new JMenuItem(Strings.menu(Menu.PREFERENCES));
	this.filePrint = new JMenuItem(Strings.menu(Menu.PRINT_GAME_WINDOW));
	if (System.getProperty(Strings.untranslated(Untranslated.OS_NAME))
		.contains(Strings.untranslated(Untranslated.WINDOWS))) {
	    this.fileExit = new JMenuItem(Strings.menu(Menu.EXIT));
	} else {
	    this.fileExit = new JMenuItem(Strings.menu(Menu.QUIT));
	}
	this.fileNew.addActionListener(mhandler);
	this.fileOpen.addActionListener(mhandler);
	this.fileOpenDefault.addActionListener(mhandler);
	this.fileClose.addActionListener(mhandler);
	this.fileSave.addActionListener(mhandler);
	this.fileSaveAs.addActionListener(mhandler);
	this.fileSaveAsProtected.addActionListener(mhandler);
	this.filePreferences.addActionListener(mhandler);
	this.filePrint.addActionListener(mhandler);
	this.fileExit.addActionListener(mhandler);
	fileMenu.add(this.fileNew);
	fileMenu.add(this.fileOpen);
	fileMenu.add(this.fileOpenDefault);
	fileMenu.add(this.fileClose);
	fileMenu.add(this.fileSave);
	fileMenu.add(this.fileSaveAs);
	fileMenu.add(this.fileSaveAsProtected);
	if (!System.getProperty(Strings.untranslated(Untranslated.OS_NAME))
		.equalsIgnoreCase(Strings.untranslated(Untranslated.MACOS))) {
	    fileMenu.add(this.filePreferences);
	}
	fileMenu.add(this.filePrint);
	if (!System.getProperty(Strings.untranslated(Untranslated.OS_NAME))
		.equalsIgnoreCase(Strings.untranslated(Untranslated.MACOS))) {
	    fileMenu.add(this.fileExit);
	}
	return fileMenu;
    }

    @Override
    public void disableDirtyCommands() {
	this.fileSave.setEnabled(false);
	Inconnuclear.getStuffBag().getMenus().disableDirtyCommands();
    }

    @Override
    public void disableLoadedCommands() {
	this.fileClose.setEnabled(false);
	this.fileSaveAs.setEnabled(false);
	this.fileSaveAsProtected.setEnabled(false);
	Inconnuclear.getStuffBag().getMenus().disableLoadedCommands();
    }

    @Override
    public void disableModeCommands() {
	this.fileNew.setEnabled(false);
	this.fileOpen.setEnabled(false);
	this.fileOpenDefault.setEnabled(false);
	Inconnuclear.getStuffBag().getMenus().disableModeCommands();
    }

    @Override
    public void enableDirtyCommands() {
	this.fileSave.setEnabled(true);
	Inconnuclear.getStuffBag().getMenus().enableDirtyCommands();
    }

    @Override
    public void enableLoadedCommands() {
	final var app = Inconnuclear.getStuffBag();
	if (app.getMode() == StuffBag.STATUS_GUI) {
	    this.fileClose.setEnabled(false);
	    this.fileSaveAs.setEnabled(false);
	    this.fileSaveAsProtected.setEnabled(false);
	} else {
	    this.fileClose.setEnabled(true);
	    this.fileSaveAs.setEnabled(true);
	    this.fileSaveAsProtected.setEnabled(true);
	}
	Inconnuclear.getStuffBag().getMenus().enableLoadedCommands();
    }

    @Override
    public void enableModeCommands() {
	this.fileNew.setEnabled(true);
	this.fileOpen.setEnabled(true);
	this.fileOpenDefault.setEnabled(true);
	Inconnuclear.getStuffBag().getMenus().enableModeCommands();
    }

    @Override
    public void handleQuitRequestWith(final QuitEvent e, final QuitResponse response) {
	final var quitOK = this.quitHandler();
	if (quitOK) {
	    response.performQuit();
	} else {
	    response.cancelQuit();
	}
    }

    public void hideGUI() {
	this.mainWindow.removeWindowListener(this.cHandler);
	this.mainWindow.restoreSaved();
	MusicLoader.stopMusic();
    }

    public boolean quitHandler() {
	final var mm = Inconnuclear.getStuffBag().getDungeonManager();
	var saved = true;
	var status = CommonDialogs.DEFAULT_OPTION;
	if (mm.getDirty()) {
	    status = DungeonManager.showSaveDialog();
	    if (status == CommonDialogs.YES_OPTION) {
		saved = mm.saveDungeon(mm.isDungeonProtected());
	    } else if (status == CommonDialogs.CANCEL_OPTION) {
		saved = false;
	    } else {
		mm.setDirty(false);
	    }
	}
	if (saved) {
	    Settings.writePrefs();
	    // Run cleanup task
	    CleanupTask.cleanUp();
	}
	return saved;
    }

    @Override
    public void setInitialState() {
	this.fileNew.setEnabled(true);
	this.fileOpen.setEnabled(true);
	this.fileOpenDefault.setEnabled(true);
	this.fileClose.setEnabled(false);
	this.fileSave.setEnabled(false);
	this.fileSaveAs.setEnabled(false);
	this.fileSaveAsProtected.setEnabled(false);
	this.filePreferences.setEnabled(true);
	this.filePrint.setEnabled(true);
	this.fileExit.setEnabled(true);
    }

    public void showGUI() {
	final var app = Inconnuclear.getStuffBag();
	app.setInGUI();
	this.attachMenus();
	MusicLoader.playMusic(Music.EXUDING_TITLENESS);
	this.mainWindow.setAndSave(this.guiPane, Strings.untranslated(Untranslated.PROGRAM_NAME));
	this.mainWindow.addWindowListener(this.cHandler);
	app.getMenus().checkFlags();
    }

    void updateLogo() {
	final var logo = LogoLoader.getLogo();
	this.logoLabel.setIcon(logo);
    }
}
