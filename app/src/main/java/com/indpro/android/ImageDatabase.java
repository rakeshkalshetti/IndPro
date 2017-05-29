package com.indpro.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.indpro.model.ImageDetails;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rakesh on 27/05/17.
 */

public class ImageDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "image.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    private static final String KEY_ID = "id";

    private String TABLE_IMAGE = "ImageTable";

    private static volatile ImageDatabase Instance = null;
    public ImageDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static ImageDatabase getInstance() {
        ImageDatabase localInstance = Instance;
        if (localInstance == null) {
            synchronized (ImageDatabase.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ImageDatabase(ApplicationLoader.applicationContext);
                }
            }
        }
        return localInstance;
    }

    private static String getDatabasePath() {
        return ApplicationLoader.applicationContext.getApplicationInfo().dataDir + DB_PATH_SUFFIX
                + DATABASE_NAME;
    }

    public static void CopyDataBaseFromAsset() throws IOException {

        InputStream myInput = ApplicationLoader.applicationContext.getAssets().open(DATABASE_NAME);

        String outFileName = getDatabasePath();

        File f = new File(ApplicationLoader.applicationContext.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
        if (!f.exists())
            f.mkdir();

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<ImageDetails> loadImageList() {
        List<ImageDetails> imageDetailsList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_IMAGE + " order by click desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ImageDetails imageDetails = null;
        if (cursor.moveToFirst()) {
            do {
                imageDetails = new ImageDetails();
                imageDetails.setId(cursor.getInt(0));
                imageDetails.setImageUrl(cursor.getString(1));
                imageDetails.setLike(cursor.getInt(2));
                imageDetails.setDisLike(cursor.getInt(3));
                imageDetails.setClick(cursor.getInt(4));
                imageDetails.setUpdatedAt(cursor.getString(5));
                imageDetails.setCreatedAt(cursor.getString(6));
                imageDetailsList.add(imageDetails);
                Log.e("TextNS", "Slite time stamp : " + imageDetails.getClick());
            } while (cursor.moveToNext());
        }

        return imageDetailsList;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int updateClick(int id, int count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("click", count);

        return db.update(TABLE_IMAGE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public ImageDetails updateLike(ImageDetails imageDetails, int type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int count = 0;
        if (type == 0) {
            count = imageDetails.getLike() + 1;
            values.put("lk", count);
            imageDetails.setLike(count);
        } else {
            count = imageDetails.getDisLike() + 1;
            values.put("dislike", count);
            imageDetails.setDisLike(count);
        }
        imageDetails.setUpdatedAt(getDateTime());
        values.put("updated_at", imageDetails.getUpdatedAt());
        db.update(TABLE_IMAGE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(imageDetails.getId())});
        return imageDetails;
    }
}
