package org.andengine.entity.sprite.batch;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.batch.vbo.ISpriteBatchVertexBufferObject;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.list.SmartList;

/**
 * Particular {@link org.andengine.entity.sprite.Sprite} flip supported.
 * Note: you can use {@link SpriteGroup} and set width=-width and height=-height
 * to get the same results.
 *
 * @author Yaroslav Havrylovych
 */
public class FlippableSpriteGroup extends SpriteGroup {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================
    public FlippableSpriteGroup(final ITexture pTexture, final int pCapacity, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pTexture, pCapacity, pVertexBufferObjectManager);
    }

    public FlippableSpriteGroup(final float pX, final float pY, final ITexture pTexture, final int pCapacity, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTexture, pCapacity, pVertexBufferObjectManager);
    }

    public FlippableSpriteGroup(final ITexture pTexture, final int pCapacity, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType) {
        super(pTexture, pCapacity, pVertexBufferObjectManager, pDrawType);
    }

    public FlippableSpriteGroup(final float pX, final float pY, final ITexture pTexture, final int pCapacity, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType) {
        super(pX, pY, pTexture, pCapacity, pVertexBufferObjectManager, pDrawType);
    }

    public FlippableSpriteGroup(final ITexture pTexture, final int pCapacity, final VertexBufferObjectManager pVertexBufferObjectManager, final ShaderProgram pShaderProgram) {
        super(pTexture, pCapacity, pVertexBufferObjectManager, pShaderProgram);
    }

    public FlippableSpriteGroup(final float pX, final float pY, final ITexture pTexture, final int pCapacity, final VertexBufferObjectManager pVertexBufferObjectManager, final ShaderProgram pShaderProgram) {
        super(pX, pY, pTexture, pCapacity, pVertexBufferObjectManager, pShaderProgram);
    }

    public FlippableSpriteGroup(final ITexture pTexture, final int pCapacity, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType, final ShaderProgram pShaderProgram) {
        super(pTexture, pCapacity, pVertexBufferObjectManager, pDrawType, pShaderProgram);
    }

    public FlippableSpriteGroup(final float pX, final float pY, final ITexture pTexture, final int pCapacity, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType, final ShaderProgram pShaderProgram) {
        super(pX, pY, pTexture, pCapacity, pVertexBufferObjectManager, pDrawType, pShaderProgram);
    }

    public FlippableSpriteGroup(final ITexture pTexture, final int pCapacity, final ISpriteBatchVertexBufferObject pSpriteBatchVertexBufferObject) {
        super(pTexture, pCapacity, pSpriteBatchVertexBufferObject);
    }

    public FlippableSpriteGroup(final float pX, final float pY, final ITexture pTexture, final int pCapacity, final ISpriteBatchVertexBufferObject pSpriteBatchVertexBufferObject) {
        super(pX, pY, pTexture, pCapacity, pSpriteBatchVertexBufferObject);
    }

    public FlippableSpriteGroup(final ITexture pTexture, final int pCapacity, final ISpriteBatchVertexBufferObject pSpriteBatchVertexBufferObject, final ShaderProgram pShaderProgram) {
        super(pTexture, pCapacity, pSpriteBatchVertexBufferObject, pShaderProgram);
    }

    public FlippableSpriteGroup(final float pX, final float pY, final ITexture pTexture, final int pCapacity, final ISpriteBatchVertexBufferObject pSpriteBatchVertexBufferObject, final ShaderProgram pShaderProgram) {
        super(pX, pY, pTexture, pCapacity, pSpriteBatchVertexBufferObject, pShaderProgram);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    protected boolean onUpdateSpriteBatch() {
        final SmartList<IEntity> children = this.mChildren;
        if (children == null) {
            return false;
        } else {
            final int childCount = children.size();
            for (int i = 0; i < childCount; i++) {
                this.drawFlippableWithoutChecks((Sprite) children.get(i));
            }
            return true;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
