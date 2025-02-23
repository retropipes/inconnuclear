/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.dungeon.gameobject;

import java.io.IOException;

import org.retropipes.diane.asset.image.BufferedImageIcon;
import org.retropipes.diane.direction.Direction;
import org.retropipes.diane.direction.DirectionResolver;
import org.retropipes.diane.direction.DirectionStrings;
import org.retropipes.diane.fileio.DataIOReader;
import org.retropipes.diane.fileio.DataIOWriter;
import org.retropipes.diane.objectmodel.ObjectModel;
import org.retropipes.diane.random.RandomRange;
import org.retropipes.inconnuclear.dungeon.base.DungeonBase;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageId;
import org.retropipes.inconnuclear.loader.image.gameobject.ObjectImageLoader;
import org.retropipes.inconnuclear.loader.sound.Sounds;
import org.retropipes.inconnuclear.locale.Colors;
import org.retropipes.inconnuclear.locale.Layer;
import org.retropipes.inconnuclear.locale.ObjectInteractMessage;
import org.retropipes.inconnuclear.locale.Strings;
import org.retropipes.inconnuclear.utility.RandomGenerationRule;

public final class GameObject implements RandomGenerationRule {
    private static final int PLASTIC_MINIMUM_REACTION_FORCE = 0;
    private static final int DEFAULT_MINIMUM_REACTION_FORCE = 1;
    private static final int METAL_MINIMUM_REACTION_FORCE = 2;
    private final static boolean[] tunnelsFull = new boolean[Strings.COLOR_COUNT];

    public static final int getImbuedRangeForce(final Material material) {
	if (material == Material.PLASTIC) {
	    return PLASTIC_MINIMUM_REACTION_FORCE;
	}
	if (material == Material.METALLIC) {
	    return METAL_MINIMUM_REACTION_FORCE;
	}
	return DEFAULT_MINIMUM_REACTION_FORCE;
    }

    public static GameObject read(final DataIOReader reader) throws IOException {
	int nid = reader.readInt();
	return new GameObject(ObjectImageId.values()[nid]);
    }

    public static boolean tunnelsFull(final Colors color) {
	return GameObject.tunnelsFull[color.ordinal()];
    }

    private final ObjectModel model;
    private final ObjectImageId id;
    private transient boolean solid;
    private transient boolean friction;
    private transient boolean sightBlock;
    private transient boolean interactive;
    private transient boolean canMove;
    private transient boolean canPush;
    private transient boolean canPull;
    private transient boolean isField;
    private transient boolean isPassThrough;
    private transient boolean isPlayer;
    private transient int myLayer;
    private transient int blockHeight;
    private transient int damageDealt;
    private transient int initialTimerValue;
    private transient int timerValue;
    private transient int frameNumber;
    private transient int maxFrameNumber;
    private transient int interactMessageIndex;
    private transient BufferedImageIcon image;
    private transient String interactMessage;
    private transient Sounds interactSound;
    private transient ObjectImageId interactMorph;
    private transient ShopType shop;
    private transient boolean lazyLoaded;
    private transient int teamId;
    private transient boolean imageOverridden;
    private transient boolean timerActive;
    private transient boolean deferSetProperties;
    private transient boolean killsOnMove;
    private transient boolean solvesOnMove;
    private transient boolean boundUniversal;
    private transient Direction direction;
    private transient Colors color;
    private transient Material material;
    private transient ObjectImageId saved;
    private transient ObjectImageId bound;
    private transient ObjectImageId previousState;
    private transient int boundX;
    private transient int boundY;
    private transient boolean triggered;
    private transient boolean waitingOnTunnel;

