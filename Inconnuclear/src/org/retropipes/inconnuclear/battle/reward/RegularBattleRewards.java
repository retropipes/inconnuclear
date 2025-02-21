/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.reward;

import org.retropipes.inconnuclear.battle.BattleResult;
import org.retropipes.inconnuclear.creature.party.PartyManager;

class RegularBattleRewards {
    // Fields
    static final String[] rewardOptions = { "Attack", "Defense", "HP", "MP" };

    public static void doRewards(final BattleResult br, final long baseExp, final int baseGold) {
	RegularBattleRewards.processExp(br, baseExp);
	RegularBattleRewards.processGold(br, baseGold);
    }

    private static void processExp(final BattleResult br, final long baseExp) {
	final var player = PartyManager.getParty().getLeader();
	if (br == BattleResult.PERFECT) {
	    player.offsetExperience(baseExp * 6 / 5);
	} else if (br == BattleResult.LOST) {
	    player.offsetExperiencePercentage(-10);
	    player.healAndRegenerateFully();
	} else if (br == BattleResult.ANNIHILATED) {
	    player.offsetExperiencePercentage(-20);
	    player.healAndRegenerateFully();
	} else if (br == BattleResult.WON) {
	    player.offsetExperience(baseExp);
	}
    }

    private static void processGold(final BattleResult br, final int baseGold) {
	final var player = PartyManager.getParty().getLeader();
	if (br == BattleResult.WON || br == BattleResult.PERFECT) {
	    player.offsetGold(baseGold);
	} else if (br == BattleResult.LOST || br == BattleResult.ANNIHILATED) {
	    player.offsetGoldPercentage(-100);
	}
    }

    // Constructor
    private RegularBattleRewards() {
	// Do nothing
    }
}
