/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.game;

import java.io.IOException;

import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;

class GameReplayEngine {
    // Inner classes
    private static class Link {
	public static Link read(final DataIOReader reader) throws IOException {
	    final var l = reader.readBoolean();
	    final var x = reader.readInt();
	    final var y = reader.readInt();
	    final var hasNextLink = reader.readBoolean();
	    final var link = new Link(l, x, y);
	    link.hasNext = hasNextLink;
	    return link;
	}

	// Fields
	public boolean laser;
	public int coordX, coordY;
	public Link next;
	public boolean hasNext;

	Link(final boolean l, final int x, final int y) {
	    this.laser = l;
	    this.coordX = x;
	    this.coordY = y;
	    this.next = null;
	}

	public void write(final DataIOWriter writer) throws IOException {
	    writer.writeBoolean(this.laser);
	    writer.writeInt(this.coordX);
	    writer.writeInt(this.coordY);
	    writer.writeBoolean(this.next != null);
	}
    }

    private static class LinkList {
	public static LinkList read(final DataIOReader reader) throws IOException {
	    final var hasData = reader.readBoolean();
	    final var ll = new LinkList();
	    if (hasData) {
		var curr = Link.read(reader);
		Link prev;
		ll.insertNext(null, curr);
		while (curr.hasNext) {
		    prev = curr;
		    curr = Link.read(reader);
		    ll.insertNext(prev, curr);
		}
	    }
	    return ll;
	}

	// Fields
	private Link first;

	LinkList() {
	    this.first = null;
	}

	public Link deleteFirst() {
	    final var temp = this.first;
	    this.first = this.first.next;
	    return temp;
	}

	public void insertFirst(final boolean l, final int x, final int y) {
	    final var newLink = new Link(l, x, y);
	    newLink.next = this.first;
	    this.first = newLink;
	}

	private void insertNext(final Link currLink, final Link newLink) {
	    if (currLink == null) {
		this.first = newLink;
	    } else {
		currLink.next = newLink;
	    }
	}

	public boolean isEmpty() {
	    return this.first == null;
	}

	private void reverse() {
	    var current = this.first;
	    this.first = null;
	    while (current != null) {
		final var save = current;
		current = current.next;
		save.next = this.first;
		this.first = save;
	    }
	}

	public void write(final DataIOWriter writer) throws IOException {
	    this.reverse();
	    if (this.isEmpty()) {
		writer.writeBoolean(false);
	    } else {
		writer.writeBoolean(true);
		var node = this.first;
		while (node != null) {
		    node.write(writer);
		    node = node.next;
		}
	    }
	}
    }

    private static class LinkStack {
	public static LinkStack read(final DataIOReader reader) throws IOException {
	    final var ls = new LinkStack();
	    ls.theList = LinkList.read(reader);
	    return ls;
	}

	// Fields
	private LinkList theList;

	LinkStack() {
	    this.theList = new LinkList();
	}

	public boolean isEmpty() {
	    return this.theList.isEmpty();
	}

	public Link pop() {
	    return this.theList.deleteFirst();
	}

	public void push(final boolean l, final int x, final int y) {
	    this.theList.insertFirst(l, x, y);
	}

	public void write(final DataIOWriter writer) throws IOException {
	    this.theList.write(writer);
	}
    }

    static GameReplayEngine readReplay(final DataIOReader reader) throws IOException {
	final var gre = new GameReplayEngine();
	gre.redoHistory = LinkStack.read(reader);
	return gre;
    }

    // Fields
    private final LinkStack undoHistory;
    private LinkStack redoHistory;
    private boolean isLaser;
    private int destX, destY;

    // Constructors
    public GameReplayEngine() {
	this.undoHistory = new LinkStack();
	this.redoHistory = new LinkStack();
	this.isLaser = false;
	this.destX = -1;
	this.destY = -1;
    }

    int getX() {
	return this.destX;
    }

    int getY() {
	return this.destY;
    }

    // Public methods
    void redo() {
	if (!this.redoHistory.isEmpty()) {
	    final var entry = this.redoHistory.pop();
	    this.isLaser = entry.laser;
	    this.destX = entry.coordX;
	    this.destY = entry.coordY;
	} else {
	    this.isLaser = false;
	    this.destX = -1;
	    this.destY = -1;
	}
    }

    boolean tryRedo() {
	return !this.redoHistory.isEmpty();
    }

    void updateRedoHistory(final boolean laser, final int x, final int y) {
	this.redoHistory.push(laser, x, y);
    }

    void updateUndoHistory(final boolean laser, final int x, final int y) {
	this.undoHistory.push(laser, x, y);
    }

    boolean wasLaser() {
	return this.isLaser;
    }

    void writeReplay(final DataIOWriter writer) throws IOException {
	this.undoHistory.write(writer);
    }
}
