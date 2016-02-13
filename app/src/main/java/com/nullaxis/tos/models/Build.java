package com.nullaxis.tos.models;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import com.nullaxis.tos.activities.BuildListActivity;
import com.nullaxis.tos.helper.ToSDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Build {

    private static final String LOG_TAG = "BuildModel";

    private int id;
    private String name;
    private int archetype;
    private int icon_resource_id;
    private String json_class_list;
    private String json_classes_skill_list;

    public final static class DATABASE_COLUMN {
        static final String ID = "_id";
        static final String NAME = "name";
        static final String ARCHETYPE = "archetype";
        static final String CLASS_LIST = "json_class_list";
        static final String CLASSES_SKILL_LIST = "json_classes_skill_list";
    }

    public Build(Context context, Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.ID));
        this.name = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.NAME));
        this.archetype = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.ARCHETYPE));
        this.json_class_list = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.CLASS_LIST));
        this.json_classes_skill_list = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.CLASSES_SKILL_LIST));

        _Class _class = _Class.getBaseClass(context, this.archetype);
        this.icon_resource_id = _class.getIcon_resource_id();
    }

    public Build(String name, ClassProgression classProgression) {
        this.name = name;
        this.archetype = classProgression.getArchetype();
        this.json_class_list = createJSONClassList(classProgression);
        this.json_classes_skill_list = createJSONSkillList(classProgression);
    }

    public void update(ClassProgression classProgression){
        this.archetype = classProgression.getArchetype();
        this.json_class_list = createJSONClassList(classProgression);
        this.json_classes_skill_list = createJSONSkillList(classProgression);
    }

    private String createJSONClassList(ClassProgression classProgression) {
        JSONArray jsonClasses = new JSONArray();

        for (_Class _class : classProgression.getProgression()) {
            jsonClasses.put(_class.getId());
        }

        Log.d(LOG_TAG, "JSON List List generated: " + jsonClasses.toString());

        return jsonClasses.toString();
    }

    private String createJSONSkillList(ClassProgression classProgression) {
        // Switch to iteration of HashTable
        JSONArray jsonClasses = new JSONArray();
        try {
            for (_Class _class : classProgression.getClassList()) {
                JSONObject jsonClass = new JSONObject();
                JSONObject jsonSkills = new JSONObject();
                jsonClass.put("class_id", _class.getId());
                for (SkillProgression skillProgression : classProgression.getClassSkillProgressionList(_class)){
                    if (skillProgression.getCurrent_rank() > 0){
                        jsonSkills.put(Integer.toString(skillProgression.getSkill().getId()),
                                skillProgression.getCurrent_rank());
                    }
                }
                jsonClass.put("skills", jsonSkills);
                jsonClasses.put(jsonClass);
            }
        }catch (JSONException e){
            Log.e(LOG_TAG, "An exception happened while constructing the JSON Skill List: " + e.toString());
        }

        Log.d(LOG_TAG, "JSON Skill List generated: " + jsonClasses.toString());

        return jsonClasses.toString();
    }

    public void save(Context context){
        ToSDatabase db = new ToSDatabase(context);
        if(id == 0){
            id = (int) db.saveBuild(name, archetype, json_class_list, json_classes_skill_list);
        }else {
            db.updateBuild(id, name, archetype, json_class_list, json_classes_skill_list);
        }
        db.close();
    }

    public static Build getBuild(Context context, int build_id) {
        Build build;
        ToSDatabase db = new ToSDatabase(context);

        Cursor cursor = db.getBuild(build_id);

        // @TODO Exception handling
        cursor.moveToFirst();
        build = new Build(context, cursor);

        cursor.close();
        db.close();

        return build;
    }


    public static ArrayList<Build> getAllBuilds(Context context) {
        ArrayList<Build> builds = new ArrayList<>();
        ToSDatabase db = new ToSDatabase(context);

        Cursor cursor = db.getAllBuilds();

        // @TODO Exception handling
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Build build = new Build(context, cursor);
                builds.add(build);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();

        return builds;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getArchetype() {
        return archetype;
    }

    public String getJson_class_list() {
        return json_class_list;
    }

    public String getJson_classes_skill_list() {
        return json_classes_skill_list;
    }

    public int getIcon_resource_id() {
        return icon_resource_id;
    }
}
