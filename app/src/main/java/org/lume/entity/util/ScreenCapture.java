package org.lume.entity.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.lume.engine.camera.Camera;
import org.lume.entity.Entity;
import org.lume.entity.util.ScreenGrabber.IScreenGrabberCallback;
import org.lume.opengl.util.GLState;
import org.lume.util.StreamUtils;
import org.lume.util.debug.Debug;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 15:11:50 - 15.03.2010
 */
public class ScreenCapture extends Entity implements IScreenGrabberCallback {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int PNG_QUALITY_MAXIMUM = 100;

	// ===========================================================
	// Fields
	// ===========================================================

	private String mFilePath;

	private final ScreenGrabber mScreenGrabber = new ScreenGrabber();

	private IScreenCaptureCallback mScreenCaptureCallback;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onManagedDraw(final GLState pGLState, final Camera pCamera) {
		this.mScreenGrabber.onManagedDraw(pGLState, pCamera);
	}

	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		/* Nothing */
	}

	@Override
	public void reset() {
		/* Nothing */
	}

	@Override
	public void onScreenGrabbed(final Bitmap pBitmap) {
		try {
			ScreenCapture.saveCapture(pBitmap, this.mFilePath);
			this.mScreenCaptureCallback.onScreenCaptured(this.mFilePath);
		} catch (final FileNotFoundException e) {
			this.mScreenCaptureCallback.onScreenCaptureFailed(this.mFilePath, e);
		}
	}

	@Override
	public void onScreenGrabFailed(final Exception pException) {
		this.mScreenCaptureCallback.onScreenCaptureFailed(this.mFilePath, pException);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void capture(final int pCaptureWidth, final int pCaptureHeight, final String pFilePath, final IScreenCaptureCallback pScreenCaptureCallback) {
		this.capture(0, 0, pCaptureWidth, pCaptureHeight, pFilePath, pScreenCaptureCallback);
	}

	public void capture(final int pCaptureX, final int pCaptureY, final int pCaptureWidth, final int pCaptureHeight, final String pFilePath, final IScreenCaptureCallback pScreencaptureCallback) {
		this.mFilePath = pFilePath;
		this.mScreenCaptureCallback = pScreencaptureCallback;
		this.mScreenGrabber.grab(pCaptureX, pCaptureY, pCaptureWidth, pCaptureHeight, this);
	}

	private static void saveCapture(final Bitmap pBitmap, final String pFilePath) throws FileNotFoundException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(pFilePath);
			pBitmap.compress(CompressFormat.PNG, PNG_QUALITY_MAXIMUM, out);
		} catch (final FileNotFoundException e) {
			StreamUtils.flushAndCloseStream(out);
			Debug.e("Error saving file to: " + pFilePath, e);
			throw e;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface IScreenCaptureCallback {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		public void onScreenCaptured(final String pFilePath);
		public void onScreenCaptureFailed(final String pFilePath, final Exception pException);
	}
}