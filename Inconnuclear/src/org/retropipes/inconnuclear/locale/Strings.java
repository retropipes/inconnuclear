/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.locale;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.retropipes.diane.LocaleUtils;

public final class Strings {
    public static final String EMPTY = ""; //$NON-NLS-1$
    public static final String SPACE = " "; //$NON-NLS-1$
    public static final String BETA = "b"; //$NON-NLS-1$
    public static final String VERSION = "V"; //$NON-NLS-1$
    public static final String UNDERSCORE = "_"; //$NON-NLS-1$
    public static final String VERSION_DELIM = "."; //$NON-NLS-1$
    private static final String CACHE_DELIM = " & "; //$NON-NLS-1$
    private static final String NAMED_DELIM = "$$"; //$NON-NLS-1$
    public static final String LOADER_DELIM = "-"; //$NON-NLS-1$
    public static final String DISPLAY_NEW_LINE = "\n"; //$NON-NLS-1$
    public static final String STAT_DELIM = "/"; //$NON-NLS-1$
    public static final int ARMOR_TYPES_COUNT = 6;
    public static final int BATTLE_MECHANICS_COUNT = 2;
    public static final int BATTLE_STYLES_COUNT = 2;
    public static final int WEAPON_TYPES_COUNT = 6;
    public static final int COLOR_COUNT = 8;
    private static final int CHEATS_COUNT = 25;
    private static final int DIFFICULTIES_COUNT = 5;
    private static final int LANGUAGES_COUNT = 1;
    public static final int SLOTS_COUNT = 12;
    private static final int EDITOR_LAYOUTS_COUNT = 3;
    private static Locale ACTIVE = Locale.getDefault();

