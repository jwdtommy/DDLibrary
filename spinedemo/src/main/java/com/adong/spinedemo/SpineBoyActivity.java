package com.adong.spinedemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import com.adong.spineaminationlibrary.SpineBaseFragment;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

/**
 * Created by J.Tommy on 17/7/12.
 */

public class SpineBoyActivity extends FragmentActivity implements AndroidFragmentApplication.Callbacks{
    private SpineBaseFragment mSpineBaseFragment;
    private SpineBoyAdapter mSpineBoyAdapter;
    private GridLayout glButtons;
    private Button btnReplaceAttachment;
    private Button btnUnstallAttachment;
    private Button btnReplaceSkin;
    private Button btnJump;
    private Button btnRun;
    private FrameLayout flSpine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spineboy);
        assignViews();
        mSpineBaseFragment = new SpineBaseFragment();
        mSpineBoyAdapter = new SpineBoyAdapter();
        mSpineBaseFragment.setAdapter(mSpineBoyAdapter);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fl_spine, mSpineBaseFragment);
        transaction.commitAllowingStateLoss();
    }

    private void assignViews() {
        glButtons = (GridLayout) findViewById(R.id.gl_buttons);
        btnReplaceAttachment = (Button) findViewById(R.id.btn_replace_attachment);
        btnUnstallAttachment = (Button) findViewById(R.id.btn_unstall_attachment);
        btnReplaceSkin = (Button) findViewById(R.id.btn_replace_skin);
        btnJump = (Button) findViewById(R.id.btn_jump);
        btnRun = (Button) findViewById(R.id.btn_run);
        flSpine = (FrameLayout) findViewById(R.id.fl_spine);
        btnReplaceAttachment.setOnClickListener(new View.OnClickListener() {
            boolean flag;

            @Override
            public void onClick(View view) {
                flag = !flag;
                if (flag) {
                    mSpineBoyAdapter.setAttachment("gun", "");
                } else {
                    mSpineBoyAdapter.setAttachment("gun", "gun");
                }
            }
        });

        btnUnstallAttachment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mSpineBoyAdapter.setAttachment("gun", "");
            }
        });
        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpineBoyAdapter.doJump();
            }
        });
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpineBoyAdapter.doRun();
            }
        });
    }

    @Override
    public void exit() {

    }
}
