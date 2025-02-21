/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.damage;

import org.retropipes.diane.random.RandomRange;

class CommonDamageEngineParts {
    static final int MULTIPLIER_DIVIDE = 100000;
    private static final int MIN_CHANCE = 0;
    private static final int MAX_CHANCE = 10000;
    static final int ALWAYS = 10001;
    static final double FAITH_MULT_START = 1.0;

    static int chance() {
	return new RandomRange(CommonDamageEngineParts.MIN_CHANCE, CommonDamageEngineParts.MAX_CHANCE).generate();
    }

    static boolean didSpecial(final int aSpecial) {
	final var rSpecial = new RandomRange(0, 10000).generate();
	return rSpecial < aSpecial;
    }

    static int fumbleDamage(final int power) {
	return new RandomRange(1, Math.max(1, power / 100)).generate();
    }

    private CommonDamageEngineParts() {
	// Do nothing
    }
}