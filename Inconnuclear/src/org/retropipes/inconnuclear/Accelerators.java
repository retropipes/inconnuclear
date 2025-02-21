/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear;

import javax.swing.KeyStroke;

import org.retropipes.inconnuclear.settings.Settings;

public abstract class Accelerators {
    public static Accelerators getAcceleratorModel() {
	if (Settings.useClassicAccelerators()) {
	    return new ClassicAccelerators();
	}
	return new ModernAccelerators();
    }

    public KeyStroke fileNewAccel, fileOpenAccel, fileCloseAccel, fileSaveAccel, fileSaveAsAccel, filePreferencesAccel,
	    filePrintAccel, fileExitAccel;
    public KeyStroke playPlayDungeonAccel, playEditDungeonAccel;
    public KeyStroke gameResetAccel, gameShowTableAccel;
    public KeyStroke editorUndoAccel, editorRedoAccel, editorCutLevelAccel, editorCopyLevelAccel, editorPasteLevelAccel,
	    editorInsertLevelFromClipboardAccel, editorClearHistoryAccel, editorGoToLocationAccel,
	    editorUpOneLevelAccel, editorDownOneLevelAccel;

    Accelerators() {
	// Do nothing
    }
}
