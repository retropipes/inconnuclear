/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JProgressBar;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.battle.Battle;
import org.retropipes.inconnuclear.battle.BattleMechanic;
import org.retropipes.inconnuclear.battle.BattleStyle;
import org.retropipes.inconnuclear.battle.map.time.MapTimeBattleLogic;
import org.retropipes.inconnuclear.battle.map.turn.MapTurnBattleLogic;
import org.retropipes.inconnuclear.battle.window.time.WindowTimeBattleLogic;
import org.retropipes.inconnuclear.battle.window.turn.WindowTurnBattleLogic;
import org.retropipes.inconnuclear.dungeon.gameobject.ShopType;
import org.retropipes.inconnuclear.dungeon.manager.DungeonManager;
import org.retropipes.inconnuclear.editor.Editor;
import org.retropipes.inconnuclear.game.Game;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.settings.Settings;
import org.retropipes.inconnuclear.shop.Shop;
import org.retropipes.inconnuclear.utility.DungeonObjects;

public final class StuffBag {
    private static final int VERSION_MAJOR = 17;
    private static final int VERSION_MINOR = 0;
    private static final int VERSION_BUGFIX = 0;
    private static final int VERSION_BETA = 1;
    public static final int STATUS_GUI = 0;
    public static final int STATUS_GAME = 1;
    public static final int STATUS_EDITOR = 2;
    public static final int STATUS_PREFS = 3;
    public static final int STATUS_HELP = 4;
    public static final int STATUS_BATTLE = 5;
    private static final int STATUS_NULL = 6;

    public static Image getIconLogo() {
	return LogoLoader.getIconLogo();
    }

    public static String getLogoVersionString() {
	if (StuffBag.isBetaModeEnabled()) {
	    return Strings.VERSION + StuffBag.VERSION_MAJOR + Strings.VERSION_DELIM + StuffBag.VERSION_MINOR
		    + Strings.VERSION_DELIM + StuffBag.VERSION_BUGFIX + Strings.BETA + StuffBag.VERSION_BETA;
	}
	return Strings.VERSION + StuffBag.VERSION_MAJOR + Strings.VERSION_DELIM + StuffBag.VERSION_MINOR
		+ Strings.VERSION_DELIM + StuffBag.VERSION_BUGFIX;
    }

    public static BufferedImageIcon getMicroLogo() {
	return LogoLoader.getMicroLogo();
    }

    private static String getVersionString() {
	if (StuffBag.isBetaModeEnabled()) {
	    return Strings.VERSION + StuffBag.VERSION_MAJOR + Strings.VERSION_DELIM + StuffBag.VERSION_MINOR
		    + Strings.VERSION_DELIM + StuffBag.VERSION_BUGFIX + Strings.BETA + StuffBag.VERSION_BETA;
	}
	return Strings.VERSION + StuffBag.VERSION_MAJOR + Strings.VERSION_DELIM + StuffBag.VERSION_MINOR
		+ Strings.VERSION_DELIM + StuffBag.VERSION_BUGFIX;
    }

    private static boolean isBetaModeEnabled() {
	return StuffBag.VERSION_BETA > 0;
    }

    // Fields
    private AboutDialog about;
    private Game game;
    private DungeonManager dungeonMgr;
    private MenuManager menuMgr;
    private Editor editor;
    private GUIManager guiMgr;
    private int mode, formerMode;
    private final DungeonObjects objects;
    private final Shop weapons, armor, healer, regenerator, spells;
    private Battle battle;
    private MapTimeBattleLogic mabattle;
    private MapTurnBattleLogic mtbattle;
    private WindowTimeBattleLogic wabattle;
    private WindowTurnBattleLogic wtbattle;

    // Constructors
    public StuffBag() {
	this.objects = new DungeonObjects();
	this.mode = StuffBag.STATUS_NULL;
	this.formerMode = StuffBag.STATUS_NULL;
	// Create Shops
	this.weapons = new Shop(ShopType.WEAPONS);
	this.armor = new Shop(ShopType.ARMOR);
	this.healer = new Shop(ShopType.HEALER);
	this.regenerator = new Shop(ShopType.SURGE);
	this.spells = new Shop(ShopType.SPELLS);
    }

    public void activeLanguageChanged() {
	// Rebuild menus
	this.getMenus().unregisterAllModeManagers();
	this.getMenus().registerModeManager(this.getGUIManager());
	this.getMenus().initMenus();
	this.getMenus().registerModeManager(this.getGame());
	this.getMenus().registerModeManager(this.getEditor());
	this.getMenus().registerModeManager(this.getAboutDialog());
	// Fire hooks
	this.getGame().activeLanguageChanged();
	this.getEditor().activeLanguageChanged();
    }

    void exitCurrentMode() {
	switch (this.mode) {
	case StuffBag.STATUS_GUI:
	    this.getGUIManager().hideGUI();
	    break;
	case StuffBag.STATUS_GAME:
	    this.getGame().exitGame();
	    break;
	case StuffBag.STATUS_EDITOR:
	    this.getEditor().exitEditor();
	    break;
	default:
	    break;
	}
    }

