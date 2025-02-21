/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Strings;

public class DungeonProtectionWrapper {
    // Constants
    private static final int BLOCK_MULTIPLIER = 16;

    private static char[] getTransform() {
	return CommonDialogs.showPasswordInputDialog(Strings.dialog(DialogString.PROTECTION_PROMPT),
		Strings.dialog(DialogString.PROTECTION_TITLE));
    }

    public static void protect(final File src, final File dst) throws IOException {
	try (var in = new FileInputStream(src); var out = new FileOutputStream(dst)) {
	    final var transform = DungeonProtectionWrapper.getTransform();
	    if (transform == null) {
		throw new ProtectionCancelException();
	    }
	    final var buf = new byte[transform.length * DungeonProtectionWrapper.BLOCK_MULTIPLIER];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		for (var x = 0; x < buf.length; x++) {
		    buf[x] += transform[x % transform.length];
		}
		out.write(buf, 0, len);
	    }
	} catch (final IOException ioe) {
	    throw ioe;
	}
    }

    public static void unprotect(final File src, final File dst) throws IOException {
	try (var in = new FileInputStream(src); var out = new FileOutputStream(dst)) {
	    final var transform = DungeonProtectionWrapper.getTransform();
	    if (transform == null) {
		throw new ProtectionCancelException();
	    }
	    final var buf = new byte[transform.length * DungeonProtectionWrapper.BLOCK_MULTIPLIER];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		for (var x = 0; x < buf.length; x++) {
		    buf[x] -= transform[x % transform.length];
		}
		out.write(buf, 0, len);
	    }
	} catch (final IOException ioe) {
	    throw ioe;
	}
    }

    private DungeonProtectionWrapper() {
	// Do nothing
    }
}
