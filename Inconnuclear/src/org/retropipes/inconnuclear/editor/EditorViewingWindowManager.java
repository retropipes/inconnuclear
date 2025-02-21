/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.editor;

import org.retropipes.inconnuclear.dungeon.base.DungeonBase;

final class EditorViewingWindowManager {
    // Fields
    private static final int VIEWING_WINDOW_SIZE_X = DungeonBase.getMinColumns();
    private static final int VIEWING_WINDOW_SIZE_Y = DungeonBase.getMinRows();
    private static final int MIN_VIEWING_WINDOW_X = 0;
    private static final int MIN_VIEWING_WINDOW_Y = 0;

    static int getLowerRightViewingWindowLocationX() {
	return EditorViewingWindowManager.VIEWING_WINDOW_SIZE_X - 1;
    }

    static int getLowerRightViewingWindowLocationY() {
	return EditorViewingWindowManager.VIEWING_WINDOW_SIZE_Y - 1;
    }

    static int getMinimumViewingWindowLocationX() {
	return EditorViewingWindowManager.MIN_VIEWING_WINDOW_X;
    }

    static int getMinimumViewingWindowLocationY() {
	return EditorViewingWindowManager.MIN_VIEWING_WINDOW_Y;
    }

    static int getViewingWindowLocationX() {
	return EditorViewingWindowManager.MIN_VIEWING_WINDOW_X;
    }

    static int getViewingWindowLocationY() {
	return EditorViewingWindowManager.MIN_VIEWING_WINDOW_Y;
    }

    static int getViewingWindowSize() {
	return EditorViewingWindowManager.VIEWING_WINDOW_SIZE_X;
    }

    static int getViewingWindowSizeX() {
	return EditorViewingWindowManager.VIEWING_WINDOW_SIZE_X;
    }

    static int getViewingWindowSizeY() {
	return EditorViewingWindowManager.VIEWING_WINDOW_SIZE_Y;
    }

    // Constructors
    private EditorViewingWindowManager() {
	// Do nothing
    }
}
