/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.types;

import org.retropipes.inconnuclear.battle.BattleCharacter;

public abstract class BattleType {
    public static BattleType createBattle(final int rows, final int columns) {
	return new RegularBattle(rows, columns);
    }

    public static BattleType createFinalBossBattle(final int rows, final int columns) {
	return new FinalBossBattle(rows, columns);
    }

    protected boolean finalBoss = false;

    public abstract BattleCharacter getBattlers();

    public final boolean isFinalBossBattle() {
	return this.finalBoss;
    }
}