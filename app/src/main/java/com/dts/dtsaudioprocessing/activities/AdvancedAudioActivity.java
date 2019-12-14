package com.dts.dtsaudioprocessing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dts.dtsaudioprocessing.R;
import com.dts.dtsaudioprocessing.fragments.DtsFragment;
import com.dts.dtsaudioprocessing.fragments.GeqFragment;
import com.dts.dtsaudioprocessing.fragments.PreferenceFragment;
import com.dts.dtsaudioprocessing.util.FeatureManager;

public class AdvancedAudioActivity extends AppCompatActivity {

    int selectedTab = -1;
    final String TAB_POSITION = "tab_position";
    public static final String ADVANCED_AUDIO_KEY = "ADV_KEY";
    public static final int GEQ_FLAG = 1;
    public static final int STEREO_FLAG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_advanced_audio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int screenFlag = intent.getIntExtra(ADVANCED_AUDIO_KEY, -1);

        if(screenFlag == GEQ_FLAG){
            switchFragment(new GeqFragment());
        }else if(screenFlag == STEREO_FLAG){
            switchFragment(new PreferenceFragment());
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(DtsFragment fragment) {
        FragmentManager mngr = getSupportFragmentManager();
        FragmentTransaction ft = mngr.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
        mngr.executePendingTransactions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_POSITION , selectedTab);
    }

}
