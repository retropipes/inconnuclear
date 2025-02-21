/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.extmusic;

import org.retropipes.inconnuclear.locale.Strings;

public class ExternalMusic {
    // Fields
    private String name;
    private String path;

    // Constructor
    public ExternalMusic() {
	this.name = Strings.EMPTY;
	this.path = Strings.EMPTY;
    }

    public String getName() {
	return this.name;
    }

    public String getPath() {
	return this.path;
    }

    public void setName(final String newName) {
	this.name = newName;
    }

    public void setPath(final String newPath) {
	this.path = newPath;
    }
}
