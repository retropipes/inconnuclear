/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.characterfile;

import java.io.File;
import java.io.FilenameFilter;

import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.Strings;

class CharacterFilter implements FilenameFilter {
    private static String getExtension(final String s) {
	String ext = null;
	final var i = s.lastIndexOf('.');
	if (i > 0 && i < s.length() - 1) {
	    ext = s.substring(i).toLowerCase();
	}
	return ext;
    }

    @Override
    public boolean accept(final File dir, final String name) {
	final var ext = CharacterFilter.getExtension(name);
	if (ext == null) {
	    return false;
	}
	if (ext.equals(Strings.fileExtension(FileExtension.CHARACTER))) {
	    return true;
	}
	return false;
    }
}
