package com.nullaxis.tos.models;

import android.content.Context;
import android.util.Log;

import com.nullaxis.tos.helper.ClassProgressionListener;
import com.nullaxis.tos.helper.ToSDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ClassProgression {
    private static final String LOG_TAG = "ClassProgression";

    private Context context;
    private int archetype;
    private List<_Class> progression;
    private List<_Class> classList;
    private HashMap<_Class, List<SkillProgression>> classesSkillProgression ;

    private List<ClassProgressionListener> listeners;

    public ClassProgression(Context context, int archetype) {
        this.context = context;
        this.archetype = archetype;
        this.progression = new ArrayList<>();
        this.classList = new ArrayList<>();
        this.classesSkillProgression = new HashMap<>();
        this.listeners = new ArrayList<>();

        addNextClass(_Class.getBaseClass(context, archetype));
    }

    public ClassProgression(Context context, Build build){
        this.context = context;
        this.archetype = build.getArchetype();
        this.progression = new ArrayList<>();
        this.classList = new ArrayList<>();
        this.classesSkillProgression = new HashMap<>();
        this.listeners = new ArrayList<>();

        try{
            JSONArray json_class_list = new JSONArray(build.getJson_class_list());
            JSONArray json_classes_skill_list = new JSONArray(build.getJson_classes_skill_list());

            Log.d(LOG_TAG, "The class list is: "+ json_class_list.toString());

            for(int i = 0; i < json_class_list.length(); ++i) {
                addNextClass(_Class.getClass(context, json_class_list.getInt(i)));
            }

            // @TODO add breaks to improve perfomance
            for(int i = 0; i < json_classes_skill_list.length(); ++i) {
                JSONObject jsonClass = json_classes_skill_list.getJSONObject(i);
                JSONObject jsonSkills = jsonClass.getJSONObject("skills");
                for(HashMap.Entry<_Class, List<SkillProgression>> entry : classesSkillProgression.entrySet()){
                    if(entry.getKey().getId() == jsonClass.getInt("class_id")){
                        Iterator<SkillProgression> iterator = entry.getValue().listIterator();
                        while (iterator.hasNext()){
                            SkillProgression skillProgression = iterator.next();
                            Iterator<String> skills_ids = jsonSkills.keys();
                            while (skills_ids.hasNext()){
                                String skill_id = skills_ids.next();
                                if(Integer.toString(skillProgression.getSkill().getId()).equals(skill_id)){
                                    skillProgression.setCurrent_rank(jsonSkills.getInt(skill_id));
                                }
                            }
                        }
                    }
                }
            }
        }catch (JSONException e){
            Log.e(LOG_TAG, "Wasn't able to parse Build ClassProgression: " + e.toString());
        }
    }

    public int getClassCircle(_Class _class){
        return Collections.frequency(progression, _class);
    }

    public List<_Class> getClassList() {
        return classList;
    }

    public int getArchetype() {
        return archetype;
    }

    public void registerListener(ClassProgressionListener listener){
        listeners.add(listener);
        Log.d("RegisterListener", listener.getClass().getCanonicalName() + "[" + listener.hashCode() + "] was registered.");
    }

    public void unregisterListener(ClassProgressionListener listener) {
        listeners.remove(listener);
        Log.d("UnregisterListener", listener.getClass().getCanonicalName() + "[" + listener.hashCode() + "] was unregistered.");
    }

    public void notifyUpdate(){
        //@// TODO: Add exception handling
        for(ClassProgressionListener listener : listeners){
            Log.d(LOG_TAG, "Calling "+listener.toString()+" ClassProgression update method");
            listener.onClassProgressionUpdated();
        }
    }

    public int getClassCircleByPosition(int position) {
        _Class currentClass = progression.get(position);
        int currentCircle = 0;
        int currentPosition = 0;
        for(_Class _class : progression){
            if (_class.equals(currentClass)) currentCircle++;
            if (currentPosition == position) break;
            currentPosition++;
        }
        return currentCircle;
    }

    public int getProgressionSize() {
        return progression.size();
    }

    public _Class getProgressionClass(int position) {
        return progression.get(position);
    }

    public void addNextClass(_Class _class){
        if(getProgressionSize() < 7) {
            progression.add(_class);
            if (!classList.contains(_class)) classList.add(_class);
            if (!classesSkillProgression.containsKey(_class)) classesSkillProgression.put(_class,
                    new ArrayList<SkillProgression>());

            int currentCircle = getClassCircle(_class);
            ArrayList<Skill> newCircleSkills = Skill.getClassSkills(context, _class, currentCircle);

            for (SkillProgression skillProgression : classesSkillProgression.get(_class))
                skillProgression.updateCircle(currentCircle);

            for (Skill skill : newCircleSkills)
                classesSkillProgression.get(_class).add(new SkillProgression(skill, currentCircle));

            notifyUpdate();
        }
    }

    public _Class removeProgressionClass(int position) {
        _Class _class = progression.remove(position);
        Log.d(LOG_TAG, _class.getName() + " removed from ClassProgression");
        if (progression.contains(_class)) {
            int currentCircle = getClassCircle(_class);
            Iterator<SkillProgression> iterator = classesSkillProgression.get(_class).listIterator();
            while (iterator.hasNext()){
                SkillProgression skillProgression = iterator.next();
                if(skillProgression.getSkill().getCircle() > currentCircle){
                    iterator.remove();
                } else {
                    skillProgression.updateCircle(currentCircle);
                }
            }
        }else{
            classList.remove(_class);
            classesSkillProgression.remove(_class);
        }

        return _class;
    }

    public void switchClassPositions(int fromPosition, int toPosition) {
        Log.d(LOG_TAG, "Attempting to move _Class from "+fromPosition+" to "+toPosition);
        _Class prev = progression.remove(fromPosition);
        progression.add(toPosition, prev);

        Log.d(LOG_TAG, prev.getName()+" from "+fromPosition+" to "+toPosition);

        classList.clear();
        for(_Class _class : progression){
            if (!classList.contains(_class)) classList.add(_class);
        }

    }

    public int getSkillLevelsLeft(_Class _class) {
        int currentCircle = getClassCircle(_class);
        // @TODO make 15 a constant somewhere
        int skillsLeft = 15 * currentCircle;
        for(SkillProgression skillProgression : classesSkillProgression.get(_class)){
            skillsLeft -= skillProgression.getCurrent_rank();
        }
        return skillsLeft;
    }

    public SkillProgression getSkillProgression(int classPosition, int skillPosition) {
        return classesSkillProgression.get(classList.get(classPosition)).get(skillPosition);
    }

    public Ability getSkillAbility(int classPosition, int skillPosition, int abilityPosition){
        return getSkillProgression(classPosition, skillPosition).getSkill().getAbilities().get(abilityPosition);
    }

    public List<SkillProgression> getClassSkillProgressionList(_Class _class) {
        return classesSkillProgression.get(_class);
    }

    public List<_Class> getProgression() {
        return progression;
    }
}
