package org.lume.util.level.simple;

import org.lume.entity.scene.Scene;
import org.lume.util.level.ILevelLoaderResult;

/**
 * (c) 2012 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 16:13:38 - 19.04.2012
 */
public class SimpleLevelLoaderResult implements ILevelLoaderResult {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Scene mScene;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SimpleLevelLoaderResult(final Scene pScene) {
		this.mScene = pScene;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Scene getScene() {
		return this.mScene;
	}

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
