/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.ai.window;

import org.retropipes.inconnuclear.battle.BattleAction;
import org.retropipes.inconnuclear.battle.ai.AIContext;
import org.retropipes.inconnuclear.battle.ai.CreatureAI;

public abstract class WindowAI extends CreatureAI {
    @Override
    public final BattleAction getNextAction(AIContext ac) {
	return getNextAction(ac.getCreature()); // Not used
    }
}
