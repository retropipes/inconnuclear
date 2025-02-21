/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.job;

import org.retropipes.inconnuclear.creature.job.description.JobDescriptionManager;

public class Job {
    static String jobIDtoName(final int jobID) {
	return JobConstants.JOB_NAMES[jobID];
    }

    private final int jobID;
    private final String desc;

    public Job(final int cid) {
	this.desc = JobDescriptionManager.getJobDescription(cid);
	this.jobID = cid;
    }

    public final int getJobID() {
	return this.jobID;
    }

    public String getDescription() {
	return this.desc;
    }
}
