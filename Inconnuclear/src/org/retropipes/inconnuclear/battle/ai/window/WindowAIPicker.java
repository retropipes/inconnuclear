/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.ai.window;

import org.retropipes.inconnuclear.creature.GameDifficulty;
import org.retropipes.inconnuclear.settings.Settings;

public final class WindowAIPicker {
    // Methods
    public static WindowAI getNextRoutine() {
	final var difficulty = Settings.getGameDifficulty();
	switch (difficulty) {
	case GameDifficulty.VERY_EASY:
	    return new VeryEasyWindowAI();
	case GameDifficulty.EASY:
	    return new EasyWindowAI();
	case GameDifficulty.NORMAL:
	    return new NormalWindowAI();
	case GameDifficulty.HARD:
	    return new HardWindowAI();
	case GameDifficulty.VERY_HARD:
	    return new VeryHardWindowAI();
	default:
	    return new NormalWindowAI();
	}
    }
}
