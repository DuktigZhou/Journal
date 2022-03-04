package com.androidtest.journal
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DatabaseHelper(
    val context: Context,
        name: String,
        version: Int
): SQLiteOpenHelper(context, name, null, version) {

    private val createTodo =
        "create table Todo (" +
                "id integer primary key autoincrement," +
                "todoContent text," +
                "todoStatus text)"

    private val createNote =
        "create table Note (" +
                "id integer primary key autoincrement," +
                "noteTitle text," +
                "noteContent text)"

    private val createUser =
        "create table User (" +
                "id integer primary key autoincrement," +
                "status text)"

    private val createRecyclerViewLayout =
        "create table RecyclerViewLayout (" +
                "id integer primary key autoincrement," +
                "recyclerViewLayout text)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTodo)
//        Toast.makeText(context, "createTodo", Toast.LENGTH_SHORT).show()
        db.execSQL(createNote)
//        Toast.makeText(context, "createNote", Toast.LENGTH_SHORT).show()
        db.execSQL(createUser)
//        Toast.makeText(context, "createUser", Toast.LENGTH_SHORT).show()
        db.execSQL(createRecyclerViewLayout)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

}