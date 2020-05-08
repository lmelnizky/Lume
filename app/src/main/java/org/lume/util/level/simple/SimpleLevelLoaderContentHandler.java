package org.lume.util.level.simple;

import java.util.HashMap;

import org.lume.entity.scene.Scene;
import org.lume.util.level.IEntityLoader;
import org.lume.util.level.IEntityLoaderListener;
import org.lume.util.level.LevelLoaderContentHandler;

/**
 * (c) 2012 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 16:12:23 - 19.04.2012
 */
public class SimpleLevelLoaderContentHandler extends LevelLoaderContentHandler<SimpleLevelEntityLoaderData, IEntityLoaderListener, SimpleLevelLoaderResult> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public SimpleLevelLoaderContentHandler(final IEntityLoader<SimpleLevelEntityLoaderData> pDefaultEntityLoader, final HashMap<String, IEntityLoader<SimpleLevelEntityLoaderData>> pEntityLoaders, final SimpleLevelEntityLoaderData pEntityLoaderData) {
		super(pDefaultEntityLoader, pEntityLoaders, pEntityLoaderData);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public SimpleLevelLoaderResult getLevelLoaderResult() {
		return new SimpleLevelLoaderResult((Scene) this.mRootEntity);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
