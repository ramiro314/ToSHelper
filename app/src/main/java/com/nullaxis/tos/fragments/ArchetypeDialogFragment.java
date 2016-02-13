package com.nullaxis.tos.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.nullaxis.tos.R;
import com.nullaxis.tos.activities.BuildActivity;
import com.nullaxis.tos.models.Archetype;
import com.wefika.flowlayout.FlowLayout;

import java.util.ArrayList;

public class ArchetypeDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.alert_archetype, container, false);
        FlowLayout archetypeContainer = (FlowLayout) v.findViewById(R.id.archetypeContainer);
        archetypeContainer.removeAllViews();

        ArrayList<Archetype> archetypes = Archetype.getAllArchetypes(getContext());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                getContext().getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                getContext().getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getContext().getResources().getDisplayMetrics());
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(width, height);
        layoutParams.setMargins(0, 0, margin, margin);

        for (final Archetype archetype : archetypes){
            ImageView archetypeIcon = new ImageView(getContext());
            archetypeIcon.setLayoutParams(layoutParams);
            archetypeIcon.setImageResource(archetype.getIcon_resource_id());

            archetypeIcon.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent mIntent = new Intent(getActivity(), BuildActivity.class);
                            mIntent.putExtra("archetype", archetype.getId());
                            startActivity(mIntent);
                        }
                    }
            );

            archetypeContainer.addView(archetypeIcon);
        }

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.closeArchetypeDialog:
                        getDialog().dismiss();
                        return true;
                }
                return true;
            }
        });

        toolbar.inflateMenu(R.menu.archetype_dialog);
        toolbar.setTitle("Create new build");
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}