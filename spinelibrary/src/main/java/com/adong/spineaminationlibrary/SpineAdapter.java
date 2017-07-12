package com.adong.spineaminationlibrary;

import android.text.TextUtils;
import android.view.View;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;

import static com.badlogic.gdx.Gdx.gl;

/**
 * 骨骼动画封装类
 * Created by J.Tommy on 17/6/5.
 */
public abstract class SpineAdapter extends ApplicationAdapter implements FragmentLifeCycleListener {
    private final String TAG = getClass().getSimpleName();

    private FileHandle mAltasFileHandle;
    private FileHandle mSkeletonFileHandle;
    //    private String mAltasPath;
//    private String mSkeletonPath;
    protected boolean mIsInited;
    protected boolean mIsVisible = true;
    protected AndroidFragmentApplication mAndroidFragmentApplication;
    protected OrthographicCamera mCamera;
    protected SpriteBatch mBatch;
    protected SkeletonRenderer mRenderer;
    protected TextureAtlas mAtlas;
    protected Skeleton mSkeleton;
    protected SkeletonBounds mSkeletonBounds;
    protected AnimationState mAnimationState;
    protected AnimationStateData mAnimationStateData;
    protected SkeletonJson mSkeletonJson;
    protected SkeletonData mSkeletonData;
    private OnSpineClickListener mSpineClickListener;
    private OnCreatedLIstener mOnCreatedLIstener;

    public void setOnSpineClickListener(final OnSpineClickListener spineClickListener) {
        mSpineClickListener = spineClickListener;
    }

    public void setOnCreatedLIstener(OnCreatedLIstener onCreatedLIstener) {
        mOnCreatedLIstener = onCreatedLIstener;
    }

    public SpineAdapter() {
    }

    public void setParentFragment(AndroidFragmentApplication fragmentApplication) {
        mAndroidFragmentApplication = fragmentApplication;
    }

    /**
     * 注意：这些周期方法都是在子线程中执行的
     */
    @Override
    public void create() {
        try {
            onCreateImpl();
            initialize();
            mIsInited = true;
            onCreatedImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mOnCreatedLIstener != null) {
            mOnCreatedLIstener.onCreated();
        }
    }

