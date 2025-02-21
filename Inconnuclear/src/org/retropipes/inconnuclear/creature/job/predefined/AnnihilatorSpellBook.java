/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.job.predefined;

import org.retropipes.inconnuclear.creature.BattleTarget;
import org.retropipes.inconnuclear.creature.StatConstants;
import org.retropipes.inconnuclear.creature.effect.Effect;
import org.retropipes.inconnuclear.creature.job.JobConstants;
import org.retropipes.inconnuclear.creature.spell.Spell;
import org.retropipes.inconnuclear.creature.spell.SpellBook;
import org.retropipes.inconnuclear.loader.sound.Sounds;

public class AnnihilatorSpellBook extends SpellBook {
    // Constructor
    public AnnihilatorSpellBook() {
	super(8, false);
	this.setName(JobConstants.JOB_NAMES[this.getLegacyID()]);
    }

    @Override
    protected void defineSpells() {
	final var spell0Effect = new Effect("Icicle", 1);
	spell0Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -1);
	spell0Effect.setScaleStat(StatConstants.STAT_LEVEL);
	spell0Effect.setMessage(Effect.MESSAGE_INITIAL, "You conjure an icicle, and throw it at an enemy!");
	spell0Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy recoils, and is hurt!");
	final var spell0 = new Spell(spell0Effect, 1, BattleTarget.ENEMY, Sounds.DAMAGE_COLD);
	this.spells[0] = spell0;
	final var spell1Effect = new Effect("Freeze", 1);
	spell1Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -1.25);
	spell1Effect.setScaleStat(StatConstants.STAT_LEVEL);
	spell1Effect.setMessage(Effect.MESSAGE_INITIAL, "You conjure a sheet of ice, and throw it at the enemy!");
	spell1Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy recoils, and is hurt!");
	final var spell1 = new Spell(spell1Effect, 2, BattleTarget.ENEMY, Sounds.DAMAGE_SPIKE);
	this.spells[1] = spell1;
	final var spell2Effect = new Effect("Scalding Steam", 1);
	spell2Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -1.5);
	spell2Effect.setScaleStat(StatConstants.STAT_LEVEL);
	spell2Effect.setMessage(Effect.MESSAGE_INITIAL, "You conjure a cloud of scalding steam!");
	spell2Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy feels the heat!");
	final var spell2 = new Spell(spell2Effect, 3, BattleTarget.ENEMY, Sounds.DAMAGE_STEAM);
	this.spells[2] = spell2;
	final var spell3Effect = new Effect("Liquid Lava", 1);
	spell3Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -1.75);
	spell3Effect.setScaleStat(StatConstants.STAT_LEVEL);
	spell3Effect.setMessage(Effect.MESSAGE_INITIAL, "You conjure a river of lava!");
	spell3Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy melts a little!");
	final var spell3 = new Spell(spell3Effect, 5, BattleTarget.ENEMY, Sounds.DAMAGE_LAVA);
	this.spells[3] = spell3;
	final var spell4Effect = new Effect("Ignite", 1);
	spell4Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -2);
	spell4Effect.setScaleStat(StatConstants.STAT_LEVEL);
	spell4Effect.setMessage(Effect.MESSAGE_INITIAL, "You ignite the air!");
	spell4Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy is engulfed!");
	final var spell4 = new Spell(spell4Effect, 7, BattleTarget.ENEMY, Sounds.DAMAGE_FIREBALL);
	this.spells[4] = spell4;
	final var spell5Effect = new Effect("Blast Wave", 1);
	spell5Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -2.5);
	spell5Effect.setScaleStat(StatConstants.STAT_LEVEL);
	spell5Effect.setMessage(Effect.MESSAGE_INITIAL,
		"You conjure an unstable glowing orb, which suddenly explodes!");
	spell5Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy recoils from the shockwave!");
	final var spell5 = new Spell(spell5Effect, 11, BattleTarget.ENEMY, Sounds.KABOOM);
	this.spells[5] = spell5;
	final var spell6Effect = new Effect("Air Tear", 1);
	spell6Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -3);
	spell6Effect.setScaleStat(StatConstants.STAT_LEVEL);
	spell6Effect.setMessage(Effect.MESSAGE_INITIAL,
		"You focus all your might into a blast powerful enough to rip the air apart!");
	spell6Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy is devastated!");
	final var spell6 = new Spell(spell6Effect, 13, BattleTarget.ENEMY, Sounds.EFFECT_WEAKNESS);
	this.spells[6] = spell6;
	final var spell7Effect = new Effect("Power Drain", 1);
	spell7Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_MP, -1);
	spell7Effect.setScaleFactor(0.4);
	spell7Effect.setScaleStat(StatConstants.STAT_MAXIMUM_MP);
	spell7Effect.setMessage(Effect.MESSAGE_INITIAL, "You conjure a draining vortex around the enemy!");
	spell7Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy loses some magic!");
	final var spell7 = new Spell(spell7Effect, 17, BattleTarget.ENEMY, Sounds.DAMAGE_ZAP);
	this.spells[7] = spell7;
    }

    @Override
    public int getLegacyID() {
	return JobConstants.JOB_ANNIHILATOR;
    }
}
