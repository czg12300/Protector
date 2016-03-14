package cn.protector.push;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import cn.protector.ProtectorApplication;
import cn.protector.logic.entity.ChatMessage;

/**
 * 描述:数据库
 *
 * @author jakechen
 * @since 2016/3/2 15:48
 */
public class DbHelper extends SQLiteOpenHelper {
  public static final int PAGE_SIZE = 10;
  private static DbHelper instance;

  public static DbHelper getInstance() {
    if (instance == null) {
      instance = new DbHelper(ProtectorApplication.getInstance());
    }
    return instance;
  }

  public DbHelper(Context context) {
    super(context, "protect", null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE IF NOT EXISTS message (_id integer primary key autoincrement, _time integer,_url text,_image text,_sender text,_content text);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }

  public void insertTable(ChatMessage message) {
    if (message != null) {
      SQLiteDatabase db = getWritableDatabase();
      try {
        ContentValues cv = new ContentValues();
        cv.put("_time", message.getTime());
        cv.put("_url", message.getUrl());
        cv.put("_image", message.getImage());
        cv.put("_sender", message.getSender());
        cv.put("_content", message.getContent());
        db.insert("message", null, cv);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (db != null) {
          db.close();
        }
        close();
      }
    }
  }

  public ArrayList<ChatMessage> queryMessage(int time) {
    SQLiteDatabase db = getReadableDatabase();
    ArrayList<ChatMessage> list = null;
    Cursor cursor = null;
    try {
      if (time >= 0) {
        cursor = db.query("message", null, "_time" + "<?", new String[]{String.valueOf(time)}, null, null, "_time" + " desc", String.valueOf(PAGE_SIZE));
      } else {
        cursor = db.query("message", null, null, null, null, null, "_time" + " desc", String.valueOf(PAGE_SIZE));
      }
      if (cursor != null && cursor.getCount() > 0) {
        list = new ArrayList<>();
        while (cursor.moveToNext()) {
          ChatMessage chatMessage = new ChatMessage();
          chatMessage.setTime(cursor.getInt(cursor.getColumnIndexOrThrow("_time")));
          chatMessage.setSender(cursor.getString(cursor.getColumnIndexOrThrow("_sender")));
          chatMessage.setImage(cursor.getString(cursor.getColumnIndexOrThrow("_image")));
          chatMessage.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("_url")));
          chatMessage.setContent(cursor.getString(cursor.getColumnIndexOrThrow("_content")));
          list.add(chatMessage);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (cursor != null) {
        cursor.close();
      }
      if (db != null) {
        db.close();
      }
      close();
    }
    return list;

  }


}
