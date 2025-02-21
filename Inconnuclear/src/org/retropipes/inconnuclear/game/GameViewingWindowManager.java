/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.settings.Settings;

public final class GameViewingWindowManager {
    private static final int VIEWING_WINDOW_SIZE_X = DungeonBase.getMinColumns();
    private static final int VIEWING_WINDOW_SIZE_Y = DungeonBase.getMinRows();

    static int getFixedLowerRightViewingWindowLocationX() {
	return GameViewingWindowManager.VIEWING_WINDOW_SIZE_X - 1;
    }

    static int getFixedLowerRightViewingWindowLocationY() {
	return GameViewingWindowManager.VIEWING_WINDOW_SIZE_Y - 1;
    }

    // Static Methods
    static int getFixedViewingWindowLocationX() {
	return 0;
    }

    static int getFixedViewingWindowLocationY() {
	return 0;
    }

    static int getFixedViewingWindowSize() {
	return GameViewingWindowManager.VIEWING_WINDOW_SIZE_X;
    }

    static int getFixedViewingWindowSizeX() {
	return GameViewingWindowManager.VIEWING_WINDOW_SIZE_X;
    }

    static int getFixedViewingWindowSizeY() {
	return GameViewingWindowManager.VIEWING_WINDOW_SIZE_Y;
    }

    public static int getOffsetFactorX() {
	return Settings.getViewingWindowSize() / 2;
    }

    public static int getOffsetFactorY() {
	return Settings.getViewingWindowSize() / 2;
    }

    // Fields
    private int oldLocX, oldLocY, locX, locY;

    // Constructors
    public GameViewingWindowManager() {
	this.locX = 0;
	this.locY = 0;
	this.oldLocX = 0;
	this.oldLocY = 0;
    }

    public int getLowerRightViewingWindowLocationX() {
	return this.locX + Settings.getViewingWindowSize() - 1;
    }

    public int getLowerRightViewingWindowLocationY() {
	return this.locY + Settings.getViewingWindowSize() - 1;
    }

    public int getViewingWindowLocationX() {
	return this.locX;
    }

    public int getViewingWindowLocationY() {
	return this.locY;
    }

    public void offsetViewingWindowLocationX(final int val) {
	this.locX += val;
    }

    public void offsetViewingWindowLocationY(final int val) {
	this.locY += val;
    }

    public void restoreViewingWindow() {
	this.locX = this.oldLocX;
	this.locY = this.oldLocY;
    }

    public void saveViewingWindow() {
	this.oldLocX = this.locX;
	this.oldLocY = this.locY;
    }

    public void setViewingWindowLocationX(final int val) {
	this.locX = val;
    }

    public void setViewingWindowLocationY(final int val) {
	this.locY = val;
    }
}
