/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.spell;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;

public class SpellCaster {
    // Fields
    private static boolean NO_SPELLS_FLAG = false;

    public static boolean castSpell(final Spell cast, final Creature caster, final Creature enemy) {
	if (cast == null) {
	    return false;
	}
	final var casterMP = caster.getCurrentMP();
	final var cost = cast.getCost();
	if (casterMP < cost) {
	    // Not enough MP
	    return false;
	}
	// Cast Spell
	caster.drain(cost);
	final var b = cast.getEffect();
	// Play spell's associated sound effect, if it has one
	final var snd = cast.getSound();
	SoundLoader.playSound(snd);
	b.resetEffect();
	final var target = SpellCaster.resolveTarget(cast, caster.getTeamID(), enemy);
	if (target.isEffectActive(b)) {
	    target.extendEffect(b, b.getInitialRounds());
	} else {
	    b.restoreEffect();
	    target.applyEffect(b);
	}
	return true;
    }

    private static Creature resolveTarget(final Spell cast, final int teamID, final Creature enemy) {
	final var target = cast.getTarget();
	switch (target) {
	case SELF:
	    if (teamID == Creature.TEAM_PARTY) {
		return PartyManager.getParty().getLeader();
	    }
	    return enemy;
	case ENEMY:
	    if (teamID == Creature.TEAM_PARTY) {
		return enemy;
	    }
	    return PartyManager.getParty().getLeader();
	default:
	    return null;
	}
    }

    public static boolean selectAndCastSpell(final Creature caster, final Creature enemy) {
	var result = false;
	SpellCaster.NO_SPELLS_FLAG = false;
	final var s = SpellCaster.selectSpell(caster);
	if (s != null) {
	    result = SpellCaster.castSpell(s, caster, enemy);
	    if (!result && !SpellCaster.NO_SPELLS_FLAG) {
		CommonDialogs.showErrorDialog("You try to cast a spell, but realize you don't have enough MP!",
			"Select Spell");
	    }
	}
	return result;
    }

    private static Spell selectSpell(final Creature caster) {
	final var book = caster.getSpellBook();
	if (book == null) {
	    SpellCaster.NO_SPELLS_FLAG = true;
	    CommonDialogs.showErrorDialog("You try to cast a spell, but realize you don't know any!", "Select Spell");
	    return null;
	}
	final var names = book.getAllSpellNames();
	final var displayNames = book.getAllSpellNamesWithCosts();
	if (names == null || displayNames == null) {
	    SpellCaster.NO_SPELLS_FLAG = true;
	    CommonDialogs.showErrorDialog("You try to cast a spell, but realize you don't know any!", "Select Spell");
	    return null;
	}
	// Play casting spell sound
	final var dialogResult = CommonDialogs.showInputDialog("Select a Spell to Cast", "Select Spell", displayNames,
		displayNames[0]);
	if (dialogResult == null) {
	    return null;
	}
	int index;
	for (index = 0; index < displayNames.length; index++) {
	    if (dialogResult.equals(displayNames[index])) {
		break;
	    }
	}
	return book.getSpellByName(names[index]);
    }

    // Private Constructor
    private SpellCaster() {
	// Do nothing
    }
}
