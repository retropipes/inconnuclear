/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.gender;

public class Gender {
    private final int genderID;

    Gender(final int gid) {
	this.genderID = gid;
    }

    public int getGenderID() {
	return this.genderID;
    }
}
