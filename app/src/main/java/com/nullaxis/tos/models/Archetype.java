package com.nullaxis.tos.models;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.nullaxis.tos.helper.ToSDatabase;

import java.util.ArrayList;

public class Archetype {

    private static final String LOG_TAG = "ArchetypeModel";

    private int id;
    private String name;
    private String icon;
    private int icon_resource_id;

    public final static class DATABASE_COLUMN {
        static final String ID = "_id";
        static final String NAME = "name";
        static final String ICON = "icon";
    }

    public Archetype(Context context, Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(DATABASE_COLUMN.ID));
        this.name = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.NAME));
        this.icon = cursor.getString(cursor.getColumnIndex(DATABASE_COLUMN.ICON));

        Resources resources = context.getResources();
        this.icon_resource_id = resources.getIdentifier(this.icon, "drawable",
                context.getPackageName());
    }

    public static ArrayList<Archetype> getAllArchetypes(Context context) {
        ArrayList<Archetype> archetypes = new ArrayList<>();
        ToSDatabase db = new ToSDatabase(context);

        Cursor cursor = db.getArchetypes();

        // @TODO Exception handling
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Archetype build = new Archetype(context, cursor);
                archetypes.add(build);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();

        return archetypes;
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
}
