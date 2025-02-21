/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.shop;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.diane.gui.dialog.CommonDialogs;
import org.retropipes.inconnuclear.creature.party.PartyManager;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.dungeon.gameobject.ShopType;
import org.retropipes.inconnuclear.loader.music.MusicLoader;
import org.retropipes.inconnuclear.loader.sound.SoundLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.locale.Music;

public class Shop {
    private class ShopDialogGUI {
	public ShopDialogGUI() {
	    // Do nothing
	}

	private boolean shopStage1() {
	    // Stage 1
	    // Play enter shop sound
	    SoundLoader.playSound(Sounds.SHOP);
	    final var playerCharacter = PartyManager.getParty().getLeader();
	    if (Shop.this.type == ShopType.HEALER || Shop.this.type == ShopType.SURGE) {
		Shop.this.choices = new String[10];
		int x;
		for (x = 0; x < Shop.this.choices.length; x++) {
		    Shop.this.choices[x] = Integer.toString((x + 1) * 10) + "%";
		}
		Shop.this.defaultChoice = 9;
	    } else if (Shop.this.type == ShopType.SPELLS) {
		Shop.this.choices = playerCharacter.getSpellBook().getAllSpellsToLearnNames();
		if (Shop.this.choices == null) {
		    Shop.this.choices = new String[1];
		    Shop.this.choices[0] = "No Spells Left To Learn";
		}
	    } else {
		// Invalid shop type
		return false;
	    }
	    return true;
	}

	private boolean shopStage2() {
	    // Stage 2
	    final var playerCharacter = PartyManager.getParty().getLeader();
	    // Check
	    if (Shop.this.type == ShopType.HEALER && playerCharacter.getCurrentHP() == playerCharacter.getMaximumHP()) {
		CommonDialogs.showDialog("You don't need healing.");
		return false;
	    }
	    if (Shop.this.type == ShopType.SURGE && playerCharacter.getCurrentMP() == playerCharacter.getMaximumMP()) {
		CommonDialogs.showDialog("You don't need regeneration.");
		return false;
	    }
	    if (Shop.this.type == ShopType.SPELLS && playerCharacter.getSpellBook()
		    .getSpellsKnownCount() == playerCharacter.getSpellBook().getMaximumSpellsKnownCount()) {
		CommonDialogs.showDialog("There are no more spells to learn.");
		return false;
	    }
	    Shop.this.result = CommonDialogs.showInputDialog("Select", Shop.this.getShopNameFromType(),
		    Shop.this.choices, Shop.this.choices[Shop.this.defaultChoice]);
	    if (Shop.this.result == null || Shop.this.index == -1) {
		return false;
	    }
	    Shop.this.index = 0;
	    if (Shop.this.type != ShopType.SPELLS) {
		for (Shop.this.index = 0; Shop.this.index < Shop.this.choices.length; Shop.this.index++) {
		    if (Shop.this.result.equals(Shop.this.choices[Shop.this.index])) {
			break;
		    }
		}
	    } else {
		Shop.this.index = playerCharacter.getSpellBook().getSpellIDByName(Shop.this.result);
	    }
	    return true;
	}

	private boolean shopStage3() {
	    // Stage 3
	    final var playerCharacter = PartyManager.getParty().getLeader();
	    Shop.this.cost = 0;
	    if (Shop.this.type == ShopType.HEALER) {
		Shop.this.cost = Shop.getHealingCost(playerCharacter.getLevel(), playerCharacter.getCurrentHP(),
			playerCharacter.getMaximumHP());
	    } else if (Shop.this.type == ShopType.SURGE) {
		Shop.this.cost = Shop.getRegenerationCost(playerCharacter.getLevel(), playerCharacter.getCurrentMP(),
			playerCharacter.getMaximumMP());
	    } else if (Shop.this.type == ShopType.SPELLS) {
		Shop.this.cost = Shop.getSpellCost(Shop.this.index);
	    }
	    // Confirm
	    final var stage4Confirm = CommonDialogs.showConfirmDialog(
		    "This will cost " + Shop.this.cost + " Gold. Are you sure?", Shop.this.getShopNameFromType());
	    if (stage4Confirm == CommonDialogs.NO_OPTION || stage4Confirm == CommonDialogs.CLOSED_OPTION) {
		return false;
	    }
	    return true;
	}

