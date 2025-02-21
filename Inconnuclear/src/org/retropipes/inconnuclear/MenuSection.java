/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear;

import javax.swing.JMenu;

public interface MenuSection {
    void attachAccelerators(final Accelerators accel);

    JMenu createCommandsMenu();

    void disableDirtyCommands();

    void disableLoadedCommands();

    void disableModeCommands();

    void enableDirtyCommands();

    void enableLoadedCommands();

    void enableModeCommands();

    void setInitialState();
}
