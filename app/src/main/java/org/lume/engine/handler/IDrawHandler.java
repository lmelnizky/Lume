package org.lume.engine.handler;

import org.lume.engine.camera.Camera;
import org.lume.opengl.util.GLState;


/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 10:50:58 - 08.08.2010
 */
public interface IDrawHandler {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void onDraw(final GLState pGLState, final Camera pCamera);
}
