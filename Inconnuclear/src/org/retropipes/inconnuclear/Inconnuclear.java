/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear;

import org.retropipes.diane.Diane;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.diane.integration.Integration;
import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.locale.ErrorString;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.settings.Settings;
import org.retropipes.inconnuclear.settings.SettingsRequest;

public class Inconnuclear {
    // Constants
    private static StuffBag stuffBag;
    private static String PROGRAM_NAME = "Inconnuclear";
    private static String ERROR_MESSAGE = null;
    private static String ERROR_TITLE = null;
    private static final int MAIN_WINDOW_SIZE = 512;
    private static final int BATTLE_MAP_SIZE = 10;
    private static final int DUNGEON_BASE_SIZE = 24;
    private static final int DUNGEON_SIZE_INCREMENT = 2;

    public static int getBattleDungeonSize() {
	return Inconnuclear.BATTLE_MAP_SIZE;
    }

    public static int getDungeonLevelSize(final int zoneID) {
	return Inconnuclear.DUNGEON_BASE_SIZE + zoneID * Inconnuclear.DUNGEON_SIZE_INCREMENT;
    }

    public static StuffBag getStuffBag() {
	return Inconnuclear.stuffBag;
    }

    public static void logError(final Throwable t) {
	CommonDialogs.showErrorDialog(Inconnuclear.ERROR_MESSAGE, Inconnuclear.ERROR_TITLE);
	t.printStackTrace();
	Diane.handleError(t);
    }

    public static void logErrorDirectly(final Throwable t) {
	t.printStackTrace();
	Diane.handleError(t);
    }

    public static void logWarningDirectly(final Throwable t) {
	t.printStackTrace(System.out);
	Diane.handleWarning(t);
    }

    public static void main(final String[] args) {
	try {
	    try {
		// Initialize strings
		Inconnuclear.preInit();
		// Initialize error logger
		Diane.installDefaultErrorHandler(Inconnuclear.PROGRAM_NAME);
	    } catch (final RuntimeException re) {
		// Something has gone horribly wrong
		System.err.println("Something has gone horribly wrong trying to load the string data!");
		System.exit(1);
	    }
	    // Integrate with host platform
	    final var i = Integration.integrate();
	    i.configureLookAndFeel();
	    // Create main window
	    MainWindow.createMainWindow(MAIN_WINDOW_SIZE, MAIN_WINDOW_SIZE);
	    // Create and initialize application
	    Inconnuclear.stuffBag = new StuffBag();
	    // Set Up Common Dialogs
	    CommonDialogs.setDefaultTitle(Inconnuclear.PROGRAM_NAME);
	    CommonDialogs.setIcon(LogoLoader.getIconLogo());
	    // Initialize preferences
	    Settings.init();
	    Settings.readPrefs();
	    // Register platform hooks
	    i.setAboutHandler(Inconnuclear.stuffBag.getAboutDialog());
	    i.setPreferencesHandler(new SettingsRequest());
	    i.setQuitHandler(Inconnuclear.stuffBag.getGUIManager());
	    // Display GUI
	    Inconnuclear.stuffBag.getGUIManager().showGUI();
	} catch (final Throwable t) {
	    Inconnuclear.logError(t);
	}
    }

    private static void preInit() {
	Creature.computeActionCap(Inconnuclear.BATTLE_MAP_SIZE, Inconnuclear.BATTLE_MAP_SIZE);
	Inconnuclear.ERROR_TITLE = Strings.error(ErrorString.ERROR_TITLE);
	Inconnuclear.ERROR_MESSAGE = Strings.error(ErrorString.ERROR_MESSAGE);
    }

    private Inconnuclear() {
	// Do nothing
    }
}
