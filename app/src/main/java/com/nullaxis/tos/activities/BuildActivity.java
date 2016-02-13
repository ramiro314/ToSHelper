package com.nullaxis.tos.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.nullaxis.tos.R;
import com.nullaxis.tos.fragments.ClassAddFragment;
import com.nullaxis.tos.fragments.ClassContainerFragment;
import com.nullaxis.tos.fragments.ClassListFragment;
import com.nullaxis.tos.fragments.ContainerFragment;
import com.nullaxis.tos.fragments.SkillListFragment;
import com.nullaxis.tos.helper.ClassProgressionHelper;
import com.nullaxis.tos.models.Build;
import com.nullaxis.tos.models.ClassProgression;

import java.util.ArrayList;
import java.util.List;


public class BuildActivity extends BaseActivity implements ClassProgressionHelper{


    private ClassProgression classProgression;
    private Build build;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_build);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Generate the ClassProgression based on the archetype
        Bundle b = getIntent().getExtras();

        int archetype = b.getInt("archetype");
        if (archetype > 0){
            classProgression = new ClassProgression(this, archetype);
        }else{
            int build_id = b.getInt("build_id");
            build = Build.getBuild(this, build_id);
            classProgression = new ClassProgression(this, build);
            ab.setTitle("ToS - " + build.getName());
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_build, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.saveBuildButton:
                if(build==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Save build as:");
                    final EditText input = new EditText(this);

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            build = new Build(input.getText().toString(), getClassProgression());
                            build.save(getBaseContext());
                            Log.d("Rami", "The new build ID is: " + build.getId());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }else{
                    build.update(getClassProgression());
                    build.save(getBaseContext());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ClassContainerFragment classFragmentContainer = new ClassContainerFragment();
        Bundle classFragmentArgs = new Bundle();
        classFragmentArgs.putString("className", ClassListFragment.class.getCanonicalName());
        classFragmentArgs.putString("extraClassName", ClassAddFragment.class.getCanonicalName());
        classFragmentContainer.setArguments(classFragmentArgs);

        Fragment skillFragmentContainer = new ContainerFragment();
        Bundle skillFragmentArgs = new Bundle();
        skillFragmentArgs.putString("className", SkillListFragment.class.getCanonicalName());
        skillFragmentContainer.setArguments(skillFragmentArgs);

        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(classFragmentContainer, "Class Tree");
        adapter.addFragment(skillFragmentContainer, "Skills");
        viewPager.setAdapter(adapter);
    }

    public ClassProgression getClassProgression() {
        return classProgression;
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}