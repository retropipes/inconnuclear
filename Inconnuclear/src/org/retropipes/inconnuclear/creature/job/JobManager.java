/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.job;

import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.creature.spell.SpellBook;
import org.retropipes.inconnuclear.creature.spell.SpellBookLoader;

public class JobManager {
    private static boolean CACHE_CREATED = false;
    private static Job[] CACHE;
    private static String[] DESC_CACHE;

    private static void createCache() {
	if (!JobManager.CACHE_CREATED) {
	    // Create cache
	    JobManager.CACHE = new Job[JobConstants.JOBS_COUNT];
	    JobManager.DESC_CACHE = new String[JobConstants.JOBS_COUNT];
	    for (var x = 0; x < JobConstants.JOBS_COUNT; x++) {
		JobManager.CACHE[x] = JobLoader.loadJob(Job.jobIDtoName(x));
		JobManager.DESC_CACHE[x] = JobManager.CACHE[x].getDescription();
	    }
	    JobManager.CACHE_CREATED = true;
	}
    }

    public static Job getJob(final int jobID) {
	JobManager.createCache();
	return JobManager.CACHE[jobID];
    }

    public static SpellBook getSpellBookByID(final int ID) {
	return SpellBookLoader.loadSpellBook(ID);
    }

    public static Job selectJob() {
	JobManager.createCache();
	final var names = JobConstants.JOB_NAMES;
	final var dialogResult = PartyManager.showCreationDialog("Select a Job", "Create Character", names,
		JobManager.DESC_CACHE);
	if (dialogResult == null) {
	    return null;
	}
	int index;
	for (index = 0; index < names.length; index++) {
	    if (dialogResult.equals(names[index])) {
		break;
	    }
	}
	return JobManager.getJob(index);
    }
}
