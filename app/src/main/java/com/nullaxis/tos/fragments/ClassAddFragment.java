package com.nullaxis.tos.fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nullaxis.tos.R;
import com.nullaxis.tos.adapters.ClassAddAdapter;
import com.nullaxis.tos.helper.ClassProgressionHelper;
import com.nullaxis.tos.helper.ClassProgressionListener;
import com.nullaxis.tos.models._Class;

public class ClassAddFragment extends Fragment implements ClassProgressionListener {

    private ClassProgressionHelper mClassProgressionHelper;
    private ClassAddAdapter adapter;

    private boolean isCollapsed;
    private int collapsedSize;
    private static final TimeInterpolator sCollapseInterpolator = new DecelerateInterpolator(2.5F);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_class, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mClassProgressionHelper = (ClassProgressionHelper) context;
            mClassProgressionHelper.getClassProgression().registerListener(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ClassProgressionHelper");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ClassAddAdapter(
                getContext(),
                R.layout.fragment_add_class,
                _Class.getByArchetype(getContext(),
                        mClassProgressionHelper.getClassProgression().getArchetype()),
                mClassProgressionHelper.getClassProgression()
        );

        ListView classAddView = (ListView) view.findViewById(R.id.addClassList);
        classAddView.setAdapter(adapter);

        isCollapsed = true;
        LinearLayout header = (LinearLayout) view.findViewById(R.id.addClassHeader);
        header.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClassContainerFragment classContainerFragment = (ClassContainerFragment) getParentFragment();
                        Log.d("Rami", Integer.toString(classContainerFragment.getContainerHeight()));
                        if (isCollapsed) {
                            collapsedSize = classContainerFragment.getExtraFragmentHeight();
                            PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[1];
                            //arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofInt("MainFragmentHeight", -300, 0);
                            arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofInt("ExtraFragmentHeight", collapsedSize, classContainerFragment.getContainerHeight()/2);
                            ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(classContainerFragment,
                                    arrayOfPropertyValuesHolder).setDuration(400);
                            localObjectAnimator.setInterpolator(sCollapseInterpolator);
                            localObjectAnimator.start();
                        } else {
                            PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[1];
                            //arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofInt("MainFragmentHeight", 0, -300);
                            arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofInt("ExtraFragmentHeight", classContainerFragment.getExtraFragmentHeight(), collapsedSize);
                            ObjectAnimator localObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(classContainerFragment,
                                    arrayOfPropertyValuesHolder).setDuration(400);
                            localObjectAnimator.setInterpolator(sCollapseInterpolator);
                            localObjectAnimator.start();
                        }
                        isCollapsed = !isCollapsed;
                    }
                }
        );

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mClassProgressionHelper.getClassProgression().unregisterListener(this);
    }

    @Override
    public void onClassProgressionUpdated() {
        adapter.setAvailableClasses();
        adapter.notifyDataSetChanged();
    }
}
