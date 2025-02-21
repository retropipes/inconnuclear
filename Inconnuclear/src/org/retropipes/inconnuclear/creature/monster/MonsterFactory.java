/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.monster;

import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.GameDifficulty;

public class MonsterFactory {
    public static Creature getNewFinalBossInstance(final GameDifficulty diff) {
	return new FinalBossMonster(diff);
    }

    public static Creature getNewMonsterInstance(final GameDifficulty diff) {
	return new NormalMonster(diff);
    }

    private MonsterFactory() {
	// Do nothing
    }
}
