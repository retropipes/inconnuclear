/*  Inconnuclear: A Dungeon-Diving RPG
Copyleft (C) 2024-present RetroPipes
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/retropipes/inconnuclear
 */
package org.retropipes.inconnuclear.loader.image.flag;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.retropipes.diane.asset.image.BufferedImageIcon;

public class FlagImageLoader {
    private static class ImageCache {
	// Fields
	private static ArrayList<ImageCacheEntry> cache;
	private static boolean cacheCreated = false;

	private static void createCache() {
	    if (!ImageCache.cacheCreated) {
		// Create the cache
		ImageCache.cache = new ArrayList<>();
		ImageCache.cacheCreated = true;
	    }
	}

	public static BufferedImageIcon getCachedImage(final String name, final URL url) {
	    if (!ImageCache.cacheCreated) {
		ImageCache.createCache();
	    }
	    for (final ImageCacheEntry entry : ImageCache.cache) {
		if (name.equals(entry.name())) {
		    // Found
		    return entry.image();
		}
	    }
	    // Not found: Add to cache
	    final var newImage = FlagImageLoader.loadUncached(url);
	    final var newEntry = new ImageCacheEntry(newImage, name);
	    ImageCache.cache.add(newEntry);
	    return newImage;
	}
    }

    private static class ImageCacheEntry {
	// Fields
	private final BufferedImageIcon image;
	private final String name;

	// Constructors
	public ImageCacheEntry(final BufferedImageIcon newImage, final String newName) {
	    this.image = newImage;
	    this.name = newName;
	}

	@Override
	public boolean equals(final Object obj) {
	    if (this == obj) {
		return true;
	    }
	    if (!(obj instanceof final ImageCacheEntry other)) {
		return false;
	    }
	    return Objects.equals(this.name, other.name);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(this.name);
	}

	public BufferedImageIcon image() {
	    return this.image;
	}

	public String name() {
	    return this.name;
	}
    }

    public static BufferedImageIcon load(final FlagImageId image) {
	return ImageCache.getCachedImage(image.getName(), image.getURL());
    }

    public static BufferedImageIcon load(final FlagImageId image, final URL url) {
	return ImageCache.getCachedImage(image.getName(), url);
    }

    public static BufferedImageIcon load(final String name, final URL url) {
	return ImageCache.getCachedImage(name, url);
    }

    static BufferedImageIcon loadUncached(final URL url) {
	try {
	    final var image = ImageIO.read(url);
	    return new BufferedImageIcon(image);
	} catch (final IOException ie) {
	    return null;
	}
    }
}
