/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map.time;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.retropipes.diane.asset.image.ImageCompositor;
import org.retropipes.diane.drawgrid.DrawGrid;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.battle.BattleCharacter;
import org.retropipes.inconnuclear.battle.map.MapBattleDraw;
import org.retropipes.inconnuclear.battle.map.MapBattleEffects;
import org.retropipes.inconnuclear.battle.map.MapBattleStats;
import org.retropipes.inconnuclear.battle.map.MapBattleViewingWindowManager;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageLoader;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Strings;

class MapTimeBattleGUI {
    private static final int MAX_TEXT = 1000;
    // Fields
    private JFrame battleFrame;
    private MapBattleDraw battlePane;
    private JLabel messageLabel;
    private JProgressBar myActionBar, enemyActionBar;
    private final MapBattleViewingWindowManager vwMgr;
    private final MapBattleStats bs;
    private final MapBattleEffects be;
    private DrawGrid drawGrid;
    boolean eventHandlersOn;
    private JButton spell, steal, drain, item;

    // Constructors
    MapTimeBattleGUI() {
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

    // Methods
    JFrame getOutputFrame() {
	return this.battleFrame;
    }

    MapBattleViewingWindowManager getViewManager() {
	return this.vwMgr;
    }

    void hideBattle() {
	if (this.battleFrame != null) {
	    this.battleFrame.setVisible(false);
	}
    }

    boolean isEnemyActionBarFull() {
	return this.enemyActionBar.getValue() == this.enemyActionBar.getMaximum();
    }

    boolean isPlayerActionBarFull() {
	return this.myActionBar.getValue() == this.myActionBar.getMaximum();
    }

    void redrawBattle(final DungeonBase battleMap) {
	// Draw the battle, if it is visible
	if (this.battleFrame.isVisible()) {
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
			final var obj1 = battleMap.getCell(y, x, 0, Layer.GROUND.ordinal());
			final var obj2 = battleMap.getCell(y, x, 0, Layer.OBJECT.ordinal());
			final var icon1 = obj1.getImage();
			final var icon2 = obj2.getImage();
			final var cacheName = Strings.compositeCacheName(obj1.getCacheName(), obj2.getCacheName());
			final var img = ImageCompositor.composite(cacheName, icon1, icon2);
			this.drawGrid.setImageCell(img, xFix, yFix);
		    } catch (final ArrayIndexOutOfBoundsException ae) {
			final var wall = new GameObject(ObjectImageId.WALL);
			this.drawGrid.setImageCell(wall.getImage(), xFix, yFix);
		    } catch (final NullPointerException np) {
			final var wall = new GameObject(ObjectImageId.WALL);
			this.drawGrid.setImageCell(wall.getImage(), xFix, yFix);
		    }
		}
	    }
	    this.battlePane.repaint();
	    this.battleFrame.pack();
	}
    }

    void redrawOneBattleSquare(final DungeonBase battleMap, final int x, final int y, final GameObject obj3) {
	// Draw the battle, if it is visible
	if (this.battleFrame.isVisible()) {
	    try {
		int xFix, yFix;
		final var xView = this.vwMgr.getViewingWindowLocationX();
		final var yView = this.vwMgr.getViewingWindowLocationY();
		xFix = y - xView;
		yFix = x - yView;
		final var obj1 = battleMap.getCell(y, x, 0, Layer.GROUND.ordinal());
		final var obj2 = battleMap.getCell(y, x, 0, Layer.OBJECT.ordinal());
		final var icon1 = obj1.getImage();
		final var icon2 = obj2.getImage();
		final var icon3 = obj3.getImage();
		final var cacheName = Strings.compositeCacheName(obj1.getCacheName(), obj2.getCacheName(),
			obj3.getCacheName());
		final var img = ImageCompositor.composite(cacheName, icon1, icon2, icon3);
		this.drawGrid.setImageCell(img, xFix, yFix);
		this.battlePane.repaint();
	    } catch (final ArrayIndexOutOfBoundsException ae) {
		// Do nothing
	    } catch (final NullPointerException np) {
		// Do nothing
	    }
	    this.battleFrame.pack();
	}
    }

    void resetEnemyActionBar() {
	this.enemyActionBar.setValue(0);
    }

    void resetPlayerActionBar() {
	this.myActionBar.setValue(0);
    }

    void setMaxEnemyActionBarValue(final int max) {
	this.enemyActionBar.setValue(0);
	this.enemyActionBar.setMaximum(max);
    }

    void setMaxPlayerActionBarValue(final int max) {
	this.myActionBar.setValue(0);
	this.myActionBar.setMaximum(max);
    }

    void setStatusMessage(final String msg) {
	if (this.messageLabel.getText().length() > MapTimeBattleGUI.MAX_TEXT) {
	    this.clearStatusMessage();
	}
	if (!msg.isEmpty() && !msg.matches("\\s+")) {
	    this.messageLabel.setText(msg);
	}
    }

    private void setUpGUI() {
	final var handler = new MapTimeBattleEventHandler(this);
	final var borderPane = new Container();
	final var buttonPane = new Container();
	final var effectBarPane = new Container();
	final var barPane = new Container();
	borderPane.setLayout(new BorderLayout());
	barPane.setLayout(new FlowLayout());
	effectBarPane.setLayout(new BorderLayout());
	this.myActionBar = new JProgressBar(0);
	this.enemyActionBar = new JProgressBar(0);
	barPane.add(this.myActionBar);
	barPane.add(this.enemyActionBar);
	effectBarPane.add(barPane, BorderLayout.NORTH);
	effectBarPane.add(this.be.getEffectsPane(), BorderLayout.CENTER);
	this.messageLabel = new JLabel(" ");
	this.messageLabel.setOpaque(true);
	this.battleFrame = new JFrame("Battle");
	this.battleFrame.setContentPane(borderPane);
	this.spell = new JButton("Cast Spell");
	this.steal = new JButton("Steal");
	this.drain = new JButton("Drain");
	this.item = new JButton("Use Item");
	buttonPane.setLayout(new GridLayout(4, 1));
	buttonPane.add(this.spell);
	buttonPane.add(this.steal);
	buttonPane.add(this.drain);
	buttonPane.add(this.item);
	this.spell.setFocusable(false);
	this.steal.setFocusable(false);
	this.drain.setFocusable(false);
	this.item.setFocusable(false);
	this.spell.addActionListener(handler);
	this.steal.addActionListener(handler);
	this.drain.addActionListener(handler);
	this.item.addActionListener(handler);
	int modKey;
	if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
	    modKey = InputEvent.META_DOWN_MASK;
	} else {
	    modKey = InputEvent.CTRL_DOWN_MASK;
	}
	this.spell.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, modKey),
		"Cast Spell");
	this.spell.getActionMap().put("Cast Spell", handler);
	this.steal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, modKey),
		"Steal");
	this.steal.getActionMap().put("Steal", handler);
	this.drain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, modKey),
		"Drain");
	this.drain.getActionMap().put("Drain", handler);
	this.item.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, modKey),
		"Use Item");
	this.item.getActionMap().put("Use Item", handler);
	// Platform.hookFrameIcon(this.battleFrame, LogoManager.getIconLogo());
	this.battleFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	this.battleFrame.setResizable(false);
	this.drawGrid = new DrawGrid(MapBattleViewingWindowManager.getViewingWindowSize());
	for (var x = 0; x < MapBattleViewingWindowManager.getViewingWindowSize(); x++) {
	    for (var y = 0; y < MapBattleViewingWindowManager.getViewingWindowSize(); y++) {
		this.drawGrid.setImageCell(ObjectImageLoader.load(ObjectImageId.DARKNESS), x, y);
	    }
	}
	this.battlePane = new MapBattleDraw(this.drawGrid);
	borderPane.add(this.battlePane, BorderLayout.CENTER);
	borderPane.add(buttonPane, BorderLayout.WEST);
	borderPane.add(this.messageLabel, BorderLayout.NORTH);
	borderPane.add(this.bs.getStatsPane(), BorderLayout.EAST);
	borderPane.add(effectBarPane, BorderLayout.SOUTH);
	this.battleFrame.addKeyListener(handler);
    }

    void showBattle() {
	this.battleFrame.setVisible(true);
	this.battleFrame.setJMenuBar(Inconnuclear.getStuffBag().getMenus().getMainMenuBar());
    }

    void turnEventHandlersOff() {
	this.eventHandlersOn = false;
	this.spell.setEnabled(false);
	this.steal.setEnabled(false);
	this.drain.setEnabled(false);
	this.item.setEnabled(false);
    }

    void turnEventHandlersOn() {
	this.eventHandlersOn = true;
	this.spell.setEnabled(true);
	this.steal.setEnabled(true);
	this.drain.setEnabled(true);
	this.item.setEnabled(true);
    }

    void updateEnemyActionBarValue() {
	this.enemyActionBar.setValue(this.enemyActionBar.getValue() + 1);
    }

    void updatePlayerActionBarValue() {
	this.myActionBar.setValue(this.myActionBar.getValue() + 1);
    }

    void updateStatsAndEffects(final BattleCharacter active) {
	this.bs.updateStats(active);
	this.be.updateEffects(active);
    }
}
