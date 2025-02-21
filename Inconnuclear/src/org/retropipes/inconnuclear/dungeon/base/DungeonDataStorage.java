/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.dungeon.base;

import org.retropipes.diane.storage.ObjectStorage;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;

public class DungeonDataStorage extends ObjectStorage<GameObject> {
    public DungeonDataStorage(final DungeonDataStorage source) {
	super(source);
    }

    // Constructor
    public DungeonDataStorage(final int... shape) {
	super(shape);
    }
}
