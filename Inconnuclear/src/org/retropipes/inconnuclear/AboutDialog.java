/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

import org.retropipes.diane.LocaleUtils;
import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Menu;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

public class AboutDialog implements AboutHandler, MenuSection {
    private class EventHandler implements ActionListener {
	public EventHandler() {
	    // Do nothing
	}

	// Handle buttons
	@Override
	public void actionPerformed(final ActionEvent e) {
	    try {
		final var ad = AboutDialog.this;
		final var cmd = e.getActionCommand();
		if (cmd.equals(Strings.dialog(DialogString.OK_BUTTON))) {
		    ad.hideAboutDialog();
		}
	    } catch (final Exception ex) {
		Inconnuclear.logError(ex);
	    }
	}
    }

    private static class MenuHandler implements ActionListener {
	public MenuHandler() {
	    // Do nothing
	}

	// Handle menus
	@Override
	public void actionPerformed(final ActionEvent e) {
	    try {
		final var app = Inconnuclear.getStuffBag();
		final var cmd = e.getActionCommand();
		if (cmd.equals(LocaleUtils.subst(Strings.menu(Menu.ABOUT_PROGRAM),
			Strings.untranslated(Untranslated.PROGRAM_NAME)))) {
		    app.getAboutDialog().showAboutDialog();
		}
		app.getMenus().checkFlags();
	    } catch (final Exception ex) {
		Inconnuclear.logError(ex);
	    }
	}
    }

    // Fields
    private MainWindow mainWindow;
    private MainContent aboutPane;
    private JMenuItem helpAbout;

    // Constructors
    AboutDialog(final String ver) {
	this.setUpGUI(ver);
    }

    @Override
    public void attachAccelerators(final Accelerators accel) {
	// Do nothing
    }

    @Override
    public JMenu createCommandsMenu() {
	final var mhandler = new MenuHandler();
	final var helpMenu = new JMenu(Strings.menu(Menu.HELP));
	this.helpAbout = new JMenuItem(
		LocaleUtils.subst(Strings.menu(Menu.ABOUT_PROGRAM), Strings.untranslated(Untranslated.PROGRAM_NAME)));
	this.helpAbout.addActionListener(mhandler);
	if (!System.getProperty(Strings.untranslated(Untranslated.OS_NAME))
		.equalsIgnoreCase(Strings.untranslated(Untranslated.MACOS))) {
	    helpMenu.add(this.helpAbout);
	}
	return helpMenu;
    }

    @Override
    public void disableDirtyCommands() {
	// Do nothing
    }

    @Override
    public void disableLoadedCommands() {
	// Do nothing
    }

    @Override
    public void disableModeCommands() {
	// Do nothing
    }

    @Override
    public void enableDirtyCommands() {
	// Do nothing
    }

    @Override
    public void enableLoadedCommands() {
	// Do nothing
    }

    @Override
    public void enableModeCommands() {
	// Do nothing
    }

    @Override
    public void handleAbout(final AboutEvent e) {
	this.showAboutDialog();
    }

    void hideAboutDialog() {
	this.mainWindow.restoreSaved();
    }

    @Override
    public void setInitialState() {
	this.helpAbout.setEnabled(true);
    }

    private void setUpGUI(final String ver) {
	MainContent textPane, buttonPane, logoPane;
	JButton aboutOK;
	EventHandler handler;
	JLabel miniLabel;
	handler = new EventHandler();
	this.mainWindow = MainWindow.mainWindow();
	this.aboutPane = MainWindow.createContent();
	textPane = MainWindow.createContent();
	buttonPane = MainWindow.createContent();
	logoPane = MainWindow.createContent();
	aboutOK = new JButton(Strings.dialog(DialogString.OK_BUTTON));
	miniLabel = new JLabel(Strings.EMPTY, LogoLoader.getMiniatureLogo(), SwingConstants.LEFT);
	miniLabel.setLabelFor(null);
	aboutOK.setDefaultCapable(true);
	this.mainWindow.setDefaultButton(aboutOK);
	this.aboutPane.setLayout(new BorderLayout());
	logoPane.setLayout(new FlowLayout());
	logoPane.add(miniLabel);
	textPane.setLayout(new GridLayout(3, 1));
	textPane.add(new JLabel(LocaleUtils.subst(Strings.dialog(DialogString.VERSION),
		Strings.untranslated(Untranslated.PROGRAM_NAME), ver)));
	textPane.add(new JLabel(LocaleUtils.subst(Strings.dialog(DialogString.AUTHOR),
		Strings.untranslated(Untranslated.GAME_AUTHOR_NAME))));
	textPane.add(new JLabel(LocaleUtils.subst(Strings.dialog(DialogString.WEB_SITE),
		Strings.untranslated(Untranslated.GAME_WEB_URL))));
	buttonPane.setLayout(new FlowLayout());
	buttonPane.add(aboutOK);
	this.aboutPane.add(logoPane, BorderLayout.WEST);
	this.aboutPane.add(textPane, BorderLayout.CENTER);
	this.aboutPane.add(buttonPane, BorderLayout.SOUTH);
	aboutOK.addActionListener(handler);
    }

    public void showAboutDialog() {
	this.mainWindow.setAndSave(this.aboutPane,
		LocaleUtils.subst(Strings.dialog(DialogString.ABOUT), Strings.untranslated(Untranslated.PROGRAM_NAME)));
    }
}