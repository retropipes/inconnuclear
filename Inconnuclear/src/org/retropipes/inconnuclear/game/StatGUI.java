/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.loader.image.status.StatusImageId;
import org.retropipes.inconnuclear.loader.image.status.StatusImageLoader;

class StatGUI {
    // Fields
    private MainContent statsPane;
    private JLabel hpLabel;
    private JLabel mpLabel;
    private JLabel goldLabel;
    private JLabel attackLabel;
    private JLabel defenseLabel;
    private JLabel xpLabel;
    private JLabel levelLabel;

    // Constructors
    StatGUI() {
	this.setUpGUI();
    }

    MainContent getStatsPane() {
	return this.statsPane;
    }

    private void setUpGUI() {
	this.statsPane = MainWindow.createContent();
	this.statsPane.setLayout(new GridLayout(7, 1));
	this.hpLabel = new JLabel("", null, SwingConstants.LEFT);
	this.mpLabel = new JLabel("", null, SwingConstants.LEFT);
	this.goldLabel = new JLabel("", null, SwingConstants.LEFT);
	this.attackLabel = new JLabel("", null, SwingConstants.LEFT);
	this.defenseLabel = new JLabel("", null, SwingConstants.LEFT);
	this.xpLabel = new JLabel("", null, SwingConstants.LEFT);
	this.levelLabel = new JLabel("", null, SwingConstants.LEFT);
	this.statsPane.add(this.hpLabel);
	this.statsPane.add(this.mpLabel);
	this.statsPane.add(this.goldLabel);
	this.statsPane.add(this.attackLabel);
	this.statsPane.add(this.defenseLabel);
	this.statsPane.add(this.xpLabel);
	this.statsPane.add(this.levelLabel);
    }

    void updateImages() {
	final var hpImage = StatusImageLoader.load(StatusImageId.HEALTH);
	this.hpLabel.setIcon(hpImage);
	final var mpImage = StatusImageLoader.load(StatusImageId.MAGIC);
	this.mpLabel.setIcon(mpImage);
	final var goldImage = StatusImageLoader.load(StatusImageId.MONEY);
	this.goldLabel.setIcon(goldImage);
	final var attackImage = StatusImageLoader.load(StatusImageId.MELEE_ATTACK);
	this.attackLabel.setIcon(attackImage);
	final var defenseImage = StatusImageLoader.load(StatusImageId.DEFENSE);
	this.defenseLabel.setIcon(defenseImage);
	final var xpImage = StatusImageLoader.load(StatusImageId.EXPERIENCE);
	this.xpLabel.setIcon(xpImage);
	final var levelImage = StatusImageLoader.load(StatusImageId.CREATURE_LEVEL);
	this.levelLabel.setIcon(levelImage);
    }

    void updateStats() {
	final var party = PartyManager.getParty();
	if (party != null) {
	    final var pc = party.getLeader();
	    if (pc != null) {
		this.hpLabel.setText(pc.getHPString());
		this.mpLabel.setText(pc.getMPString());
		this.goldLabel.setText(Integer.toString(pc.getGold()));
		this.attackLabel.setText(Integer.toString(pc.getAttack()));
		this.defenseLabel.setText(Integer.toString(pc.getDefense()));
		this.xpLabel.setText(pc.getXPString());
		this.levelLabel.setText(party.getZoneString());
	    }
	}
    }
}
