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

class ClassicAccelerators extends Accelerators {
    ClassicAccelerators() {
	int modKey;
	if (System.getProperty(Strings.untranslated(Untranslated.OS_NAME))
		.equalsIgnoreCase(Strings.untranslated(Untranslated.MACOS))) {
	    modKey = InputEvent.META_DOWN_MASK;
	} else {
	    modKey = InputEvent.CTRL_DOWN_MASK;
	}
	this.fileNewAccel = KeyStroke.getKeyStroke(KeyEvent.VK_N, 0);
	this.fileOpenAccel = KeyStroke.getKeyStroke(KeyEvent.VK_O, 0);
	this.fileCloseAccel = KeyStroke.getKeyStroke(KeyEvent.VK_W, 0);
	this.fileSaveAccel = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
	this.fileSaveAsAccel = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0);
	this.filePrintAccel = KeyStroke.getKeyStroke(KeyEvent.VK_P, 0);
	this.fileExitAccel = KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0);
	this.editorUndoAccel = KeyStroke.getKeyStroke(KeyEvent.VK_U, 0);
	this.editorRedoAccel = KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0);
	this.editorCutLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_X, 0);
	this.editorCopyLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_C, 0);
	this.editorPasteLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_V, 0);
	this.editorInsertLevelFromClipboardAccel = KeyStroke.getKeyStroke(KeyEvent.VK_F, 0);
	this.filePreferencesAccel = KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0);
	this.editorClearHistoryAccel = KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0);
	this.editorGoToLocationAccel = KeyStroke.getKeyStroke(KeyEvent.VK_G, 0);
	this.playPlayDungeonAccel = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
	this.playEditDungeonAccel = KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0);
	this.gameResetAccel = KeyStroke.getKeyStroke(KeyEvent.VK_R, 0);
	this.gameShowTableAccel = KeyStroke.getKeyStroke(KeyEvent.VK_T, 0);
	this.editorUpOneLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_UP, modKey);
	this.editorDownOneLevelAccel = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, modKey);
    }
}
