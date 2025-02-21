/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.reward;

import org.retropipes.inconnuclear.battle.BattleResult;
import org.retropipes.inconnuclear.battle.types.BattleType;

public abstract class BattleRewards {
    public static void doRewards(final BattleType bt, final BattleResult br, final long bonusExp, final int bonusGold) {
	if (bt.isFinalBossBattle()) {
	    FinalBossBattleRewards.doRewards(br);
	} else {
	    RegularBattleRewards.doRewards(br, bonusExp, bonusGold);
	}
    }
}