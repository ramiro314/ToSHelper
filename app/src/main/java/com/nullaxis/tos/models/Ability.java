package com.nullaxis.tos.models;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import com.nullaxis.tos.helper.ToSDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ramiro on 2/6/16.
 */
public class Ability {
    private int id;
    private String name;
    private String icon;
    private int icon_resource_id;
    private String type;
    private int circle;
    private int max_level;
    private String description;
    private int required_skill_level;
    private int skill_id;

    public final static class DATABASE_COLUMN {
        static final String ID = "_id";
        static final String NAME = "name";
        static final String ICON = "icon";
        static final String TYPE = "type";
        static final String CIRCLE = "circle";
        static final String MAX_LEVEL = "max_level";
        static final String DESCRIPTION = "desc";
        static final String REQUIRED_SKILL_LEVEL = "req_skill_level";
        static final String SKILL = "skill_id";
    }

    public Ability(Context context, Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.ID));
        this.name = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.NAME));
        this.icon = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.ICON));
        this.type = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.TYPE));
        this.circle = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.CIRCLE));
        this.max_level = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.MAX_LEVEL));
        this.description = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.DESCRIPTION));
        this.required_skill_level = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.REQUIRED_SKILL_LEVEL));
        this.skill_id = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.SKILL));

        Resources resources = context.getResources();
        this.icon_resource_id = resources.getIdentifier(this.icon, "drawable",
                context.getPackageName());

    }


    public static ArrayList<Ability> getSkillAbilities(Context context, ToSDatabase db, Skill skill){
        ArrayList<Ability> skillAbilities = new ArrayList<>();

        Cursor cursor = db.getSkillAbilities(skill.getId());

        // @TODO Exception handling
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                skillAbilities.add(new Ability(context, cursor));
                cursor.moveToNext();
            }
        }

        cursor.close();

        return skillAbilities;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ability ability = (Ability) o;

        return id == ability.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getIcon_resource_id() {
        return icon_resource_id;
    }

    public String getType() {
        return type;
    }

    public int getCircle() {
        return circle;
    }

    public int getMax_level() {
        return max_level;
    }

    public String getDescription() {
        return description;
    }

    public int getRequired_skill_level() {
        return required_skill_level;
    }

    public int getSkill_id() {
        return skill_id;
    }
}
