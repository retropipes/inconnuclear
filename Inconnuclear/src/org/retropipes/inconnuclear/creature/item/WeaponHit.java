/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.item;

import org.retropipes.inconnuclear.loader.sound.Sounds;

public class WeaponHit {
    private static final Sounds[] HIT_SOUND_LOOKUP = { Sounds.AXE_HIT, Sounds.SPEAR_HIT, Sounds.HAMMER_HIT,
	    Sounds.CLUB_HIT, Sounds.SWORD_HIT, Sounds.MACE_HIT };

    public static Sounds getWeaponTypeHitSound(final int index) {
	return WeaponHit.HIT_SOUND_LOOKUP[index];
    }

    // Private Constructor
    private WeaponHit() {
	// Do nothing
    }
}
