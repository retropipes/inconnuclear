/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.spell;

import org.retropipes.inconnuclear.creature.job.JobConstants;
import org.retropipes.inconnuclear.creature.job.predefined.AnnihilatorSpellBook;
import org.retropipes.inconnuclear.creature.job.predefined.BufferSpellBook;
import org.retropipes.inconnuclear.creature.job.predefined.CurerSpellBook;
import org.retropipes.inconnuclear.creature.job.predefined.DebufferSpellBook;

public class SpellBookLoader {
    public static SpellBook loadSpellBook(final int sbid) {
	return switch (sbid) {
	case JobConstants.JOB_ANNIHILATOR -> new AnnihilatorSpellBook();
	case JobConstants.JOB_BUFFER -> new BufferSpellBook();
	case JobConstants.JOB_CURER -> new CurerSpellBook();
	case JobConstants.JOB_DEBUFFER -> new DebufferSpellBook();
	default -> /* Invalid caste name */ null;
	};
    }

    // Constructors
    private SpellBookLoader() {
	// Do nothing
    }
}