    AboutDialog getAboutDialog() {
	if (this.about == null) {
	    this.about = new AboutDialog(StuffBag.getVersionString());
	}
	return this.about;
    }

    public Battle getBattle() {
	// If battles aren't initialized, do so
	if (this.battle == null) {
	    this.mabattle = new MapTimeBattleLogic();
	    this.mtbattle = new MapTurnBattleLogic();
	    this.wabattle = new WindowTimeBattleLogic();
	    this.wtbattle = new WindowTurnBattleLogic();
	}
	// Select battle type from user settings
	BattleMechanic bm = Settings.getBattleMechanic();
	BattleStyle bs = Settings.getBattleStyle();
	switch (bm) {
	case ACTION_BAR:
	    switch (bs) {
	    case MAP:
		this.battle = this.mabattle;
		break;
	    case WINDOW:
		this.battle = this.wabattle;
		break;
	    default:
		this.battle = this.mtbattle;
		break;
	    }
	    break;
	case TAKE_TURNS:
	    switch (bs) {
	    case MAP:
		this.battle = this.mtbattle;
		break;
	    case WINDOW:
		this.battle = this.wtbattle;
		break;
	    default:
		this.battle = this.mtbattle;
		break;
	    }
	    break;
	default:
	    this.battle = this.mtbattle;
	    break;
	}
	// Return corresponding battle object
	return this.battle;
    }

    public DungeonManager getDungeonManager() {
	if (this.dungeonMgr == null) {
	    this.dungeonMgr = new DungeonManager();
	}
	return this.dungeonMgr;
    }

    public Editor getEditor() {
	if (this.editor == null) {
	    this.editor = new Editor();
	}
	return this.editor;
    }

    public int getFormerMode() {
	return this.formerMode;
    }

    public Game getGame() {
	if (this.game == null) {
	    this.game = new Game();
	}
	return this.game;
    }

    public GUIManager getGUIManager() {
	if (this.guiMgr == null) {
	    this.guiMgr = new GUIManager();
	    this.guiMgr.updateLogo();
	}
	return this.guiMgr;
    }

    public String[] getLevelInfoList() {
	return this.getDungeonManager().getDungeonBase().getLevelInfoList();
    }

    public MenuManager getMenus() {
	if (this.menuMgr == null) {
	    this.menuMgr = new MenuManager();
	}
	return this.menuMgr;
    }

    public int getMode() {
	return this.mode;
    }

    public DungeonObjects getObjects() {
	return this.objects;
    }

    public Shop getShopByType(final ShopType shopType) {
	this.getGame().stopMovement();
	return switch (shopType) {
	case ARMOR -> this.armor;
	case HEALER -> this.healer;
	case SURGE -> this.regenerator;
	case SPELLS -> this.spells;
	case WEAPONS -> this.weapons;
	default -> /* Invalid shop type */ null;
	};
    }

    public boolean modeChanged() {
	return this.formerMode != this.mode;
    }

    public void resetBattleGUI() {
	this.getBattle().resetGUI();
    }

    public void restoreFormerMode() {
	this.mode = this.formerMode;
    }

    public void saveFormerMode() {
	this.formerMode = this.mode;
    }

    public void setInEditor() {
	this.mode = StuffBag.STATUS_EDITOR;
	this.getMenus().modeChanged(this.getEditor());
    }

    public void setInGame() {
	this.mode = StuffBag.STATUS_GAME;
	this.getMenus().modeChanged(this.getGame());
    }

    void setInGUI() {
	this.mode = StuffBag.STATUS_GUI;
	this.getMenus().modeChanged(this.getGUIManager());
    }

    public void setInHelp() {
	this.formerMode = this.mode;
	this.mode = StuffBag.STATUS_HELP;
	this.getMenus().modeChanged(null);
    }

    public void setInPrefs() {
	this.formerMode = this.mode;
	this.mode = StuffBag.STATUS_PREFS;
	this.getMenus().modeChanged(null);
    }

    public void setMode(final int newMode) {
	this.formerMode = this.mode;
	this.mode = newMode;
    }

    public void showMessage(final String msg) {
	if (this.mode == StuffBag.STATUS_EDITOR) {
	    this.getEditor().setStatusMessage(msg);
	} else if (this.mode == StuffBag.STATUS_BATTLE) {
	    this.getBattle().setStatusMessage(msg);
	} else {
	    CommonDialogs.showDialog(msg);
	}
    }

    public void updateLevelInfoList() {
	MainWindow mainWindow;
	JProgressBar loadBar;
	mainWindow = MainWindow.mainWindow();
	loadBar = new JProgressBar();
	loadBar.setIndeterminate(true);
	loadBar.setPreferredSize(new Dimension(600, 20));
	final var loadContent = MainWindow.createContent();
	loadContent.add(loadBar);
	mainWindow.setAndSave(loadContent, Strings.dialog(DialogString.UPDATING_LEVEL_INFO));
	this.getDungeonManager().getDungeonBase().generateLevelInfoList();
	mainWindow.restoreSaved();
    }
}
