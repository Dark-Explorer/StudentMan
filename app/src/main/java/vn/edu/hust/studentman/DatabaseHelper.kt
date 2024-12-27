package vn.edu.hust.studentman

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "StudentDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_STUDENTS = "students"
        private const val KEY_NAME = "name"
        private const val KEY_ID = "id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_STUDENTS (
                $KEY_ID TEXT PRIMARY KEY,
                $KEY_NAME TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    fun addStudent(student: StudentModel) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_ID, student.studentId)
            put(KEY_NAME, student.studentName)
        }
        db.insert(TABLE_STUDENTS, null, values)
        db.close()
    }

    fun getStudents(): List<StudentModel> {
        val students = mutableListOf<StudentModel>()
        val selectQuery = "SELECT * FROM $TABLE_STUDENTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val name = it.getString(it.getColumnIndexOrThrow(KEY_NAME))
                    val id = it.getString(it.getColumnIndexOrThrow(KEY_ID))
                    students.add(StudentModel(name, id))
                } while (it.moveToNext())
            }
        }
        return students
    }

    fun updateStudent(student: StudentModel) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, student.studentName)
        }
        db.update(TABLE_STUDENTS, values, "$KEY_ID = ?", arrayOf(student.studentId))
        db.close()
    }

    fun deleteStudent(studentId: String) {
        val db = this.writableDatabase
        db.delete(TABLE_STUDENTS, "$KEY_ID = ?", arrayOf(studentId))
        db.close()
    }
}