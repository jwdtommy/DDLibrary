package com.hyena.framework.samples;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.content.ContextCompat;

import com.hyena.framework.app.activity.NavigateActivity;
import com.hyena.framework.app.fragment.BaseFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.UIViewFactory;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.samples.evn.ViewFactoryImpl;
import com.hyena.framework.samples.plugin.InstrumentationHook;
import com.hyena.framework.utils.ToastUtils;

public class MainActivity extends NavigateActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InstrumentationHook.hook();
        UIViewFactory.getViewFactory().registViewBuilder(new ViewFactoryImpl());
//        showFragment(BaseUIFragment.newFragment(this, WebViewBrowser.class, null));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                boolean result = HttpHelper.storeFile("http://knowapp.b0.upaiyun.com/ss/cityList/cityList20.json",
//                        new File(Environment.getExternalStorageDirectory(), "ss.json").getAbsolutePath(), null);
//                LogUtil.v("yangzc", result + "");
//            }
//        }).start();
        showFragment(BaseUIFragment.newFragment(this, Pull2RefreshFragment.class, null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int checkOp = appOpsManager.checkOp(AppOpsManager.OPSTR_CAMERA, Process.myUid(), getPackageName());
            if (checkOp == AppOpsManager.MODE_IGNORED) {
                // 权限被拒绝了
                ToastUtils.showShortToast(this, "ignored");
            } else {
                ToastUtils.showShortToast(this, "yes");
            }
        }

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            ToastUtils.showShortToast(this, "PERMISSION_GRANTED");
        } else {
            ToastUtils.showShortToast(this, "PERMISSION_DENIED");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.v("yangzc", "onActivityResult");
    }

    @Override
    public void onPreCreate() {
        super.onPreCreate();
        setTranslucentStatus(true);
    }

    @Override
    public BaseUIFragmentHelper getUIFragmentHelper(BaseFragment fragment) {
        if (fragment instanceof BaseUIFragment) {
            return new BaseUIFragmentHelper((BaseUIFragment)fragment);
        }
        return null;
    }

    @Override
    public void showFragment(BaseFragment fragment) {
        super.showFragment(fragment);
        if (fragment instanceof BaseUIFragment) {
            ((BaseUIFragment) fragment).setStatusTintBarColor(Color.BLUE);
            ((BaseUIFragment) fragment).setStatusTintBarEnable(true);
        }
    }
}
