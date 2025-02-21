/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.io.File;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.diane.scoring.SavedScoreManager;
import org.retropipes.diane.scoring.ScoreManager;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.locale.FileExtension;
import org.retropipes.inconnuclear.locale.GameString;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.locale.Untranslated;

class ScoreTracker {
    private static String getScoreDirectory() {
	final var osName = System.getProperty(Strings.untranslated(Untranslated.OS_NAME));
	String base;
	if (osName.indexOf(Strings.untranslated(Untranslated.MACOS)) != -1) {
	    // Mac OS X
	    base = Strings.untranslated(Untranslated.MACOS_SUPPORT);
	} else if (osName.indexOf(Strings.untranslated(Untranslated.WINDOWS)) != -1) {
	    // Windows
	    base = Strings.EMPTY;
	} else {
	    // Other - assume UNIX-like
	    base = Strings.untranslated(Untranslated.UNIX_SUPPORT);
	}
	if (base != Strings.EMPTY) {
	    return base + File.pathSeparator + Strings.untranslated(Untranslated.COMPANY_SUBFOLDER) + File.pathSeparator
		    + Strings.untranslated(Untranslated.PROGRAM_NAME);
	}
	return Strings.untranslated(Untranslated.COMPANY_SUBFOLDER) + File.pathSeparator
		+ Strings.untranslated(Untranslated.PROGRAM_NAME);
    }

    private static String getScoreDirPrefix() {
	final var osName = System.getProperty(Strings.untranslated(Untranslated.OS_NAME));
	if (osName.indexOf(Strings.untranslated(Untranslated.MACOS)) != -1) {
	    // Mac OS X
	    return System.getenv(Strings.untranslated(Untranslated.UNIX_HOME));
	}
	if (osName.indexOf(Strings.untranslated(Untranslated.WINDOWS)) != -1) {
	    // Windows
	    return System.getenv(Strings.untranslated(Untranslated.WINDOWS_SUPPORT));
	}
	// Other - assume UNIX-like
	return System.getenv(Strings.untranslated(Untranslated.UNIX_HOME));
    }

    private static File getScoresFile(final String filename) {
	final var b = new StringBuilder();
	b.append(ScoreTracker.getScoreDirPrefix());
	b.append(ScoreTracker.getScoreDirectory());
	b.append(filename);
	b.append(Strings.UNDERSCORE);
	b.append(Inconnuclear.getStuffBag().getDungeonManager().getDungeonBase().getActiveLevel() + 1);
	b.append(Strings.fileExtension(FileExtension.SCORES));
	return new File(b.toString());
    }

    // Fields
    private SavedScoreManager ssMgr;
    private long moves;
    private long shots;
    private long others;
    private boolean trackScores;

    // Constructors
    public ScoreTracker() {
	this.moves = 0L;
	this.shots = 0L;
	this.others = 0L;
	this.ssMgr = null;
	this.trackScores = true;
    }

    boolean checkScore() {
	if (this.trackScores) {
	    return this.ssMgr.checkScore(new long[] { this.moves, this.shots, this.others });
	}
	return false;
    }

    void commitScore() {
	if (this.trackScores) {
	    final var result = this.ssMgr.addScore(new long[] { this.moves, this.shots, this.others });
	    if (result) {
		this.ssMgr.viewTable();
	    }
	}
    }

    void decrementMoves() {
	this.moves--;
    }

    void decrementOthers() {
	this.others--;
    }

    void decrementShots() {
	this.shots--;
    }

    long getMoves() {
	return this.moves;
    }

    long getOthers() {
	return this.others;
    }

    long getShots() {
	return this.shots;
    }

    void incrementMoves() {
	this.moves++;
    }

    void incrementOthers() {
	this.others++;
    }

    void incrementShots() {
	this.shots++;
    }

    void resetScore() {
	this.moves = 0L;
	this.shots = 0L;
	this.others = 0L;
    }

    void resetScore(final String filename) {
	this.setScoreFile(filename);
	this.moves = 0L;
	this.shots = 0L;
	this.others = 0L;
    }

    void setMoves(final long m) {
	this.moves = m;
    }

    void setOthers(final long o) {
	this.others = o;
    }

    void setScoreFile(final String filename) {
	this.trackScores = true;
	// Check filename argument
	if (filename != null) {
	    if (filename.isEmpty()) {
		this.trackScores = false;
	    }
	} else {
	    this.trackScores = false;
	}
	if (this.trackScores) {
	    // Make sure the needed directories exist first
	    final var sf = ScoreTracker.getScoresFile(filename);
	    final var parent = new File(sf.getParent());
	    if (!parent.exists()) {
		final var success = parent.mkdirs();
		if (!success) {
		    this.trackScores = false;
		}
	    }
	    if (this.trackScores) {
		final var scoresFile = sf.getAbsolutePath();
		this.ssMgr = new SavedScoreManager(3, 10, ScoreManager.SORT_ORDER_DESCENDING, 10000L,
			Strings.untranslated(Untranslated.PROGRAM_NAME) + Strings.SPACE
				+ Strings.game(GameString.SCORES),
			new String[] { Strings.game(GameString.SCORE_MOVES), Strings.game(GameString.SCORE_SHOTS),
				Strings.game(GameString.SCORE_OTHERS) },
			scoresFile);
	    }
	}
    }

    void setShots(final long s) {
	this.shots = s;
    }

    void showScoreTable() {
	if (this.trackScores) {
	    this.ssMgr.viewTable();
	} else {
	    CommonDialogs.showDialog(Strings.game(GameString.SCORES_UNAVAILABLE));
	}
    }
}
