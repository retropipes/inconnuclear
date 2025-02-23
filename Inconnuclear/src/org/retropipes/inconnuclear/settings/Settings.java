/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.settings;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.battle.BattleMechanic;
import org.retropipes.inconnuclear.battle.BattleStyle;
import org.retropipes.inconnuclear.creature.GameDifficulty;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.locale.EditorLayout;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.SettingKey;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

public class Settings {
    // Fields
    private static SettingsFile file;
    private static SettingsGUI gui;
    private final static int FALLBACK_LANGUAGE = 0;
    private final static EditorLayout DEFAULT_EDITOR_LAYOUT = EditorLayout.VERTICAL;
    private static final int VIEWING_WINDOW_SIZE = 11;
    private static final GameDifficulty DEFAULT_DIFFICULTY = GameDifficulty.NORMAL;
    private static final BattleMechanic DEFAULT_BATTLE_MECHANIC = BattleMechanic.TAKE_TURNS;
    private static final BattleStyle DEFAULT_BATTLE_STYLE = BattleStyle.MAP;

    public static void activeLanguageChanged() {
	Settings.gui.activeLanguageChanged();
    }

    public static boolean enableAnimation() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ENABLE_ANIMATION), true);
    }

    static int getActionDelay() {
	return Settings.file.getInteger(Strings.settingKey(SettingKey.ACTION_DELAY), 2);
    }

    public static int getActionSpeed() {
	return (Settings.getActionDelay() + 1) * 5;
    }

    public static int getBattleSpeed() {
	return (Settings.getActionDelay() + 1) * 200 + 400;
    }

    public static BattleMechanic getBattleMechanic() {
	return BattleMechanic.values()[Settings.file.getInteger(Strings.settingKey(SettingKey.BATTLE_MECHANIC),
		Settings.DEFAULT_BATTLE_MECHANIC.ordinal())];
    }

    public static BattleStyle getBattleStyle() {
	return BattleStyle.values()[Settings.file.getInteger(Strings.settingKey(SettingKey.BATTLE_STYLE),
		Settings.DEFAULT_BATTLE_STYLE.ordinal())];
    }

    public static GameObject getEditorDefaultFill() {
	return new GameObject(ObjectImageId.GROUND);
    }

    public static EditorLayout getEditorLayout() {
	return EditorLayout.values()[Settings.file.getInteger(Strings.settingKey(SettingKey.EDITOR_LAYOUT),
		Settings.DEFAULT_EDITOR_LAYOUT.ordinal())];
    }

    public static boolean getEditorShowAllObjects() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.EDITOR_SHOW_ALL_OBJECTS), true);
    }

    public static GameDifficulty getGameDifficulty() {
	return GameDifficulty.values()[Settings.file.getInteger(Strings.settingKey(SettingKey.GAME_DIFFICULTY),
		Settings.DEFAULT_DIFFICULTY.ordinal())];
    }

    public static int getLanguageID() {
	return Settings.file.getInteger(Strings.settingKey(SettingKey.ACTIVE_LANGUAGE), Settings.FALLBACK_LANGUAGE);
    }

    public static String getLastDirOpen() {
	return Settings.file.getString(Strings.settingKey(SettingKey.LAST_FOLDER_OPEN), Strings.EMPTY);
    }

    public static String getLastDirSave() {
	return Settings.file.getString(Strings.settingKey(SettingKey.LAST_FOLDER_SAVE), Strings.EMPTY);
    }

    public static boolean getMusicEnabled() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ENABLE_MUSIC), true);
    }

    private static String getPrefsDirectory() {
	final var osName = System.getProperty(Strings.untranslated(Untranslated.OS_NAME));
	String base;
	if (osName.indexOf(Strings.untranslated(Untranslated.MACOS)) != -1) {
	    // Mac OS X
	    base = Strings.untranslated(Untranslated.MACOS_SUPPORT);
	} else if (osName.indexOf(Strings.untranslated(Untranslated.WINDOWS)) != -1) {
	    // Windows
	    base = Strings.EMPTY;
	} else {
	    // Other - assume UNIX-like
	    base = Strings.untranslated(Untranslated.UNIX_SUPPORT);
	}
	if (base != Strings.EMPTY) {
	    return base + File.pathSeparator + Strings.untranslated(Untranslated.COMPANY_SUBFOLDER) + File.pathSeparator
		    + Strings.untranslated(Untranslated.PROGRAM_NAME);
	}
	return Strings.untranslated(Untranslated.COMPANY_SUBFOLDER) + File.pathSeparator
		+ Strings.untranslated(Untranslated.PROGRAM_NAME);
    }

    private static String getPrefsDirPrefix() {
	final var osName = System.getProperty(Strings.untranslated(Untranslated.OS_NAME));
	if (osName.indexOf(Strings.untranslated(Untranslated.MACOS)) != -1) {
	    // Mac OS X
	    return System.getenv(Strings.untranslated(Untranslated.UNIX_HOME));
	}
	if (osName.indexOf(Strings.untranslated(Untranslated.WINDOWS)) != -1) {
	    // Windows
	    return System.getenv(Strings.untranslated(Untranslated.WINDOWS_SUPPORT));
	}
	// Other - assume UNIX-like
	return System.getenv(Strings.untranslated(Untranslated.UNIX_HOME));
    }

    private static String getPrefsFile() {
	final var b = new StringBuilder();
	b.append(Settings.getPrefsDirPrefix());
	b.append(Settings.getPrefsDirectory());
	b.append(Settings.getPrefsFileName());
	b.append(Settings.getPrefsFileExtension());
	return b.toString();
    }

    private static String getPrefsFileExtension() {
	return Strings.fileExtension(FileExtension.PREFS);
    }

    private static String getPrefsFileName() {
	return Strings.untranslated(Untranslated.PREFS_FILE);
    }

    public static long getReplaySpeed() {
	return (Settings.getActionDelay() + 1) * 10;
    }

    public static boolean getSoundsEnabled() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ENABLE_SOUNDS), true);
    }

    public static int getViewingWindowSize() {
	return Settings.VIEWING_WINDOW_SIZE;
    }

    public static void init() {
	Settings.file = new SettingsFile();
	Settings.gui = new SettingsGUI();
    }

    public static boolean isDeadlyDifficultyEnabled() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_DEADLY), true);
    }

    public static boolean isEasyDifficultyEnabled() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_EASY), true);
    }

    public static boolean isHardDifficultyEnabled() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_HARD), true);
    }

    public static boolean isKidsDifficultyEnabled() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_KIDS), true);
    }

    public static boolean isMediumDifficultyEnabled() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_MEDIUM), true);
    }

    public static boolean oneMove() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ONE_MOVE), true);
    }

    public static void readPrefs() {
	try (var buf = new BufferedInputStream(new FileInputStream(Settings.getPrefsFile()))) {
	    // Read new preferences
	    Settings.file.loadStore(buf);
	} catch (final IOException io) {
	    // Populate store with defaults
	    Settings.file.setString(Strings.settingKey(SettingKey.LAST_FOLDER_OPEN), Strings.EMPTY);
	    Settings.file.setString(Strings.settingKey(SettingKey.LAST_FOLDER_SAVE), Strings.EMPTY);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.UPDATES_STARTUP), true);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ONE_MOVE), true);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_SOUNDS), true);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_MUSIC), true);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_ANIMATION), true);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_KIDS), true);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_EASY), true);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_MEDIUM), true);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_HARD), true);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_DEADLY), true);
	    Settings.file.setInteger(Strings.settingKey(SettingKey.ACTION_DELAY), 2);
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.ACCELERATOR_MODEL), false);
	    Settings.file.setInteger(Strings.settingKey(SettingKey.ACTIVE_LANGUAGE), Settings.FALLBACK_LANGUAGE);
	    Settings.file.setInteger(Strings.settingKey(SettingKey.EDITOR_LAYOUT),
		    Settings.DEFAULT_EDITOR_LAYOUT.ordinal());
	    Settings.file.setBoolean(Strings.settingKey(SettingKey.EDITOR_SHOW_ALL_OBJECTS), true);
	}
    }

    static void setActionDelay(final int value) {
	Settings.file.setInteger(Strings.settingKey(SettingKey.ACTION_DELAY), value);
    }

    public static void setBattleMechanic(final BattleMechanic value) {
	Settings.file.setInteger(Strings.settingKey(SettingKey.BATTLE_MECHANIC), value.ordinal());
    }

    public static void setBattleStyle(final BattleStyle value) {
	Settings.file.setInteger(Strings.settingKey(SettingKey.BATTLE_STYLE), value.ordinal());
    }

    static void setCheckUpdatesAtStartup(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.UPDATES_STARTUP), value);
    }

    public static void setClassicAccelerators(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ACCELERATOR_MODEL), value);
    }

    public static void setDeadlyDifficultyEnabled(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_DEADLY), value);
    }

    public static void setEasyDifficultyEnabled(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_EASY), value);
    }

    public static void setEditorLayout(final EditorLayout value) {
	Settings.file.setInteger(Strings.settingKey(SettingKey.EDITOR_LAYOUT), value.ordinal());
	Inconnuclear.getStuffBag().getEditor().resetBorderPane();
    }

    public static void setEditorShowAllObjects(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.EDITOR_SHOW_ALL_OBJECTS), value);
	Inconnuclear.getStuffBag().getEditor().resetBorderPane();
    }

    static void setEnableAnimation(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_ANIMATION), value);
    }

    static void setGameDifficulty(final int value) {
	Settings.file.setInteger(Strings.settingKey(SettingKey.GAME_DIFFICULTY), value);
    }

    public static void setHardDifficultyEnabled(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_HARD), value);
    }

    public static void setKidsDifficultyEnabled(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_KIDS), value);
    }

    public static void setLanguageID(final int value) {
	final var oldValue = Settings.getLanguageID();
	Settings.file.setInteger(Strings.settingKey(SettingKey.ACTIVE_LANGUAGE), value);
	if (oldValue != value) {
	    Strings.changeLanguage(Locale.getDefault());
	    Inconnuclear.getStuffBag().activeLanguageChanged();
	    Settings.activeLanguageChanged();
	}
    }

    public static void setLastDirOpen(final String value) {
	Settings.file.setString(Strings.settingKey(SettingKey.LAST_FOLDER_OPEN), value);
    }

    public static void setLastDirSave(final String value) {
	Settings.file.setString(Strings.settingKey(SettingKey.LAST_FOLDER_SAVE), value);
    }

    public static void setMediumDifficultyEnabled(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_DIFFICULTY_MEDIUM), value);
    }

    static void setMusicEnabled(final boolean status) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_MUSIC), status);
    }

    static void setOneMove(final boolean value) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ONE_MOVE), value);
    }

    static void setSoundsEnabled(final boolean status) {
	Settings.file.setBoolean(Strings.settingKey(SettingKey.ENABLE_SOUNDS), status);
    }

    public static boolean shouldCheckUpdatesAtStartup() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.UPDATES_STARTUP), true);
    }

    public static void showPrefs() {
	Settings.gui.showPrefs();
    }

    public static boolean useClassicAccelerators() {
	return Settings.file.getBoolean(Strings.settingKey(SettingKey.ACCELERATOR_MODEL), false);
    }

    public static void writePrefs() {
	try (var buf = new BufferedOutputStream(new FileOutputStream(Settings.getPrefsFile()))) {
	    Settings.file.saveStore(buf);
	} catch (final IOException io) {
	    // Ignore
	}
    }

    // Private constructor
    private Settings() {
	// Do nothing
    }
}
