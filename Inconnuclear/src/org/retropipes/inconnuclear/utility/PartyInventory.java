/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.utility;

import java.io.IOException;

import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;

public class PartyInventory {
    // Fields
    private static int missilesLeft = 0;
    private static int stunnersLeft = 0;
    private static int boostsLeft = 0;
    private static int magnetsLeft = 0;
    private static int blueLasersLeft = 0;
    private static int disruptorsLeft = 0;
    private static int redKeysLeft = 0;
    private static int greenKeysLeft = 0;
    private static int blueKeysLeft = 0;
    private static int bombsLeft = 0;
    private static int heatBombsLeft = 0;
    private static int iceBombsLeft = 0;

    public static void addOneBlueKey() {
	PartyInventory.blueKeysLeft++;
    }

    public static void addOneBlueLaser() {
	PartyInventory.blueLasersLeft++;
    }

    public static void addOneBomb() {
	PartyInventory.bombsLeft++;
    }

    public static void addOneBoost() {
	PartyInventory.boostsLeft++;
    }

    public static void addOneDisruptor() {
	PartyInventory.disruptorsLeft++;
    }

    public static void addOneGreenKey() {
	PartyInventory.greenKeysLeft++;
    }

    public static void addOneHeatBomb() {
	PartyInventory.heatBombsLeft++;
    }

    public static void addOneIceBomb() {
	PartyInventory.iceBombsLeft++;
    }

    public static void addOneMagnet() {
	PartyInventory.magnetsLeft++;
    }

    public static void addOneMissile() {
	PartyInventory.missilesLeft++;
    }

    public static void addOneRedKey() {
	PartyInventory.redKeysLeft++;
    }

    public static void addOneStunner() {
	PartyInventory.stunnersLeft++;
    }

    public static void addTenBlueLasers() {
	PartyInventory.blueLasersLeft += 10;
    }

    public static void addTenBombs() {
	PartyInventory.bombsLeft += 10;
    }

    public static void addTenBoosts() {
	PartyInventory.boostsLeft += 10;
    }

    public static void addTenDisruptors() {
	PartyInventory.disruptorsLeft += 10;
    }

    public static void addTenHeatBombs() {
	PartyInventory.heatBombsLeft += 10;
    }

    public static void addTenIceBombs() {
	PartyInventory.iceBombsLeft += 10;
    }

    public static void addTenMagnets() {
	PartyInventory.magnetsLeft += 10;
    }

    public static void addTenMissiles() {
	PartyInventory.missilesLeft += 10;
    }

    public static void addTenStunners() {
	PartyInventory.stunnersLeft += 10;
    }

    public static void fireBlueLaser() {
	PartyInventory.blueLasersLeft--;
    }

    public static void fireBomb() {
	PartyInventory.bombsLeft--;
    }

    public static void fireBoost() {
	PartyInventory.boostsLeft--;
    }

    public static void fireDisruptor() {
	PartyInventory.disruptorsLeft--;
    }

    public static void fireHeatBomb() {
	PartyInventory.heatBombsLeft--;
    }

    public static void fireIceBomb() {
	PartyInventory.iceBombsLeft--;
    }

    public static void fireMagnet() {
	PartyInventory.magnetsLeft--;
    }

    public static void fireMissile() {
	PartyInventory.missilesLeft--;
    }

    public static void fireStunner() {
	PartyInventory.stunnersLeft--;
    }

    public static int getBlueKeysLeft() {
	return PartyInventory.blueKeysLeft;
    }

    public static int getBlueLasersLeft() {
	return PartyInventory.blueLasersLeft;
    }

    public static int getBombsLeft() {
	return PartyInventory.bombsLeft;
    }

    public static int getBoostsLeft() {
	return PartyInventory.boostsLeft;
    }

    public static int getDisruptorsLeft() {
	return PartyInventory.disruptorsLeft;
    }

    public static int getGreenKeysLeft() {
	return PartyInventory.greenKeysLeft;
    }

    public static int getHeatBombsLeft() {
	return PartyInventory.heatBombsLeft;
    }

    public static int getIceBombsLeft() {
	return PartyInventory.iceBombsLeft;
    }

    public static int getMagnetsLeft() {
	return PartyInventory.magnetsLeft;
    }

    public static int getMissilesLeft() {
	return PartyInventory.missilesLeft;
    }

    public static int getRedKeysLeft() {
	return PartyInventory.redKeysLeft;
    }

    public static int getStunnersLeft() {
	return PartyInventory.stunnersLeft;
    }

    public static void readInventory(final DataIOReader reader) throws IOException {
	PartyInventory.missilesLeft = reader.readInt();
	PartyInventory.stunnersLeft = reader.readInt();
	PartyInventory.boostsLeft = reader.readInt();
	PartyInventory.magnetsLeft = reader.readInt();
	PartyInventory.blueLasersLeft = reader.readInt();
	PartyInventory.disruptorsLeft = reader.readInt();
	PartyInventory.redKeysLeft = reader.readInt();
	PartyInventory.greenKeysLeft = reader.readInt();
	PartyInventory.blueKeysLeft = reader.readInt();
	PartyInventory.bombsLeft = reader.readInt();
	PartyInventory.heatBombsLeft = reader.readInt();
	PartyInventory.iceBombsLeft = reader.readInt();
    }

    public static void resetInventory() {
	PartyInventory.missilesLeft = 0;
	PartyInventory.stunnersLeft = 0;
	PartyInventory.boostsLeft = 0;
	PartyInventory.magnetsLeft = 0;
	PartyInventory.blueLasersLeft = 0;
	PartyInventory.disruptorsLeft = 0;
	PartyInventory.redKeysLeft = 0;
	PartyInventory.greenKeysLeft = 0;
	PartyInventory.blueKeysLeft = 0;
	PartyInventory.bombsLeft = 0;
	PartyInventory.heatBombsLeft = 0;
	PartyInventory.iceBombsLeft = 0;
    }

    public static void setBlueKeysLeft(final int newBlueKeys) {
	PartyInventory.blueKeysLeft = newBlueKeys;
    }

    public static void setGreenKeysLeft(final int newGreenKeys) {
	PartyInventory.greenKeysLeft = newGreenKeys;
    }

    public static void setRedKeysLeft(final int newRedKeys) {
	PartyInventory.redKeysLeft = newRedKeys;
    }

    public static void useBlueKey() {
	PartyInventory.blueKeysLeft--;
    }

    public static void useGreenKey() {
	PartyInventory.greenKeysLeft--;
    }

    public static void useRedKey() {
	PartyInventory.redKeysLeft--;
    }

    public static void writeInventory(final DataIOWriter writer) throws IOException {
	writer.writeInt(PartyInventory.missilesLeft);
	writer.writeInt(PartyInventory.stunnersLeft);
	writer.writeInt(PartyInventory.boostsLeft);
	writer.writeInt(PartyInventory.magnetsLeft);
	writer.writeInt(PartyInventory.blueLasersLeft);
	writer.writeInt(PartyInventory.disruptorsLeft);
	writer.writeInt(PartyInventory.redKeysLeft);
	writer.writeInt(PartyInventory.greenKeysLeft);
	writer.writeInt(PartyInventory.blueKeysLeft);
	writer.writeInt(PartyInventory.bombsLeft);
	writer.writeInt(PartyInventory.heatBombsLeft);
	writer.writeInt(PartyInventory.iceBombsLeft);
    }

    // Constructor
    private PartyInventory() {
	// Do nothing
    }
}
