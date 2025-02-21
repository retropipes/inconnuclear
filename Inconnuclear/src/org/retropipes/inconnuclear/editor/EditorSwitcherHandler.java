/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Strings;

class EditorSwitcherHandler implements ActionListener {
    private final Editor editor;

    EditorSwitcherHandler(Editor theEditor) {
	this.editor = theEditor;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	try {
	    final var cmd = e.getActionCommand();
	    final var ae = this.editor;
	    if (cmd.equals(Strings.layer(Layer.GROUND))) {
		ae.changeLayerImpl(Layer.GROUND.ordinal());
	    } else if (cmd.equals(Strings.layer(Layer.OBJECT))) {
		ae.changeLayerImpl(Layer.OBJECT.ordinal());
	    } else if (cmd.equals(Strings.layer(Layer.STATUS))) {
		ae.changeLayerImpl(Layer.STATUS.ordinal());
	    } else if (cmd.equals(Strings.layer(Layer.MARKER))) {
		ae.changeLayerImpl(Layer.MARKER.ordinal());
	    }
	} catch (final Exception ex) {
	    Inconnuclear.logError(ex);
	}
    }
}