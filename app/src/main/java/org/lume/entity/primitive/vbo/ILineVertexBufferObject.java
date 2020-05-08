package org.lume.entity.primitive.vbo;

import org.lume.entity.primitive.Line;
import org.lume.opengl.vbo.IVertexBufferObject;

/**
 * (c) 2012 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 18:45:00 - 28.03.2012
 */
public interface ILineVertexBufferObject extends IVertexBufferObject {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void onUpdateColor(final Line pLine);
	public void onUpdateVertices(final Line pLine);
}