    public static String[] allArmorTypes() {
	final var result = new String[Strings.ARMOR_TYPES_COUNT];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.armortype", Strings.ACTIVE) //$NON-NLS-1$
		    .getString(Integer.toString(index));
	}
	return result;
    }

    public static String[] allBattleMechanics() {
	final var result = new String[Strings.BATTLE_MECHANICS_COUNT];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.battlemechanic", Strings.ACTIVE) //$NON-NLS-1$
		    .getString(Integer.toString(index));
	}
	return result;
    }

    public static String[] allBattleStyles() {
	final var result = new String[Strings.BATTLE_STYLES_COUNT];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.battlestyle", Strings.ACTIVE) //$NON-NLS-1$
		    .getString(Integer.toString(index));
	}
	return result;
    }

    public static ArrayList<String> allCheats() {
	final var result = new ArrayList<String>();
	for (var index = 0; index < Strings.CHEATS_COUNT; index++) {
	    result.add(ResourceBundle.getBundle("locale.cheat", Strings.ACTIVE).getString(Integer.toString(index))); //$NON-NLS-1$
	}
	return result;
    }

    public static String[] allDifficulties() {
	final var result = new String[Strings.DIFFICULTIES_COUNT];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.difficulty", Strings.ACTIVE) //$NON-NLS-1$
		    .getString(Integer.toString(index));
	}
	return result;
    }

    public static String[] allEditorLayouts() {
	final var result = new String[Strings.EDITOR_LAYOUTS_COUNT];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.editorlayout", Strings.ACTIVE) //$NON-NLS-1$
		    .getString(Integer.toString(index));
	}
	return result;
    }

    public static String[] allLanguages() {
	final var result = new String[Strings.LANGUAGES_COUNT];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.language", Strings.ACTIVE) //$NON-NLS-1$
		    .getString(Integer.toString(index));
	}
	return result;
    }

    public static String[] allLayers() {
	final var result = new String[Layer.values().length];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.layer", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
	}
	return result;
    }

    public static String[] allSlots() {
	final var result = new String[Strings.SLOTS_COUNT];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.slot", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
	}
	return result;
    }

    public static String[] allTimeTravelEras() {
	final var result = new String[TimeTravel.values().length];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.timetravel", Strings.ACTIVE) //$NON-NLS-1$
		    .getString(Integer.toString(index));
	}
	return result;
    }

    public static ArrayList<String> allVowels() {
	final var result = new ArrayList<String>();
	final var file = ResourceBundle.getBundle("locale.vowel", Strings.ACTIVE); //$NON-NLS-1$
	final var limit = file.keySet().size();
	for (var index = 0; index < limit; index++) {
	    result.add(file.getString(Integer.toString(index)));
	}
	return result;
    }

    public static String[] allWeaponTypes() {
	final var result = new String[Strings.WEAPON_TYPES_COUNT];
	for (var index = 0; index < result.length; index++) {
	    result[index] = ResourceBundle.getBundle("locale.weapontype", Strings.ACTIVE) //$NON-NLS-1$
		    .getString(Integer.toString(index));
	}
	return result;
    }

    public static String armor(final int index) {
	return ResourceBundle.getBundle("locale.armor", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String armorName(final int mID, final int aID) {
	return LocaleUtils.subst(Strings.group(Group.PAIR), Strings.armorType(mID), Strings.armor(aID));
    }

    public static String armorType(final int index) {
	return ResourceBundle.getBundle("locale.armortype", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String boss(final int index) {
	return ResourceBundle.getBundle("locale.boss", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String compositeCacheName(final String... inputs) {
	final var result = new StringBuilder();
	for (var index = 0; index < inputs.length; index++) {
	    result.append(inputs[index]);
	    if (index <= inputs.length - 1) {
		result.append(Strings.CACHE_DELIM);
	    }
	}
	return result.toString();
    }

    public static void changeLanguage(final Locale newLang) {
	Strings.ACTIVE = newLang;
    }

    public static String cheat(final int index) {
	return ResourceBundle.getBundle("locale.cheat", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String color(final Colors item) {
	return ResourceBundle.getBundle("locale.color").getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String color(final int index) {
	return ResourceBundle.getBundle("locale.color").getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String dialog(final DialogString item) {
	return ResourceBundle.getBundle("locale.dialog", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String difficulty(final Difficulty item) {
	return ResourceBundle.getBundle("locale.difficulty", Strings.ACTIVE) //$NON-NLS-1$
		.getString(Integer.toString(item.ordinal()));
    }

    public static String editor(final EditorString item) {
	return ResourceBundle.getBundle("locale.editor", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String effectImage(final int index) {
	return ResourceBundle.getBundle("locale.effectimage").getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String error(final ErrorString item) {
	return ResourceBundle.getBundle("locale.error", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String fileExtension(final FileExtension item) {
	return ResourceBundle.getBundle("locale.file").getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String fileType(final FileType item) {
	return ResourceBundle.getBundle("locale.filetype").getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String game(final GameString item) {
	return ResourceBundle.getBundle("locale.game", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String generic(final Generic item) {
	return ResourceBundle.getBundle("locale.generic", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String group(final Group item) {
	return ResourceBundle.getBundle("locale.group", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String layer(final Layer item) {
	return ResourceBundle.getBundle("locale.layer", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String item(final ItemString item) {
	return ResourceBundle.getBundle("locale.item", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String menu(final Menu item) {
	return ResourceBundle.getBundle("locale.menu", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String monster(final int index) {
	return ResourceBundle.getBundle("locale.monster", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String monsterzone(final int zoneID, final int monID) {
	return LocaleUtils.subst(Strings.group(Group.PAIR), Strings.zone(zoneID), Strings.monster(monID));
    }

    public static String music(final Music item) {
	return ResourceBundle.getBundle("locale.music", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String namedSubst(final String orig, final NamedSubst param, final String value) {
	return orig.replace(Strings.NAMED_DELIM + param.toString() + Strings.NAMED_DELIM, value);
    }

    public static String objectDescription(final int index) {
	return ResourceBundle.getBundle("locale.objectdesc").getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String objectInteractMessage(final ObjectInteractMessage item) {
	return ResourceBundle.getBundle("locale.object_interact_message", Strings.ACTIVE) //$NON-NLS-1$
		.getString(Integer.toString(item.ordinal()));
    }

    public static String objectName(final int index) {
	return ResourceBundle.getBundle("locale.objectname").getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String settingKey(final SettingKey item) {
	return ResourceBundle.getBundle("locale.settingkeys", Strings.ACTIVE) //$NON-NLS-1$
		.getString(Integer.toString(item.ordinal()));
    }

    public static String settings(final SettingString item) {
	return ResourceBundle.getBundle("locale.settings", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String slot(final int index) {
	return ResourceBundle.getBundle("locale.slot", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String slot(final Slot item) {
	return ResourceBundle.getBundle("locale.slot", Strings.ACTIVE).getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String sound(final int index) {
	return ResourceBundle.getBundle("locale.sound", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String stat(final int index) {
	return ResourceBundle.getBundle("locale.stat", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String timeTravel(final int index) {
	return ResourceBundle.getBundle("locale.timetravel", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String timeTravel(final TimeTravel item) {
	return ResourceBundle.getBundle("locale.timetravel", Strings.ACTIVE) //$NON-NLS-1$
		.getString(Integer.toString(item.ordinal()));
    }

    public static String untranslated(final Untranslated item) {
	return ResourceBundle.getBundle("locale.untranslated").getString(Integer.toString(item.ordinal())); //$NON-NLS-1$
    }

    public static String weapon(final int index) {
	return ResourceBundle.getBundle("locale.weapon", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String weaponName(final int mID, final int wID) {
	return LocaleUtils.subst(Strings.group(Group.PAIR), Strings.weaponType(mID), Strings.weapon(wID));
    }

    public static String weaponType(final int index) {
	return ResourceBundle.getBundle("locale.weapontype", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    public static String zone(final int index) {
	return ResourceBundle.getBundle("locale.zone", Strings.ACTIVE).getString(Integer.toString(index)); //$NON-NLS-1$
    }

    private Strings() {
    }
}
