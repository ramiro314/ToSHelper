package com.nullaxis.tos.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nullaxis.tos.R;
import com.nullaxis.tos.fragments.ContainerFragment;
import com.nullaxis.tos.fragments.SkillDetailsFragment;
import com.nullaxis.tos.models.ClassProgression;
import com.nullaxis.tos.models.SkillProgression;
import com.nullaxis.tos.models._Class;

public class SkillsAdapter extends BaseExpandableListAdapter {

    private Fragment parentFragment;
    private Context context;
    private ClassProgression progression;

    public SkillsAdapter(Fragment parentFragment, ClassProgression progression) {
        this.parentFragment = parentFragment;
        this.context = parentFragment.getContext();
        this.progression = progression;
    }

    @Override
    public int getGroupCount() {
        return progression.getClassList().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return progression.getClassSkillProgressionList(progression.getClassList()
                .get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return progression.getClassList().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.progression.getSkillProgression(groupPosition, childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return progression.getClassList().get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return this.progression.getSkillProgression(groupPosition, childPosition).getSkill().getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        _Class _class = (_Class) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row_class_skills, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.classIcon);
        TextView name = (TextView) convertView.findViewById(R.id.className);

        icon.setImageResource(_class.getIcon_resource_id());
        name.setText(_class.getName());

        // Calc skills left
        TextView levelsLeft = (TextView) convertView.findViewById(R.id.levelsLeft);
        int skillLevelsLeft = progression.getSkillLevelsLeft(_class);
        if (skillLevelsLeft > 0) {
            levelsLeft.setText(Integer.toString(skillLevelsLeft));
            levelsLeft.setVisibility(View.VISIBLE);
        } else {
            levelsLeft.setVisibility(View.GONE);
        }

        // Add Rank Stars
        LinearLayout starsContainer = (LinearLayout) convertView.findViewById(R.id.starsContainer);
        int currentCircle = progression.getClassCircle(_class);
        starsContainer.removeAllViews();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                convertView.getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                convertView.getResources().getDisplayMetrics());
        int i = 0;
        while (i < 3) {
            ImageView star = new ImageView(convertView.getContext());
            if (i < currentCircle) {
                star.setImageResource(R.drawable.ic_action_star);
            } else {
                star.setImageResource(R.drawable.ic_action_star_empty);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            star.setLayoutParams(layoutParams);
            starsContainer.addView(star);
            i++;
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final _Class _class = (_Class) getGroup(groupPosition);
        final SkillProgression skillProgression =
                (SkillProgression) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row_skill, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.skillIcon);
        TextView name = (TextView) convertView.findViewById(R.id.skillName);
        Button buttonUp = (Button) convertView.findViewById(R.id.levelUp);
        Button buttonDown = (Button) convertView.findViewById(R.id.levelDown);

        icon.setImageResource(skillProgression.getSkill().getIcon_resource_id());
        name.setText(skillProgression.getSkill().getName());

        icon.setOnClickListener(
                new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContainerFragment fragmentContainer = (ContainerFragment)
                                parentFragment.getParentFragment();
                        Fragment skillDetailsFragment = new SkillDetailsFragment();
                        Bundle args = new Bundle();
                        args.putInt("classPosition", groupPosition);
                        args.putInt("skillPosition", childPosition);
                        skillDetailsFragment.setArguments(args);
                        fragmentContainer.addFragment(skillDetailsFragment);
                    }
                }
        );

        buttonUp.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (progression.getSkillLevelsLeft(_class) > 0) {
                            skillProgression.rankUp();
                            progression.notifyUpdate();
                        }
                    }
                }
        );

        buttonDown.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skillProgression.rankDown();
                        progression.notifyUpdate();
                    }
                }
        );

        TextView skillLevelCurrent = (TextView) convertView.findViewById(R.id.skillLevelCurrent);
        TextView skillLevelMax = (TextView) convertView.findViewById(R.id.skillLevelMax);
        skillLevelCurrent.setText(Integer.toString(skillProgression.getCurrent_rank()));
        skillLevelMax.setText(Integer.toString(skillProgression.getMax_rank()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
