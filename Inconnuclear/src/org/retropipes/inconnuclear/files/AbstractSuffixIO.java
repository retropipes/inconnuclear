/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.files;

import java.io.IOException;

import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;

public interface AbstractSuffixIO {
    void readSuffix(DataIOReader reader, int formatVersion) throws IOException;

    void writeSuffix(DataIOWriter writer) throws IOException;
}
