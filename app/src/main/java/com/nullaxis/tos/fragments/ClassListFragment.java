package com.nullaxis.tos.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nullaxis.tos.R;
import com.nullaxis.tos.adapters.ClassListAdapter;
import com.nullaxis.tos.helper.ClassProgressionHelper;
import com.nullaxis.tos.helper.ClassProgressionListener;
import com.nullaxis.tos.helper.OnStartDragListener;
import com.nullaxis.tos.helper.SimpleItemTouchHelperCallback;

public class ClassListFragment extends Fragment implements OnStartDragListener, ClassProgressionListener {

    private ClassProgressionHelper mClassProgressionHelper;
    private ItemTouchHelper mItemTouchHelper;
    private ClassListAdapter adapter;

    public ClassListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_list, container, false);
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

        adapter = new ClassListAdapter(getContext(), this,
                mClassProgressionHelper.getClassProgression());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.classesList);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
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
