package com.nullaxis.tos.models;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.nullaxis.tos.helper.ToSDatabase;

import java.util.ArrayList;
import java.util.List;

public class Skill {

    private int id;
    private String name;
    private String icon;
    private int icon_resource_id;
    private int circle;
    private String identifier;
    private int rank_per_circle;
    private int max_level;
    private int _class;
    private String video;
    private String description;
    private String details;
    private String type1;
    private String type2;
    private int cooldown;
    private String element;
    private String required_stance;
    private String level_list;
    private int use_overheat;
    private List<Ability> abilities;

    public final static class DATABASE_COLUMN {
        static final String ID = "_id";
        static final String NAME = "name";
        static final String ICON = "icon";
        static final String CIRCLE = "circle";
        static final String IDENTIFIER = "identifier";
        static final String RANK_LEVEL = "rank_level";
        static final String MAX_LEVEL = "max_level";
        static final String CLASS = "class";
        static final String VIDEO = "video";
        static final String DESCRIPTION = "desc";
        static final String DETAILS = "details";
        static final String TYPE1 = "type1";
        static final String TYPE2 = "type2";
        static final String COOLDOWN = "cooldown";
        static final String ELEMENT = "element";
        static final String REQUIRED_STANCE = "req_stance";
        static final String LEVEL_LIST = "level_list";
        static final String USE_OVERHEAT = "use_overheat";
    }

    public Skill(Context context, Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.ID));
        this.name = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.NAME));
        this.icon = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.ICON));
        this.circle = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.CIRCLE));
        this.identifier = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.IDENTIFIER));
        this.rank_per_circle = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.RANK_LEVEL));
        this.max_level = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.MAX_LEVEL));
        this._class = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.CLASS));
        this.video = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.VIDEO));
        this.description = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.DESCRIPTION));
        this.details = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.DETAILS));
        this.type1 = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.TYPE1));
        this.type2 = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.TYPE2));
        this.cooldown = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.COOLDOWN));
        this.element = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.ELEMENT));
        this.required_stance = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.REQUIRED_STANCE));
        this.level_list = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.LEVEL_LIST));
        this.use_overheat = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.USE_OVERHEAT));

        Resources resources = context.getResources();
        this.icon_resource_id = resources.getIdentifier(this.icon, "drawable",
                context.getPackageName());

    }

    public static ArrayList<Skill> getClassSkills(Context context, _Class _class, int circle){
        ArrayList<Skill> classSkills = new ArrayList<>();
        ToSDatabase db = new ToSDatabase(context);

        Cursor cursor = db.getClassSkills(_class.getId(), circle);

        // @TODO Exception handling
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Skill skill = new Skill(context, cursor);
                skill.setAbilities(Ability.getSkillAbilities(context, db, skill));
                classSkills.add(skill);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();

        return classSkills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Skill skill = (Skill) o;

        return id == skill.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCircle() {
        return circle;
    }

    public void setCircle(int circle) {
        this.circle = circle;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getRank_per_circle() {
        return rank_per_circle;
    }

    public void setRank_per_circle(int rank_per_circle) {
        this.rank_per_circle = rank_per_circle;
    }

    public int getMax_level() {
        return max_level;
    }

    public void setMax_level(int max_level) {
        this.max_level = max_level;
    }

    public int getSkillClass() {
        return _class;
    }

    public void setSkillClass(int _class) {
        this._class = _class;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getRequired_stance() {
        return required_stance;
    }

    public void setRequired_stance(String required_stance) {
        this.required_stance = required_stance;
    }

    public String getLevel_list() {
        return level_list;
    }

    public void setLevel_list(String level_list) {
        this.level_list = level_list;
    }

    public int getUse_overheat() {
        return use_overheat;
    }

    public void setUse_overheat(int use_overheat) {
        this.use_overheat = use_overheat;
    }

    public int getIcon_resource_id() {
        return icon_resource_id;
    }

    public void setIcon_resource_id(int icon_resource_id) {
        this.icon_resource_id = icon_resource_id;
    }

    public int get_class() {
        return _class;
    }

    public void set_class(int _class) {
        this._class = _class;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }
}
