package com.hyena.framework.samples.animator.render.node;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CSprite;
import com.hyena.framework.animation.texture.CTexture;

/**
 * Created by yangzc on 16/4/28.
 */
public class StateSprite extends CSprite {

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_UNABLE = 2;
    public static final int STATUS_OPENED = 3;

    private CTexture mNormalTexture;
    private CTexture mDisableTexture;
    private CTexture mOpenedTexture;
    private int mStatus = STATUS_NORMAL;

    private String mNextBagId;

    public static StateSprite create(Director director, CTexture texture) {
        if (texture == null)
            texture = CTexture.create(director, null);
        StateSprite sprite = new StateSprite(director, texture);
        return sprite;
    }

    protected StateSprite(Director director, CTexture texture) {
        super(director, texture);
    }

    public void setTexture(CTexture normalTexture, CTexture disableTexture
            , CTexture openedTexture) {
        this.mNormalTexture = normalTexture;
        this.mDisableTexture = disableTexture;
        this.mOpenedTexture = openedTexture;
        setStatus(mStatus);
    }

    public void setStatus(int status) {
        this.mStatus = status;
        switch (mStatus) {
            case STATUS_NORMAL: {
                setTexture(mNormalTexture);
                break;
            }
            case STATUS_UNABLE: {
                setTexture(mDisableTexture);
                break;
            }
            case STATUS_OPENED: {
                setTexture(mOpenedTexture);
                break;
            }
        }
    }

    public int getStatus() {
        return mStatus;
    }

    public void setNextBagId(String bagId) {
        this.mNextBagId = bagId;
    }

    public String getNextBagId() {
        return mNextBagId;
    }
}
