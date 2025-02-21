/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear;

import java.io.IOException;

public class VersionException extends IOException {
    private static final long serialVersionUID = 7521249394165201264L;

    public VersionException(final String message) {
	super(message);
    }
}
