package org.lume.entity.scene.background;

import org.lume.engine.camera.Camera;
import org.lume.entity.IEntity;
import org.lume.opengl.util.GLState;
import org.lume.util.debug.Debug;

import java.util.ArrayList;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @author Yaroslav Havrylovych
 */
public class ParallaxBackground extends Background {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private final ArrayList<ParallaxEntity> mParallaxEntities = new ArrayList<ParallaxEntity>();
    protected float mParallaxValue;
    private int mParallaxEntityCount;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ParallaxBackground(final float pRed, final float pGreen, final float pBlue) {
        super(pRed, pGreen, pBlue);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public void setParallaxValue(final float pParallaxValue) {
        this.mParallaxValue = pParallaxValue;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void onDraw(final GLState pGLState, final Camera pCamera) {
        super.onDraw(pGLState, pCamera);

        final float parallaxValue = this.mParallaxValue;
        final ArrayList<ParallaxEntity> parallaxEntities = this.mParallaxEntities;

        for (int i = 0; i < this.mParallaxEntityCount; i++) {
            parallaxEntities.get(i).onDraw(pGLState, pCamera, parallaxValue);
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public void attachParallaxEntity(final ParallaxEntity pParallaxEntity) {
        this.mParallaxEntities.add(pParallaxEntity);
        this.mParallaxEntityCount++;
    }

    public boolean detachParallaxEntity(final ParallaxEntity pParallaxEntity) {
        this.mParallaxEntityCount--;
        final boolean success = this.mParallaxEntities.remove(pParallaxEntity);
        if (!success) {
            this.mParallaxEntityCount++;
        }
        return success;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static class ParallaxEntity {
        // ===========================================================
        // Constants
        // ===========================================================

        // ===========================================================
        // Fields
        // ===========================================================

        final float mParallaxFactor;
        final IEntity mEntity;

        // ===========================================================
        // Constructors
        // ===========================================================

        public ParallaxEntity(final float pParallaxFactor, final IEntity pEntity) {
            this.mParallaxFactor = pParallaxFactor;
            this.mEntity = pEntity;

            // TODO Adjust onDraw calculations, so that these assumptions aren't necessary.
            if (this.mEntity.getX() != 0) {
                Debug.w("The X position of a " + this.getClass().getSimpleName() + " is expected to be 0.");
            }

            if (this.mEntity.getOffsetCenterX() != 0) {
                Debug.w("The OffsetCenterXposition of a " + this.getClass().getSimpleName() + " is expected to be 0.");
            }
        }

        // ===========================================================
        // Getter & Setter
        // ===========================================================

        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================

        // ===========================================================
        // Methods
        // ===========================================================

        public void onDraw(final GLState pGLState, final Camera pCamera, final float pParallaxValue) {
            pGLState.pushModelViewGLMatrix();
            {
                final float entityWidthScaled = this.mEntity.getWidth() * this.mEntity.getScaleX();
                //image which center x biggest than centerMaximumX are invisible
                final float centerMaximumX = pCamera.getCameraSceneWidth() + entityWidthScaled / 2;
                float baseOffset = (pParallaxValue * this.mParallaxFactor) % entityWidthScaled;

                while (baseOffset > 0) {
                    baseOffset -= entityWidthScaled;
                }
                pGLState.translateModelViewGLMatrixf(baseOffset, 0, 0);

                float currentCenterMaxX = baseOffset;

                do {
                    this.mEntity.onDraw(pGLState, pCamera);
                    pGLState.translateModelViewGLMatrixf(entityWidthScaled, 0, 0);
                    currentCenterMaxX += entityWidthScaled;
                } while (currentCenterMaxX < centerMaximumX);
            }
            pGLState.popModelViewGLMatrix();
        }

        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================
    }
}