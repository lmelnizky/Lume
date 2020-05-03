package org.lume.opengl.shader.source;

import org.lume.opengl.util.GLState;

/**
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 16:24:57 - 10.10.2011
 */
public interface IShaderSource {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public String getShaderSource(final GLState pGLState);
}
