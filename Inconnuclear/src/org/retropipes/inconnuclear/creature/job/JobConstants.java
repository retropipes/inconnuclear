/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.job;

public class JobConstants {
    public static final int JOB_ANNIHILATOR = 0;
    public static final int JOB_BUFFER = 1;
    public static final int JOB_CURER = 2;
    public static final int JOB_DEBUFFER = 3;
    public static final int JOBS_COUNT = 4;
    public static final String[] JOB_NAMES = { "Annihilator", "Buffer", "Curer", "Debuffer" };

    private JobConstants() {
	// Do nothing
    }
}