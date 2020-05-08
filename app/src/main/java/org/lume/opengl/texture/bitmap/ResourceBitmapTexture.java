package org.lume.opengl.texture.bitmap;

import java.io.IOException;

import org.lume.opengl.texture.ITextureStateListener;
import org.lume.opengl.texture.TextureManager;
import org.lume.opengl.texture.TextureOptions;
import org.lume.util.adt.io.in.ResourceInputStreamOpener;

import android.content.res.Resources;

/**
 * (c) 2012 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 12:10:08 - 02.03.2012
 */
public class ResourceBitmapTexture extends BitmapTexture {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public ResourceBitmapTexture(final TextureManager pTextureManager, final Resources pResources, final int pDrawableResourceID) throws IOException {
		super(pTextureManager, new ResourceInputStreamOpener(pResources, pDrawableResourceID));
	}

	public ResourceBitmapTexture(final TextureManager pTextureManager, final Resources pResources, final int pDrawableResourceID, final BitmapTextureFormat pBitmapTextureFormat) throws IOException {
		super(pTextureManager, new ResourceInputStreamOpener(pResources, pDrawableResourceID), pBitmapTextureFormat);
	}

	public ResourceBitmapTexture(final TextureManager pTextureManager, final Resources pResources, final int pDrawableResourceID, final TextureOptions pTextureOptions) throws IOException {
		super(pTextureManager, new ResourceInputStreamOpener(pResources, pDrawableResourceID), pTextureOptions);
	}

	public ResourceBitmapTexture(final TextureManager pTextureManager, final Resources pResources, final int pDrawableResourceID, final BitmapTextureFormat pBitmapTextureFormat, final TextureOptions pTextureOptions) throws IOException {
		super(pTextureManager, new ResourceInputStreamOpener(pResources, pDrawableResourceID), pBitmapTextureFormat, pTextureOptions);
	}

	public ResourceBitmapTexture(final TextureManager pTextureManager, final Resources pResources, final int pDrawableResourceID, final BitmapTextureFormat pBitmapTextureFormat, final TextureOptions pTextureOptions, final ITextureStateListener pTextureStateListener) throws IOException {
		super(pTextureManager, new ResourceInputStreamOpener(pResources, pDrawableResourceID), pBitmapTextureFormat, pTextureOptions, pTextureStateListener);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
