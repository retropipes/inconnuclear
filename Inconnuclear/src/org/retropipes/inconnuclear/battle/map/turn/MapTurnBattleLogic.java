/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map.turn;

import java.io.IOException;

import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.diane.random.RandomRange;
import org.retropipes.inconnuclear.Inconnuclear;
import org.retropipes.inconnuclear.StuffBag;
import org.retropipes.inconnuclear.battle.Battle;
import org.retropipes.inconnuclear.battle.BattleAction;
import org.retropipes.inconnuclear.battle.BattleCharacter;
import org.retropipes.inconnuclear.battle.BattleResult;
import org.retropipes.inconnuclear.battle.ai.AIContext;
import org.retropipes.inconnuclear.battle.ai.map.AutoMapAI;
import org.retropipes.inconnuclear.battle.damage.DamageEngine;
import org.retropipes.inconnuclear.battle.map.MapBattleAITask;
import org.retropipes.inconnuclear.battle.map.MapBattleDefinitions;
import org.retropipes.inconnuclear.battle.reward.BattleRewards;
import org.retropipes.inconnuclear.battle.types.BattleType;
import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.StatConstants;
import org.retropipes.inconnuclear.creature.effect.Effect;
import org.retropipes.inconnuclear.creature.monster.MonsterFactory;
import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.creature.spell.SpellCaster;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.dungeon.gameobject.GameObject;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.loader.music.MusicLoader;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.Music;
import org.retropipes.inconnuclear.settings.Settings;
import org.retropipes.inconnuclear.utility.InvalidDungeonException;

public class MapTurnBattleLogic extends Battle {
    private static final int STEAL_ACTION_POINTS = 3;
    private static final int DRAIN_ACTION_POINTS = 3;
    // Fields
    private BattleType battleType;
    private MapBattleDefinitions bd;
    private DamageEngine pde;
    private DamageEngine ede;
    private final AutoMapAI auto;
    private int damage;
    private BattleResult result;
    private int activeIndex;
    private long battleExp;
    private boolean newRound;
    private int[] speedArray;
    private int lastSpeed;
    private boolean[] speedMarkArray;
    private boolean resultDoneAlready;
    private boolean lastAIActionResult;
    private final MapBattleAITask ait;
    private MapTurnBattleGUI battleGUI;
    private BattleCharacter enemy;

    // Constructors
    public MapTurnBattleLogic() {
	this.battleGUI = new MapTurnBattleGUI();
	this.auto = new AutoMapAI();
	this.ait = new MapBattleAITask(this);
	this.ait.start();
    }

