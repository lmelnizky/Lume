package org.lume.opengl.font;

import org.lume.opengl.font.exception.LetterNotFoundException;
import org.lume.opengl.texture.ITexture;

/**
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 12:15:25 - 03.11.2011
 */
public interface IFont {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void load();
	public void unload();

	public ITexture getTexture();

	public float getAscent();
	public float getLineHeight();

	public Letter getLetter(final char pChar) throws LetterNotFoundException;
}
