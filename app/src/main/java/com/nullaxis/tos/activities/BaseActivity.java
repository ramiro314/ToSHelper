package com.nullaxis.tos.activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.nullaxis.tos.R;

/**
 * Created by Ramiro on 2/11/16.
 */
abstract public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout mDrawerLayout;

    protected void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent mIntent;
                        switch (menuItem.getItemId()){
                            case R.id.nav_build_list:
                                mIntent = new Intent(mDrawerLayout.getContext(), BuildListActivity.class);
                                startActivity(mIntent);
                                break;
                            case R.id.nav_settings:
                                mIntent = new Intent(mDrawerLayout.getContext(), SettingsActivity.class);
                                startActivity(mIntent);
                                break;
                            case R.id.nav_about:
                                mIntent = new Intent(mDrawerLayout.getContext(), AboutActivity.class);
                                startActivity(mIntent);
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
