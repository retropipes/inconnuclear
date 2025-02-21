/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.utility;

public class FileFormats {
    private static final int DUNGEON_5 = 5;
    private static final int DUNGEON_6 = 6;
    private static final int DUNGEON_7 = 7;
    private static final int DUNGEON_8 = 8;
    private static final int DUNGEON_9 = 9;
    private static final int DUNGEON_10 = 10;
    private static final int DUNGEON_11 = 11;
    private static final int DUNGEON_12 = 12;
    private static final int DUNGEON_15 = 15;
    private static final int DUNGEON_16 = 16;
    private static final int DUNGEON_17 = 17;
    private static final int DUNGEON_18 = 18;
    public static final int DUNGEON_LATEST = 18;

    public static final boolean isFormatVersionValidGeneration1(final int ver) {
	return ver == FileFormats.DUNGEON_5 || ver == FileFormats.DUNGEON_6;
    }

    public static final boolean isFormatVersionValidGeneration2(final int ver) {
	return ver == FileFormats.DUNGEON_7 || ver == FileFormats.DUNGEON_8;
    }

    public static final boolean isFormatVersionValidGeneration3(final int ver) {
	return ver == FileFormats.DUNGEON_9;
    }

    public static final boolean isFormatVersionValidGeneration4(final int ver) {
	return ver == FileFormats.DUNGEON_10 || ver == FileFormats.DUNGEON_11;
    }

    public static final boolean isFormatVersionValidGeneration5(final int ver) {
	return ver == FileFormats.DUNGEON_12 || ver == FileFormats.DUNGEON_15 || ver == FileFormats.DUNGEON_16;
    }

    public static final boolean isFormatVersionValidGeneration6(final int ver) {
	return ver == FileFormats.DUNGEON_17;
    }

    public static final boolean isFormatVersionValidGeneration7(final int ver) {
	return ver == FileFormats.DUNGEON_18;
    }

    public static final boolean isLevelListStored(final int ver) {
	return ver >= FileFormats.DUNGEON_17;
    }

    public static final boolean isMoveShootAllowed(final int ver) {
	return ver >= FileFormats.DUNGEON_11;
    }

    private FileFormats() {
	// Do nothing
    }
}
