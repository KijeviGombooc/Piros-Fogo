package com.kijevigombooc.pirosfogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "pirosfogo.db";
    private static final int DB_VER = 1;
    private static final String TABLE_PROFILES = "Profiles";
    private static final String COL_PROFILE_ID = "ProfileID";
    private static final String COL_PROFILE_NAME = "Name";
    private static final String COL_PROFILE_IMAGE = "Image";

    private static final String TABLE_MATCHES = "Matches";
    private static final String COL_MATCH_ID = "MatchID";
    private static final String COL_MATCH_DATE = "MatchDate";

    private static final String TABLE_SCORES = "Scores";
    private static final String COL_SCORE_ID = "ScoreID";
    private static final String COL_PLAYER_INDEX = "PlayerIndex";
    private static final String COL_SCORE = "Score";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMatchTable = "CREATE TABLE "+ TABLE_MATCHES + " ("+ COL_MATCH_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " + COL_MATCH_DATE + " NUMERIC NOT NULL);";
        String createPlayersTable = "CREATE TABLE " + TABLE_PROFILES + " (" + COL_PROFILE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " + COL_PROFILE_NAME + " TEXT, " + COL_PROFILE_IMAGE + " BLOB);";
        String createScoresTable = "CREATE TABLE " + TABLE_SCORES + " (" + COL_SCORE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " + COL_MATCH_ID + " INTEGER NOT NULL, " + COL_PROFILE_ID + " INTEGER, " + COL_PLAYER_INDEX + " INTEGER NOT NULL, " + COL_SCORE + " BLOB);";

        db.execSQL(createMatchTable);
        db.execSQL(createPlayersTable);
        db.execSQL(createScoresTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        onCreate(db);
    }

    long addProfile(String name, byte[] image) throws SQLiteException{
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PROFILE_NAME, name);
        cv.put(COL_PROFILE_IMAGE, image);
        long res =  database.insert(TABLE_PROFILES, null, cv);
        database.close();
        return res;
    }
    long removeProfile(long id) throws SQLiteException{
        SQLiteDatabase database = this.getReadableDatabase();
        long res = database.delete(TABLE_PROFILES, COL_PROFILE_ID + "=?", new String[]{String.valueOf(id)});
        database.close();
        return res;
    }
    long updateProfile(long id, String name, byte[] image) throws SQLiteException{
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PROFILE_NAME, name);
        cv.put(COL_PROFILE_IMAGE, image);

        long res = database.update(TABLE_PROFILES, cv, COL_PROFILE_ID + "=?", new String[]{String.valueOf(id)});
        database.close();
        return res;
    }
    ArrayList<Profile> GetAllProfiles() throws SQLiteException{
        SQLiteDatabase database = this.getReadableDatabase();
        String[] columns = {COL_PROFILE_ID, COL_PROFILE_NAME, COL_PROFILE_IMAGE};
        Cursor c = database.query(TABLE_PROFILES, columns, null, null, null, null, null);

        ArrayList<Profile> profiles = new ArrayList<>();

        int colIndexID = c.getColumnIndex(COL_PROFILE_ID);
        int colIndexName = c.getColumnIndex(COL_PROFILE_NAME);
        int colIndexImage = c.getColumnIndex(COL_PROFILE_IMAGE);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            profiles.add(new Profile(
                    (long)c.getInt(colIndexID),
                    c.getString(colIndexName),
                    c.getBlob(colIndexImage)));
        }

        database.close();
        return profiles;
    }
    Profile getProfileByID(long id) throws SQLiteException{
        Profile p = null;

        SQLiteDatabase database = this.getReadableDatabase();
        String[] columns = {COL_PROFILE_ID, COL_PROFILE_NAME, COL_PROFILE_IMAGE};
        Cursor c = database.query(TABLE_PROFILES, columns,  COL_PROFILE_ID+ " = ?", new String[]{String.valueOf(id)}, null, null, null);

        int colIndexID = c.getColumnIndex(COL_PROFILE_ID);
        int colIndexName = c.getColumnIndex(COL_PROFILE_NAME);
        int colIndexImage = c.getColumnIndex(COL_PROFILE_IMAGE);
        if(c.moveToFirst())
            p = new Profile(
                    (long)c.getInt(colIndexID),
                    c.getString(colIndexName),
                    c.getBlob(colIndexImage));

        database.close();
        return p;
    }
    int getProfileMatchCount(long id){

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COL_PROFILE_ID};
        Cursor c = db.query(TABLE_SCORES, columns, COL_PROFILE_ID + " = ? ", new String[]{String.valueOf(id)}, null, null, null);
        return c.getCount();
    }
    int getProfileTotalReds(long id){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_PROFILE_ID, COL_SCORE};
        Cursor c = db.query(TABLE_SCORES, columns, COL_PROFILE_ID + " = ? ", new String[]{String.valueOf(id)}, null, null, null);

        int colIndex = c.getColumnIndex(COL_SCORE);

        int sum = 0;
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            byte[] score = c.getBlob(colIndex);
            for (int i = 0; i < score.length && score[i] >= 0; i++) {
                sum += score[i];
            }
        }

        return sum;
    }


    long addMatch(String date, Long playerIDs[], byte scores[][]) throws SQLiteException{

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_MATCH_DATE, date);
        long matchID = db.insert(TABLE_MATCHES, null, cv);
        if(matchID == -1)
        {
            db.close();
            return matchID;
        }

        for(int i = 0; i < 4; i++)
        {
            cv.clear();
            cv.put(COL_MATCH_ID, matchID);
            cv.put(COL_PROFILE_ID, playerIDs[i]);
            cv.put(COL_PLAYER_INDEX, i);
            cv.put(COL_SCORE, scores[i]);
            long scoreID = db.insert(TABLE_SCORES, null, cv);
            if(scoreID == -1)
            {
                db.close();
                return scoreID;
            }
        }

        db.close();
        return matchID;
    }
    long removeMatch(long id) throws SQLiteException{

        SQLiteDatabase database = this.getReadableDatabase();

        long res1 = database.delete(TABLE_MATCHES, COL_MATCH_ID + "=?", new String[]{String.valueOf(id)});
        if(res1 == 0)
        {
            database.close();
            return res1;
        }

        long res2 = database.delete(TABLE_SCORES, COL_MATCH_ID + "=?", new String[]{String.valueOf(id)});
        database.close();
        if(res2 == 0)
        {
            database.close();
            return res2;
        }

        database.close();
        return (res1 + res2);
    }
    long updateScores(Long matchID, Long playerIDs[], byte scores[][]) throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        long totalRes = 0;
        for(int i = 0; i < 4; i++)
        {
            cv.clear();
            cv.put(COL_SCORE, scores[i]);
            long res = db.update(TABLE_SCORES, cv, COL_MATCH_ID + "=? AND " + COL_PLAYER_INDEX + "=?", new String[]{String.valueOf(matchID), String.valueOf(i)});
            if(res == 0)
            {
                db.close();
                return res;
            }
            totalRes += res;
        }

        db.close();
        return totalRes;
    }
    ArrayList<Match> getAllMatchesDatesWithIDs(){

        SQLiteDatabase database = this.getReadableDatabase();
        String[] columns = {COL_MATCH_ID, COL_MATCH_DATE};
        Cursor c = database.query(TABLE_MATCHES, columns, null, null, null, null, COL_MATCH_ID + " DESC");

        ArrayList<Match> matches = new ArrayList<>();

        int colIndexMatchID = c.getColumnIndex(COL_MATCH_ID);
        int colIndexMatchDate = c.getColumnIndex(COL_MATCH_DATE);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            matches.add(new Match(c.getLong(colIndexMatchID), c.getString(colIndexMatchDate)));

        database.close();
        return matches;
    }
    MatchData getMatchDataByID(long id) throws SQLiteException{

        SQLiteDatabase db = this.getReadableDatabase();
        MatchData md = new MatchData();
        String[] matchColumns = {COL_MATCH_ID, COL_MATCH_DATE};
        Cursor c = db.query(TABLE_MATCHES, matchColumns, COL_MATCH_ID +" = ?", new String[]{String.valueOf(id)}, null, null, null);

        int columnIndexMatchID = c.getColumnIndex(COL_MATCH_ID);
        int columnIndexMatchDate = c.getColumnIndex(COL_MATCH_DATE);
        if(c.moveToFirst())
        {
            md.ID = c.getInt(columnIndexMatchID);
            md.date = c.getString(columnIndexMatchDate);
        }
        else
        {
            db.close();
            return null;
        }


        String[] scoreColumns = {COL_PLAYER_INDEX, COL_PROFILE_ID, COL_SCORE};

        c = db.query(TABLE_SCORES, scoreColumns, COL_MATCH_ID + " = ?", new String[]{String.valueOf(md.ID)}, null, null, COL_PLAYER_INDEX);

        int columnIndexPlayerIndex = c.getColumnIndex(COL_PLAYER_INDEX);
        int columnIndexPlayerID = c.getColumnIndex(COL_PROFILE_ID);
        int columnIndexScores = c.getColumnIndex(COL_SCORE);
        int count = c.getCount();
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            int playerIndex = c.getInt(columnIndexPlayerIndex);
            int profileID = c.getInt(columnIndexPlayerID);

            md.profiles[playerIndex] = getProfileByID(profileID);
            md.scores[playerIndex] = c.getBlob(columnIndexScores);
        }

        db.close();
        return md;
    }
}































