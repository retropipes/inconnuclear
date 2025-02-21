/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map.turn;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import org.retropipes.diane.asset.image.ImageCompositor;
import org.retropipes.diane.drawgrid.DrawGrid;
import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.integration.Integration;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.battle.map.MapBattleDefinitions;
import org.retropipes.inconnuclear.battle.map.MapBattleDraw;
import org.retropipes.inconnuclear.battle.map.MapBattleEffects;
import org.retropipes.inconnuclear.battle.map.MapBattleStats;
import org.retropipes.inconnuclear.battle.map.MapBattleViewingWindowManager;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Strings;

class MapTurnBattleGUI {
    private static final int MAX_TEXT = 1000;
    // Fields
    private MainWindow mainWindow;
    private MapTurnBattleEventHandler handler;
    private MainContent borderPane;
    private MapBattleDraw battlePane;
    private JLabel messageLabel;
    private final MapBattleViewingWindowManager vwMgr;
    private final MapBattleStats bs;
    private final MapBattleEffects be;
    private DrawGrid drawGrid;
    boolean eventHandlersOn;
    private JButton spell, steal, drain, end;

    // Constructors
    MapTurnBattleGUI() {
	this.vwMgr = new MapBattleViewingWindowManager();
	this.bs = new MapBattleStats();
	this.be = new MapBattleEffects();
	this.setUpGUI();
	this.eventHandlersOn = true;
    }

    boolean areEventHandlersOn() {
	return this.eventHandlersOn;
    }

    void clearStatusMessage() {
	this.messageLabel.setText(" ");
    }

    MapBattleViewingWindowManager getViewManager() {
	return this.vwMgr;
    }

    void hideBattle() {
	if (this.mainWindow != null) {
	    this.mainWindow.removeKeyListener(this.handler);
	    this.mainWindow.restoreSaved();
	}
    }

    void redrawBattle(final MapBattleDefinitions bd) {
	// Draw the battle, if it is visible
	if (this.mainWindow.checkContent(this.battlePane)) {
	    int x, y;
	    int xFix, yFix;
	    final var xView = this.vwMgr.getViewingWindowLocationX();
	    final var yView = this.vwMgr.getViewingWindowLocationY();
	    final var xlView = this.vwMgr.getLowerRightViewingWindowLocationX();
	    final var ylView = this.vwMgr.getLowerRightViewingWindowLocationY();
	    for (x = xView; x <= xlView; x++) {
		for (y = yView; y <= ylView; y++) {
		    xFix = x - xView;
		    yFix = y - yView;
		    try {
			final var lgobj = bd.getBattleDungeonBase().getCell(y, x, 0, Layer.GROUND.ordinal());
			final var ugobj = bd.getBattleDungeonBase().getCell(y, x, 0, Layer.OBJECT.ordinal());
			final var lgimg = lgobj.getImage();
			final var ugimg = ugobj.getImage();
			final var cacheName = Strings.compositeCacheName(lgobj.getCacheName(), ugobj.getCacheName());
			final var img = ImageCompositor.composite(cacheName, lgimg, ugimg);
			this.drawGrid.setImageCell(img, xFix, yFix);
		    } catch (final ArrayIndexOutOfBoundsException ae) {
			final var wall = new GameObject(ObjectImageId.WALL);
			this.drawGrid.setImageCell(wall.getImage(), xFix, yFix);
		    }
		}
	    }
	    this.battlePane.repaint();
	}
    }

    void redrawOneBattleSquare(final MapBattleDefinitions bd, final int x, final int y, final GameObject obj3) {
	// Draw the battle, if it is visible
	if (this.mainWindow.checkContent(this.battlePane)) {
	    try {
		int xFix, yFix;
		final var xView = this.vwMgr.getViewingWindowLocationX();
		final var yView = this.vwMgr.getViewingWindowLocationY();
		xFix = y - xView;
		yFix = x - yView;
		final var lgobj = bd.getBattleDungeonBase().getCell(y, x, 0, Layer.GROUND.ordinal());
		final var ugobj = bd.getBattleDungeonBase().getCell(y, x, 0, Layer.OBJECT.ordinal());
		final var lgimg = lgobj.getImage();
		final var ugimg = ugobj.getImage();
		final var o3img = obj3.getImage();
		final var cacheName = Strings.compositeCacheName(lgobj.getCacheName(), ugobj.getCacheName());
		final var img = ImageCompositor.composite(cacheName, lgimg, ugimg, o3img);
		this.drawGrid.setImageCell(img, xFix, yFix);
		this.battlePane.repaint();
	    } catch (final ArrayIndexOutOfBoundsException ae) {
		// Do nothing
	    }
	}
    }

