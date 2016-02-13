package com.nullaxis.tos.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.nullaxis.tos.R;
import com.nullaxis.tos.adapters.BuildsAdapter;
import com.nullaxis.tos.fragments.ArchetypeDialogFragment;
import com.nullaxis.tos.models.Build;

import java.util.ArrayList;


public class BuildListActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_build_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ArrayList<Build> builds = Build.getAllBuilds(this);
        BuildsAdapter adapter = new BuildsAdapter(this, builds);

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_build_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.newBuildButton:
                FragmentManager fm = getSupportFragmentManager();
                ArchetypeDialogFragment archetypeDialog = new ArchetypeDialogFragment();
                archetypeDialog.show(fm, "archetype_dialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}