    private void initialize() {
        if (mAltasFileHandle == null || mSkeletonFileHandle == null) {
            throw new RuntimeException("请在createImpl中设置altas路径和skeleton路径,并确保路径有效");
        }
        mCamera = new OrthographicCamera();
        mBatch = new SpriteBatch();
        mRenderer = new SkeletonRenderer();
        mRenderer.setPremultipliedAlpha(true);

        mAtlas = new TextureAtlas(mAltasFileHandle);
        mSkeletonJson = new SkeletonJson(mAtlas);
        mSkeletonData = mSkeletonJson.readSkeletonData(mSkeletonFileHandle);
        float scale = (float) ((float) Gdx.graphics.getHeight() / mSkeletonData.getHeight());
        mSkeletonJson.setScale(scale);//设置完scale之后要重新读取一下mSkeletonData
        mSkeletonData = mSkeletonJson.readSkeletonData(mSkeletonFileHandle);
        mSkeleton = new Skeleton(mSkeletonData);
        mSkeleton.setPosition(Gdx.graphics.getWidth() / 2, 0);
        mSkeletonBounds = new SkeletonBounds();
        mAnimationStateData = new AnimationStateData(mSkeletonData);
        mAnimationStateData.setDefaultMix(0.3f);
        mAnimationState = new AnimationState(mAnimationStateData);
        Gdx.input.setInputProcessor(new InputAdapter() {
            final Vector3 point = new Vector3();

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                mCamera.unproject(point.set(screenX, screenY, 0));
                mSkeletonBounds.update(mSkeleton, false);
                if (mSkeletonBounds.aabbContainsPoint(point.x, point.y)) {
                    doClick();
                    if (mSpineClickListener != null) {
                        mAndroidFragmentApplication.getView().post(new Runnable() {
                            @Override
                            public void run() {
                                mSpineClickListener.onClick();
                            }
                        });
                        return true;
                    }
                }
                return true;
            }
        });
    }

    public abstract void onCreatedImpl();

    public abstract void doClick();

    public void findAltas(String path, Files.FileType fileType) {
        mAltasFileHandle = Gdx.files.getFileHandle(path, fileType);
    }

    public void findSkeleton(String path, Files.FileType fileType) {
        mSkeletonFileHandle = Gdx.files.getFileHandle(path, fileType);
    }

    @Override
    public void resize(int width, int height) {
        try {
            onResizeImpl(width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume() {
        try {
            onResumeImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        try {
            onRenderImpl();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        try {
            onPauseImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        try {
            onDisposeImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void onCreateImpl();

    public void onResizeImpl(int width, int height) {
        if (mCamera != null) {
            mCamera.setToOrtho(false);
        }
    }

    public void onResumeImpl() {

    }

    public void onRenderImpl() {
        mAnimationState.update(Gdx.graphics.getDeltaTime());
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mAnimationState.apply(mSkeleton);
        mSkeleton.updateWorldTransform();
        mCamera.update();
        mBatch.getProjectionMatrix().set(mCamera.combined);
        mBatch.begin();
        mRenderer.draw(mBatch, mSkeleton);
        mBatch.end();
    }

    public void onPauseImpl() {

    }

    public void onDisposeImpl() {
        if (mAtlas != null) {
            mAtlas.dispose();
        }
    }

    public boolean isInited() {
        return mIsInited;
    }

    @Override
    public void onVisibleChange(boolean isVisible) {
        this.mIsVisible = isVisible;
        if (mAndroidFragmentApplication != null && mAndroidFragmentApplication.getGraphics() != null) {
            mAndroidFragmentApplication.getGraphics().requestRendering();
        }
        if (!isVisible) {
            setGone();
        } else {
            setVisible();
        }
    }

    private void setVisible() {
        if (mAndroidFragmentApplication != null && mAndroidFragmentApplication.getGraphics() != null) {
            mAndroidFragmentApplication.onResume();
            View view = ((AndroidGraphics) mAndroidFragmentApplication.getGraphics()).getView();
            view.setVisibility(View.VISIBLE);
        }
    }

    private void setGone() {
        if (mAndroidFragmentApplication != null && mAndroidFragmentApplication.getGraphics() != null) {
            mAndroidFragmentApplication.onPause();
            View view = ((AndroidGraphics) mAndroidFragmentApplication.getGraphics()).getView();
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 换装饰
     *
     * @param slotName       插槽名称
     * @param attachmentName 装饰名称
     * @return
     */
    public boolean setAttachment(String slotName, String attachmentName) {
        if (mSkeleton == null || TextUtils.isEmpty(slotName)) {
            return false;
        }
        Slot slot = mSkeleton.findSlot(slotName);
        if (slot == null) {
            return false;
        }
        if (TextUtils.isEmpty(attachmentName)) {
            slot.setAttachment(null);
        } else {
            Attachment attachment = mSkeleton.getAttachment(slotName, attachmentName);
            if (attachment == null) {
                return false;
            }
            mSkeleton.setAttachment(slotName, attachmentName);
        }
        return true;
    }

    /**
     * 卸载装饰
     *
     * @param slotName
     * @return
     */
    private boolean unsetAttachment(String slotName) {
        Slot slot = mSkeleton.findSlot(slotName);
        if (slot != null) {
            slot.setAttachment(null);
            return true;
        }
        return false;
    }

    /**
     * 换肤
     *
     * @param skinName 皮肤名称
     * @return
     */
    public boolean setSkin(String skinName) {
        if (mSkeleton == null || mSkeletonData == null || TextUtils.isEmpty(skinName)) {
            return false;
        }
        if (mSkeletonData.findSkin(skinName) == null) {
            return false;
        }
        mSkeleton.setSkin(skinName);
        return true;
    }
}

