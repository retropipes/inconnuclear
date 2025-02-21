/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.party;

import java.io.IOException;

import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.creature.GameDifficulty;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.locale.Strings;

public class Party {
    static Party read(final DataIOReader worldFile, final GameDifficulty diff) throws IOException {
	worldFile.readInt();
	final var lid = worldFile.readInt();
	final var apc = worldFile.readInt();
	final var lvl = worldFile.readInt();
	final var pty = new Party();
	pty.leaderID = lid;
	pty.activePCs = apc;
	pty.zone = lvl;
	final var present = worldFile.readBoolean();
	if (present) {
	    pty.members = PartyMember.read(worldFile, diff);
	}
	return pty;
    }

    // Fields
    private PartyMember members;
    private int leaderID;
    private int activePCs;
    private int zone;

    // Constructors
    public Party() {
	this.members = null;
	this.leaderID = 0;
	this.activePCs = 0;
	this.zone = 0;
    }

    boolean addPartyMember(final PartyMember member) {
	this.members = member;
	return true;
    }

    public void checkPartyLevelUp() {
	// Level Up Check
	if (this.members.checkLevelUp()) {
	    this.members.levelUp();
	    SoundLoader.playSound(Sounds.GAIN_LEVEL);
	    CommonDialogs.showTitledDialog(this.members.getName() + " reached level " + this.members.getLevel() + "!",
		    "Level Up");
	}
    }

    public int getLayer() {
	return 3; // DungeonConstants.LAYER_UPPER_OBJECTS
    }

    public PartyMember getLeader() {
	return this.members;
    }

    public long getPartyMaxToNextLevel() {
	return this.members.getToNextLevelValue();
    }

    public int getZone() {
	return this.zone;
    }

    public String getZoneString() {
	return "Zone Name: " + Strings.zone(this.zone);
    }

    public boolean isAlive() {
	return this.members.isAlive();
    }

    public void offsetZone(final int offset) {
	if (this.zone + offset > Integer.MAX_VALUE || this.zone + offset < 0) {
	    return;
	}
	this.zone += offset;
    }

    void resetZone() {
	this.zone = 0;
    }

    void write(final DataIOWriter worldFile) throws IOException {
	worldFile.writeInt(1);
	worldFile.writeInt(this.leaderID);
	worldFile.writeInt(this.activePCs);
	worldFile.writeInt(this.zone);
	if (this.members == null) {
	    worldFile.writeBoolean(false);
	} else {
	    worldFile.writeBoolean(true);
	    this.members.write(worldFile);
	}
    }
}
