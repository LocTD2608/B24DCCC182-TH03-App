package com.example.appquanlighichi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên cơ sở dữ liệu và phiên bản
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";

    // Câu lệnh SQL tạo bảng
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_CREATED_AT + " TEXT NOT NULL, " +
                    COLUMN_UPDATED_AT + " TEXT NOT NULL" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng khi database được tạo lần đầu
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ và tạo lại khi nâng cấp phiên bản
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // Lấy thời gian hiện tại theo định dạng dd/MM/yyyy HH:mm
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    // ==================== CRUD Operations ====================

    // CREATE: Thêm ghi chú mới vào cơ sở dữ liệu
    public long insertNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentTime = getCurrentDateTime();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        values.put(COLUMN_CREATED_AT, currentTime);
        values.put(COLUMN_UPDATED_AT, currentTime);

        // insert() trả về ID của dòng vừa thêm, hoặc -1 nếu lỗi
        long id = db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    // READ: Lấy tất cả ghi chú, sắp xếp theo thời gian cập nhật mới nhất
    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn tất cả ghi chú, sắp xếp theo id giảm dần (mới nhất trước)
        String query = "SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COLUMN_ID + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        // Duyệt qua từng dòng kết quả
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
                );
                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return noteList;
    }

    // READ: Lấy một ghi chú theo ID
    public Note getNoteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        Note note = null;
        if (cursor.moveToFirst()) {
            note = new Note(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
            );
        }

        cursor.close();
        db.close();
        return note;
    }

    // UPDATE: Cập nhật ghi chú đã có
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentTime = getCurrentDateTime();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        values.put(COLUMN_UPDATED_AT, currentTime); // Chỉ cập nhật thời gian sửa

        // update() trả về số dòng bị ảnh hưởng
        int rowsAffected = db.update(TABLE_NOTES, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});

        db.close();
        return rowsAffected;
    }

    // DELETE: Xóa ghi chú theo ID
    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NOTES,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
    }

    // SEARCH: Tìm kiếm ghi chú theo tiêu đề hoặc nội dung
    public List<Note> searchNotes(String keyword) {
        List<Note> noteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Dùng LIKE để tìm kiếm chuỗi con trong tiêu đề hoặc nội dung
        String query = "SELECT * FROM " + TABLE_NOTES +
                " WHERE " + COLUMN_TITLE + " LIKE ? OR " + COLUMN_CONTENT + " LIKE ?" +
                " ORDER BY " + COLUMN_ID + " DESC";

        String searchPattern = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
                );
                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return noteList;
    }
}
