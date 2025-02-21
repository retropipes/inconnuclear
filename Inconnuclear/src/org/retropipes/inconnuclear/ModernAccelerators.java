/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

class ModernAccelerators extends Accelerators {
    ModernAccelerators() {
	int modKey;
	if (System.getProperty(Strings.untranslated(Untranslated.OS_NAME))
		.equalsIgnoreCase(Strings.untranslated(Untranslated.MACOS))) {
	    modKey = InputEvent.META_DOWN_MASK;
	} else {
	    modKey = InputEvent.CTRL_DOWN_MASK;
	}
	this.fileNewAccel = KeyStroke.getKeyStroke(KeyEvent.VK_N, modKey);
	this.fileOpenAccel = KeyStroke.getKeyStroke(KeyEvent.VK_O, modKey);
	this.fileCloseAccel = KeyStroke.getKeyStroke(KeyEvent.VK_W, modKey);
	this.fileSaveAccel = KeyStroke.getKeyStroke(KeyEvent.VK_S, modKey);
	this.fileSaveAsAccel = KeyStroke.getKeyStroke(KeyEvent.VK_S, modKey | InputEvent.SHIFT_DOWN_MASK);
	this.filePrintAccel = KeyStroke.getKeyStroke(KeyEvent.VK_P, modKey);
	this.fileExitAccel = KeyStroke.getKeyStroke(KeyEvent.VK_Q, modKey);
	this.editorUndoAccel = KeyStroke.getKeyStroke(KeyEvent.VK_Z, modKey);
	this.editorRedoAccel = KeyStroke.getKeyStroke(KeyEvent.VK_Z, modKey | InputEvent.SHIFT_DOWN_MASK);
	this.editorCutLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_X, modKey);
	this.editorCopyLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_C, modKey);
	this.editorPasteLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_V, modKey);
	this.editorInsertLevelFromClipboardAccel = KeyStroke.getKeyStroke(KeyEvent.VK_F, modKey);
	this.filePreferencesAccel = KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, modKey);
	this.editorClearHistoryAccel = KeyStroke.getKeyStroke(KeyEvent.VK_Y, modKey);
	this.editorGoToLocationAccel = KeyStroke.getKeyStroke(KeyEvent.VK_G, modKey | InputEvent.SHIFT_DOWN_MASK);
	this.playPlayDungeonAccel = KeyStroke.getKeyStroke(KeyEvent.VK_P, modKey | InputEvent.SHIFT_DOWN_MASK);
	this.playEditDungeonAccel = KeyStroke.getKeyStroke(KeyEvent.VK_E, modKey);
	this.gameResetAccel = KeyStroke.getKeyStroke(KeyEvent.VK_R, modKey);
	this.gameShowTableAccel = KeyStroke.getKeyStroke(KeyEvent.VK_T, modKey);
	this.editorUpOneLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_UP, modKey);
	this.editorDownOneLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, modKey);
    }
}
