package com.nullaxis.tos.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ToSDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "ToSDatabase.db";
    private static final int DATABASE_VERSION = 1;


    public ToSDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        context.deleteDatabase(DATABASE_NAME);
//        setForcedUpgrade();
    }

    public Cursor getArchetypes() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTable = "archetypes";
        String [] sqlSelect = {"id _id", "name", "icon"};
        String orderBy = "id";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, orderBy);

        c.moveToFirst();
        return c;

    }

    public Cursor getClass(int class_id) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTable = "classes";
        String [] sqlSelect = {"id _id", "archetype", "rank", "name", "icon"};
        String sqlQuery = "_id = ?";
        String [] sqlParams = {Integer.toString(class_id)};

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, sqlQuery, sqlParams, null, null, null);

        return c;

    }

    public Cursor getClassesByRank(int archetype, int rank) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTable = "classes";
        String [] sqlSelect = {"id _id", "archetype", "rank", "name", "icon"};
        String sqlQuery = "archetype = ? AND rank <= ? AND rank > 0";
        String [] sqlParams = {Integer.toString(archetype), Integer.toString(rank)};
        String sqlSort = "rank ASC";

        qb.setTables(sqlTable);

        return qb.query(db, sqlSelect, sqlQuery, sqlParams, null, null, sqlSort);

    }

    public Cursor getClassSkills(int _class, int circle) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTable = "skills";
        String [] sqlSelect = {"id _id", "name", "icon", "circle", "identifier", "rank_level",
                "max_level", "class", "video", "desc", "details", "type1", "type2", "cooldown",
                "element", "req_stance", "level_list", "use_overheat"};
        String sqlQuery = "class = ? AND circle = ?";
        String [] sqlParams = {Integer.toString(_class), Integer.toString(circle)};

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, sqlQuery, sqlParams, null, null, null);

        c.moveToFirst();
        return c;

    }


    public Cursor getSkillAbilities(int skill) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTable = "skill_abilities";
        String [] sqlSelect = {"id _id", "name", "icon", "circle", "req_skill_level", "skill_id",
                "desc", "type", "max_level"};
        String sqlQuery = "skill_id = ?";
        String [] sqlParams = {Integer.toString(skill)};

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, sqlQuery, sqlParams, null, null, null);

        c.moveToFirst();
        return c;

    }

    public Cursor getBuild(int build_id) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTable = "builds";
        String [] sqlSelect = {"id _id", "name", "archetype", "json_class_list", "json_classes_skill_list"};
        String sqlQuery = "id = ?";
        String [] sqlParams = {Integer.toString(build_id)};

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, sqlQuery, sqlParams, null, null, null);

        return c;

    }


    public Cursor getAllBuilds() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTable = "builds";
        String [] sqlSelect = {"id _id", "name", "archetype", "json_class_list", "json_classes_skill_list"};

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        return c;
    }

    public long saveBuild(String name, int archetype, String json_class_list, String json_classes_skill_list) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(
                "INSERT INTO builds (name, archetype, json_class_list, json_classes_skill_list) VALUES (?, ?, ?, ?)");
        statement.bindString(1, name);
        statement.bindLong(2, archetype);
        statement.bindString(3, json_class_list);
        statement.bindString(4, json_classes_skill_list);
        return statement.executeInsert();
    }

    public void updateBuild(int id, String name, int archetype, String json_class_list, String json_classes_skill_list) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(
                "UPDATE builds SET name=?, archetype=?, json_class_list=?, json_classes_skill_list=? WHERE id=?");
        statement.bindString(1, name);
        statement.bindLong(2, archetype);
        statement.bindString(3, json_class_list);
        statement.bindString(4, json_classes_skill_list);
        statement.bindLong(5, id);
        statement.executeUpdateDelete();
    }

}
