package com.nullaxis.tos.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.nullaxis.tos.R;
import com.nullaxis.tos.adapters.SkillsAdapter;
import com.nullaxis.tos.helper.ClassProgressionHelper;
import com.nullaxis.tos.helper.ClassProgressionListener;

public class SkillListFragment extends Fragment implements ClassProgressionListener {

    private ClassProgressionHelper mClassProgressionHelper;
    private SkillsAdapter adapter;
    private ExpandableListView skillListView;

    public SkillListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_skill_list, container, false);
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
        skillListView = (ExpandableListView) view.findViewById(R.id.classSkillsList);

        adapter = new SkillsAdapter(this, mClassProgressionHelper.getClassProgression());
        skillListView.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mClassProgressionHelper.getClassProgression().unregisterListener(this);
    }

    @Override
    public void onClassProgressionUpdated() {
        adapter.notifyDataSetChanged();
    }

}
