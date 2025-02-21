/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.dungeon.gameobject;

import java.util.ResourceBundle;

import org.retropipes.diane.direction.Direction;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.locale.Colors;

final class GameObjectDataLoader {
    static ObjectImageId bound(final ObjectImageId index) {
	var maxOid = ObjectImageId.values().length;
	var oid = (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.bound")
		.getObject(Integer.toString(index.ordinal()));
	if (oid < 0 || oid > maxOid) {
	    return null;
	}
	return ObjectImageId.values()[oid];
    }

    static boolean boundUniversal(final ObjectImageId index) {
	return (boolean) ResourceBundle
		.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.bound_universal")
		.getObject(Integer.toString(index.ordinal()));
    }

    static Colors color(final ObjectImageId index) {
	var maxCid = Colors.values().length;
	var cid = (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.color")
		.getObject(Integer.toString(index.ordinal()));
	if (cid < 0 || cid > maxCid) {
	    return Colors._NONE;
	}
	return Colors.values()[cid];
    }

    static int damage(final ObjectImageId index) {
	return (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.damaging")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean deferSetProperties(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.defer_set")
		.getObject(Integer.toString(index.ordinal()));
    }

    static Direction direction(final ObjectImageId index) {
	var maxDid = Direction.values().length;
	var did = (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.direction")
		.getObject(Integer.toString(index.ordinal()));
	if (did < 0 || did > maxDid) {
	    return Direction.NONE;
	}
	return Direction.values()[did];
    }

    static boolean isField(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.field")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean friction(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.friction")
		.getObject(Integer.toString(index.ordinal()));
    }

    static int height(final ObjectImageId index) {
	return (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.height")
		.getObject(Integer.toString(index.ordinal()));
    }

    static int initialTimer(final ObjectImageId index) {
	return (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.initial_timer")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean isInteractive(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.interact")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean isMoving(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.moving")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean isPlayer(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.player")
		.getObject(Integer.toString(index.ordinal()));
    }

    static ObjectImageId interactionMorph(final ObjectImageId index) {
	var maxOid = ObjectImageId.values().length;
	var oid = (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.interact_morph")
		.getObject(Integer.toString(index.ordinal()));
	if (oid < 0 || oid > maxOid) {
	    return null;
	}
	return ObjectImageId.values()[oid];
    }

    static int interactionMessageIndex(final ObjectImageId index) {
	return (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.interact_message")
		.getObject(Integer.toString(index.ordinal()));
    }

    static Sounds interactionSound(final ObjectImageId index) {
	return Sounds.values()[((int) ResourceBundle
		.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.interact_sound")
		.getObject(Integer.toString(index.ordinal())))];
    }

    static boolean isPassThrough(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.pass_through")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean killsOnMove(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.move_kill")
		.getObject(Integer.toString(index.ordinal()));
    }

    static int layer(final ObjectImageId index) {
	return (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.layer")
		.getObject(Integer.toString(index.ordinal()));
    }

    static Material material(final ObjectImageId index) {
	return Material.values()[((int) ResourceBundle
		.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.material")
		.getObject(Integer.toString(index.ordinal())))];
    }

    static int maxFrame(final ObjectImageId index) {
	return (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.max_frame")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean pullable(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.pullable")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean pushable(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.pushable")
		.getObject(Integer.toString(index.ordinal()));
    }

    static ShopType shopType(final ObjectImageId index) {
	var maxSid = ShopType.values().length;
	var sid = (int) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.shop")
		.getObject(Integer.toString(index.ordinal()));
	if (sid < 0 || sid > maxSid) {
	    return null;
	}
	return ShopType.values()[sid];
    }

    static boolean sightBlocking(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.sightblocking")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean solid(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.solid")
		.getObject(Integer.toString(index.ordinal()));
    }

    static boolean solvesOnMove(final ObjectImageId index) {
	return (boolean) ResourceBundle.getBundle("org.retropipes.inconnuclear.dungeon.gameobject.data.move_solve")
		.getObject(Integer.toString(index.ordinal()));
    }

    private GameObjectDataLoader() {
    }
}
