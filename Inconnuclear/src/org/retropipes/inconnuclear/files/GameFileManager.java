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

import org.retropipes.diane.random.RandomRange;

class GameFileManager {
    public static void load(final File src, final File dst) throws IOException {
	try (var in = new FileInputStream(src); var out = new FileOutputStream(dst)) {
	    final var buf = new byte[1024];
	    int len;
	    final var transform = (byte) in.read();
	    while ((len = in.read(buf)) > 0) {
		for (var x = 0; x < buf.length; x++) {
		    buf[x] -= transform;
		}
		out.write(buf, 0, len);
	    }
	} catch (final IOException ioe) {
	    throw ioe;
	}
    }

    public static void save(final File src, final File dst) throws IOException {
	try (var in = new FileInputStream(src); var out = new FileOutputStream(dst)) {
	    final var buf = new byte[1024];
	    int len;
	    final var transform = (byte) new RandomRange(1, 250).generate();
	    out.write(transform);
	    while ((len = in.read(buf)) > 0) {
		for (var x = 0; x < buf.length; x++) {
		    buf[x] += transform;
		}
		out.write(buf, 0, len);
	    }
	} catch (final IOException ioe) {
	    throw ioe;
	}
    }

    private GameFileManager() {
	// Do nothing
    }
}
