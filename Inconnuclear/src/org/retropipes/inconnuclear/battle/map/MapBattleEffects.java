/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.


All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map;

import java.awt.GridLayout;

import javax.swing.JLabel;

import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.inconnuclear.battle.BattleCharacter;

public class MapBattleEffects {
    // Fields
    private MainContent effectsPane;
    private JLabel[] effectLabels;

    // Constructors
    public MapBattleEffects() {
	// Do nothing
    }

    public MainContent getEffectsPane() {
	if (this.effectsPane == null) {
	    this.effectsPane = MainWindow.createContent();
	}
	return this.effectsPane;
    }

    private void setUpGUI(final int count) {
	this.effectsPane = this.getEffectsPane();
	this.effectsPane.removeAll();
	this.effectsPane.setLayout(new GridLayout(count, 1));
	this.effectLabels = new JLabel[count];
	for (var x = 0; x < count; x++) {
	    this.effectLabels[x] = new JLabel(" ");
	}
    }

    public void updateEffects(final BattleCharacter bc) {
	final var count = bc.getCreature().getActiveEffectCount();
	if (count > 0) {
	    this.setUpGUI(count);
	    final var es = bc.getCreature().getCompleteEffectStringArray();
	    for (var x = 0; x < count; x++) {
		this.effectLabels[x].setText(es[x]);
	    }
	}
    }
}
