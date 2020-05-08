package org.lume.input.touch.controller;

import org.lume.input.touch.TouchEvent;

/**
 * (c) 2012 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 16:44:26 - 04.04.2012
 */
public interface ITouchEventCallback {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public boolean onTouchEvent(final TouchEvent pTouchEvent);
}
