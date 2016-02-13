package com.nullaxis.tos.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nullaxis.tos.R;
import com.nullaxis.tos.activities.BuildActivity;
import com.nullaxis.tos.models.Build;

import java.util.List;

public class BuildsAdapter extends ArrayAdapter<Build>{

    public BuildsAdapter(Context context, List<Build> objects) {
        super(context, -1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Build build = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_build, parent, false);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.buildIcon);
        TextView name = (TextView) convertView.findViewById(R.id.buildName);
        Button editButton = (Button) convertView.findViewById(R.id.editBuildButton);

        icon.setImageResource(build.getIcon_resource_id());
        name.setText(build.getName());

        editButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(getContext(), BuildActivity.class);
                        mIntent.putExtra("build_id", build.getId());
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(mIntent);
                    }
                }
        );

        return convertView;
    }

}
