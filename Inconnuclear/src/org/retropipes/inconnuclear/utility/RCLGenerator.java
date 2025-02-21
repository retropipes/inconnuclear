/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.utility;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;

public class RCLGenerator {
    public static MainContent generateRowColumnLabels() {
	final var outerOutputPane = MainWindow.createContent();
	outerOutputPane.setLayout(new BorderLayout());
	final var rowsPane = MainWindow.createContent();
	rowsPane.setLayout(new BoxLayout(rowsPane, BoxLayout.Y_AXIS));
	// Generate row labels
	rowsPane.add(Box.createVerticalGlue());
	for (var r = 1; r <= DungeonBase.getMinRows(); r++) {
	    final var j = new JLabel(Integer.toString(r));
	    j.setLabelFor(null);
	    j.setHorizontalAlignment(SwingConstants.RIGHT);
	    j.setVerticalAlignment(SwingConstants.CENTER);
	    rowsPane.add(j);
	    if (r < DungeonBase.getMinRows()) {
		rowsPane.add(Box.createVerticalGlue());
	    }
	}
	final var columnsPane = MainWindow.createContent();
	columnsPane.setLayout(new BoxLayout(columnsPane, BoxLayout.X_AXIS));
	// Generate column labels
	columnsPane.add(Box.createHorizontalGlue());
	for (var c = 1; c <= DungeonBase.getMinColumns(); c++) {
	    final var j = new JLabel(Character.toString((char) (c + 64)));
	    j.setLabelFor(null);
	    j.setHorizontalAlignment(SwingConstants.CENTER);
	    j.setVerticalAlignment(SwingConstants.BOTTOM);
	    columnsPane.add(j);
	    if (c < DungeonBase.getMinColumns()) {
		columnsPane.add(Box.createHorizontalGlue());
	    }
	}
	outerOutputPane.add(rowsPane, BorderLayout.WEST);
	outerOutputPane.add(columnsPane, BorderLayout.NORTH);
	return outerOutputPane;
    }

    // Constructor
    private RCLGenerator() {
	// Do nothing
    }
}
