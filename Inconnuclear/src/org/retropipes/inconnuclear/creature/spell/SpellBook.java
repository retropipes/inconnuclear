/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.spell;

import java.util.Arrays;
import java.util.Objects;

public class SpellBook {
    // Fields
    private String name;
    protected final Spell[] spells;
    private final boolean[] known;

    // Constructors
    protected SpellBook(final int numSpells, final boolean flag) {
	this.name = "No Name";
	this.spells = new Spell[numSpells];
	this.known = new boolean[numSpells];
	if (flag) {
	    this.learnAllSpells();
	} else {
	    this.forgetAllSpells();
	}
	this.defineSpells();
    }

    protected void defineSpells() {
	// Do nothing
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null || !(obj instanceof final SpellBook other) || !Arrays.equals(this.known, other.known)
		|| !Objects.equals(this.name, other.name)) {
	    return false;
	}
	if (!Arrays.equals(this.spells, other.spells)) {
	    return false;
	}
	return true;
    }

    private final void forgetAllSpells() {
	for (var x = 0; x < this.spells.length; x++) {
	    this.known[x] = false;
	}
    }

    public final int[] getAllSpellCosts() {
	int x;
	var k = 0;
	int[] costs;
	for (x = 0; x < this.spells.length; x++) {
	    if (this.known[x]) {
		k++;
	    }
	}
	if (k != 0) {
	    costs = new int[k];
	    k = 0;
	    for (x = 0; x < this.spells.length; x++) {
		if (this.known[x]) {
		    costs[k] = this.spells[x].getCost();
		    k++;
		}
	    }
	} else {
	    costs = null;
	}
	return costs;
    }

    final String[] getAllSpellNames() {
	int x;
	var k = 0;
	String[] names;
	final var tempnames = new String[this.spells.length];
	for (x = 0; x < this.spells.length; x++) {
	    if (this.known[x]) {
		tempnames[x] = this.spells[x].getEffect().getName();
		k++;
	    }
	}
	if (k != 0) {
	    names = new String[k];
	    k = 0;
	    for (x = 0; x < this.spells.length; x++) {
		if (this.known[x]) {
		    names[k] = this.spells[x].getEffect().getName();
		    k++;
		}
	    }
	} else {
	    names = null;
	}
	return names;
    }

    final String[] getAllSpellNamesWithCosts() {
	int x;
	var k = 0;
	String[] names;
	final var tempnames = new String[this.spells.length];
	for (x = 0; x < this.spells.length; x++) {
	    if (this.known[x]) {
		tempnames[x] = this.spells[x].getEffect().getName();
		k++;
	    }
	}
	if (k != 0) {
	    names = new String[k];
	    k = 0;
	    for (x = 0; x < this.spells.length; x++) {
		if (this.known[x]) {
		    names[k] = this.spells[x].getEffect().getName();
		    k++;
		}
	    }
	} else {
	    names = null;
	}
	if (names != null) {
	    k = 0;
	    for (x = 0; x < this.spells.length; x++) {
		if (this.known[x]) {
		    final var cost = this.spells[x].getCost();
		    final var costStr = Integer.toString(cost);
		    names[k] += " (" + costStr + " MP)";
		    k++;
		}
	    }
	}
	return names;
    }

    public final String[] getAllSpellsToLearnNames() {
	final var numKnown = this.getSpellsKnownCount();
	final var max = this.getMaximumSpellsKnownCount();
	if (numKnown == max) {
	    return null;
	}
	var counter = 0;
	final var res = new String[max - numKnown];
	for (var x = 0; x < this.spells.length; x++) {
	    if (!this.known[x]) {
		res[counter] = this.spells[x].getEffect().getName();
		counter++;
	    }
	}
	return res;
    }

    public int getLegacyID() {
	return -1;
    }

    public final int getMaximumSpellsKnownCount() {
	return this.known.length;
    }

    public final Spell getSpellByID(final int ID) {
	return this.spells[ID];
    }

    final Spell getSpellByName(final String sname) {
	int x;
	for (x = 0; x < this.spells.length; x++) {
	    final var currName = this.spells[x].getEffect().getName();
	    if (currName.equals(sname)) {
		// Found it
		return this.spells[x];
	    }
	}
	// Didn't find it
	return null;
    }

    public final int getSpellCount() {
	return this.spells.length;
    }

    public final int getSpellIDByName(final String sname) {
	int x;
	for (x = 0; x < this.spells.length; x++) {
	    final var currName = this.spells[x].getEffect().getName();
	    if (currName.equals(sname)) {
		// Found it
		return x;
	    }
	}
	// Didn't find it
	return -1;
    }

    public final int getSpellsKnownCount() {
	var k = 0;
	for (final boolean element : this.known) {
	    if (element) {
		k++;
	    }
	}
	return k;
    }

    @Override
    public int hashCode() {
	return Objects.hash(Arrays.hashCode(this.known), this.name, Arrays.hashCode(this.spells));
    }

    public final boolean isSpellKnown(final int ID) {
	return this.known[ID];
    }

    public final void learnAllSpells() {
	for (var x = 0; x < this.spells.length; x++) {
	    this.known[x] = true;
	}
    }

    public final void learnSpellByID(final int ID) {
	if (ID != -1) {
	    this.known[ID] = true;
	}
    }

    public final void setName(final String n) {
	this.name = n;
    }
}
