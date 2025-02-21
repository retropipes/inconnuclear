/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.job;

import org.retropipes.inconnuclear.creature.job.predefined.Annihilator;
import org.retropipes.inconnuclear.creature.job.predefined.Buffer;
import org.retropipes.inconnuclear.creature.job.predefined.Curer;
import org.retropipes.inconnuclear.creature.job.predefined.Debuffer;

class JobLoader {
    static Job loadJob(final String name) {
	if (name.equals(JobConstants.JOB_NAMES[JobConstants.JOB_ANNIHILATOR])) {
	    return new Annihilator();
	}
	if (name.equals(JobConstants.JOB_NAMES[JobConstants.JOB_BUFFER])) {
	    return new Buffer();
	}
	if (name.equals(JobConstants.JOB_NAMES[JobConstants.JOB_CURER])) {
	    return new Curer();
	}
	if (name.equals(JobConstants.JOB_NAMES[JobConstants.JOB_DEBUFFER])) {
	    return new Debuffer();
	}
	// Invalid job name
	return null;
    }

    // Constructors
    private JobLoader() {
	// Do nothing
    }
}