    private boolean areTeamEnemiesAlive(final int teamID) {
	for (var x = 0; x < this.bd.getBattlers().length; x++) {
	    if (this.bd.getBattlers()[x] != null && this.bd.getBattlers()[x].getTeamID() != teamID) {
		final var res = this.bd.getBattlers()[x].getCreature().isAlive();
		if (res) {
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean areTeamEnemiesDeadOrGone(final int teamID) {
	var deadCount = 0;
	for (var x = 0; x < this.bd.getBattlers().length; x++) {
	    if (this.bd.getBattlers()[x] != null && this.bd.getBattlers()[x].getTeamID() != teamID) {
		final var res = this.bd.getBattlers()[x].getCreature().isAlive() && this.bd.getBattlers()[x].isActive();
		if (res) {
		    return false;
		}
		if (!this.bd.getBattlers()[x].getCreature().isAlive()) {
		    deadCount++;
		}
	    }
	}
	return deadCount > 0;
    }

    private boolean areTeamEnemiesGone(final int teamID) {
	var res = true;
	for (var x = 0; x < this.bd.getBattlers().length; x++) {
	    if (this.bd.getBattlers()[x] != null && this.bd.getBattlers()[x].getTeamID() != teamID
		    && this.bd.getBattlers()[x].getCreature().isAlive()) {
		res = res && !this.bd.getBattlers()[x].isActive();
		if (!res) {
		    return false;
		}
	    }
	}
	return true;
    }

    @Override
    public void battleDone() {
	// Leave Battle
	this.hideBattle();
	Inconnuclear.getStuffBag().setMode(StuffBag.STATUS_GAME);
	// Return to whence we came
	Inconnuclear.getStuffBag().getGame().showOutput();
	Inconnuclear.getStuffBag().getGame().redrawDungeon();
    }

    @Override
    public boolean castSpell() {
	// Check Spell Counter
	if (this.getActiveSpellCounter() <= 0) {
	    // Deny cast - out of actions
	    if (!this.bd.getActiveCharacter().hasAI()) {
		this.setStatusMessage("Out of actions!");
	    }
	    return false;
	}
	if (!this.bd.getActiveCharacter().hasAI()) {
	    // Active character has no AI, or AI is turned off
	    final var success = SpellCaster.selectAndCastSpell(this.bd.getActiveCharacter().getCreature(),
		    this.enemy.getCreature());
	    if (success) {
		SoundLoader.playSound(Sounds.PARTY_SPELL);
		this.decrementActiveSpellCounter();
	    }
	    final var currResult = this.getResult();
	    if (currResult != BattleResult.IN_PROGRESS) {
		// Battle Done
		this.result = currResult;
		this.doResult();
	    }
	    return success;
	}
	// Active character has AI, and AI is turned on
	final var sp = this.bd.getActiveCharacter().getAI().getSpellToCast();
	final var success = SpellCaster.castSpell(sp, this.bd.getActiveCharacter().getCreature(),
		this.enemy.getCreature());
	if (success) {
	    SoundLoader.playSound(Sounds.ENEMY_SPELL);
	    this.decrementActiveSpellCounter();
	}
	final var currResult = this.getResult();
	if (currResult != BattleResult.IN_PROGRESS) {
	    // Battle Done
	    this.result = currResult;
	    this.doResult();
	}
	return success;
    }

    private void clearStatusMessage() {
	this.battleGUI.clearStatusMessage();
    }

    private void computeDamage(final Creature theEnemy, final Creature acting, final DamageEngine activeDE) {
	// Compute Damage
	this.damage = 0;
	final var actual = activeDE.computeDamage(theEnemy, acting);
	// Hit or Missed
	this.damage = actual;
	if (activeDE.weaponFumble()) {
	    acting.doDamage(this.damage);
	} else if (this.damage < 0) {
	    acting.doDamage(-this.damage);
	} else {
	    theEnemy.doDamage(this.damage);
	}
	if (acting.getTeamID() == Creature.TEAM_PARTY) {
	    this.displayPartyRoundResults(theEnemy, acting, activeDE);
	} else {
	    this.displayEnemyRoundResults(theEnemy, acting, activeDE);
	}
    }

    private void decrementActiveActionCounterBy(final int amount) {
	this.bd.getActiveCharacter().modifyAP(amount);
    }

    private void decrementActiveAttackCounter() {
	this.bd.getActiveCharacter().modifyAttacks(1);
    }

    private void decrementActiveSpellCounter() {
	this.bd.getActiveCharacter().modifySpells(1);
    }

    @Override
    public void displayActiveEffects() {
	// Do nothing
    }

    @Override
    public void displayBattleStats() {
	// Do nothing
    }

    private void displayPartyRoundResults(final Creature theEnemy, final Creature active, final DamageEngine activeDE) {
	// Display round results
	final var hitSound = active.getItems().getWeaponHitSound(active);
	final var activeName = active.getName();
	final var enemyName = theEnemy.getName();
	var damageString = Integer.toString(this.damage);
	var displayDamageString = " ";
	if (this.damage == 0) {
	    if (activeDE.weaponMissed()) {
		displayDamageString = activeName + " tries to hit " + enemyName + ", but MISSES!";
	    } else if (activeDE.enemyDodged()) {
		displayDamageString = activeName + " tries to hit " + enemyName + ", but " + enemyName
			+ " AVOIDS the attack!";
	    } else {
		displayDamageString = activeName + " tries to hit " + enemyName + ", but the attack is BLOCKED!";
	    }
	    SoundLoader.playSound(Sounds.MISSED);
	} else if (this.damage < 0) {
	    damageString = Integer.toString(-this.damage);
	    var displayDamagePrefix = "";
	    if (activeDE.weaponCrit() && activeDE.weaponPierce()) {
		displayDamagePrefix = "PIERCING CRITICAL HIT! ";
		SoundLoader.playSound(Sounds.PARTY_COUNTER);
		SoundLoader.playSound(Sounds.CRITICAL);
	    } else if (activeDE.weaponCrit()) {
		displayDamagePrefix = "CRITICAL HIT! ";
		SoundLoader.playSound(Sounds.CRITICAL);
	    } else if (activeDE.weaponPierce()) {
		displayDamagePrefix = "PIERCING HIT! ";
		SoundLoader.playSound(Sounds.PARTY_COUNTER);
	    }
	    displayDamageString = displayDamagePrefix + activeName + " tries to hit " + enemyName + ", but " + enemyName
		    + " RIPOSTES for " + damageString + " damage!";
	    SoundLoader.playSound(Sounds.PARTY_COUNTER);
	} else {
	    var displayDamagePrefix = "";
	    if (activeDE.weaponFumble()) {
		SoundLoader.playSound(Sounds.FUMBLE);
		displayDamageString = "FUMBLE! " + activeName + " drops their weapon on themselves, doing "
			+ damageString + " damage!";
	    } else {
		if (activeDE.weaponCrit() && activeDE.weaponPierce()) {
		    displayDamagePrefix = "PIERCING CRITICAL HIT! ";
		    SoundLoader.playSound(Sounds.PARTY_COUNTER);
		    SoundLoader.playSound(Sounds.CRITICAL);
		} else if (activeDE.weaponCrit()) {
		    displayDamagePrefix = "CRITICAL HIT! ";
		    SoundLoader.playSound(Sounds.CRITICAL);
		} else if (activeDE.weaponPierce()) {
		    displayDamagePrefix = "PIERCING HIT! ";
		    SoundLoader.playSound(Sounds.PARTY_COUNTER);
		}
		displayDamageString = displayDamagePrefix + activeName + " hits " + enemyName + " for " + damageString
			+ " damage!";
		SoundLoader.playSound(hitSound);
	    }
	}
	this.setStatusMessage(displayDamageString);
    }

    private void displayEnemyRoundResults(final Creature theEnemy, final Creature active, final DamageEngine activeDE) {
	// Display round results
	final var hitSound = active.getItems().getWeaponHitSound(active);
	final var activeName = active.getName();
	final var enemyName = theEnemy.getName();
	var damageString = Integer.toString(this.damage);
	var displayDamageString = " ";
	if (this.damage == 0) {
	    if (activeDE.weaponMissed()) {
		displayDamageString = activeName + " tries to hit " + enemyName + ", but MISSES!";
	    } else if (activeDE.enemyDodged()) {
		displayDamageString = activeName + " tries to hit " + enemyName + ", but " + enemyName
			+ " AVOIDS the attack!";
	    } else {
		displayDamageString = activeName + " tries to hit " + enemyName + ", but the attack is BLOCKED!";
	    }
	    SoundLoader.playSound(Sounds.MISSED);
	} else if (this.damage < 0) {
	    damageString = Integer.toString(-this.damage);
	    var displayDamagePrefix = "";
	    if (activeDE.weaponCrit() && activeDE.weaponPierce()) {
		displayDamagePrefix = "PIERCING CRITICAL HIT! ";
		SoundLoader.playSound(Sounds.ENEMY_COUNTER);
		SoundLoader.playSound(Sounds.CRITICAL);
	    } else if (activeDE.weaponCrit()) {
		displayDamagePrefix = "CRITICAL HIT! ";
		SoundLoader.playSound(Sounds.CRITICAL);
	    } else if (activeDE.weaponPierce()) {
		displayDamagePrefix = "PIERCING HIT! ";
		SoundLoader.playSound(Sounds.ENEMY_COUNTER);
	    }
	    displayDamageString = displayDamagePrefix + activeName + " tries to hit " + enemyName + ", but " + enemyName
		    + " RIPOSTES for " + damageString + " damage!";
	    SoundLoader.playSound(Sounds.ENEMY_COUNTER);
	} else {
	    var displayDamagePrefix = "";
	    if (activeDE.weaponFumble()) {
		SoundLoader.playSound(Sounds.FUMBLE);
		displayDamageString = "FUMBLE! " + activeName + " drops their weapon on themselves, doing "
			+ damageString + " damage!";
	    } else {
		if (activeDE.weaponCrit() && activeDE.weaponPierce()) {
		    displayDamagePrefix = "PIERCING CRITICAL HIT! ";
		    SoundLoader.playSound(Sounds.ENEMY_COUNTER);
		    SoundLoader.playSound(Sounds.CRITICAL);
		} else if (activeDE.weaponCrit()) {
		    displayDamagePrefix = "CRITICAL HIT! ";
		    SoundLoader.playSound(Sounds.CRITICAL);
		} else if (activeDE.weaponPierce()) {
		    displayDamagePrefix = "PIERCING HIT! ";
		    SoundLoader.playSound(Sounds.ENEMY_COUNTER);
		}
		displayDamageString = displayDamagePrefix + activeName + " hits " + enemyName + " for " + damageString
			+ " damage!";
		SoundLoader.playSound(hitSound);
	    }
	}
	this.setStatusMessage(displayDamageString);
    }

    @Override
    public void doBattle() {
	// Initialize Battle
	DungeonBase bMap = null;
	try {
	    bMap = DungeonBase.getTemporaryBattleCopy();
	} catch (final IOException e) {
	    Inconnuclear.logError(e);
	}
	if (bMap == null) {
	    Inconnuclear.logError(new InvalidDungeonException());
	    return; // This code will never execute
	}
	this.battleType = BattleType.createBattle(bMap.getRows(), bMap.getColumns());
	if (MusicLoader.isMusicPlaying()) {
	    MusicLoader.stopMusic();
	}
	MusicLoader.playMusic(Music.BATTLE);
	this.doBattleInternal(bMap);
    }

    @Override
    public void doBattleByProxy() {
	final var m = MonsterFactory.getNewMonsterInstance(Settings.getGameDifficulty());
	final var playerCharacter = PartyManager.getParty().getLeader();
	playerCharacter.offsetExperience(m.getExperience());
	playerCharacter.offsetGold(m.getGold());
	// Level Up Check
	if (playerCharacter.checkLevelUp()) {
	    playerCharacter.levelUp();
	    Inconnuclear.getStuffBag().getGame().keepNextMessage();
	    Inconnuclear.getStuffBag().showMessage("You reached level " + playerCharacter.getLevel() + ".");
	}
    }

    private void doBattleInternal(final DungeonBase bMap) {
	Inconnuclear.getStuffBag().getGame().hideOutput();
	Inconnuclear.getStuffBag().setMode(StuffBag.STATUS_BATTLE);
	this.bd = new MapBattleDefinitions();
	this.bd.setBattleDungeonBase(bMap);
	this.pde = DamageEngine.getPlayerInstance();
	this.ede = DamageEngine.getEnemyInstance();
	this.resultDoneAlready = false;
	this.result = BattleResult.IN_PROGRESS;
	// Generate Friends
	this.bd.addBattler(new BattleCharacter(PartyManager.getParty().getLeader(), bMap.getRows(), bMap.getColumns()));
	// Generate Enemies
	this.enemy = this.battleType.getBattlers();
	this.enemy.getCreature().healAndRegenerateFully();
	this.enemy.getCreature().loadCreature();
	this.bd.addBattler(this.enemy);
	// Reset Inactive Indicators and Action Counters
	this.bd.resetBattlers();
	// Generate Speed Array
	this.generateSpeedArray();
	// Set Character Locations
	this.setCharacterLocations();
	// Set First Active
	this.newRound = this.setNextActive(true);
	// Clear status message
	this.clearStatusMessage();
	// Start Battle
	this.battleGUI.getViewManager().setViewingWindowCenterX(this.bd.getActiveCharacter().getY());
	this.battleGUI.getViewManager().setViewingWindowCenterY(this.bd.getActiveCharacter().getX());
	SoundLoader.playSound(Sounds.DRAW_SWORD);
	this.showBattle();
	this.updateStatsAndEffects();
	this.redrawBattle();
    }

    @Override
    public void doFinalBossBattle() {
	// Initialize Battle
	DungeonBase bMap = null;
	try {
	    bMap = DungeonBase.getTemporaryBattleCopy();
	} catch (final IOException e) {
	    Inconnuclear.logError(e);
	}
	if (bMap == null) {
	    Inconnuclear.logError(new InvalidDungeonException());
	    return; // This code will never execute
	}
	this.battleType = BattleType.createFinalBossBattle(bMap.getRows(), bMap.getColumns());
	if (MusicLoader.isMusicPlaying()) {
	    MusicLoader.stopMusic();
	}
	MusicLoader.playMusic(Music.BOSS);
	this.doBattleInternal(bMap);
    }

    @Override
    public boolean doPlayerActions(final BattleAction action) {
	switch (action) {
	case BattleAction.CAST_SPELL:
	    this.castSpell();
	    break;
	case BattleAction.DRAIN:
	    this.drain();
	    break;
	case BattleAction.STEAL:
	    this.steal();
	    break;
	default:
	    this.endTurn();
	    break;
	}
	return true;
    }

    @Override
    public void doResult() {
	this.stopWaitingForAI();
	if (!this.resultDoneAlready) {
	    // Handle Results
	    this.resultDoneAlready = true;
	    if (this.result == BattleResult.WON) {
		SoundLoader.playSound(Sounds.VICTORY);
		CommonDialogs.showTitledDialog("The party is victorious!", "Victory!");
	    } else if (this.result == BattleResult.PERFECT) {
		SoundLoader.playSound(Sounds.VICTORY);
		CommonDialogs.showTitledDialog("The party is victorious, and avoided damage!", "Perfect Victory!");
	    } else if (this.result == BattleResult.LOST) {
		CommonDialogs.showTitledDialog("The party has been defeated!", "Defeat!");
	    } else if (this.result == BattleResult.ANNIHILATED) {
		CommonDialogs.showTitledDialog("The party has been defeated without dealing any damage!",
			"Annihilated!");
	    } else if (this.result == BattleResult.DRAW) {
		CommonDialogs.showTitledDialog("The battle was a draw.", "Draw");
	    } else if (this.result == BattleResult.FLED) {
		CommonDialogs.showTitledDialog("The party fled!", "Party Fled");
	    } else if (this.result == BattleResult.ENEMY_FLED) {
		CommonDialogs.showTitledDialog("The enemy fled!", "Enemy Fled");
	    } else {
		CommonDialogs.showTitledDialog("The battle isn't over, but somehow the game thinks it is.", "Uh-Oh!");
	    }
	    // Rewards
	    final var exp = this.battleExp;
	    final var gold = this.getEnemy().getGold();
	    BattleRewards.doRewards(this.battleType, this.result, exp, gold);
	    // Strip effects
	    PartyManager.getParty().getLeader().stripAllEffects();
	    // Level Up Check
	    PartyManager.getParty().checkPartyLevelUp();
	    // Battle Done
	    this.battleDone();
	}
    }

    @Override
    public boolean drain() {
	// Check Action Counter
	if (this.getActiveActionCounter() <= 0) {
	    // Deny drain - out of actions
	    if (!this.bd.getActiveCharacter().hasAI()) {
		this.setStatusMessage("Out of actions!");
	    }
	    return false;
	}
	Creature activeEnemy = null;
	final BattleCharacter enemyBC = this.getEnemyBC();
	if (enemyBC != null) {
	    activeEnemy = enemyBC.getCreature();
	}
	int drainChance;
	var drainAmount = 0;
	this.bd.getActiveCharacter().modifyAP(MapTurnBattleLogic.DRAIN_ACTION_POINTS);
	drainChance = StatConstants.CHANCE_DRAIN;
	if (activeEnemy == null) {
	    // Failed - nobody to drain from
	    this.setStatusMessage(
		    this.bd.getActiveCharacter().getName() + " tries to drain, but nobody is there to drain from!");
	    return false;
	}
	if (drainChance <= 0) {
	    // Failed
	    this.setStatusMessage(this.bd.getActiveCharacter().getName() + " tries to drain, but fails!");
	    return false;
	}
	if (drainChance >= 100) {
	    // Succeeded, unless target has 0 MP
	    final var drained = new RandomRange(0, activeEnemy.getCurrentMP());
	    drainAmount = drained.generate();
	    if (drainAmount == 0) {
		this.setStatusMessage(
			this.bd.getActiveCharacter().getName() + " tries to drain, but no MP is left to drain!");
		return false;
	    }
	    activeEnemy.offsetCurrentMP(-drainAmount);
	    this.bd.getActiveCharacter().getCreature().offsetCurrentMP(drainAmount);
	    this.setStatusMessage(this.bd.getActiveCharacter().getName() + " tries to drain, and successfully drains "
		    + drainAmount + " MP!");
	    return true;
	}
	final var chance = new RandomRange(0, 100);
	final var randomChance = chance.generate();
	if (randomChance > drainChance) {
	    // Failed
	    this.setStatusMessage(this.bd.getActiveCharacter().getName() + " tries to drain, but fails!");
	    return false;
	}
	// Succeeded
	final var drained = new RandomRange(0, activeEnemy.getCurrentMP());
	drainAmount = drained.generate();
	if (drainAmount == 0) {
	    this.setStatusMessage(
		    this.bd.getActiveCharacter().getName() + " tries to drain, but no MP is left to drain!");
	    return false;
	}
	activeEnemy.offsetCurrentMP(-drainAmount);
	this.bd.getActiveCharacter().getCreature().offsetCurrentMP(drainAmount);
	this.setStatusMessage(this.bd.getActiveCharacter().getName() + " tries to drain, and successfully drains "
		+ drainAmount + " MP!");
	return true;
    }

    @Override
    public void endTurn() {
	this.newRound = this.setNextActive(this.newRound);
	if (this.newRound) {
	    this.setStatusMessage("New Round");
	    this.newRound = this.setNextActive(this.newRound);
	    // Check result
	    this.result = this.getResult();
	    if (this.result != BattleResult.IN_PROGRESS) {
		this.doResult();
		return;
	    }
	}
	this.updateStatsAndEffects();
	this.battleGUI.getViewManager().setViewingWindowCenterX(this.bd.getActiveCharacter().getY());
	this.battleGUI.getViewManager().setViewingWindowCenterY(this.bd.getActiveCharacter().getX());
	this.redrawBattle();
    }

    private void executeAutoAI(final BattleCharacter acting) {
	final var index = this.bd.findBattler(acting.getName());
	final var action = this.auto.getNextAction(this.bd.getBattlerAIContexts()[index]);
	switch (action) {
	case BattleAction.MOVE:
	    final var x = this.auto.getMoveX();
	    final var y = this.auto.getMoveY();
	    final var activeTID = this.bd.getActiveCharacter().getTeamID();
	    final var theEnemy = activeTID == Creature.TEAM_PARTY ? this.enemy
		    : this.bd.getBattlers()[this.bd.findFirstBattlerOnTeam(Creature.TEAM_PARTY)];
	    final var activeDE = activeTID == Creature.TEAM_PARTY ? this.ede : this.pde;
	    this.updatePositionInternal(x, y, false, acting, theEnemy, activeDE);
	    break;
	default:
	    break;
	}
    }

    @Override
    public void executeNextAIAction() {
	if (this.bd != null && this.bd.getActiveCharacter() != null
		&& this.bd.getActiveCharacter().getCreature() != null && this.bd.getActiveCharacter().getAI() != null) {
	    final var active = this.bd.getActiveCharacter();
	    if (active.getCreature().isAlive()) {
		final var action = active.getAI().getNextAction(this.bd.getBattlerAIContexts()[this.activeIndex]);
		switch (action) {
		case BattleAction.MOVE:
		    final var x = active.getAI().getMoveX();
		    final var y = active.getAI().getMoveY();
		    this.lastAIActionResult = this.updatePosition(x, y);
		    active.getAI().setLastResult(this.lastAIActionResult);
		    break;
		case BattleAction.CAST_SPELL:
		    this.lastAIActionResult = this.castSpell();
		    active.getAI().setLastResult(this.lastAIActionResult);
		    break;
		case BattleAction.DRAIN:
		    this.lastAIActionResult = this.drain();
		    active.getAI().setLastResult(this.lastAIActionResult);
		    break;
		case BattleAction.STEAL:
		    this.lastAIActionResult = this.steal();
		    active.getAI().setLastResult(this.lastAIActionResult);
		    break;
		default:
		    this.lastAIActionResult = true;
		    this.endTurn();
		    this.stopWaitingForAI();
		    this.ait.aiWait();
		    break;
		}
	    }
	}
    }

    private int findNextSmallestSpeed(final int max) {
	var res = -1;
	var found = 0;
	for (var x = 0; x < this.speedArray.length; x++) {
	    if (!this.speedMarkArray[x] && this.speedArray[x] <= max && this.speedArray[x] > found) {
		res = x;
		found = this.speedArray[x];
	    }
	}
	if (res != -1) {
	    this.speedMarkArray[res] = true;
	}
	return res;
    }

    private void generateSpeedArray() {
	this.speedArray = new int[this.bd.getBattlers().length];
	this.speedMarkArray = new boolean[this.speedArray.length];
	this.resetSpeedArray();
    }

    private int getActiveActionCounter() {
	return this.bd.getActiveCharacter().getActionsLeft();
    }

    private int getActiveAttackCounter() {
	return this.bd.getActiveCharacter().getAttacksLeft();
    }

    private int getActiveSpellCounter() {
	return this.bd.getActiveCharacter().getSpellsLeft();
    }

    @Override
    public Creature getEnemy() {
	return this.enemy.getCreature();
    }

    private BattleCharacter getEnemyBC() {
	return this.bd.getFirstBattlerNotOnTeam(this.bd.getActiveCharacter().getTeamID());
    }

    @Override
    public boolean getLastAIActionResult() {
	return this.lastAIActionResult;
    }

    @Override
    public BattleResult getResult() {
	BattleResult currResult;
	if (this.result != BattleResult.IN_PROGRESS) {
	    return this.result;
	}
	if (this.areTeamEnemiesAlive(Creature.TEAM_PARTY) && !this.isTeamAlive(Creature.TEAM_PARTY)) {
	    currResult = BattleResult.LOST;
	} else if (!this.areTeamEnemiesAlive(Creature.TEAM_PARTY) && this.isTeamAlive(Creature.TEAM_PARTY)) {
	    currResult = BattleResult.WON;
	} else if (!this.areTeamEnemiesAlive(Creature.TEAM_PARTY) && !this.isTeamAlive(Creature.TEAM_PARTY)) {
	    currResult = BattleResult.DRAW;
	} else if (this.isTeamAlive(Creature.TEAM_PARTY) && !this.isTeamGone(Creature.TEAM_PARTY)
		&& this.areTeamEnemiesDeadOrGone(Creature.TEAM_PARTY)) {
	    currResult = BattleResult.WON;
	} else if (!this.isTeamAlive(Creature.TEAM_PARTY) && !this.isTeamGone(Creature.TEAM_PARTY)
		&& !this.areTeamEnemiesDeadOrGone(Creature.TEAM_PARTY)) {
	    currResult = BattleResult.LOST;
	} else if (this.areTeamEnemiesGone(Creature.TEAM_PARTY)) {
	    currResult = BattleResult.ENEMY_FLED;
	} else if (this.isTeamGone(Creature.TEAM_PARTY)) {
	    currResult = BattleResult.FLED;
	} else {
	    currResult = BattleResult.IN_PROGRESS;
	}
	return currResult;
    }

    private void handleDeath(final BattleCharacter activeBC) {
	// Something has died
	SoundLoader.playSound(Sounds.DEATH);
	final var active = activeBC.getCreature();
	// Set dead character to inactive
	activeBC.deactivate();
	// Remove effects from dead character
	active.stripAllEffects();
	// Remove character from battle
	this.bd.getBattleDungeonBase().setCell(new GameObject(ObjectImageId.EMPTY), activeBC.getX(), activeBC.getY(), 0,
		Layer.STATUS.ordinal());
	if (this.bd.getActiveCharacter().getName().equals(activeBC.getName())) {
	    // Active character died, end turn
	    this.endTurn();
	}
    }

    private void hideBattle() {
	this.battleGUI.hideBattle();
    }

    private boolean isTeamAlive(final int teamID) {
	for (var x = 0; x < this.bd.getBattlers().length; x++) {
	    if (this.bd.getBattlers()[x] != null && this.bd.getBattlers()[x].getTeamID() == teamID) {
		final var res = this.bd.getBattlers()[x].getCreature().isAlive();
		if (res) {
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean isTeamGone(final int teamID) {
	var res = true;
	for (var x = 0; x < this.bd.getBattlers().length; x++) {
	    if (this.bd.getBattlers()[x] != null && this.bd.getBattlers()[x].getTeamID() == teamID
		    && this.bd.getBattlers()[x].getCreature().isAlive()) {
		res = res && !this.bd.getBattlers()[x].isActive();
		if (!res) {
		    return false;
		}
	    }
	}
	return true;
    }

    @Override
    public boolean isWaitingForAI() {
	return !this.battleGUI.areEventHandlersOn();
    }

    @Override
    public void maintainEffects(final boolean player) {
	for (var x = 0; x < this.bd.getBattlers().length; x++) {
	    // Maintain Effects
	    final var activeBC = this.bd.getBattlers()[x];
	    if (activeBC != null && activeBC.isActive()) {
		final var active = activeBC.getCreature();
		// Use Effects
		active.useEffects();
		// Display all effect messages
		final var effectMessages = activeBC.getCreature().getAllCurrentEffectMessages();
		final var individualEffectMessages = effectMessages.split("\n");
		for (final String message : individualEffectMessages) {
		    if (!message.equals(Effect.getNullMessage())) {
			this.setStatusMessage(message);
			try {
			    Thread.sleep(Settings.getBattleSpeed());
			} catch (final InterruptedException ie) {
			    // Ignore
			}
		    }
		}
		// Handle low health for party members
		if (active.isAlive() && active.getTeamID() == Creature.TEAM_PARTY
			&& active.getCurrentHP() <= active.getMaximumHP() * 3 / 10) {
		    SoundLoader.playSound(Sounds.LOW_HEALTH);
		}
		// Cull Inactive Effects
		active.cullInactiveEffects();
		// Handle death caused by effects
		if (!active.isAlive()) {
		    if (activeBC.getTeamID() != Creature.TEAM_PARTY) {
			// Update victory spoils
			this.battleExp = activeBC.getCreature().getExperience();
		    }
		    this.handleDeath(activeBC);
		}
	    }
	}
    }

    private void performNewRoundActions() {
	for (var x = 0; x < this.bd.getBattlers().length; x++) {
	    if (this.bd.getBattlers()[x] != null) {
		// Perform New Round Actions
		if (this.bd.getBattlerAIContexts()[x] != null
			&& this.bd.getBattlerAIContexts()[x].getCharacter().hasAI()
			&& this.bd.getBattlers()[x].isActive() && this.bd.getBattlers()[x].getCreature().isAlive()) {
		    this.bd.getBattlerAIContexts()[x].getCharacter().getAI().newRoundHook();
		}
	    }
	}
    }

    private void redrawBattle() {
	this.battleGUI.redrawBattle(this.bd);
    }

    @Override
    public void resetGUI() {
	this.battleGUI = new MapTurnBattleGUI();
    }

    private void resetSpeedArray() {
	for (var x = 0; x < this.speedArray.length; x++) {
	    if (this.bd.getBattlers()[x] != null && this.bd.getBattlers()[x].getCreature().isAlive()) {
		this.speedArray[x] = (int) this.bd.getBattlers()[x].getCreature()
			.getEffectedStat(StatConstants.STAT_AGILITY);
	    } else {
		this.speedArray[x] = Integer.MIN_VALUE;
	    }
	}
	for (var x = 0; x < this.speedMarkArray.length; x++) {
	    if (this.speedArray[x] != Integer.MIN_VALUE) {
		this.speedMarkArray[x] = false;
	    } else {
		this.speedMarkArray[x] = true;
	    }
	}
    }

    private void setCharacterLocations() {
	final var randX = new RandomRange(0, this.bd.getBattleDungeonBase().getRows() - 1);
	final var randY = new RandomRange(0, this.bd.getBattleDungeonBase().getColumns() - 1);
	int rx, ry;
	// Set Character Locations
	for (var x = 0; x < this.bd.getBattlers().length; x++) {
	    if (this.bd.getBattlers()[x] != null && this.bd.getBattlers()[x].isActive()
		    && this.bd.getBattlers()[x].getCreature().getX() == -1
		    && this.bd.getBattlers()[x].getCreature().getY() == -1) {
		rx = randX.generate();
		ry = randY.generate();
		var obj = this.bd.getBattleDungeonBase().getCell(rx, ry, 0, Layer.STATUS.ordinal());
		while (obj.isSolid()) {
		    rx = randX.generate();
		    ry = randY.generate();
		    obj = this.bd.getBattleDungeonBase().getCell(rx, ry, 0, Layer.STATUS.ordinal());
		}
		this.bd.getBattlers()[x].setX(rx);
		this.bd.getBattlers()[x].setY(ry);
		this.bd.getBattleDungeonBase().setCell(this.bd.getBattlers()[x].getTile(), rx, ry, 0,
			Layer.STATUS.ordinal());
	    }
	}
    }

    private boolean setNextActive(final boolean isNewRound) {
	var res = 0;
	if (isNewRound) {
	    res = this.findNextSmallestSpeed(Integer.MAX_VALUE);
	} else {
	    res = this.findNextSmallestSpeed(this.lastSpeed);
	}
	if (res == -1) {
	    // Reset Speed Array
	    this.resetSpeedArray();
	    // Reset Action Counters
	    this.bd.roundResetBattlers();
	    // Maintain effects
	    this.maintainEffects(true);
	    this.updateStatsAndEffects();
	    // Perform new round actions
	    this.performNewRoundActions();
	    // Play new round sound
	    SoundLoader.playSound(Sounds.NEXT_ROUND);
	    // Nobody to act next, set new round flag
	    return true;
	}
	this.lastSpeed = this.speedArray[res];
	this.activeIndex = res;
	this.bd.setActiveCharacter(this.bd.getBattlers()[this.activeIndex]);
	// Check
	if (!this.bd.getActiveCharacter().isActive()) {
	    // Inactive, pick new active character
	    return this.setNextActive(isNewRound);
	}
	// AI Check
	if (this.bd.getActiveCharacter().hasAI()) {
	    // Run AI
	    this.waitForAI();
	    this.ait.aiRun();
	} else {
	    // No AI
	    SoundLoader.playSound(Sounds.PLAYER_UP);
	}
	return false;
    }

    @Override
    public void setResult(final BattleResult resultCode) {
	// Do nothing
    }

    @Override
    public void setStatusMessage(final String msg) {
	this.battleGUI.setStatusMessage(msg);
    }

    private void showBattle() {
	this.battleGUI.showBattle();
    }

    @Override
    public boolean steal() {
	// Check Action Counter
	if (this.getActiveActionCounter() <= 0) {
	    // Deny steal - out of actions
	    if (!this.bd.getActiveCharacter().hasAI()) {
		this.setStatusMessage("Out of actions!");
	    }
	    return false;
	}
	Creature activeEnemy = null;
	final BattleCharacter enemyBC = this.getEnemyBC();
	if (enemyBC != null) {
	    activeEnemy = enemyBC.getCreature();
	}
	int stealChance;
	var stealAmount = 0;
	this.bd.getActiveCharacter().modifyAP(MapTurnBattleLogic.STEAL_ACTION_POINTS);
	stealChance = StatConstants.CHANCE_STEAL;
	if (activeEnemy == null) {
	    // Failed - nobody to steal from
	    this.setStatusMessage(
		    this.bd.getActiveCharacter().getName() + " tries to steal, but nobody is there to steal from!");
	    return false;
	}
	if (stealChance <= 0) {
	    // Failed
	    this.setStatusMessage(this.bd.getActiveCharacter().getName() + " tries to steal, but fails!");
	    return false;
	}
	if (stealChance >= 100) {
	    // Succeeded, unless target has 0 Gold
	    final var stole = new RandomRange(0, activeEnemy.getGold());
	    stealAmount = stole.generate();
	    if (stealAmount == 0) {
		this.setStatusMessage(
			this.bd.getActiveCharacter().getName() + " tries to steal, but no Gold is left to steal!");
		return false;
	    }
	    this.bd.getActiveCharacter().getCreature().offsetGold(stealAmount);
	    this.setStatusMessage(this.bd.getActiveCharacter().getName() + " tries to steal, and successfully steals "
		    + stealAmount + " gold!");
	    return true;
	}
	final var chance = new RandomRange(0, 100);
	final var randomChance = chance.generate();
	if (randomChance > stealChance) {
	    // Failed
	    this.setStatusMessage(this.bd.getActiveCharacter().getName() + " tries to steal, but fails!");
	    return false;
	}
	// Succeeded, unless target has 0 Gold
	final var stole = new RandomRange(0, activeEnemy.getGold());
	stealAmount = stole.generate();
	if (stealAmount == 0) {
	    this.setStatusMessage(
		    this.bd.getActiveCharacter().getName() + " tries to steal, but no Gold is left to steal!");
	    return false;
	}
	this.bd.getActiveCharacter().getCreature().offsetGold(stealAmount);
	this.setStatusMessage(this.bd.getActiveCharacter().getName() + " tries to steal, and successfully steals "
		+ stealAmount + " gold!");
	return true;
    }

    private void stopWaitingForAI() {
	this.battleGUI.turnEventHandlersOn();
    }

    private void updateAllAIContexts() {
	for (var x = 0; x < this.bd.getBattlers().length; x++) {
	    // Update all AI Contexts
	    if (this.bd.getBattlers()[x] != null && this.bd.getBattlerAIContexts()[x] != null) {
		this.bd.getBattlerAIContexts()[x].updateContext(this.bd.getBattleDungeonBase());
	    }
	}
    }

    @Override
    public boolean updatePosition(final int x, final int y) {
	final var activeTID = this.bd.getActiveCharacter().getTeamID();
	var theEnemy = activeTID == Creature.TEAM_PARTY ? this.enemy
		: this.bd.getBattlers()[this.bd.findFirstBattlerOnTeam(Creature.TEAM_PARTY)];
	final var activeDE = activeTID == Creature.TEAM_PARTY ? this.ede : this.pde;
	if (x == 0 && y == 0) {
	    theEnemy = this.bd.getActiveCharacter();
	}
	return this.updatePositionInternal(x, y, true, this.bd.getActiveCharacter(), theEnemy, activeDE);
    }

    private boolean updatePositionInternal(final int x, final int y, final boolean useAP,
	    final BattleCharacter activeBC, final BattleCharacter theEnemy, final DamageEngine activeDE) {
	final var active = activeBC.getCreature();
	this.updateAllAIContexts();
	var px = activeBC.getX();
	var py = activeBC.getY();
	final var m = this.bd.getBattleDungeonBase();
	GameObject next = null;
	GameObject nextGround = null;
	GameObject currGround = null;
	activeBC.saveLocation();
	this.battleGUI.getViewManager().saveViewingWindow();
	try {
	    next = m.getCell(px + x, py + y, 0, Layer.STATUS.ordinal());
	    nextGround = m.getCell(px + x, py + y, 0, Layer.GROUND.ordinal());
	    currGround = m.getCell(px, py, 0, Layer.GROUND.ordinal());
	} catch (final ArrayIndexOutOfBoundsException aioob) {
	    // Ignore
	}
	if (next == null || nextGround == null || currGround == null) {
	    // Confirm Flee
	    if (!activeBC.hasAI()) {
		SoundLoader.playSound(Sounds.QUESTION);
		final var confirm = CommonDialogs.showConfirmDialog("Embrace Cowardice?", "Battle");
		if (confirm != CommonDialogs.YES_OPTION) {
		    this.battleGUI.getViewManager().restoreViewingWindow();
		    activeBC.restoreLocation();
		    return false;
		}
	    }
	    // Flee
	    SoundLoader.playSound(Sounds.RUN);
	    this.battleGUI.getViewManager().restoreViewingWindow();
	    activeBC.restoreLocation();
	    // Set fled character to inactive
	    activeBC.deactivate();
	    // Remove character from battle
	    m.setCell(new GameObject(ObjectImageId.EMPTY), activeBC.getX(), activeBC.getY(), 0, Layer.STATUS.ordinal());
	    // End Turn
	    this.endTurn();
	    this.updateStatsAndEffects();
	    final var currResult = this.getResult();
	    if (currResult != BattleResult.IN_PROGRESS) {
		// Battle Done
		this.result = currResult;
		this.doResult();
	    }
	    this.battleGUI.getViewManager().setViewingWindowCenterX(py);
	    this.battleGUI.getViewManager().setViewingWindowCenterY(px);
	    this.redrawBattle();
	    return true;
	}
	if (!next.isSolid()) {
	    if ((!useAP || this.getActiveActionCounter() < AIContext.getAPCost()) && useAP) {
		// Deny move - out of actions
		if (!this.bd.getActiveCharacter().hasAI()) {
		    this.setStatusMessage("Out of moves!");
		}
		return false;
	    }
	    // Move
	    GameObject obj1 = null;
	    GameObject obj2 = null;
	    GameObject obj3 = null;
	    GameObject obj4 = null;
	    GameObject obj6 = null;
	    GameObject obj7 = null;
	    GameObject obj8 = null;
	    GameObject obj9 = null;
	    try {
		obj1 = m.getCell(px - 1, py - 1, 0, Layer.STATUS.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException aioob) {
		// Ignore
	    }
	    try {
		obj2 = m.getCell(px, py - 1, 0, Layer.STATUS.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException aioob) {
		// Ignore
	    }
	    try {
		obj3 = m.getCell(px + 1, py - 1, 0, Layer.STATUS.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException aioob) {
		// Ignore
	    }
	    try {
		obj4 = m.getCell(px - 1, py, 0, Layer.STATUS.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException aioob) {
		// Ignore
	    }
	    try {
		obj6 = m.getCell(px + 1, py - 1, 0, Layer.STATUS.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException aioob) {
		// Ignore
	    }
	    try {
		obj7 = m.getCell(px - 1, py + 1, 0, Layer.STATUS.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException aioob) {
		// Ignore
	    }
	    try {
		obj8 = m.getCell(px, py + 1, 0, Layer.STATUS.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException aioob) {
		// Ignore
	    }
	    try {
		obj9 = m.getCell(px + 1, py + 1, 0, Layer.STATUS.ordinal());
	    } catch (final ArrayIndexOutOfBoundsException aioob) {
		// Ignore
	    }
	    // Auto-attack check
	    if (obj1 != null && obj1.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		if ((x != -1 || y != 0) && (x != -1 || y != -1) && (x != 0 || y != -1)) {
		    final var bc1 = this.bd.getFirstBattlerOnTeam(obj1.getTeamID());
		    if (bc1 != null && obj1.getTeamID() != active.getTeamID()) {
			this.executeAutoAI(bc1);
		    }
		}
	    }
	    if (obj2 != null && obj2.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		if (y == 1) {
		    final var bc2 = this.bd.getFirstBattlerOnTeam(obj2.getTeamID());
		    if (bc2 != null && obj2.getTeamID() != active.getTeamID()) {
			this.executeAutoAI(bc2);
		    }
		}
	    }
	    if (obj3 != null && obj3.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		if ((x != 0 || y != -1) && (x != 1 || y != -1) && (x != 1 || y != 0)) {
		    final var bc3 = this.bd.getFirstBattlerOnTeam(obj3.getTeamID());
		    if (bc3 != null && obj3.getTeamID() != active.getTeamID()) {
			this.executeAutoAI(bc3);
		    }
		}
	    }
	    if (obj4 != null && obj4.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		if (x == 1) {
		    final var bc4 = this.bd.getFirstBattlerOnTeam(obj4.getTeamID());
		    if (bc4 != null && obj4.getTeamID() != active.getTeamID()) {
			this.executeAutoAI(bc4);
		    }
		}
	    }
	    if (obj6 != null && obj6.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		if (x == -1) {
		    final var bc6 = this.bd.getFirstBattlerOnTeam(obj6.getTeamID());
		    if (bc6 != null && obj6.getTeamID() != active.getTeamID()) {
			this.executeAutoAI(bc6);
		    }
		}
	    }
	    if (obj7 != null && obj7.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		if ((x != -1 || y != 0) && (x != -1 || y != 1) && (x != 0 || y != 1)) {
		    final var bc7 = this.bd.getFirstBattlerOnTeam(obj7.getTeamID());
		    if (bc7 != null && obj7.getTeamID() != active.getTeamID()) {
			this.executeAutoAI(bc7);
		    }
		}
	    }
	    if (obj8 != null && obj8.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		if (y == -1) {
		    final var bc8 = this.bd.getFirstBattlerOnTeam(obj8.getTeamID());
		    if (bc8 != null && obj8.getTeamID() != active.getTeamID()) {
			this.executeAutoAI(bc8);
		    }
		}
	    }
	    if (obj9 != null && obj9.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		if ((x != 0 || y != 1) && (x != 1 || y != 1) && (x != 1 || y != 0)) {
		    final var bc9 = this.bd.getFirstBattlerOnTeam(obj9.getTeamID());
		    if (bc9 != null && obj9.getTeamID() != active.getTeamID()) {
			this.executeAutoAI(bc9);
		    }
		}
	    }
	    m.setCell(activeBC.getTile().getSavedObject(), px, py, 0, Layer.STATUS.ordinal());
	    activeBC.offsetX(x);
	    activeBC.offsetY(y);
	    px += x;
	    py += y;
	    this.battleGUI.getViewManager().offsetViewingWindowLocationX(y);
	    this.battleGUI.getViewManager().offsetViewingWindowLocationY(x);
	    activeBC.getTile().setSavedObject(m.getCell(px, py, 0, Layer.STATUS.ordinal()));
	    m.setCell(activeBC.getTile(), px, py, 0, Layer.STATUS.ordinal());
	    this.decrementActiveActionCounterBy(AIContext.getAPCost());
	    if (activeBC.getTeamID() == Creature.TEAM_PARTY) {
		SoundLoader.playSound(Sounds.STEP_PARTY);
	    } else {
		SoundLoader.playSound(Sounds.STEP_ENEMY);
	    }
	} else if (next.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
	    if ((!useAP || this.getActiveAttackCounter() <= 0) && useAP) {
		// Deny attack - out of actions
		if (!this.bd.getActiveCharacter().hasAI()) {
		    this.setStatusMessage("Out of attacks!");
		}
		return false;
	    }
	    // Attack
	    final var bc = this.bd.getFirstBattlerOnTeam(next.getTeamID());
	    if (bc.getTeamID() == activeBC.getTeamID()) {
		// Attack Friend?
		if (activeBC.hasAI()) {
		    return false;
		}
		final var confirm = CommonDialogs.showConfirmDialog("Attack Friend?", "Battle");
		if (confirm != CommonDialogs.YES_OPTION) {
		    return false;
		}
	    }
	    if (useAP) {
		this.decrementActiveAttackCounter();
	    }
	    // Do damage
	    this.computeDamage(theEnemy.getCreature(), active, activeDE);
	    // Handle low health for party members
	    if (theEnemy.getCreature().isAlive() && theEnemy.getTeamID() == Creature.TEAM_PARTY
		    && theEnemy.getCreature().getCurrentHP() <= theEnemy.getCreature().getMaximumHP() * 3 / 10) {
		SoundLoader.playSound(Sounds.LOW_HEALTH);
	    }
	    // Handle enemy death
	    if (!theEnemy.getCreature().isAlive()) {
		if (theEnemy.getTeamID() != Creature.TEAM_PARTY) {
		    // Update victory spoils
		    this.battleExp = theEnemy.getCreature().getExperience();
		}
		this.handleDeath(bc);
	    }
	    // Handle self death
	    if (!active.isAlive()) {
		this.handleDeath(activeBC);
	    }
	} else {
	    // Move Failed
	    if (!activeBC.hasAI()) {
		this.setStatusMessage("Can't go that way");
	    }
	    return false;
	}
	this.updateStatsAndEffects();
	final var currResult = this.getResult();
	if (currResult != BattleResult.IN_PROGRESS) {
	    // Battle Done
	    this.result = currResult;
	    this.doResult();
	}
	this.battleGUI.getViewManager().setViewingWindowCenterX(py);
	this.battleGUI.getViewManager().setViewingWindowCenterY(px);
	this.redrawBattle();
	return true;
    }

    private void updateStatsAndEffects() {
	this.battleGUI.updateStatsAndEffects(this.bd);
    }

    private void waitForAI() {
	this.battleGUI.turnEventHandlersOff();
    }
}
