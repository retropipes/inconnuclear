/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.ai.map;

import java.awt.Point;

import org.retropipes.diane.random.RandomRange;
import org.retropipes.inconnuclear.battle.ai.AIContext;

class CommonMapAIParts {
    // Constants
    static final int STUCK_THRESHOLD = 16;
    static final int MIN_VISION = 1;
    static final int SPELL_INDEX_HEAL = 1;

    static boolean check(final AIContext ac, final int effChance) {
	final var random = new RandomRange(1, 100);
	final var chance = random.generate();
	if (chance > effChance) {
	    // Not acting
	    return false;
	}
	if (ac.getCharacter().getActionsLeft() > 0) {
	    return true;
	}
	// Can't act any more times
	return false;
    }

    static int getMaxCastIndex(final AIContext ac) {
	final var currMP = ac.getCharacter().getCreature().getCurrentMP();
	final var allCosts = ac.getCharacter().getCreature().getSpellBook().getAllSpellCosts();
	var result = -1;
	if (currMP > 0) {
	    for (var x = 0; x < allCosts.length; x++) {
		if (currMP >= allCosts[x]) {
		    result = x;
		}
	    }
	}
	return result;
    }

    static Point turnLeft45(final int x, final int y) {
	if (x == -1 && y == -1) {
	    return new Point(-1, 0);
	}
	if (x == -1 && y == 0) {
	    return new Point(-1, 1);
	}
	if (x == -1 && y == 1) {
	    return new Point(0, 1);
	}
	if (x == 0 && y == -1) {
	    return new Point(-1, -1);
	}
	if (x == 0 && y == 1) {
	    return new Point(1, 1);
	}
	if (x == 1 && y == -1) {
	    return new Point(0, -1);
	}
	if (x == 1 && y == 0) {
	    return new Point(1, -1);
	}
	if (x == 1 && y == 1) {
	    return new Point(0, -1);
	}
	return new Point(x, y);
    }

    static Point turnRight45(final int x, final int y) {
	if (x == -1 && y == -1) {
	    return new Point(-1, 0);
	}
	if (x == -1 && y == 0) {
	    return new Point(-1, -1);
	}
	if (x == -1 && y == 1) {
	    return new Point(-1, 0);
	}
	if (x == 0 && y == -1) {
	    return new Point(1, -1);
	}
	if (x == 0 && y == 1) {
	    return new Point(-1, 1);
	}
	if (x == 1 && y == -1) {
	    return new Point(1, 0);
	}
	if (x == 1 && y == 0) {
	    return new Point(1, 1);
	}
	if (x == 1 && y == 1) {
	    return new Point(0, 1);
	}
	return new Point(x, y);
    }

    // Constructor
    private CommonMapAIParts() {
	// Do nothing
    }
}
