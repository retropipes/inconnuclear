/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.utility;

public class FileExtensions {
    // Constants
    private static final String STRING_EXTENSION = "properties";

    public static String getStringExtensionWithPeriod() {
	return "." + FileExtensions.STRING_EXTENSION;
    }

    private FileExtensions() {
	// Do nothing
    }
}