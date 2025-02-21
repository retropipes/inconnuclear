/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.battle.map.time;

import java.io.IOException;
import java.util.Timer;

import javax.swing.JOptionPane;

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
import org.retropipes.inconnuclear.battle.map.MapBattleDefinitions;
import org.retropipes.inconnuclear.battle.reward.BattleRewards;
import org.retropipes.inconnuclear.battle.types.BattleType;
import org.retropipes.inconnuclear.creature.Creature;
import org.retropipes.inconnuclear.creature.StatConstants;
import org.retropipes.inconnuclear.creature.effect.Effect;
import org.retropipes.inconnuclear.creature.monster.FinalBossMonster;
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

public class MapTimeBattleLogic extends Battle {
    // Fields
    private BattleType battleType;
    private MapBattleDefinitions bd;
    private DungeonBase battleMap;
    private DamageEngine pde;
    private DamageEngine ede;
    private final AutoMapAI auto;
    private int damage;
    private BattleResult result;
    private long battleExp;
    private boolean resultDoneAlready;
    private boolean lastAIActionResult;
    MapTimeBattleGUI battleGUI;
    private BattleCharacter me;
    private BattleCharacter enemy;
    private AIContext myContext;
    private AIContext enemyContext;
    private final Timer battleTimer;

    // Constructors
    public MapTimeBattleLogic() {
	this.battleGUI = new MapTimeBattleGUI();
	this.auto = new AutoMapAI();
	this.battleTimer = new Timer();
	this.battleTimer.schedule(new MapTimeBattlePlayerTask(this), 0, MapTimeBattleSpeed.getSpeed());
	this.battleTimer.schedule(new MapTimeBattleEnemyTask(this), 0, MapTimeBattleSpeed.getSpeed());
    }

    private boolean areTeamEnemiesAlive(final int teamID) {
	if (teamID == Creature.TEAM_PARTY) {
	    return this.enemy.getCreature().isAlive();
	}
	return this.me.getCreature().isAlive();
    }

    private boolean areTeamEnemiesDeadOrGone(final int teamID) {
	if (teamID == Creature.TEAM_PARTY) {
	    var deadCount = 0;
	    if (this.enemy != null) {
		final var res = this.enemy.getCreature().isAlive() && this.enemy.isActive();
		if (res) {
		    return false;
		}
		if (!this.enemy.getCreature().isAlive()) {
		    deadCount++;
		}
	    }
	    return deadCount > 0;
	}
	var deadCount = 0;
	if (this.me != null) {
	    final var res = this.me.getCreature().isAlive() && this.me.isActive();
	    if (res) {
		return false;
	    }
	    if (!this.me.getCreature().isAlive()) {
		deadCount++;
	    }
	}
	return deadCount > 0;
    }

