/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.editor;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.retropipes.diane.gui.MainContent;

public abstract class ObjectEditorBase extends EditorBase {
    public static final boolean ENTRY_TYPE_TEXT = false;
    public static final boolean ENTRY_TYPE_LIST = true;
    // Fields
    final int actionCmdLen;
    private final int abRows;
    private final int abCols;
    private JLabel[] nameLabels;
    private JTextField[] entryFields;
    private ArrayList<JComboBox<String>> entryLists;
    private JButton[][] actionButtons;
    boolean autoStore;
    private ObjectEditorBaseActionHandler ahandler;
    private ObjectEditorBaseFocusHandler fhandler;

    protected ObjectEditorBase(final String newSource, final int actionCommandLength, final int actionButtonRows,
	    final int actionButtonCols, final boolean autoStoreEnabled) {
	super(newSource);
	this.actionCmdLen = actionCommandLength;
	this.abRows = actionButtonRows;
	this.abCols = actionButtonCols;
	this.autoStore = autoStoreEnabled;
    }

    protected abstract void autoStoreEntryFieldValue(JTextField entry, int num);

    protected abstract void autoStoreEntryListValue(JComboBox<String> list, int num);

    @Override
    protected void borderPaneHook() {
	// Do nothing
    }

    @Override
    protected void editObjectChanged() {
	// Do nothing
    }

    protected JTextField getEntryField(final int num) {
	return this.entryFields[num];
    }

    protected JComboBox<String> getEntryList(final int num) {
	return this.entryLists.get(num);
    }

    protected abstract String guiActionButtonActionCommand(int row, int col);

    protected abstract void guiActionButtonProperties(JButton actBtn, int row, int col);

    protected abstract void guiEntryFieldProperties(JTextField entry, int num);

    protected abstract String[] guiEntryListItems(int num);

    protected abstract void guiEntryListProperties(JComboBox<String> list, int num);

    protected abstract boolean guiEntryType(int num);

    protected abstract void guiNameLabelProperties(JLabel nameLbl, int num);

    protected abstract void handleButtonClick(String cmd, int num);

    @Override
    public void redrawEditor() {
	// Do nothing
    }

    @Override
    protected void reSetUpGUIHook(final MainContent outputPane) {
	this.setUpGUIHook(outputPane);
    }

    @Override
    protected void setUpGUIHook(final MainContent outputPane) {
	this.ahandler = new ObjectEditorBaseActionHandler(this);
	this.fhandler = new ObjectEditorBaseFocusHandler(this);
	outputPane.setLayout(new GridLayout(this.abCols, this.abRows));
	this.nameLabels = new JLabel[this.abCols];
	this.entryFields = new JTextField[this.abCols];
	this.entryLists = new ArrayList<>(this.abCols);
	for (var x = 0; x < this.abCols; x++) {
	    this.entryLists.add(x, new JComboBox<>(new String[] {}));
	}
	this.actionButtons = new JButton[this.abRows - 2][this.abCols];
	// Grid rows
	for (var x = 0; x < this.abCols; x++) {
	    // Create controls
	    this.nameLabels[x] = new JLabel();
	    this.guiNameLabelProperties(this.nameLabels[x], x);
	    final var entryType = this.guiEntryType(x);
	    if (entryType == ObjectEditorBase.ENTRY_TYPE_LIST) {
		this.entryLists.set(x, new JComboBox<>(this.guiEntryListItems(x)));
		this.guiEntryListProperties(this.entryLists.get(x), x);
		if (this.isReadOnly()) {
		    this.entryLists.get(x).setEnabled(false);
		} else if (this.autoStore) {
		    this.entryLists.get(x).setName(Integer.toString(x));
		    this.entryLists.get(x).addFocusListener(this.fhandler);
		}
	    } else if (entryType == ObjectEditorBase.ENTRY_TYPE_TEXT) {
		this.entryFields[x] = new JTextField();
		this.guiEntryFieldProperties(this.entryFields[x], x);
		if (this.isReadOnly()) {
		    this.entryFields[x].setEnabled(false);
		} else if (this.autoStore) {
		    this.entryFields[x].setName(Integer.toString(x));
		    this.entryFields[x].addFocusListener(this.fhandler);
		}
	    }
	    for (var y = 0; y < this.abRows - 2; y++) {
		this.actionButtons[y][x] = new JButton();
		this.guiActionButtonProperties(this.actionButtons[y][x], x, y);
		this.actionButtons[y][x].setActionCommand(this.guiActionButtonActionCommand(x, y));
		// Add action listener for button
		this.actionButtons[y][x].addActionListener(this.ahandler);
		if (this.isReadOnly()) {
		    this.actionButtons[y][x].setEnabled(false);
		}
	    }
	    // Add controls
	    outputPane.add(this.nameLabels[x]);
	    if (entryType == ObjectEditorBase.ENTRY_TYPE_LIST) {
		outputPane.add(this.entryLists.get(x));
	    } else if (entryType == ObjectEditorBase.ENTRY_TYPE_TEXT) {
		outputPane.add(this.entryFields[x]);
	    }
	    for (var y = 0; y < this.abRows - 2; y++) {
		outputPane.add(this.actionButtons[y][x]);
	    }
	}
    }

    @Override
    public void switchFromSubEditor() {
	// Do nothing
    }

    @Override
    public void switchToSubEditor() {
	// Do nothing
    }
}
