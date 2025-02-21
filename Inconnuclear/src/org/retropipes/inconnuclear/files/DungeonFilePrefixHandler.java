/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.files;

import java.io.IOException;

import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.inconnuclear.locale.ErrorString;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.utility.FileFormats;

public class DungeonFilePrefixHandler implements AbstractPrefixIO {
    private static final byte FORMAT_VERSION = (byte) FileFormats.DUNGEON_LATEST;

    private static boolean checkFormatVersion(final byte version) {
	if (version > DungeonFilePrefixHandler.FORMAT_VERSION) {
	    return false;
	}
	return true;
    }

    private static byte readFormatVersion(final DataIOReader reader) throws IOException {
	return reader.readByte();
    }

    private static void writeFormatVersion(final DataIOWriter writer) throws IOException {
	writer.writeByte(DungeonFilePrefixHandler.FORMAT_VERSION);
    }

    @Override
    public int readPrefix(final DataIOReader reader) throws IOException {
	final var formatVer = DungeonFilePrefixHandler.readFormatVersion(reader);
	final var res = DungeonFilePrefixHandler.checkFormatVersion(formatVer);
	if (!res) {
	    throw new IOException(Strings.error(ErrorString.UNKNOWN_FILE_FORMAT));
	}
	return formatVer;
    }

    @Override
    public void writePrefix(final DataIOWriter writer) throws IOException {
	DungeonFilePrefixHandler.writeFormatVersion(writer);
    }
}