	private boolean shopStage4() {
	    // Stage 4
	    final var playerCharacter = PartyManager.getParty().getLeader();
	    if (playerCharacter.getGold() < Shop.this.cost) {
		CommonDialogs.showErrorDialog("Not Enough Gold!", Shop.this.getShopNameFromType());
		return false;
	    }
	    return true;
	}

	private void shopStage5() {
	    // Stage 5
	    final var playerCharacter = PartyManager.getParty().getLeader();
	    // Play transact sound
	    SoundLoader.playSound(Sounds.TRANSACT);
	    if (Shop.this.type == ShopType.HEALER) {
		playerCharacter.offsetGold(-Shop.this.cost);
		playerCharacter.healPercentage((Shop.this.index + 1) * 10);
	    } else if (Shop.this.type == ShopType.SURGE) {
		playerCharacter.offsetGold(-Shop.this.cost);
		playerCharacter.regeneratePercentage((Shop.this.index + 1) * 10);
	    } else if (Shop.this.type == ShopType.SPELLS) {
		playerCharacter.offsetGold(-Shop.this.cost);
		if (Shop.this.index != -1) {
		    playerCharacter.getSpellBook().learnSpellByID(Shop.this.index);
		}
	    }
	}

	public void showShop() {
	    Shop.this.index = 0;
	    Shop.this.defaultChoice = 0;
	    Shop.this.choices = null;
	    Shop.this.result = null;
	    Shop.this.cost = 0;
	    var valid = this.shopStage1();
	    if (valid) {
		valid = this.shopStage2();
	    }
	    if (valid) {
		valid = this.shopStage3();
	    }
	    if (valid) {
		valid = this.shopStage4();
	    }
	    if (valid) {
		this.shopStage5();
	    }
	}
    }

    // Fields
    private static final String[] SHOP_NAMES = { "Weapons", "Armor", "Healer", "Regenerator", "Spells" };
    static final int MAX_ENHANCEMENTS = 9;

    static int getHealingCost(final int x, final int y, final int z) {
	return (int) (Math.log10(x) * (z - y));
    }

    static int getRegenerationCost(final int x, final int y, final int z) {
	final var diff = z - y;
	if (diff == 0) {
	    return 0;
	}
	final var cost = (int) (Math.log(x) / Math.log(2) * diff);
	if (cost < 1) {
	    return 1;
	}
	return cost;
    }

    static int getSpellCost(final int i) {
	if (i == -1) {
	    return 0;
	}
	return 20 * i * i + 20;
    }

    final ShopType type;
    int index;
    int defaultChoice;
    String[] choices;
    String result;
    int cost;
    String[] typeChoices;
    int typeDefault;
    String typeResult;
    int typeIndex;
    BufferedImageIcon[] imageChoices;
    private final ShopDialogGUI defaultUI;

    // Constructors
    public Shop(final ShopType shopType) {
	this.defaultUI = new ShopDialogGUI();
	this.type = shopType;
	this.index = 0;
    }

    String getShopNameFromType() {
	return Shop.SHOP_NAMES[this.type.ordinal()];
    }

    public void showShop() {
	if (MusicLoader.isMusicPlaying()) {
	    MusicLoader.stopMusic();
	}
	MusicLoader.playMusic(Music.SHOP);
	this.defaultUI.showShop();
	MusicLoader.stopMusic();
	final var zoneID = PartyManager.getParty().getZone();
	if (zoneID == DungeonBase.getMaxLevels() - 1) {
	    MusicLoader.playMusic(Music.VOLCANO);
	} else {
	    MusicLoader.playMusic(Music.DUNGEON);
	}
    }
}
