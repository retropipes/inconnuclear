/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.sound;

import java.net.URL;

import org.retropipes.diane.asset.sound.DianeSoundIndex;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;

public enum Sounds implements DianeSoundIndex {
    ACTION_FAILED, ATTACK_AXE, ATTACK_BITE, ATTACK_CLAW, ATTACK_CLUB, ATTACK_HAMMER, ATTACK_KICK, ATTACK_KNIFE,
    ATTACK_MACE, ATTACK_PUNCH, ATTACK_SWORD, BLOCKED, BOSS_DIE, BUTTON, CLICK, CRITICAL, DAMAGE_BOLT, DAMAGE_BUBBLE,
    DAMAGE_COLD, DAMAGE_EARTH, DAMAGE_FIRE, DAMAGE_FIREBALL, DAMAGE_LAVA, DAMAGE_SONAR, DAMAGE_SPIKE, DAMAGE_STEAM,
    DAMAGE_WATER, DAMAGE_WIND, DAMAGE_ZAP, DEATH, DOOR_CLOSES, DOOR_OPENS, DRAW_SWORD, EFFECT_BLESSED, EFFECT_BUFF,
    EFFECT_CHARMED, EFFECT_CLOAK, EFFECT_CONFUSED, EFFECT_CRACK, EFFECT_CREATE, EFFECT_CURSED, EFFECT_DARKNESS,
    EFFECT_DEBUFF, EFFECT_DESTROY, EFFECT_DISPEL, EFFECT_DIZZY, EFFECT_DRAIN, EFFECT_DRUNK, EFFECT_DUMBFOUNDED,
    EFFECT_FEAR, EFFECT_FOCUS, EFFECT_HASTED, EFFECT_HEAL, EFFECT_LIGHT, EFFECT_PARALYSIS, EFFECT_RETURN, EFFECT_ROTATE,
    EFFECT_SLOWED, EFFECT_U_TURNED, EFFECT_WEAKNESS, ENEMY_COUNTER, ENEMY_HIT, ENEMY_SPELL, EQUIP, ERROR, FALLING,
    FUMBLE, GAIN_LEVEL, GAME_OVER, GRAB, IDENTIFY, INTO_PIT, KABOOM, LIGHT_FUSE, LOW_HEALTH, MISSED, MISSILE,
    NEXT_ROUND, ON_WHO, PARTY_COUNTER, PARTY_SPELL, PLAYER_UP, PUSH, PUSH_FAILED, QUESTION, RUN, SELECT_SPELL, SHOP,
    SINK, SPRING, STAIRS, STAT_DOWN, STAT_UP, STEP_CLOAK, STEP_ENEMY, STEP_FAIL, STEP_ICE, STEP_LAVA, STEP_PARTY,
    STEP_WATER, SUMMON, TELEPORT, TRANSACT, TRAP, UNLOCK, VICTORY, WARNING, WEAPON_TOO_WEAK, WEAR_OFF, WIN_GAME, WRONG,
    YOUR_ADVENTURE_AWAITS, _NONE;

    public String getDisplayName() {
	return this == _NONE ? null : Strings.sound(this.ordinal());
    }

    @Override
    public String getName() {
	return this == _NONE ? null : this.toString().toLowerCase().replace('_', '-');
    }

    @Override
    public URL getURL() {
	return Sounds.class.getResource("/asset/sound/" + this.getName() + Strings.fileExtension(FileExtension.SOUND));
    }
}