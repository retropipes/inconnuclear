/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.utility;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import org.retropipes.diane.gui.MainContent;
import org.retropipes.diane.gui.MainWindow;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.loader.extmusic.ExternalMusicImporter;
import org.retropipes.inconnuclear.locale.DialogString;
import org.retropipes.inconnuclear.locale.Strings;

public class Importer {
    private static class CloseHandler extends WindowAdapter {
	public CloseHandler() {
	    // Do nothing
	}

	@Override
	public void windowClosing(final WindowEvent we) {
	    Importer.mainWindow.removeTransferHandler();
	    Importer.mainWindow.restoreSaved();
	}
    }

    // Fields
    static MainWindow mainWindow;
    private static MainContent guiPane;
    private static JLabel logoLabel;
    private static boolean inited = false;
    private static TransferHandler handler = new TransferHandler() {
	private static final long serialVersionUID = 233255543L;

	@Override
	public boolean canImport(final TransferHandler.TransferSupport support) {
	    if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
		return false;
	    }
	    final var copySupported = (TransferHandler.COPY & support.getSourceDropActions()) == TransferHandler.COPY;
	    if (!copySupported) {
		return false;
	    }
	    support.setDropAction(TransferHandler.COPY);
	    return true;
	}

	private String getExtension(final File f) {
	    String ext = null;
	    final var s = f.getName();
	    final var i = s.lastIndexOf('.');
	    if (i > 0 && i < s.length() - 1) {
		ext = s.substring(i + 1).toLowerCase();
	    }
	    return ext;
	}

	@Override
	public boolean importData(final TransferHandler.TransferSupport support) {
	    if (!this.canImport(support)) {
		return false;
	    }
	    final var t = support.getTransferable();
	    try {
		final var o = t.getTransferData(DataFlavor.javaFileListFlavor);
		if (o instanceof final List<?> l) {
		    for (final Object o2 : l) {
			if (o2 instanceof final File f) {
			    final var ext = this.getExtension(f);
			    if (ext.equalsIgnoreCase("mod") || ext.equalsIgnoreCase("s3m")
				    || ext.equalsIgnoreCase("xm")) {
				// Import External Music
				var emf = ExternalMusicImporter.importMusic(f);
				Inconnuclear.getStuffBag().getEditor().setMusicFilename(emf);
				CommonDialogs.showDialog("Music successfully imported.");
				Importer.mainWindow.restoreSaved();
			    } else {
				// Unknown file type
				CommonDialogs.showDialog(Strings.dialog(DialogString.IMPORT_FAILED_FILE_TYPE));
			    }
			} else {
			    // Not a file
			    CommonDialogs.showDialog(Strings.dialog(DialogString.IMPORT_FAILED_NON_FILE));
			}
		    }
		}
		return true;
	    } catch (final UnsupportedFlavorException | IOException e) {
		return false;
	    }
	}
    };

    private static void init() {
	Importer.mainWindow = MainWindow.mainWindow();
	Importer.guiPane = MainWindow.createContent();
	Importer.mainWindow.addWindowListener(new CloseHandler());
	Importer.guiPane.setLayout(new GridLayout(1, 1));
	Importer.logoLabel = new JLabel(Strings.dialog(DialogString.IMPORT_INSTRUCTIONS), null, SwingConstants.CENTER);
	Importer.logoLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
	Importer.logoLabel.setPreferredSize(new Dimension(500, 500));
	Importer.guiPane.add(Importer.logoLabel);
	Importer.inited = true;
    }

    public static boolean isImporterVisible() {
	if (!Importer.inited) {
	    Importer.init();
	}
	return Importer.mainWindow.checkContent(Importer.guiPane);
    }

    public static void showImporter() {
	if (!Importer.inited) {
	    Importer.init();
	}
	Importer.mainWindow.setAndSave(Importer.guiPane, Strings.dialog(DialogString.IMPORT_TITLE));
	Importer.mainWindow.setTransferHandler(Importer.handler);
    }
}
