package com.nullaxis.tos.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nullaxis.tos.R;
import com.nullaxis.tos.models.ClassProgression;
import com.nullaxis.tos.models._Class;

import java.util.ArrayList;
import java.util.List;


public class ClassAddAdapter extends ArrayAdapter<_Class> {

    ClassProgression progression;
    List<_Class> archetypeClasses;

    public ClassAddAdapter(Context context, int resource, List<_Class> objects,
                           ClassProgression progression) {
        super(context, resource, objects);
        this.progression = progression;
        this.archetypeClasses = new ArrayList<>(objects);
        setAvailableClasses();
    }

    public void setAvailableClasses() {
        clear();
        for (_Class _class : archetypeClasses) {
            if (progression.getClassCircle(_class) < 3 &&
                    _class.getBase_rank() <= (progression.getProgressionSize() + 1)) {
                add(_class);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final _Class _class = getItem(position);
        int currentCircle = progression.getClassCircle(_class);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_add_class, parent, false);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.classIcon);
        TextView name = (TextView) convertView.findViewById(R.id.className);
        Button button = (Button) convertView.findViewById(R.id.addClassButton);

        icon.setImageResource(_class.getIcon_resource_id());
        name.setText(_class.getName());
        button.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progression.addNextClass(_class);
                    }
                }
        );


        // Add Rank Stars
        LinearLayout starsContainer = (LinearLayout) convertView.findViewById(R.id.starsContainer);
        starsContainer.removeAllViews();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                convertView.getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                convertView.getResources().getDisplayMetrics());
        int i = 0;
        while (i < 3) {
            ImageView star = new ImageView(convertView.getContext());
            if (i <= currentCircle) {
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
}