    void setStatusMessage(final String msg) {
	if (this.messageLabel.getText().length() > MapTurnBattleGUI.MAX_TEXT) {
	    this.clearStatusMessage();
	}
	if (!msg.isEmpty() && !msg.matches("\\s+")) {
	    this.messageLabel.setText(msg);
	}
    }

    private void setUpGUI() {
	this.handler = new MapTurnBattleEventHandler(this);
	this.mainWindow = MainWindow.mainWindow();
	this.borderPane = MainWindow.createContent();
	final var buttonPane = MainWindow.createContent();
	this.borderPane.setLayout(new BorderLayout());
	this.messageLabel = new JLabel(" ");
	this.messageLabel.setOpaque(true);
	this.spell = new JButton("Cast Spell");
	this.steal = new JButton("Steal");
	this.drain = new JButton("Drain");
	this.end = new JButton("End Turn");
	buttonPane.setLayout(new GridLayout(5, 1));
	buttonPane.add(this.spell);
	buttonPane.add(this.steal);
	buttonPane.add(this.drain);
	buttonPane.add(this.end);
	this.spell.setFocusable(false);
	this.steal.setFocusable(false);
	this.drain.setFocusable(false);
	this.end.setFocusable(false);
	this.spell.addActionListener(this.handler);
	this.steal.addActionListener(this.handler);
	this.drain.addActionListener(this.handler);
	this.end.addActionListener(this.handler);
	int modKey;
	if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
	    modKey = InputEvent.META_DOWN_MASK;
	} else {
	    modKey = InputEvent.CTRL_DOWN_MASK;
	}
	this.spell.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, modKey),
		"Cast Spell");
	this.spell.getActionMap().put("Cast Spell", this.handler);
	this.steal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, modKey),
		"Steal");
	this.steal.getActionMap().put("Steal", this.handler);
	this.drain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, modKey),
		"Drain");
	this.drain.getActionMap().put("Drain", this.handler);
	this.end.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, modKey),
		"End Turn");
	this.end.getActionMap().put("End Turn", this.handler);
	this.drawGrid = new DrawGrid(MapBattleViewingWindowManager.getViewingWindowSize());
	for (var x = 0; x < MapBattleViewingWindowManager.getViewingWindowSize(); x++) {
	    for (var y = 0; y < MapBattleViewingWindowManager.getViewingWindowSize(); y++) {
		final GameObject dark = new GameObject(ObjectImageId.DARKNESS);
		this.drawGrid.setImageCell(dark.getImage(), x, y);
	    }
	}
	this.battlePane = new MapBattleDraw(this.drawGrid);
	this.borderPane.add(this.battlePane, BorderLayout.CENTER);
	this.borderPane.add(buttonPane, BorderLayout.WEST);
	this.borderPane.add(this.messageLabel, BorderLayout.NORTH);
	this.borderPane.add(this.bs.getStatsPane(), BorderLayout.EAST);
	this.borderPane.add(this.be.getEffectsPane(), BorderLayout.SOUTH);
    }

    void showBattle() {
	Integration.integrate().setDefaultMenuBar(Inconnuclear.getStuffBag().getMenus().getMainMenuBar());
	this.mainWindow.setAndSave(this.borderPane, "Battle");
	this.mainWindow.addKeyListener(this.handler);
    }

    void turnEventHandlersOff() {
	this.eventHandlersOn = false;
	this.spell.setEnabled(false);
	this.steal.setEnabled(false);
	this.drain.setEnabled(false);
	this.end.setEnabled(false);
    }

    void turnEventHandlersOn() {
	this.eventHandlersOn = true;
	this.spell.setEnabled(true);
	this.steal.setEnabled(true);
	this.drain.setEnabled(true);
	this.end.setEnabled(true);
    }

    void updateStatsAndEffects(final MapBattleDefinitions bd) {
	this.bs.updateStats(bd.getActiveCharacter());
	this.be.updateEffects(bd.getActiveCharacter());
    }
}
