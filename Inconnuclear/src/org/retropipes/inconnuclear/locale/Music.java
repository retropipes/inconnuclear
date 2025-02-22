/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.locale;

import java.net.URL;

import org.retropipes.diane.asset.music.DianeMusicIndex;

public enum Music implements DianeMusicIndex {
    AFRICAN_BUSH,
    ALTAR_OF_LIGHT,
    AMBIENT_POWER,
    ANCIENT_EGYPT,
    APOPLEXY,
    APPROACH,
    ARMYBEAT,
    ARYX,
    AURORA,
    BATTLE_VICTORY,
    BATTLE,
    BEACH,
    BLACK_SCORPION,
    BODYBLOW,
    BOILING,
    BREAKTHROUGH,
    BRIGHTNESS,
    BRILLIANCE,
    CHALLENGE,
    CLOUD_CITY,
    CLOUD_TRAVELLER,
    COMPULSION_TO_OBEY,
    CREATION_X,
    CRITICAL_MASS,
    CYCLONE_LOADER,
    D20_FINLANDIA,
    DANGEROUS_GROUND,
    DEATH_WINDS,
    DEEP_SPACE,
    DESERT_SANDS_2,
    DESIRE,
    DEVILS_DANCE_1,
    DEVILS_DREAM,
    DIFFERENCES,
    DISTANCE,
    DONT_FORGET_KYLE,
    DUEL,
    DUNGEON_00,
    DUNGEON_01,
    DUNGEON_02,
    DUNGEON_03,
    DUNGEON_04,
    DUNGEON_05,
    DUNGEON_06,
    ECLIPSE,
    ELIPTICAL,
    ENDLESS_SYMPHONY,
    EXUDING_TITLENESS,
    FADE,
    FINAL_BATTLE_2,
    FINNISH_CITY,
    GALAXY_HERO_REMIX,
    GENETIC,
    GOLDEN_BEETLE_CAVERN,
    GRAVEYARD,
    HALL_OF_MIRRORS,
    HEAVENS_TRADER,
    HELLS_DITCH,
    HIGHLANDS,
    INTO_MY_UNIVERSE,
    JINGLE,
    KATAKOMB,
    LADY_LUCIFER,
    LETHAL_WORLD,
    LEVEL5BOSS,
    LONELINESS,
    LOST_IN_CYBERSPACE,
    LUCID_BREEDS,
    MACHINE,
    MELTDOWN,
    MEMORY,
    MIDMOON_DREAMS,
    MIDNIGHT_VISION,
    MILITARY_JULIA,
    MONGOLIAN_GLORY,
    NEPAL,
    NIGHTMARE,
    NUCLEAR_EXPLOSION,
    NUCLEAR_TIDE,
    NUCLEAR_WARHEAD,
    ONE_MUST_FALL_1,
    PLASMATTACK,
    POLYGONS_IN_SPACE,
    PRINCESS_OF_DAWN,
    RAZORBACK,
    RIONA_TOWN,
    ROMENTY,
    RUN_FOR_YOUR_LIFE,
    SAHARA,
    SECRET_GRAVEYARD,
    SEQUENTIAL,
    SEVENTH_SIGN,
    SIGMA_QUEST,
    SNOWFALL,
    UNWANTED_DISCOVERY,
    XTECH_INDUSTRIES,
    _NONE;

    @Override
    public String getName() {
	return this == _NONE ? null : Strings.music(this);
    }

    @Override
    public URL getURL() {
	return Music.class.getResource(
		"/asset/music/" + Integer.toString(this.ordinal()) + Strings.fileExtension(FileExtension.MUSIC));
    }
}