package org.lume.entity.particle.modifier;

import org.lume.entity.IEntity;
import org.lume.entity.particle.Particle;
import org.lume.entity.particle.initializer.IParticleInitializer;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 20:06:05 - 14.03.2010
 */
public interface IParticleModifier<T extends IEntity> extends IParticleInitializer<T> {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void onUpdateParticle(final Particle<T> pParticle);
}
