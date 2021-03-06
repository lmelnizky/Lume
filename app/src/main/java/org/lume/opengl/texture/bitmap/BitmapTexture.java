package org.lume.opengl.texture.bitmap;

import java.io.IOException;
import java.io.InputStream;

import org.lume.opengl.texture.ITextureStateListener;
import org.lume.opengl.texture.PixelFormat;
import org.lume.opengl.texture.Texture;
import org.lume.opengl.texture.TextureManager;
import org.lume.opengl.texture.TextureOptions;
import org.lume.opengl.util.GLState;
import org.lume.util.StreamUtils;
import org.lume.util.adt.io.in.IInputStreamOpener;
import org.lume.util.exception.NullBitmapException;
import org.lume.util.math.MathUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 16:16:25 - 30.07.2011
 */
public class BitmapTexture extends Texture {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final int mWidth;
	private final int mHeight;
	private final IInputStreamOpener mInputStreamOpener;
	private final BitmapTextureFormat mBitmapTextureFormat;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BitmapTexture(final TextureManager pTextureManager, final IInputStreamOpener pInputStreamOpener) throws IOException {
		this(pTextureManager, pInputStreamOpener, BitmapTextureFormat.RGBA_8888, TextureOptions.DEFAULT, null);
	}

	public BitmapTexture(final TextureManager pTextureManager, final IInputStreamOpener pInputStreamOpener, final BitmapTextureFormat pBitmapTextureFormat) throws IOException {
		this(pTextureManager, pInputStreamOpener, pBitmapTextureFormat, TextureOptions.DEFAULT, null);
	}

	public BitmapTexture(final TextureManager pTextureManager, final IInputStreamOpener pInputStreamOpener, final TextureOptions pTextureOptions) throws IOException {
		this(pTextureManager, pInputStreamOpener, BitmapTextureFormat.RGBA_8888, pTextureOptions, null);
	}

	public BitmapTexture(final TextureManager pTextureManager, final IInputStreamOpener pInputStreamOpener, final BitmapTextureFormat pBitmapTextureFormat, final TextureOptions pTextureOptions) throws IOException {
		this(pTextureManager, pInputStreamOpener, pBitmapTextureFormat, pTextureOptions, null);
	}

	public BitmapTexture(final TextureManager pTextureManager, final IInputStreamOpener pInputStreamOpener, final BitmapTextureFormat pBitmapTextureFormat, final TextureOptions pTextureOptions, final ITextureStateListener pTextureStateListener) throws IOException {
		super(pTextureManager, pBitmapTextureFormat.getPixelFormat(), pTextureOptions, pTextureStateListener);

		this.mInputStreamOpener = pInputStreamOpener;
		this.mBitmapTextureFormat = pBitmapTextureFormat;

		final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.inJustDecodeBounds = true;

		final InputStream in = null;
		try {
			BitmapFactory.decodeStream(pInputStreamOpener.open(), null, decodeOptions);
		} finally {
			StreamUtils.close(in);
		}

		this.mWidth = decodeOptions.outWidth;
		this.mHeight = decodeOptions.outHeight;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public int getWidth() {
		return this.mWidth;
	}

	@Override
	public int getHeight() {
		return this.mHeight;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void writeTextureToHardware(final GLState pGLState) throws IOException {
		final Config bitmapConfig = this.mBitmapTextureFormat.getBitmapConfig();
		final Bitmap bitmap = this.onGetBitmap(bitmapConfig);

		if (bitmap == null) {
			throw new NullBitmapException("Caused by: '" + this.toString() + "'.");
		}

		final boolean useDefaultAlignment = MathUtils.isPowerOfTwo(bitmap.getWidth()) && MathUtils.isPowerOfTwo(bitmap.getHeight()) && (this.mPixelFormat == PixelFormat.RGBA_8888);
		if (!useDefaultAlignment) {
			/* Adjust unpack alignment. */
			GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
		}

		final boolean preMultipyAlpha = this.mTextureOptions.mPreMultiplyAlpha;
		if (preMultipyAlpha) {
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		} else {
			pGLState.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0, this.mPixelFormat);
		}

		if (!useDefaultAlignment) {
			/* Restore default unpack alignment. */
			GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLState.GL_UNPACK_ALIGNMENT_DEFAULT);
		}

		bitmap.recycle();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected Bitmap onGetBitmap(final Config pBitmapConfig) throws IOException {
		final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.inPreferredConfig = pBitmapConfig;
		decodeOptions.inDither = false;

		return BitmapFactory.decodeStream(this.mInputStreamOpener.open(), null, decodeOptions);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
