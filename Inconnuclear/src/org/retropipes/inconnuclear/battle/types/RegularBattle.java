/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.types;

import org.retropipes.inconnuclear.battle.BattleCharacter;
import org.retropipes.inconnuclear.battle.ai.map.MapAIPicker;
import org.retropipes.inconnuclear.creature.monster.MonsterFactory;
import org.retropipes.inconnuclear.settings.Settings;

class RegularBattle extends BattleType {
    // Fields
    final BattleCharacter monster;

    // Constructors
    public RegularBattle(final int rows, final int columns) {
	this.monster = new BattleCharacter(MonsterFactory.getNewMonsterInstance(Settings.getGameDifficulty()), rows,
		columns);
	this.monster.setAI(MapAIPicker.getNextRoutine());
    }

    @Override
    public BattleCharacter getBattlers() {
	return this.monster;
    }
}
