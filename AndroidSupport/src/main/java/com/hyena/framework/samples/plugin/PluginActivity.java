package com.hyena.framework.samples.plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hyena.framework.samples.R;

/**
 * Created by yangzc on 16/6/29.
 */
public class PluginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_game_map);
//        Intent intent = new Intent(this, PluginActivity2.class);
//        startActivity(intent);
    }
}
