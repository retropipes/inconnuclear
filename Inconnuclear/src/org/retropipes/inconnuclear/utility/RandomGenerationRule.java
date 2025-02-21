/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.utility;

import org.retropipes.inconnuclear.dungeon.base.DungeonBase;

public interface RandomGenerationRule {
    int NO_LIMIT = 0;

    int getMaximumRequiredQuantity(DungeonBase dungeonBase);

    int getMinimumRequiredQuantity(DungeonBase dungeonBase);

    boolean isRequired(DungeonBase dungeonBase);

    boolean shouldGenerateObject(DungeonBase dungeonBase, int row, int col, int level, int layer);
}
