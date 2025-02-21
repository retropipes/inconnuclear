/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.retropipes.inconnuclear.Inconnuclear;

class ObjectEditorBaseActionHandler implements ActionListener {
    /**
     * 
     */
    private final ObjectEditorBase self;

    public ObjectEditorBaseActionHandler(ObjectEditorBase objectEditorBase) {
	this.self = objectEditorBase;
	// Do nothing
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	final var ge = this.self;
	try {
	    final var cmd = e.getActionCommand().substring(0, ge.actionCmdLen);
	    final var num = Integer.parseInt(e.getActionCommand().substring(ge.actionCmdLen));
	    ge.handleButtonClick(cmd, num);
	    if (ge.autoStore) {
		if (ge.guiEntryType(num) == ObjectEditorBase.ENTRY_TYPE_LIST) {
		    final var list = ge.getEntryList(num);
		    ge.autoStoreEntryListValue(list, num);
		} else if (ge.guiEntryType(num) == ObjectEditorBase.ENTRY_TYPE_TEXT) {
		    final var entry = ge.getEntryField(num);
		    ge.autoStoreEntryFieldValue(entry, num);
		}
	    }
	} catch (final NumberFormatException nfe) {
	    // Ignore
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
    }
}