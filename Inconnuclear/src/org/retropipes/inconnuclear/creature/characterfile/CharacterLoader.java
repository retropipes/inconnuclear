/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.characterfile;

import java.io.File;
import java.io.IOException;

import org.retropipes.diane.Diane;
import org.retropipes.diane.fileio.DataIOException;
import org.retropipes.diane.fileio.DataIOFactory;
import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.diane.fileio.DataMode;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.creature.GameDifficulty;
import org.retropipes.inconnuclear.creature.party.PartyMember;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;

public class CharacterLoader {
    static void deleteCharacter(final String name, final boolean showResults) {
	final var basePath = CharacterRegistration.getBasePath();
	final var characterFile = basePath + File.separator + name + Strings.fileExtension(FileExtension.CHARACTER);
	final var toDelete = new File(characterFile);
	if (toDelete.exists()) {
	    final var success = toDelete.delete();
	    if (success) {
		if (showResults) {
		    CommonDialogs.showDialog("Character removed.");
		} else {
		    CommonDialogs.showDialog("Character " + name + " autoremoved due to version change.");
		}
	    } else if (showResults) {
		CommonDialogs.showDialog("Character removal failed!");
	    } else {
		CommonDialogs.showDialog("Character " + name + " failed to autoremove!");
	    }
	} else if (showResults) {
	    CommonDialogs.showDialog("The character to be removed does not have a corresponding file.");
	} else {
	    CommonDialogs.showDialog("The character to be autoremoved does not have a corresponding file.");
	}
    }

    public static PartyMember[] loadAllRegisteredCharacters(final GameDifficulty diff) {
	final var registeredNames = CharacterRegistration.getCharacterNameList();
	if (registeredNames != null) {
	    final var res = new PartyMember[registeredNames.length];
	    // Load characters
	    for (var x = 0; x < registeredNames.length; x++) {
		final var name = registeredNames[x];
		final var characterWithName = CharacterLoader.loadCharacter(name, diff);
		if (characterWithName == null) {
		    // Auto-removed character
		    return CharacterLoader.loadAllRegisteredCharacters(diff);
		}
		res[x] = characterWithName;
	    }
	    return res;
	}
	return null;
    }

    private static PartyMember loadCharacter(final String name, final GameDifficulty diff) {
	final var basePath = CharacterRegistration.getBasePath();
	final var loadPath = basePath + File.separator + name + Strings.fileExtension(FileExtension.CHARACTER);
	try (DataIOReader loader = DataIOFactory.createReader(DataMode.CUSTOM_XML, loadPath)) {
	    return PartyMember.read(loader, diff);
	} catch (CharacterVersionException | DataIOException e) {
	    CharacterRegistration.autoremoveCharacter(name);
	    return null;
	} catch (final IOException e) {
	    Diane.handleError(e);
	    return null;
	}
    }

    public static void saveCharacter(final PartyMember character) {
	final var basePath = CharacterRegistration.getBasePath();
	final var name = character.getName();
	final var characterFile = basePath + File.separator + name + Strings.fileExtension(FileExtension.CHARACTER);
	try (DataIOWriter saver = DataIOFactory.createWriter(DataMode.CUSTOM_XML, characterFile)) {
	    character.write(saver);
	} catch (final IOException e) {
	    Diane.handleError(e);
	}
    }
}