    private boolean areTeamEnemiesGone(final int teamID) {
	if (teamID == Creature.TEAM_PARTY) {
	    var res = true;
	    if ((this.enemy != null) && this.enemy.getCreature().isAlive()) {
		res = res && !this.enemy.isActive();
		if (!res) {
		    return false;
		}
	    }
	    return true;
	}
	var res = true;
	if ((this.me != null) && this.me.getCreature().isAlive()) {
	    res = res && !this.me.isActive();
	    if (!res) {
		return false;
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

    private boolean castEnemySpell() {
	// Active character has AI, and AI is turned on
	final var sp = this.enemy.getAI().getSpellToCast();
	final var success = SpellCaster.castSpell(sp, this.enemy.getCreature(), PartyManager.getParty().getLeader());
	final var currResult = this.getResult();
	if (currResult != BattleResult.IN_PROGRESS) {
	    // Battle Done
	    this.result = currResult;
	    this.doResult();
	}
	return success;
    }

    @Override
    public boolean castSpell() {
	final var success = SpellCaster.selectAndCastSpell(this.me.getCreature(), this.enemy.getCreature());
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
	this.displayRoundResults(theEnemy, acting, activeDE);
    }

    @Override
    public void displayActiveEffects() {
	// Do nothing
    }

    @Override
    public void displayBattleStats() {
	// Do nothing
    }

    private void displayRoundResults(final Creature theEnemy, final Creature active, final DamageEngine activeDE) {
	// Display round results
	final var activeName = active.getName();
	final var enemyName = theEnemy.getName();
	final var isPlayer = active.getTeamID() == Creature.TEAM_PARTY;
	final var counterSound = isPlayer ? Sounds.PARTY_COUNTER : Sounds.ENEMY_COUNTER;
	final var hitSound = isPlayer ? Sounds.ATTACK_PUNCH : Sounds.ENEMY_HIT;
	var damageString = Integer.toString(this.damage);
	var displayDamageString = " ";
	if (this.damage == 0) {
	    if (activeDE.weaponMissed()) {
		displayDamageString = activeName + " tries to hit " + enemyName + ", but MISSES!";
		SoundLoader.playSound(Sounds.MISSED);
	    } else if (activeDE.enemyDodged()) {
		displayDamageString = activeName + " tries to hit " + enemyName + ", but " + enemyName
			+ " AVOIDS the attack!";
		SoundLoader.playSound(Sounds.MISSED);
	    } else {
		displayDamageString = activeName + " tries to hit " + enemyName + ", but the attack is BLOCKED!";
		SoundLoader.playSound(Sounds.MISSED);
	    }
	} else if (this.damage < 0) {
	    damageString = Integer.toString(-this.damage);
	    var displayDamagePrefix = "";
	    if (activeDE.weaponCrit() && activeDE.weaponPierce()) {
		displayDamagePrefix = "PIERCING CRITICAL HIT! ";
		SoundLoader.playSound(counterSound);
		SoundLoader.playSound(Sounds.CRITICAL);
	    } else if (activeDE.weaponCrit()) {
		displayDamagePrefix = "CRITICAL HIT! ";
		SoundLoader.playSound(Sounds.CRITICAL);
	    } else if (activeDE.weaponPierce()) {
		displayDamagePrefix = "PIERCING HIT! ";
		SoundLoader.playSound(counterSound);
	    }
	    displayDamageString = displayDamagePrefix + activeName + " tries to hit " + enemyName + ", but " + enemyName
		    + " RIPOSTES for " + damageString + " damage!";
	    SoundLoader.playSound(counterSound);
	} else {
	    var displayDamagePrefix = "";
	    if (activeDE.weaponFumble()) {
		SoundLoader.playSound(Sounds.FUMBLE);
		displayDamageString = "FUMBLE! " + activeName + " drops their weapon on themselves, doing "
			+ damageString + " damage!";
	    } else {
		if (activeDE.weaponCrit() && activeDE.weaponPierce()) {
		    displayDamagePrefix = "PIERCING CRITICAL HIT! ";
		    SoundLoader.playSound(counterSound);
		    SoundLoader.playSound(Sounds.CRITICAL);
		} else if (activeDE.weaponCrit()) {
		    displayDamagePrefix = "CRITICAL HIT! ";
		    SoundLoader.playSound(Sounds.CRITICAL);
		} else if (activeDE.weaponPierce()) {
		    displayDamagePrefix = "PIERCING HIT! ";
		    SoundLoader.playSound(counterSound);
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
	// Initialize Battle
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
	// Set Action Bars
	this.battleGUI.setMaxPlayerActionBarValue(PartyManager.getParty().getLeader().getActionBarSpeed());
	this.battleGUI.setMaxEnemyActionBarValue(this.enemy.getCreature().getActionBarSpeed());
	// Set Character Locations
	this.setCharacterLocations();
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
	    break;
	}
	this.battleGUI.resetPlayerActionBar();
	return true;
    }

    @Override
    public void doResult() {
	if (!this.resultDoneAlready) {
	    // Handle Results
	    this.resultDoneAlready = true;
	    if (this.getEnemy() instanceof FinalBossMonster) {
		switch (this.result) {
		case BattleResult.WON:
		case BattleResult.PERFECT:
		    this.setStatusMessage("You defeated the Boss!");
		    SoundLoader.playSound(Sounds.VICTORY);
		    break;
		case BattleResult.LOST:
		    this.setStatusMessage("The Boss defeated you...");
		    SoundLoader.playSound(Sounds.GAME_OVER);
		    PartyManager.getParty().getLeader().onDeath(-10);
		    break;
		case BattleResult.ANNIHILATED:
		    this.setStatusMessage("The Boss defeated you without suffering damage... you were annihilated!");
		    SoundLoader.playSound(Sounds.GAME_OVER);
		    PartyManager.getParty().getLeader().onDeath(-20);
		    break;
		case BattleResult.DRAW:
		    this.setStatusMessage("The Boss battle was a draw. You are fully healed!");
		    PartyManager.getParty().getLeader().healPercentage(Creature.FULL_HEAL_PERCENTAGE);
		    PartyManager.getParty().getLeader().regeneratePercentage(Creature.FULL_HEAL_PERCENTAGE);
		    break;
		case BattleResult.FLED:
		    this.setStatusMessage("You ran away successfully!");
		    break;
		case BattleResult.ENEMY_FLED:
		    this.setStatusMessage("The Boss ran away!");
		    break;
		default:
		    break;
		}
	    } else {
		switch (this.result) {
		case BattleResult.WON:
		    SoundLoader.playSound(Sounds.VICTORY);
		    CommonDialogs.showTitledDialog("The party is victorious!", "Victory!");
		    PartyManager.getParty().getLeader().offsetGold(this.getGold());
		    PartyManager.getParty().getLeader().offsetExperience(this.battleExp);
		    break;
		case BattleResult.LOST:
		    CommonDialogs.showTitledDialog("The party has been defeated!", "Defeat...");
		    break;
		case BattleResult.DRAW:
		    CommonDialogs.showTitledDialog("The battle was a draw.", "Draw");
		    break;
		case BattleResult.FLED:
		    CommonDialogs.showTitledDialog("The party fled!", "Party Fled");
		    break;
		case BattleResult.ENEMY_FLED:
		    CommonDialogs.showTitledDialog("The enemies fled!", "Enemies Fled");
		    break;
		case BattleResult.IN_PROGRESS:
		    CommonDialogs.showTitledDialog("The battle isn't over, but somehow the game thinks it is.",
			    "Uh-Oh!");
		    break;
		default:
		    CommonDialogs.showTitledDialog("The result of the battle is unknown!", "Uh-Oh!");
		    break;
		}
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
	Creature activeEnemy = null;
	try {
	    activeEnemy = this.getEnemyBC(this.me).getCreature();
	} catch (final NullPointerException npe) {
	    // Ignore
	}
	int drainChance;
	var drainAmount = 0;
	drainChance = StatConstants.CHANCE_DRAIN;
	if (activeEnemy == null) {
	    // Failed - nobody to drain from
	    this.setStatusMessage(this.me.getName() + " tries to drain, but nobody is there to drain from!");
	    return false;
	}
	if (drainChance <= 0) {
	    // Failed
	    this.setStatusMessage(this.me.getName() + " tries to drain, but fails!");
	    return false;
	} else if (drainChance >= 100) {
	    // Succeeded, unless target has 0 MP
	    final var drained = new RandomRange(0, activeEnemy.getCurrentMP());
	    drainAmount = drained.generate();
	    if (drainAmount == 0) {
		this.setStatusMessage(this.me.getName() + " tries to drain, but no MP is left to drain!");
		return false;
	    }
	    activeEnemy.offsetCurrentMP(-drainAmount);
	    this.me.getCreature().offsetCurrentMP(drainAmount);
	    this.setStatusMessage(
		    this.me.getName() + " tries to drain, and successfully drains " + drainAmount + " MP!");
	    return true;
	} else {
	    final var chance = new RandomRange(0, 100);
	    final var randomChance = chance.generate();
	    if (randomChance <= drainChance) {
		// Succeeded
		final var drained = new RandomRange(0, activeEnemy.getCurrentMP());
		drainAmount = drained.generate();
		if (drainAmount == 0) {
		    this.setStatusMessage(this.me.getName() + " tries to drain, but no MP is left to drain!");
		    return false;
		}
		activeEnemy.offsetCurrentMP(-drainAmount);
		this.me.getCreature().offsetCurrentMP(drainAmount);
		this.setStatusMessage(
			this.me.getName() + " tries to drain, and successfully drains " + drainAmount + " MP!");
		return true;
	    }
	    // Failed
	    this.setStatusMessage(this.me.getName() + " tries to drain, but fails!");
	    return false;
	}
    }

    @Override
    public void endTurn() {
	// Do nothing
    }

    private boolean enemyDrain() {
	Creature activeEnemy = null;
	try {
	    activeEnemy = this.getEnemyBC(this.enemy).getCreature();
	} catch (final NullPointerException npe) {
	    // Ignore
	}
	int drainChance;
	var drainAmount = 0;
	drainChance = StatConstants.CHANCE_DRAIN;
	if (activeEnemy == null) {
	    // Failed - nobody to drain from
	    this.setStatusMessage(this.enemy.getName() + " tries to drain, but nobody is there to drain from!");
	    return false;
	}
	if (drainChance <= 0) {
	    // Failed
	    this.setStatusMessage(this.enemy.getName() + " tries to drain, but fails!");
	    return false;
	} else if (drainChance >= 100) {
	    // Succeeded, unless target has 0 MP
	    final var drained = new RandomRange(0, activeEnemy.getCurrentMP());
	    drainAmount = drained.generate();
	    if (drainAmount == 0) {
		this.setStatusMessage(this.enemy.getName() + " tries to drain, but no MP is left to drain!");
		return false;
	    }
	    activeEnemy.offsetCurrentMP(-drainAmount);
	    this.enemy.getCreature().offsetCurrentMP(drainAmount);
	    this.setStatusMessage(
		    this.enemy.getName() + " tries to drain, and successfully drains " + drainAmount + " MP!");
	    return true;
	} else {
	    final var chance = new RandomRange(0, 100);
	    final var randomChance = chance.generate();
	    if (randomChance <= drainChance) {
		// Succeeded
		final var drained = new RandomRange(0, activeEnemy.getCurrentMP());
		drainAmount = drained.generate();
		if (drainAmount == 0) {
		    this.setStatusMessage(this.enemy.getName() + " tries to drain, but no MP is left to drain!");
		    return false;
		}
		activeEnemy.offsetCurrentMP(-drainAmount);
		this.enemy.getCreature().offsetCurrentMP(drainAmount);
		this.setStatusMessage(
			this.enemy.getName() + " tries to drain, and successfully drains " + drainAmount + " MP!");
		return true;
	    }
	    // Failed
	    this.setStatusMessage(this.enemy.getName() + " tries to drain, but fails!");
	    return false;
	}
    }

    private boolean enemySteal() {
	Creature activeEnemy = null;
	try {
	    activeEnemy = this.getEnemyBC(this.enemy).getCreature();
	} catch (final NullPointerException npe) {
	    // Ignore
	}
	int stealChance;
	var stealAmount = 0;
	stealChance = StatConstants.CHANCE_STEAL;
	if (activeEnemy == null) {
	    // Failed - nobody to steal from
	    this.setStatusMessage(this.enemy.getName() + " tries to steal, but nobody is there to steal from!");
	    return false;
	}
	if (stealChance <= 0) {
	    // Failed
	    this.setStatusMessage(this.enemy.getName() + " tries to steal, but fails!");
	    return false;
	} else if (stealChance >= 100) {
	    // Succeeded, unless target has 0 Gold
	    final var stole = new RandomRange(0, activeEnemy.getGold());
	    stealAmount = stole.generate();
	    if (stealAmount == 0) {
		this.setStatusMessage(this.enemy.getName() + " tries to steal, but no Gold is left to steal!");
		return false;
	    }
	    this.enemy.getCreature().offsetGold(stealAmount);
	    this.setStatusMessage(
		    this.enemy.getName() + " tries to steal, and successfully steals " + stealAmount + " gold!");
	    return true;
	} else {
	    final var chance = new RandomRange(0, 100);
	    final var randomChance = chance.generate();
	    if (randomChance <= stealChance) {
		// Succeeded, unless target has 0 Gold
		final var stole = new RandomRange(0, activeEnemy.getGold());
		stealAmount = stole.generate();
		if (stealAmount == 0) {
		    this.setStatusMessage(this.enemy.getName() + " tries to steal, but no Gold is left to steal!");
		    return false;
		}
		this.enemy.getCreature().offsetGold(stealAmount);
		this.setStatusMessage(
			this.enemy.getName() + " tries to steal, and successfully steals " + stealAmount + " gold!");
		return true;
	    }
	    // Failed
	    this.setStatusMessage(this.enemy.getName() + " tries to steal, but fails!");
	    return false;
	}
    }

    private void executeAutoAI(final BattleCharacter acting, final BattleCharacter theEnemy,
	    final AIContext theContext) {
	final var action = this.auto.getNextAction(theContext);
	switch (action) {
	case BattleAction.MOVE:
	    final var x = this.auto.getMoveX();
	    final var y = this.auto.getMoveY();
	    final var activeTID = acting.getTeamID();
	    final var activeDE = activeTID == Creature.TEAM_PARTY ? this.ede : this.pde;
	    this.updatePositionInternal(x, y, acting, theEnemy, activeDE, theContext, false);
	    break;
	default:
	    break;
	}
    }

    @Override
    public void executeNextAIAction() {
	if (this.enemy != null && this.enemy.getCreature() != null && this.enemy.getAI() != null) {
	    final var active = this.enemy;
	    if (active.getCreature().isAlive()) {
		final var action = active.getAI().getNextAction(this.enemyContext);
		switch (action) {
		case BattleAction.MOVE:
		    final var x = active.getAI().getMoveX();
		    final var y = active.getAI().getMoveY();
		    this.lastAIActionResult = this.updatePositionInternal(x, y, this.enemy, this.me, this.ede,
			    this.enemyContext, false);
		    active.getAI().setLastResult(this.lastAIActionResult);
		    break;
		case BattleAction.CAST_SPELL:
		    this.lastAIActionResult = this.castEnemySpell();
		    active.getAI().setLastResult(this.lastAIActionResult);
		    break;
		case BattleAction.DRAIN:
		    this.lastAIActionResult = this.enemyDrain();
		    active.getAI().setLastResult(this.lastAIActionResult);
		    break;
		case BattleAction.STEAL:
		    this.lastAIActionResult = this.enemySteal();
		    active.getAI().setLastResult(this.lastAIActionResult);
		    break;
		default:
		    this.lastAIActionResult = true;
		    break;
		}
	    }
	}
    }

    @Override
    public Creature getEnemy() {
	return this.enemy.getCreature();
    }

    private BattleCharacter getEnemyBC(final BattleCharacter acting) {
	return this.bd.getFirstBattlerOnTeam(acting.getTeamID());
    }

    private int getGold() {
	return this.enemy.getCreature().getGold();
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

    private void hideBattle() {
	this.battleGUI.hideBattle();
    }

    private boolean isTeamAlive(final int teamID) {
	if (teamID == Creature.TEAM_PARTY) {
	    return this.me.getCreature().isAlive();
	}
	return this.enemy.getCreature().isAlive();
    }

    private boolean isTeamGone(final int teamID) {
	if (teamID == Creature.TEAM_PARTY) {
	    var res = true;
	    if ((this.me != null) && this.me.getCreature().isAlive()) {
		res = res && !this.me.isActive();
		if (!res) {
		    return false;
		}
	    }
	    return true;
	}
	var res = true;
	if ((this.enemy != null) && this.enemy.getCreature().isAlive()) {
	    res = res && !this.enemy.isActive();
	    if (!res) {
		return false;
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
	if (player) {
	    if (this.me != null && this.me.isActive()) {
		final var active = this.me.getCreature();
		// Use Effects
		active.useEffects();
		// Display all effect messages
		final var effectMessages = this.me.getCreature().getAllCurrentEffectMessages();
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
		    if (this.me.getTeamID() != Creature.TEAM_PARTY) {
			// Update victory spoils
			this.battleExp = this.me.getCreature().getExperience();
		    }
		    // Set dead character to inactive
		    this.me.deactivate();
		    // Remove effects from dead character
		    active.stripAllEffects();
		    // Remove character from battle
		    this.battleMap.setCell(new GameObject(ObjectImageId.EMPTY), this.me.getX(), this.me.getY(), 0,
			    Layer.OBJECT.ordinal());
		}
	    }
	} else if (this.enemy != null && this.enemy.isActive()) {
	    final var active = this.enemy.getCreature();
	    // Use Effects
	    active.useEffects();
	    // Display all effect messages
	    final var effectMessages = this.enemy.getCreature().getAllCurrentEffectMessages();
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
		if (this.enemy.getTeamID() != Creature.TEAM_PARTY) {
		    // Update victory spoils
		    this.battleExp = this.enemy.getCreature().getExperience();
		}
		// Set dead character to inactive
		this.enemy.deactivate();
		// Remove effects from dead character
		active.stripAllEffects();
		// Remove character from battle
		this.battleMap.setCell(new GameObject(ObjectImageId.EMPTY), this.enemy.getX(), this.enemy.getY(), 0,
			Layer.OBJECT.ordinal());
	    }
	}
    }

    private void redrawBattle() {
	this.battleGUI.redrawBattle(this.battleMap);
    }

    @Override
    public void resetGUI() {
	// Destroy old GUI
	this.battleGUI.getOutputFrame().dispose();
	// Create new GUI
	this.battleGUI = new MapTimeBattleGUI();
    }

    private void setCharacterLocations() {
	final var randX = new RandomRange(0, this.battleMap.getRows() - 1);
	final var randY = new RandomRange(0, this.battleMap.getColumns() - 1);
	int rx, ry;
	// Set Player Location
	if ((this.me != null)
		&& (this.me.isActive() && this.me.getCreature().getX() == -1 && this.me.getCreature().getY() == -1)) {
	    rx = randX.generate();
	    ry = randY.generate();
	    var obj = this.battleMap.getCell(rx, ry, 0, Layer.OBJECT.ordinal());
	    while (obj.isSolid()) {
		rx = randX.generate();
		ry = randY.generate();
		obj = this.battleMap.getCell(rx, ry, 0, Layer.OBJECT.ordinal());
	    }
	    this.me.setX(rx);
	    this.me.setY(ry);
	    this.battleMap.setCell(this.me.getTile(), rx, ry, 0, Layer.OBJECT.ordinal());
	}
	// Set Enemy Location
	if ((this.enemy != null) && (this.enemy.isActive() && this.enemy.getCreature().getX() == -1
		&& this.enemy.getCreature().getY() == -1)) {
	    rx = randX.generate();
	    ry = randY.generate();
	    var obj = this.battleMap.getCell(rx, ry, 0, Layer.OBJECT.ordinal());
	    while (obj.isSolid()) {
		rx = randX.generate();
		ry = randY.generate();
		obj = this.battleMap.getCell(rx, ry, 0, Layer.OBJECT.ordinal());
	    }
	    this.enemy.setX(rx);
	    this.enemy.setY(ry);
	    this.battleMap.setCell(this.enemy.getTile(), rx, ry, 0, Layer.OBJECT.ordinal());
	}
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
	Creature activeEnemy = null;
	try {
	    activeEnemy = this.getEnemyBC(this.me).getCreature();
	} catch (final NullPointerException npe) {
	    // Ignore
	}
	int stealChance;
	var stealAmount = 0;
	stealChance = StatConstants.CHANCE_STEAL;
	if (activeEnemy == null) {
	    // Failed - nobody to steal from
	    this.setStatusMessage(this.me.getName() + " tries to steal, but nobody is there to steal from!");
	    return false;
	}
	if (stealChance <= 0) {
	    // Failed
	    this.setStatusMessage(this.me.getName() + " tries to steal, but fails!");
	    return false;
	} else if (stealChance >= 100) {
	    // Succeeded, unless target has 0 Gold
	    final var stole = new RandomRange(0, activeEnemy.getGold());
	    stealAmount = stole.generate();
	    if (stealAmount == 0) {
		this.setStatusMessage(this.me.getName() + " tries to steal, but no Gold is left to steal!");
		return false;
	    }
	    this.me.getCreature().offsetGold(stealAmount);
	    this.setStatusMessage(
		    this.me.getName() + " tries to steal, and successfully steals " + stealAmount + " gold!");
	    return true;
	} else {
	    final var chance = new RandomRange(0, 100);
	    final var randomChance = chance.generate();
	    if (randomChance <= stealChance) {
		// Succeeded, unless target has 0 Gold
		final var stole = new RandomRange(0, activeEnemy.getGold());
		stealAmount = stole.generate();
		if (stealAmount == 0) {
		    this.setStatusMessage(this.me.getName() + " tries to steal, but no Gold is left to steal!");
		    return false;
		}
		this.me.getCreature().offsetGold(stealAmount);
		this.setStatusMessage(
			this.me.getName() + " tries to steal, and successfully steals " + stealAmount + " gold!");
		return true;
	    }
	    // Failed
	    this.setStatusMessage(this.me.getName() + " tries to steal, but fails!");
	    return false;
	}
    }

    private void updateAllAIContexts() {
	this.myContext.updateContext(this.battleMap);
	this.enemyContext.updateContext(this.battleMap);
    }

    @Override
    public boolean updatePosition(final int x, final int y) {
	var theEnemy = this.enemy;
	final var activeDE = this.pde;
	if (x == 0 && y == 0) {
	    theEnemy = this.me;
	}
	return this.updatePositionInternal(x, y, this.me, theEnemy, activeDE, this.myContext, true);
    }

    private boolean updatePositionInternal(final int x, final int y, final BattleCharacter active,
	    final BattleCharacter theEnemy, final DamageEngine activeDE, final AIContext activeContext,
	    final boolean updateView) {
	final var isPlayer = active.getTeamID() == Creature.TEAM_PARTY;
	final var stepSound = isPlayer ? Sounds.STEP_PARTY : Sounds.STEP_ENEMY;
	this.updateAllAIContexts();
	var px = active.getX();
	var py = active.getY();
	final var m = this.battleMap;
	GameObject next = null;
	GameObject nextGround = null;
	GameObject currGround = null;
	active.saveLocation();
	if (updateView) {
	    this.battleGUI.getViewManager().saveViewingWindow();
	}
	try {
	    next = m.getCell(px + x, py + y, 0, Layer.OBJECT.ordinal());
	    nextGround = m.getCell(px + x, py + y, 0, Layer.GROUND.ordinal());
	    currGround = m.getCell(px, py, 0, Layer.GROUND.ordinal());
	} catch (final ArrayIndexOutOfBoundsException aioob) {
	    // Ignore
	}
	if (next != null && nextGround != null && currGround != null) {
	    if (!next.isSolid()) {
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
		    obj1 = m.getCell(px - 1, py - 1, 0, Layer.OBJECT.ordinal());
		} catch (final ArrayIndexOutOfBoundsException aioob) {
		    // Ignore
		}
		try {
		    obj2 = m.getCell(px, py - 1, 0, Layer.OBJECT.ordinal());
		} catch (final ArrayIndexOutOfBoundsException aioob) {
		    // Ignore
		}
		try {
		    obj3 = m.getCell(px + 1, py - 1, 0, Layer.OBJECT.ordinal());
		} catch (final ArrayIndexOutOfBoundsException aioob) {
		    // Ignore
		}
		try {
		    obj4 = m.getCell(px - 1, py, 0, Layer.OBJECT.ordinal());
		} catch (final ArrayIndexOutOfBoundsException aioob) {
		    // Ignore
		}
		try {
		    obj6 = m.getCell(px + 1, py - 1, 0, Layer.OBJECT.ordinal());
		} catch (final ArrayIndexOutOfBoundsException aioob) {
		    // Ignore
		}
		try {
		    obj7 = m.getCell(px - 1, py + 1, 0, Layer.OBJECT.ordinal());
		} catch (final ArrayIndexOutOfBoundsException aioob) {
		    // Ignore
		}
		try {
		    obj8 = m.getCell(px, py + 1, 0, Layer.OBJECT.ordinal());
		} catch (final ArrayIndexOutOfBoundsException aioob) {
		    // Ignore
		}
		try {
		    obj9 = m.getCell(px + 1, py + 1, 0, Layer.OBJECT.ordinal());
		} catch (final ArrayIndexOutOfBoundsException aioob) {
		    // Ignore
		}
		// Auto-attack check
		if (obj1 != null && obj1.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		    if ((x != -1 || y != 0) && (x != -1 || y != -1) && (x != 0 || y != -1)) {
			final var bc1 = this.bd.getFirstBattlerOnTeam(obj1.getTeamID());
			if (bc1 != null && obj1.getTeamID() != active.getTeamID()) {
			    this.executeAutoAI(bc1, active, activeContext);
			}
		    }
		}
		if (obj2 != null && obj2.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		    if (y == 1) {
			final var bc2 = this.bd.getFirstBattlerOnTeam(obj2.getTeamID());
			if (bc2 != null && obj2.getTeamID() != active.getTeamID()) {
			    this.executeAutoAI(bc2, active, activeContext);
			}
		    }
		}
		if (obj3 != null && obj3.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		    if ((x != 0 || y != -1) && (x != 1 || y != -1) && (x != 1 || y != 0)) {
			final var bc3 = this.bd.getFirstBattlerOnTeam(obj3.getTeamID());
			if (bc3 != null && obj3.getTeamID() != active.getTeamID()) {
			    this.executeAutoAI(bc3, active, activeContext);
			}
		    }
		}
		if (obj4 != null && obj4.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		    if (x == 1) {
			final var bc4 = this.bd.getFirstBattlerOnTeam(obj4.getTeamID());
			if (bc4 != null && obj4.getTeamID() != active.getTeamID()) {
			    this.executeAutoAI(bc4, active, activeContext);
			}
		    }
		}
		if (obj6 != null && obj6.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		    if (x == -1) {
			final var bc6 = this.bd.getFirstBattlerOnTeam(obj6.getTeamID());
			if (bc6 != null && obj6.getTeamID() != active.getTeamID()) {
			    this.executeAutoAI(bc6, active, activeContext);
			}
		    }
		}
		if (obj7 != null && obj7.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		    if ((x != -1 || y != 0) && (x != -1 || y != 1) && (x != 0 || y != 1)) {
			final var bc7 = this.bd.getFirstBattlerOnTeam(obj7.getTeamID());
			if (bc7 != null && obj7.getTeamID() != active.getTeamID()) {
			    this.executeAutoAI(bc7, active, activeContext);
			}
		    }
		}
		if (obj8 != null && obj8.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		    if (y == -1) {
			final var bc8 = this.bd.getFirstBattlerOnTeam(obj8.getTeamID());
			if (bc8 != null && obj8.getTeamID() != active.getTeamID()) {
			    this.executeAutoAI(bc8, active, activeContext);
			}
		    }
		}
		if (obj9 != null && obj9.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		    if ((x != 0 || y != 1) && (x != 1 || y != 1) && (x != 1 || y != 0)) {
			final var bc9 = this.bd.getFirstBattlerOnTeam(obj9.getTeamID());
			if (bc9 != null && obj9.getTeamID() != active.getTeamID()) {
			    this.executeAutoAI(bc9, active, activeContext);
			}
		    }
		}
		m.setCell(active.getTile().getSavedObject(), px, py, 0, Layer.OBJECT.ordinal());
		active.offsetX(x);
		active.offsetY(y);
		px += x;
		py += y;
		if (updateView) {
		    this.battleGUI.getViewManager().offsetViewingWindowLocationX(y);
		    this.battleGUI.getViewManager().offsetViewingWindowLocationY(x);
		}
		active.getTile().setSavedObject(m.getCell(px, py, 0, Layer.OBJECT.ordinal()));
		m.setCell(active.getTile(), px, py, 0, Layer.OBJECT.ordinal());
		SoundLoader.playSound(stepSound);
	    } else if (next.getIdValue() == ObjectImageId._CREATURE.ordinal()) {
		// Attack
		final var bc = this.bd.getFirstBattlerOnTeam(next.getTeamID());
		if (next.getTeamID() == active.getTeamID()) {
		    // Attack Friend?
		    if (!active.hasAI()) {
			final var confirm = CommonDialogs.showConfirmDialog("Attack Friend?", "Battle");
			if (confirm != JOptionPane.YES_OPTION) {
			    return false;
			}
		    } else {
			return false;
		    }
		}
		// Do damage
		this.computeDamage(theEnemy.getCreature(), active.getCreature(), activeDE);
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
		    // Remove effects from dead character
		    bc.getCreature().stripAllEffects();
		    // Set dead character to inactive
		    bc.deactivate();
		    // Remove character from battle
		    m.setCell(new GameObject(ObjectImageId.EMPTY), bc.getX(), bc.getY(), 0, Layer.OBJECT.ordinal());
		}
		// Handle self death
		if (!active.getCreature().isAlive()) {
		    // Remove effects from dead character
		    active.getCreature().stripAllEffects();
		    // Set dead character to inactive
		    active.deactivate();
		    // Remove character from battle
		    m.setCell(new GameObject(ObjectImageId.EMPTY), active.getX(), active.getY(), 0,
			    Layer.OBJECT.ordinal());
		}
	    } else {
		// Move Failed
		if (!active.hasAI()) {
		    this.setStatusMessage("Can't go that way");
		}
		return false;
	    }
	} else {
	    // Confirm Flee
	    if (!active.hasAI()) {
		SoundLoader.playSound(Sounds.RUN);
		final var confirm = CommonDialogs.showConfirmDialog("Embrace Cowardice?", "Battle");
		if (confirm != JOptionPane.YES_OPTION) {
		    if (updateView) {
			this.battleGUI.getViewManager().restoreViewingWindow();
		    }
		    active.restoreLocation();
		    return false;
		}
	    }
	    // Flee
	    if (updateView) {
		this.battleGUI.getViewManager().restoreViewingWindow();
	    }
	    active.restoreLocation();
	    // Set fled character to inactive
	    active.deactivate();
	    // Remove character from battle
	    m.setCell(new GameObject(ObjectImageId.EMPTY), active.getX(), active.getY(), 0, Layer.OBJECT.ordinal());
	    // End Turn
	    this.endTurn();
	    this.updateStatsAndEffects();
	    final var currResult = this.getResult();
	    if (currResult != BattleResult.IN_PROGRESS) {
		// Battle Done
		this.result = currResult;
		this.doResult();
	    }
	    if (updateView) {
		this.battleGUI.getViewManager().setViewingWindowCenterX(py);
		this.battleGUI.getViewManager().setViewingWindowCenterY(px);
	    }
	    this.redrawBattle();
	    return true;
	}
	this.updateStatsAndEffects();
	final var currResult = this.getResult();
	if (currResult != BattleResult.IN_PROGRESS) {
	    // Battle Done
	    this.result = currResult;
	    this.doResult();
	}
	if (updateView) {
	    this.battleGUI.getViewManager().setViewingWindowCenterX(py);
	    this.battleGUI.getViewManager().setViewingWindowCenterY(px);
	}
	this.redrawBattle();
	return true;
    }

    private void updateStatsAndEffects() {
	this.battleGUI.updateStatsAndEffects(this.me);
    }
}
