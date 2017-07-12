package com.adong.spinedemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.adong.spineaminationlibrary.SpineBaseFragment;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
/**
 * Created by J.Tommy on 17/7/12.
 */

public class GoblinsActivity extends FragmentActivity implements AndroidFragmentApplication.Callbacks {
    private SpineBaseFragment mSpineBaseFragment;
    private SpineGoblinsAdapter mSpineGoblinsAdapter;
    private Button btnReplaceBoySkin;
    private Button btnReplaceGirlSkin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goblins);
        assignViews();
        mSpineBaseFragment = new SpineBaseFragment();
        mSpineGoblinsAdapter = new SpineGoblinsAdapter();
        mSpineBaseFragment.setAdapter(mSpineGoblinsAdapter);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fl_spine, mSpineBaseFragment);
        transaction.commitAllowingStateLoss();
    }

    private void assignViews() {
        btnReplaceBoySkin = (Button) findViewById(R.id.btn_replace_boy_skin);
        btnReplaceGirlSkin = (Button) findViewById(R.id.btn_replace_girl_skin);
        btnReplaceBoySkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpineGoblinsAdapter.setSkin("goblin");
            }
        });
        btnReplaceGirlSkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpineGoblinsAdapter.setSkin("goblingirl");
            }
        });
    }

    @Override
    public void exit() {

    }
}
