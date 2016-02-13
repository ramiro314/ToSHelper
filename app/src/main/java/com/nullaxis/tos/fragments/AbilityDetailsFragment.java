package com.nullaxis.tos.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nullaxis.tos.R;
import com.nullaxis.tos.helper.ClassProgressionHelper;
import com.nullaxis.tos.models.Ability;


public class AbilityDetailsFragment extends Fragment {

    private static final String LOG_TAG = "AbilityDetailsFragment";

    private ClassProgressionHelper mClassProgressionHelper;
    private Ability ability;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ability_details, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mClassProgressionHelper = (ClassProgressionHelper) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ClassProgressionHelper");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        int classPosition = args.getInt("classPosition");
        int skillPosition = args.getInt("skillPosition");
        int abilityPosition = args.getInt("abilityPosition");
        ability = mClassProgressionHelper.getClassProgression()
                .getSkillAbility(classPosition, skillPosition, abilityPosition);

        TextView name = (TextView) view.findViewById(R.id.abilityName);
        ImageView buttonClose = (ImageView) view.findViewById(R.id.closeAbilityDetail);
        ImageView icon = (ImageView) view.findViewById(R.id.abilityIcon);
        TextView type = (TextView) view.findViewById(R.id.abilityType);
        TextView maxLevel = (TextView) view.findViewById(R.id.abilityMaxLevel);
        TextView description = (TextView) view.findViewById(R.id.abilityDescription);

        name.setText(ability.getName());
        icon.setImageResource(ability.getIcon_resource_id());
        type.setText(ability.getType());
        maxLevel.setText("Max Level: " + Integer.toString(ability.getMax_level()));

        description.setText(Html.fromHtml(ability.getDescription()));

        buttonClose.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContainerFragment fragmentContainer =
                                (ContainerFragment) getParentFragment();
                        fragmentContainer.hideExtraFragment();
                    }
                }
        );

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
