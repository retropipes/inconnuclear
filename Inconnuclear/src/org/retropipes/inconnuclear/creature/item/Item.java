/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.creature.item;

import java.io.IOException;
import java.util.Objects;

import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;

public class Item {
    protected static Item readItem(final DataIOReader dr) throws IOException {
	final var itemName = dr.readString();
	if (itemName.equals("null")) {
	    // Abort
	    return null;
	}
	final var buyFor = dr.readInt();
	final var grams = dr.readInt();
	final var power = dr.readInt();
	return new Item(itemName, buyFor, grams, power);
    }

    // Properties
    private final String name;
    private final int buyPrice;
    private final int weight;
    private final int potency;

    protected Item(final Item i) {
	this.name = i.getName();
	this.buyPrice = i.buyPrice;
	this.weight = i.weight;
	this.potency = i.potency;
    }

    // Constructors
    public Item(final String itemName, final int buyFor, final int grams, final int power) {
	this.name = itemName;
	this.buyPrice = buyFor;
	this.weight = grams;
	this.potency = power;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null || this.getClass() != obj.getClass()) {
	    return false;
	}
	final var other = (Item) obj;
	if (this.buyPrice != other.buyPrice || !Objects.equals(this.name, other.name) || this.potency != other.potency
		|| this.weight != other.weight) {
	    return false;
	}
	return true;
    }

    public final int getBuyPrice() {
	return this.buyPrice;
    }

    public String getName() {
	return this.name;
    }

    public final int getPotency() {
	return this.potency;
    }

    public final int getWeight() {
	return this.weight;
    }

    @Override
    public int hashCode() {
	return Objects.hash(this.buyPrice, this.name, this.potency, this.weight);
    }

    @Override
    public String toString() {
	return this.name;
    }

    final void writeItem(final DataIOWriter dw) throws IOException {
	dw.writeString(this.name);
	dw.writeInt(this.buyPrice);
	dw.writeInt(this.weight);
	dw.writeInt(this.potency);
    }
}
