/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.


All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.party;

import java.io.IOException;

import org.retropipes.diane.ack.AvatarConstructionKit;
import org.retropipes.diane.ack.AvatarImageModel;
import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.creature.GameDifficulty;
import org.retropipes.inconnuclear.creature.characterfile.CharacterLoader;
import org.retropipes.inconnuclear.creature.characterfile.CharacterRegistration;
import org.retropipes.inconnuclear.creature.gender.GenderManager;
import org.retropipes.inconnuclear.creature.job.JobManager;
import org.retropipes.inconnuclear.loader.music.MusicLoader;
import org.retropipes.inconnuclear.locale.Music;

public class PartyManager {
    // Fields
    private static Party party;
    private static int bank = 0;
    private static final int PARTY_SIZE = 1;
    private final static String[] buttonNames = { "Done", "Create", "Pick" };

    public static void addGoldToBank(final int newGold) {
	PartyManager.bank += newGold;
    }

    private static String[] buildNameList(final PartyMember[] members) {
	final var tempNames = new String[1];
	var nnc = 0;
	for (var x = 0; x < tempNames.length; x++) {
	    if (members != null) {
		tempNames[x] = members[x].getName();
		nnc++;
	    }
	}
	final var names = new String[nnc];
	nnc = 0;
	for (final String tempName : tempNames) {
	    if (tempName != null) {
		names[nnc] = tempName;
		nnc++;
	    }
	}
	return names;
    }

    private static PartyMember createNewPC(final GameDifficulty diff) {
	final var name = CommonDialogs.showTextInputDialog("Character Name", "Create Character");
	if (name != null) {
	    final var job = JobManager.selectJob();
	    if (job != null) {
		final var gender = GenderManager.selectGender();
		if (gender != null) {
		    AvatarImageModel avatar = null;
		    avatar = AvatarConstructionKit.constructAvatar();
		    if (avatar != null) {
			final var aid = avatar.getAvatarImageID();
			return new PartyMember(job, gender, name, aid, diff);
		    }
		}
	    }
	}
	return null;
    }

    public static boolean createParty(final GameDifficulty diff) {
	if (MusicLoader.isMusicPlaying()) {
	    MusicLoader.stopMusic();
	}
	MusicLoader.playMusic(Music.CREATE);
	PartyManager.party = new Party();
	var mem = 0;
	final var pickMembers = CharacterLoader.loadAllRegisteredCharacters(diff);
	for (var x = 0; x < PartyManager.PARTY_SIZE; x++) {
	    PartyMember pc = null;
	    if (pickMembers == null) {
		// No characters registered - must create one
		pc = PartyManager.createNewPC(diff);
		if (pc != null) {
		    CharacterRegistration.autoregisterCharacter(pc.getName());
		    CharacterLoader.saveCharacter(pc);
		}
	    } else {
		final var response = CommonDialogs.showCustomDialogWithDefault("Pick, Create, or Done?", "Create Party",
			PartyManager.buttonNames, PartyManager.buttonNames[2]);
		if (response == 2) {
		    pc = PartyManager.pickOnePartyMemberCreate(pickMembers);
		} else if (response == 1) {
		    pc = PartyManager.createNewPC(diff);
		    if (pc != null) {
			CharacterRegistration.autoregisterCharacter(pc.getName());
			CharacterLoader.saveCharacter(pc);
		    }
		}
	    }
	    if (pc == null) {
		break;
	    }
	    PartyManager.party.addPartyMember(pc);
	    mem++;
	}
	if (mem == 0) {
	    return false;
	}
	return true;
    }

    public static int getGoldInBank() {
	return PartyManager.bank;
    }

    public static PartyMember getNewPCInstance(final int c, final int g, final String n, final String aid, final GameDifficulty diff) {
	final var job = JobManager.getJob(c);
	final var gender = GenderManager.getGender(g);
	return new PartyMember(job, gender, n, aid, diff);
    }

    public static Party getParty() {
	return PartyManager.party;
    }

    public static void loadGameHook(final DataIOReader partyFile, final GameDifficulty diff) throws IOException {
	final var containsPCData = partyFile.readBoolean();
	if (containsPCData) {
	    final var gib = partyFile.readInt();
	    PartyManager.setGoldInBank(gib);
	    PartyManager.party = Party.read(partyFile, diff);
	}
    }

    private static PartyMember pickOnePartyMemberCreate(final PartyMember[] members) {
	final var pickNames = PartyManager.buildNameList(members);
	final var response = CommonDialogs.showInputDialog("Pick 1 Party Member", "Create Party", pickNames,
		pickNames[0]);
	if (response == null) {
	    return null;
	}
	for (final PartyMember member : members) {
	    if (member.getName().equals(response)) {
		return member;
	    }
	}
	return null;
    }

    public static void removeGoldFromBank(final int cost) {
	PartyManager.bank -= cost;
	if (PartyManager.bank < 0) {
	    PartyManager.bank = 0;
	}
    }

    public static void saveGameHook(final DataIOWriter partyFile) throws IOException {
	if (PartyManager.party != null) {
	    partyFile.writeBoolean(true);
	    partyFile.writeInt(PartyManager.getGoldInBank());
	    PartyManager.party.write(partyFile);
	} else {
	    partyFile.writeBoolean(false);
	}
    }

    private static void setGoldInBank(final int newGold) {
	PartyManager.bank = newGold;
    }

    public static String showCreationDialog(final String labelText, final String title, final String[] input,
	    final String[] descriptions) {
	return CommonDialogs.showListWithDescDialog(labelText, title, input, input[0], descriptions[0], descriptions);
    }

    public static void updatePostKill() {
	final var leader = PartyManager.getParty().getLeader();
	leader.initPostKill(leader.getJob(), leader.getGender());
    }

    // Constructors
    private PartyManager() {
	// Do nothing
    }
}
