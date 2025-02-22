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

public class DebufferSpellBook extends SpellBook {
    // Constructor
    public DebufferSpellBook() {
	super(8, false);
	this.setName(JobConstants.JOB_NAMES[this.getLegacyID()]);
    }

    @Override
    protected void defineSpells() {
	final var spell0Effect = new Effect("Damage Lock", 5);
	spell0Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -1.5);
	spell0Effect.setMessage(Effect.MESSAGE_INITIAL, "You perpetrate some locksmithery on your enemy!");
	spell0Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy recoils, taking a little damage!");
	spell0Effect.setMessage(Effect.MESSAGE_WEAR_OFF, "The enemy recovers!");
	final var spell0 = new Spell(spell0Effect, 1, BattleTarget.ENEMY, Sounds.BOLT);
	this.spells[0] = spell0;
	final var spell1Effect = new Effect("Speed Down", 5);
	spell1Effect.setEffect(Effect.EFFECT_MULTIPLY, StatConstants.STAT_AGILITY, 0.5, Effect.DEFAULT_SCALE_FACTOR,
		StatConstants.STAT_NONE);
	spell1Effect.setMessage(Effect.MESSAGE_INITIAL, "You take out a whip, and tangle the enemy with it!");
	spell1Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy's speed is reduced!");
	spell1Effect.setMessage(Effect.MESSAGE_WEAR_OFF, "The enemy breaks free of the tangle!");
	final var spell1 = new Spell(spell1Effect, 2, BattleTarget.ENEMY, Sounds.CONFUSED);
	this.spells[1] = spell1;
	final var spell2Effect = new Effect("Power Lock", 5);
	spell2Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -10);
	spell2Effect.setMessage(Effect.MESSAGE_INITIAL, "You lock your enemy into a damage trap!");
	spell2Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy recoils, taking damage!");
	spell2Effect.setMessage(Effect.MESSAGE_WEAR_OFF, "The trap vanishes!");
	final var spell2 = new Spell(spell2Effect, 3, BattleTarget.ENEMY, Sounds.TRAP);
	this.spells[2] = spell2;
	final var spell3Effect = new Effect("Attack Lock", 10);
	spell3Effect.setEffect(Effect.EFFECT_MULTIPLY, StatConstants.STAT_ATTACK, 0, Effect.DEFAULT_SCALE_FACTOR,
		Effect.DEFAULT_SCALE_STAT);
	spell3Effect.setMessage(Effect.MESSAGE_INITIAL, "You lock your enemy's weapon!");
	spell3Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy cannot attack!");
	spell3Effect.setMessage(Effect.MESSAGE_WEAR_OFF, "The lock breaks!");
	final var spell3 = new Spell(spell3Effect, 5, BattleTarget.ENEMY, Sounds.BUBBLE);
	this.spells[3] = spell3;
	final var spell4Effect = new Effect("Weapon Steal", 5);
	spell4Effect.setEffect(Effect.EFFECT_MULTIPLY, StatConstants.STAT_ATTACK, 0.5, Effect.DEFAULT_SCALE_FACTOR,
		StatConstants.STAT_NONE);
	spell4Effect.setMessage(Effect.MESSAGE_INITIAL, "You steal the enemy's weapon!");
	spell4Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy's attack is significantly reduced!");
	spell4Effect.setMessage(Effect.MESSAGE_WEAR_OFF, "The enemy recovers their weapon!");
	final var spell4 = new Spell(spell4Effect, 7, BattleTarget.ENEMY, Sounds.ATTACK_DOWN);
	this.spells[4] = spell4;
	final var spell5Effect = new Effect("Armor Bind", 5);
	spell5Effect.setEffect(Effect.EFFECT_MULTIPLY, StatConstants.STAT_DEFENSE, 0, Effect.DEFAULT_SCALE_FACTOR,
		StatConstants.STAT_NONE);
	spell5Effect.setMessage(Effect.MESSAGE_INITIAL, "You bind the enemy's armor, rendering it useless!");
	spell5Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy is unable to defend!");
	spell5Effect.setMessage(Effect.MESSAGE_WEAR_OFF, "The binding breaks!");
	final var spell5 = new Spell(spell5Effect, 11, BattleTarget.ENEMY, Sounds.DEFENSE_DOWN);
	this.spells[5] = spell5;
	final var spell6Effect = new Effect("Killer Poison", 10);
	spell6Effect.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -5, Effect.DEFAULT_SCALE_FACTOR,
		Effect.DEFAULT_SCALE_STAT);
	spell6Effect.setMessage(Effect.MESSAGE_INITIAL, "You profusely poison the enemy!");
	spell6Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy is badly hurt by the poison!");
	spell6Effect.setMessage(Effect.MESSAGE_WEAR_OFF, "The poison fades!");
	final var spell6 = new Spell(spell6Effect, 13, BattleTarget.ENEMY, Sounds.DRAIN);
	this.spells[6] = spell6;
	final var spell7Effect = new Effect("Blindness", 10);
	spell7Effect.setEffect(Effect.EFFECT_MULTIPLY, StatConstants.STAT_EVADE, 0, Effect.DEFAULT_SCALE_FACTOR,
		Effect.DEFAULT_SCALE_STAT);
	spell7Effect.setMessage(Effect.MESSAGE_INITIAL, "You blind an enemy!");
	spell7Effect.setMessage(Effect.MESSAGE_SUBSEQUENT, "The enemy cannot dodge attacks!");
	spell7Effect.setMessage(Effect.MESSAGE_WEAR_OFF, "The enemy's vision returns to normal!");
	final var spell7 = new Spell(spell7Effect, 17, BattleTarget.ENEMY, Sounds.DEFENSE_DOWN);
	this.spells[7] = spell7;
    }

    @Override
    public int getLegacyID() {
	return JobConstants.JOB_DEBUFFER;
    }
}