    public GameObject(final GameObject source) {
	this.model = new ObjectModel();
	this.model.setId(source.id);
	this.id = source.id;
	this.solid = source.solid;
	this.friction = source.friction;
	this.sightBlock = source.sightBlock;
	this.interactive = source.interactive;
	this.canMove = source.canMove;
	this.canPush = source.canPush;
	this.canPull = source.canPull;
	this.isField = source.isField;
	this.isPassThrough = source.isPassThrough;
	this.isPlayer = source.isPlayer;
	this.myLayer = source.myLayer;
	this.blockHeight = source.blockHeight;
	this.damageDealt = source.damageDealt;
	this.initialTimerValue = source.initialTimerValue;
	this.timerValue = source.timerValue;
	this.frameNumber = source.frameNumber;
	this.maxFrameNumber = source.maxFrameNumber;
	this.interactMessageIndex = source.interactMessageIndex;
	this.image = source.image;
	this.interactMessage = source.interactMessage;
	this.interactSound = source.interactSound;
	this.interactMorph = source.interactMorph;
	this.shop = source.shop;
	this.lazyLoaded = source.lazyLoaded;
	this.teamId = source.teamId;
	this.imageOverridden = source.imageOverridden;
	this.timerActive = source.timerActive;
	this.deferSetProperties = source.deferSetProperties;
	this.killsOnMove = source.killsOnMove;
	this.solvesOnMove = source.solvesOnMove;
	this.boundUniversal = source.boundUniversal;
	this.direction = source.direction;
	this.color = source.color;
	this.material = source.material;
	this.saved = source.saved;
	this.bound = source.bound;
	this.previousState = source.previousState;
	this.boundX = source.boundX;
	this.boundY = source.boundY;
	this.triggered = source.triggered;
	this.waitingOnTunnel = source.waitingOnTunnel;
    }

    public GameObject(final ObjectImageId oid) {
	this.id = oid;
	this.imageOverridden = false;
	this.triggered = false;
	this.model = new ObjectModel();
	this.model.setId(oid);
	this.lazyLoaded = false;
    }

    private GameObject(final ObjectImageId oid, final ObjectImageId savedOid) {
	this.id = oid;
	this.saved = savedOid;
	this.imageOverridden = false;
	this.triggered = false;
	this.model = new ObjectModel();
	this.model.setId(oid);
	this.lazyLoaded = false;
    }

    public final void activateTimer() {
	this.timerActive = true;
	this.timerValue = this.initialTimerValue;
    }

    public boolean boundToSameObject(final GameObject boundTo) {
	if (this.getBoundObject() == null) {
	    if (boundTo != null) {
		return false;
	    }
	} else if (!this.getBoundObject().getClass().equals(boundTo.getClass())) {
	    return false;
	}
	return true;
    }

    public final boolean canMove() {
	return this.isMoving() || this.isPlayer() || this.isPullable() || this.isPushable();
    }

    @SuppressWarnings("static-method")
    public final boolean canMoveBoxes() {
	return false;
    }

    @SuppressWarnings("static-method")
    public final boolean canMoveMirrors() {
	return false;
    }

    @SuppressWarnings("static-method")
    public final boolean canMoveParty() {
	return false;
    }

    @SuppressWarnings("static-method")
    public final boolean canShoot() {
	return false;
    }

    /**
     * @param materialID
     */
    public GameObject changesToOnExposure(final Material materialID) {
	return this;
    }

