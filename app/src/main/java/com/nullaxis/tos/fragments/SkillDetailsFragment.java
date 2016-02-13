package com.nullaxis.tos.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.nullaxis.tos.R;
import com.nullaxis.tos.ToSApp;
import com.nullaxis.tos.helper.ClassProgressionHelper;
import com.nullaxis.tos.helper.ClassProgressionListener;
import com.nullaxis.tos.models.Ability;
import com.nullaxis.tos.models.Build;
import com.nullaxis.tos.models.SkillProgression;
import com.wefika.flowlayout.FlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Ramiro on 1/28/16.
 */
public class SkillDetailsFragment extends Fragment implements ClassProgressionListener, CacheListener {

    private static final String LOG_TAG = "SkillDetailsFragment";

    private ClassProgressionHelper mClassProgressionHelper;
    private SkillProgression skillProgression;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.skill_details, container, false);
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

        Bundle args = getArguments();
        final int classPosition = args.getInt("classPosition");
        final int skillPosition = args.getInt("skillPosition");
        skillProgression = mClassProgressionHelper.getClassProgression()
                .getSkillProgression(classPosition, skillPosition);

        //TEMP LINE, REMOVE
        Build b = new Build("The name", mClassProgressionHelper.getClassProgression());

        TextView name = (TextView) view.findViewById(R.id.skillName);
        ImageView buttonClose = (ImageView) view.findViewById(R.id.closeSkillDetail);
        ImageView icon = (ImageView) view.findViewById(R.id.skillIcon);
        FlowLayout abilitiesContainer = (FlowLayout) view.findViewById(R.id.abilitiesContainer);
        Button buttonVideo = (Button) view.findViewById(R.id.skillVideoButton);
        final VideoView skillVideo = (VideoView) view.findViewById(R.id.skillVideo);
        final LinearLayout skillVideoContainer = (LinearLayout) view.findViewById(R.id.skillVideoContainer);
        final LinearLayout mediaControllerView = (LinearLayout) view.findViewById(R.id.skillMediaControllerView);
        TextView type1 = (TextView) view.findViewById(R.id.skillType1);
        TextView type2 = (TextView) view.findViewById(R.id.skillType2);
        TextView element = (TextView) view.findViewById(R.id.skillElement);
        TextView cooldown = (TextView) view.findViewById(R.id.skillCooldown);
        TextView description = (TextView) view.findViewById(R.id.skillDescription);

        name.setText(skillProgression.getSkill().getName());
        icon.setImageResource(skillProgression.getSkill().getIcon_resource_id());
        type1.setText(skillProgression.getSkill().getType1());
        type2.setText(skillProgression.getSkill().getType2());

        abilitiesContainer.removeAllViews();
        final ArrayList<Ability> abilities = (ArrayList) skillProgression.getSkill().getAbilities();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                getContext().getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                getContext().getResources().getDisplayMetrics());
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(width, height);

        for (final Ability ability : abilities){
            ImageView abilityIcon = new ImageView(getContext());
            abilityIcon.setLayoutParams(layoutParams);
            abilityIcon.setImageResource(ability.getIcon_resource_id());

            abilityIcon.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContainerFragment fragmentContainer =
                                    (ContainerFragment) getParentFragment();
                            Fragment abilityDetailsFragment = new AbilityDetailsFragment();
                            Bundle args = new Bundle();
                            args.putInt("classPosition", classPosition);
                            args.putInt("skillPosition", skillPosition);
                            args.putInt("abilityPosition", abilities.indexOf(ability));
                            abilityDetailsFragment.setArguments(args);
                            fragmentContainer.addFragment(abilityDetailsFragment);
                        }
                    }
            );

            abilitiesContainer.addView(abilityIcon);
        }

        if (skillProgression.getSkill().getElement().toLowerCase().equals("melee")) {
            element.setVisibility(View.GONE);
        } else {
            element.setText(skillProgression.getSkill().getElement());
        }

        cooldown.setText(Integer.toString(skillProgression.getSkill().getCooldown()) + " sec");
        // @TODO Reparse skills and replace span blocks with font blocks.
        // Span is not supported by fromHtml
        description.setText(Html.fromHtml(skillProgression.getSkill().getDescription()));

        HttpProxyCacheServer proxy = ToSApp.getProxy(getContext());
        String url = "http://www.tosbase.com/content/video/skills/" +
                Integer.toString(skillProgression.getSkill().getId()) + ".mp4";
        proxy.registerCacheListener(this, url);
        String proxyUrl = proxy.getProxyUrl(url);
        final MediaController mediaController = new MediaController(getContext());
        skillVideo.setMediaController(mediaController);
        skillVideo.setVideoPath(proxyUrl);

        updateSkillInfo();

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

        buttonVideo.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skillVideoContainer.setVisibility(View.VISIBLE);
                        skillVideo.requestFocus();
                        skillVideo.start();
                        mediaController.setAnchorView(mediaControllerView);
                        mediaControllerView.setVisibility(View.GONE);
                    }
                }
        );

//        skillVideo.setOnPreparedListener(
//                new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        mediaController.hide();
//                    }
//                }
//        );

        skillVideo.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        skillVideoContainer.setVisibility(View.GONE);
                    }
                }
        );

        skillVideo.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            if (mediaControllerView.getVisibility() == View.GONE) {
                                mediaControllerView.setVisibility(View.VISIBLE);
                            }
                        }
                        return false;
                    }
                }
        );

    }

    private void updateSkillInfo() {
        TextView info = (TextView) getView().findViewById(R.id.skillInfo);
        TextView sp = (TextView) getView().findViewById(R.id.skillSP);

        try {
            JSONObject levelInfoList = new JSONObject(skillProgression.getSkill().getLevel_list());
            JSONObject currentLevelInfoList = levelInfoList.getJSONObject(Integer.toString(
                    Math.max(skillProgression.getCurrent_rank(), 1)));
            String infoString = skillProgression.getSkill().getDetails();
            Iterator<String> iterator = currentLevelInfoList.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = currentLevelInfoList.getString(key);
                infoString = infoString.replace(key, value);
            }
            info.setText(Html.fromHtml(infoString));

            sp.setText("SP. " + Integer.toString(currentLevelInfoList.getInt("sp")));
        } catch (JSONException e) {
            Log.e("updateSkillInfo", "Wasn't able to parse Skill Level List: " + e.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mClassProgressionHelper.getClassProgression().unregisterListener(this);
        ToSApp.getProxy(getContext()).unregisterCacheListener(this);
    }

    @Override
    public void onClassProgressionUpdated() {
        updateSkillInfo();
    }

    @Override
    public void onCacheAvailable(File file, String url, int percentsAvailable) {
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        if (percentsAvailable < 100){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(percentsAvailable);
        }else{
            progressBar.setVisibility(View.GONE);
        }
        Log.d(LOG_TAG, String.format("onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, file, url));
    }

}
