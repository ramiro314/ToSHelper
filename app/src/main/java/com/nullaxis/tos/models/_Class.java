package com.nullaxis.tos.models;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.nullaxis.tos.helper.ToSDatabase;

import java.util.ArrayList;

public class _Class {

    private int id;
    private int archetype;
    private int base_rank;
    private String name;
    private String icon;
    private int icon_resource_id;

    public final static class DATABASE_COLUMN {
        static final String ID = "_id";
        static final String ARCHETYPE = "archetype";
        static final String BASE_RANK = "rank";
        static final String NAME = "name";
        static final String ICON = "icon";
    }

    public _Class(Context context, Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.ID));
        this.archetype = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.ARCHETYPE));
        this.base_rank = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.BASE_RANK));
        this.name = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.NAME));
        this.icon = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.ICON));

        Resources resources = context.getResources();
        this.icon_resource_id = resources.getIdentifier(this.icon, "drawable",
                context.getPackageName());
    }

    public static ArrayList<_Class> getByArchetype(Context context, int archetype){
        ArrayList<_Class> classes = new ArrayList<>();
        ToSDatabase db = new ToSDatabase(context);

        //@TODO max rank should be a constant
        Cursor cursor = db.getClassesByRank(archetype, 7);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                classes.add(new _Class(context, cursor));
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();

        return classes;
    }

    public static _Class getBaseClass(Context context, int archetype){
        _Class baseClass;
        ToSDatabase db = new ToSDatabase(context);

        Cursor cursor = db.getClassesByRank(archetype, 1);

        // @TODO Exception handling
        cursor.moveToFirst();
        baseClass = new _Class(context, cursor);

        cursor.close();
        db.close();

        return baseClass;
    }

    public static _Class getClass(Context context, int class_id){
        _Class _class;
        ToSDatabase db = new ToSDatabase(context);

        Cursor cursor = db.getClass(class_id);

        // @TODO Exception handling
        cursor.moveToFirst();
        _class = new _Class(context, cursor);

        cursor.close();
        db.close();

        return _class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        _Class aClass = (_Class) o;

        return id == aClass.id;

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

    public int getArchetype() {
        return archetype;
    }

    public void setArchetype(int archetype) {
        this.archetype = archetype;
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

    public int getIcon_resource_id() {
        return icon_resource_id;
    }

    public void setIcon_resource_id(int icon_resource_id) {
        this.icon_resource_id = icon_resource_id;
    }

    public int getBase_rank() {
        return base_rank;
    }

    public void setBase_rank(int base_rank) {
        this.base_rank = base_rank;
    }
}