    public final boolean defersSetProperties() {
	this.lazyLoad();
	return this.deferSetProperties;
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public final void editorPlaceHook(final int x, final int y, final int z) {
	// Do nothing
    }

    public GameObject editorPropertiesHook() {
	if (this.hasDirection()) {
	    this.toggleDirection();
	    return this;
	}
	if (this.hasColor()) {
	    this.toggleColor();
	    return this;
	}
	return null;
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public final void editorRemoveHook(final int x, final int y, final int z) {
	// Do nothing
    }

    public final GameObject getBoundObject() {
	this.lazyLoad();
	if (this.bound == null || this.bound == this.id) {
	    return this;
	}
	return new GameObject(this.bound, this.id);
    }

    public final int getBoundObjectX() {
	return this.boundX;
    }

    public final int getBoundObjectY() {
	return this.boundY;
    }

    public final String getCacheName() {
	return Integer.toString(this.id.ordinal()) + this.getDirectionSuffix() + this.getFrameSuffix();
    }

    public final Colors getColor() {
	this.lazyLoad();
	return this.color;
    }

    public final int getDamage() {
	this.lazyLoad();
	return this.damageDealt;
    }

    public final String getDescription() {
	return Strings.objectDescription(this.id.ordinal());
    }

    public final Direction getDirection() {
	this.lazyLoad();
	return this.direction;
    }

    private final String getDirectionSuffix() {
	if (this.hasDirection()) {
	    return Strings.SPACE + DirectionStrings.directionSuffix(this.direction);
	}
	return Strings.EMPTY;
    }

    public final int getFrameNumber() {
	this.lazyLoad();
	return this.frameNumber;
    }

    private final String getFrameSuffix() {
	if (this.isAnimated()) {
	    return Strings.SPACE + this.frameNumber;
	}
	return Strings.EMPTY;
    }

    public final int getHeight() {
	this.lazyLoad();
	return this.blockHeight;
    }

    public final ObjectImageId getId() {
	return this.id;
    }

    public final String getIdentityName() {
	return this.getLocalColorPrefix() + Strings.objectName(this.getIdValue());
    }

    public final int getIdValue() {
	return this.id.ordinal();
    }

    public final BufferedImageIcon getImage() {
	this.lazyLoad();
	return this.image;
    }

    public final String getInteractMessage() {
	this.lazyLoad();
	return this.interactMessage;
    }

    public final ObjectImageId getInteractMorph() {
	this.lazyLoad();
	return this.interactMorph;
    }

    public final Sounds getInteractSound() {
	this.lazyLoad();
	return this.interactSound;
    }

    public final int getLayer() {
	this.lazyLoad();
	return this.myLayer;
    }

    private final String getLocalColorPrefix() {
	if (this.hasColor()) {
	    return Strings.color(this.color) + Strings.SPACE;
	}
	return Strings.EMPTY;
    }

    public final Material getMaterial() {
	this.lazyLoad();
	return this.material;
    }

    @Override
    public int getMaximumRequiredQuantity(final DungeonBase dungeonBase) {
	return RandomGenerationRule.NO_LIMIT;
    }

    @Override
    public int getMinimumRequiredQuantity(final DungeonBase dungeonBase) {
	return RandomGenerationRule.NO_LIMIT;
    }

    public final String getName() {
	return Strings.objectName(this.id.ordinal());
    }

    public final GameObject getPreviousStateObject() {
	if (this.previousState == null || this.previousState == this.id) {
	    return this;
	}
	return new GameObject(this.previousState, this.id);
    }

    public final GameObject getSavedObject() {
	if (this.saved == null || this.saved == this.id) {
	    return this;
	}
	return new GameObject(this.saved, this.id);
    }

    public final ShopType getShopType() {
	this.lazyLoad();
	return this.shop;
    }

    public final int getTeamID() {
	return this.teamId;
    }

    private final boolean hasColor() {
	return this.color != null && this.color != Colors._NONE;
    }

    private final boolean hasDirection() {
	return this.direction != null && this.direction != Direction.NONE;
    }

    public final boolean hasFriction() {
	this.lazyLoad();
	return this.friction;
    }

    public final boolean hasSameBoundObject(final GameObject testObject) {
	if (this.bound == null && testObject.bound == null) {
	    return true;
	}
	return this.bound == testObject.bound;
    }

    public final void interactAction() {
	// Do nothing
    }

    public final boolean isAnimated() {
	this.lazyLoad();
	return this.maxFrameNumber > 0;
    }

    public final boolean isBoundUniversally() {
	this.lazyLoad();
	return this.boundUniversal;
    }

    public final boolean isDamaging() {
	this.lazyLoad();
	return this.damageDealt > 0;
    }

    public final boolean isField() {
	this.lazyLoad();
	return this.isField;
    }

    @SuppressWarnings("static-method")
    public final boolean isFinalBoss() {
	return false;
    }

    public final boolean isInteractive() {
	this.lazyLoad();
	return this.interactive;
    }

    public final boolean isMoving() {
	this.lazyLoad();
	return this.canMove;
    }

    public final boolean isPassThrough() {
	this.lazyLoad();
	return this.isPassThrough;
    }

    public final boolean isPlayer() {
	this.lazyLoad();
	return this.isPlayer;
    }

    public final boolean isPullable() {
	this.lazyLoad();
	return this.canPull;
    }

    public final boolean isPushable() {
	this.lazyLoad();
	return this.canPush;
    }

    @Override
    public boolean isRequired(final DungeonBase dungeonBase) {
	return false;
    }

    public final boolean isShop() {
	return this.getShopType() != null;
    }

    public final boolean isSightBlocking() {
	this.lazyLoad();
	return this.sightBlock;
    }

    public final boolean isSolid() {
	this.lazyLoad();
	return this.solid;
    }

    public boolean isTriggered() {
	return this.triggered;
    }

    public boolean killsOnMove() {
	this.lazyLoad();
	return this.killsOnMove;
    }

    private void lazyLoad() {
	if (!this.lazyLoaded) {
	    if (!this.imageOverridden) {
		this.image = ObjectImageLoader.load(this.id);
	    }
	    this.interactMessageIndex = GameObjectDataLoader.interactionMessageIndex(this.id);
	    this.interactMessage = Strings
		    .objectInteractMessage(ObjectInteractMessage.values()[this.interactMessageIndex]);
	    this.interactMorph = GameObjectDataLoader.interactionMorph(this.id);
	    this.interactSound = GameObjectDataLoader.interactionSound(this.id);
	    this.color = GameObjectDataLoader.color(this.id);
	    this.direction = GameObjectDataLoader.direction(this.id);
	    this.shop = GameObjectDataLoader.shopType(this.id);
	    this.solid = GameObjectDataLoader.solid(this.id);
	    this.friction = GameObjectDataLoader.friction(this.id);
	    this.sightBlock = GameObjectDataLoader.sightBlocking(this.id);
	    this.interactive = GameObjectDataLoader.isInteractive(this.id);
	    this.canPush = GameObjectDataLoader.pushable(this.id);
	    this.canPull = GameObjectDataLoader.pullable(this.id);
	    this.canMove = GameObjectDataLoader.isMoving(this.id);
	    this.isField = GameObjectDataLoader.isField(this.id);
	    this.isPlayer = GameObjectDataLoader.isPlayer(this.id);
	    this.myLayer = GameObjectDataLoader.layer(this.id);
	    this.blockHeight = GameObjectDataLoader.height(this.id);
	    this.damageDealt = GameObjectDataLoader.damage(this.id);
	    this.initialTimerValue = GameObjectDataLoader.initialTimer(this.id);
	    this.maxFrameNumber = GameObjectDataLoader.maxFrame(this.id);
	    this.bound = GameObjectDataLoader.bound(this.id);
	    this.deferSetProperties = GameObjectDataLoader.deferSetProperties(this.id);
	    this.killsOnMove = GameObjectDataLoader.killsOnMove(this.id);
	    this.solvesOnMove = GameObjectDataLoader.solvesOnMove(this.id);
	    this.isPassThrough = GameObjectDataLoader.isPassThrough(this.id);
	    this.material = GameObjectDataLoader.material(this.id);
	    this.boundUniversal = GameObjectDataLoader.boundUniversal(this.id);
	    this.lazyLoaded = true;
	}
    }

    public final void overrideImage(final BufferedImageIcon imageOverride) {
	this.imageOverridden = true;
	this.image = imageOverride;
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public final void moveFailedAction(final int x, final int y, final int z) {
	// Do nothing
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public final void postMoveAction(final int x, final int y, final int z) {
	// Do nothing
    }

    /**
     * @param ie
     * @param x
     * @param y
     */
    @SuppressWarnings("static-method")
    public final boolean preMoveAction(final boolean ie, final int x, final int y) {
	// Do nothing
	return true;
    }

    /**
     * @param moving
     * @param x
     * @param y
     * @param z
     */
    public final void pushCollideAction(final GameObject moving, final int x, final int y, final int z) {
	// Do nothing
    }

    /**
     * @param moving
     * @param x
     * @param y
     * @param z
     */
    @SuppressWarnings("static-method")
    public final boolean pushIntoAction(final GameObject moving, final int x, final int y, final int z) {
	// Do nothing
	return false;
    }

    /**
     * @param moving
     * @param x
     * @param y
     * @param z
     */
    public final void pushOutAction(final GameObject moving, final int x, final int y, final int z) {
	// Do nothing
    }

    public final void setBoundObjectX(final int newBX) {
	this.boundX = newBX;
    }

    public final void setBoundObjectY(final int newBY) {
	this.boundY = newBY;
    }

    public final void setDirection(final Direction dir) {
	this.direction = dir;
    }

    public final void setPreviousStateObject(final GameObject savedObject) {
	this.previousState = savedObject.getId();
    }

    public final void setSavedObject(final GameObject savedObject) {
	this.saved = savedObject.getId();
    }

    public final void setTeamID(final int tid) {
	this.teamId = tid;
    }

    public final void setTriggered(final boolean isTriggered) {
	this.triggered = isTriggered;
    }

    public void setWaitingOnTunnel(final boolean value) {
	this.waitingOnTunnel = value;
    }

    @Override
    public boolean shouldGenerateObject(final DungeonBase dungeonBase, final int row, final int col, final int level,
	    final int layer) {
	if (layer == Layer.STATUS.ordinal()) {
	    // Handle object layer
	    // Limit generation of other objects to 20%, unless required
	    if (this.isPassThrough() || this.isRequired(dungeonBase)) {
		return true;
	    }
	    final var r = new RandomRange(1, 100);
	    if (r.generate() <= 20) {
		return true;
	    }
	    return false;
	}
	if (!this.isField()) {
	    // Generate other ground at 100%
	    return true;
	}
	// Limit generation of fields to 20%
	final var r = new RandomRange(1, 100);
	if (r.generate() <= 20) {
	    return true;
	}
	return false;
    }

    public boolean solvesOnMove() {
	this.lazyLoad();
	return this.solvesOnMove;
    }

    public final void tickTimer() {
	if (this.timerActive) {
	    this.timerValue--;
	    if (this.timerValue == 0) {
		this.timerActive = false;
		// Time's up!
	    }
	}
    }

    private final void toggleColor() {
	var maxColor = Colors.values().length;
	if (this.hasColor()) {
	    var oldColorValue = this.color.ordinal();
	    final var newColorValue = oldColorValue;
	    oldColorValue++;
	    Colors newColor;
	    if (newColorValue >= maxColor) {
		newColor = Colors.GRAY;
	    } else {
		newColor = Colors.values()[newColorValue];
	    }
	    this.color = newColor;
	}
    }

    public final void toggleDirection() {
	this.direction = DirectionResolver.rotateRight90(this.direction);
    }

    public final void toggleFrameNumber() {
	if (this.isAnimated()) {
	    this.frameNumber++;
	    if (this.frameNumber > this.maxFrameNumber) {
		this.frameNumber = 0;
	    }
	}
    }

    public boolean waitingOnTunnel() {
	return this.waitingOnTunnel;
    }

    public final void write(final DataIOWriter writer) throws IOException {
	writer.writeInt(this.id.ordinal());
    }
}
