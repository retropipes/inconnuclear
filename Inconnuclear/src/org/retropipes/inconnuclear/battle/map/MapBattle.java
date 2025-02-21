/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map;

import org.retropipes.inconnuclear.battle.BattleCharacter;
import org.retropipes.inconnuclear.creature.monster.MonsterFactory;
import org.retropipes.inconnuclear.settings.Settings;

public class MapBattle {
    // Fields
    private final BattleCharacter monster;

    // Constructors
    public MapBattle(final int rows, final int columns) {
	this.monster = new BattleCharacter(MonsterFactory.getNewMonsterInstance(Settings.getGameDifficulty()), rows,
		columns);
    }

    // Methods
    public BattleCharacter getBattlers() {
	return this.monster;
    }
}
