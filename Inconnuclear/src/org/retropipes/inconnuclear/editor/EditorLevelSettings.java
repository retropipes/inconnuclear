/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Difficulty;
import org.retropipes.inconnuclear.locale.EditorString;
import org.retropipes.inconnuclear.locale.Strings;

class EditorLevelSettings {
    private class EventHandler implements ActionListener, WindowListener {
	public EventHandler() {
	    // Do nothing
	}

	// Handle buttons
	@Override
	public void actionPerformed(final ActionEvent e) {
	    try {
		final var lpm = EditorLevelSettings.this;
		final var cmd = e.getActionCommand();
		if (cmd.equals(Strings.dialog(DialogString.OK_BUTTON))) {
		    lpm.setPrefs();
		    lpm.hidePrefs();
		} else if (cmd.equals(Strings.dialog(DialogString.CANCEL_BUTTON))) {
		    lpm.hidePrefs();
		}
	    } catch (final Exception ex) {
		Inconnuclear.logError(ex);
	    }
	}

	@Override
	public void windowActivated(final WindowEvent e) {
	    // Do nothing
	}

	@Override
	public void windowClosed(final WindowEvent e) {
	    // Do nothing
	}

	@Override
	public void windowClosing(final WindowEvent e) {
	    final var pm = EditorLevelSettings.this;
	    pm.hidePrefs();
	}

	@Override
	public void windowDeactivated(final WindowEvent e) {
	    // Do nothing
	}

	@Override
	public void windowDeiconified(final WindowEvent e) {
	    // Do nothing
	}

	@Override
	public void windowIconified(final WindowEvent e) {
	    // Do nothing
	}

	// handle window
	@Override
	public void windowOpened(final WindowEvent e) {
	    // Do nothing
	}
    }

    // Fields
    private MainWindow mainWindow;
    private EventHandler handler;
    private JCheckBox horizontalWrap;
    private JCheckBox verticalWrap;
    private JCheckBox thirdWrap;
    private JTextField name;
    private JTextField author;
    private JTextArea hint;
    private JComboBox<String> difficulty;
    private JCheckBox moveShoot;
    private MainContent mainPrefPane;

    // Constructors
    public EditorLevelSettings() {
	this.setUpGUI();
    }

    void hidePrefs() {
	this.mainWindow.removeWindowListener(this.handler);
	this.mainWindow.restoreSaved();
	Inconnuclear.getStuffBag().getEditor().enableOutput();
	Inconnuclear.getStuffBag().getDungeonManager().setDirty(true);
	Inconnuclear.getStuffBag().getEditor().redrawEditor();
    }

    private void loadPrefs() {
	final var m = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase();
	this.horizontalWrap.setSelected(m.isHorizontalWraparoundEnabled());
	this.verticalWrap.setSelected(m.isVerticalWraparoundEnabled());
	this.thirdWrap.setSelected(m.isThirdDimensionWraparoundEnabled());
	this.name.setText(m.getName());
	this.author.setText(m.getAuthor());
	this.hint.setText(m.getHint());
	this.difficulty.setSelectedIndex(m.getDifficulty().ordinal() - 1);
	this.moveShoot.setSelected(m.isMoveShootAllowedThisLevel());
    }

    void setPrefs() {
	final var m = Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase();
	if (this.horizontalWrap.isSelected()) {
	    m.enableHorizontalWraparound();
	} else {
	    m.disableHorizontalWraparound();
	}
	if (this.verticalWrap.isSelected()) {
	    m.enableVerticalWraparound();
	} else {
	    m.disableVerticalWraparound();
	}
	if (this.thirdWrap.isSelected()) {
	    m.enableThirdDimensionWraparound();
	} else {
	    m.disableThirdDimensionWraparound();
	}
	m.setName(this.name.getText());
	m.setAuthor(this.author.getText());
	m.setHint(this.hint.getText());
	m.setDifficulty(Difficulty.values()[this.difficulty.getSelectedIndex() + 1]);
	m.setMoveShootAllowedThisLevel(this.moveShoot.isSelected());
    }

    private void setUpGUI() {
	MainContent contentPane, buttonPane;
	JButton prefsOK, prefsCancel;
	this.handler = new EventHandler();
	this.mainWindow = MainWindow.mainWindow();
	this.mainPrefPane = MainWindow.createContent();
	contentPane = MainWindow.createContent();
	buttonPane = MainWindow.createContent();
	prefsOK = new JButton(Strings.dialog(DialogString.OK_BUTTON));
	prefsOK.setDefaultCapable(true);
	this.mainWindow.setDefaultButton(prefsOK);
	prefsCancel = new JButton(Strings.dialog(DialogString.CANCEL_BUTTON));
	prefsCancel.setDefaultCapable(false);
	this.horizontalWrap = new JCheckBox(Strings.editor(EditorString.ENABLE_HORIZONTAL_WRAP_AROUND), false);
	this.verticalWrap = new JCheckBox(Strings.editor(EditorString.ENABLE_VERTICAL_WRAP_AROUND), false);
	this.thirdWrap = new JCheckBox(Strings.editor(EditorString.ENABLE_THIRD_DIMENSION_WRAP_AROUND), false);
	this.name = new JTextField();
	this.author = new JTextField();
	this.hint = new JTextArea(8, 32);
	this.difficulty = new JComboBox<>(Strings.allDifficulties());
	this.moveShoot = new JCheckBox(Strings.editor(EditorString.ENABLE_MOVE_SHOOT), true);
	this.mainPrefPane.setLayout(new BorderLayout());
	contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
	contentPane.add(this.horizontalWrap);
	contentPane.add(this.verticalWrap);
	contentPane.add(this.thirdWrap);
	contentPane.add(new JLabel(Strings.editor(EditorString.LEVEL_NAME)));
	contentPane.add(this.name);
	contentPane.add(new JLabel(Strings.editor(EditorString.LEVEL_AUTHOR)));
	contentPane.add(this.author);
	contentPane.add(new JLabel(Strings.editor(EditorString.LEVEL_HINT)));
	contentPane.add(this.hint);
	contentPane.add(new JLabel(Strings.editor(EditorString.LEVEL_DIFFICULTY)));
	contentPane.add(this.difficulty);
	contentPane.add(this.moveShoot);
	buttonPane.setLayout(new FlowLayout());
	buttonPane.add(prefsOK);
	buttonPane.add(prefsCancel);
	this.mainPrefPane.add(contentPane, BorderLayout.CENTER);
	this.mainPrefPane.add(buttonPane, BorderLayout.SOUTH);
	prefsOK.addActionListener(this.handler);
	prefsCancel.addActionListener(this.handler);
    }

    void showPrefs() {
	this.loadPrefs();
	Inconnuclear.getStuffBag().getEditor().disableOutput();
	this.mainWindow.setAndSave(this.mainPrefPane, Strings.editor(EditorString.LEVEL_PREFERENCES));
	this.mainWindow.addWindowListener(this.handler);
    }
}
