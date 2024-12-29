package vn.edu.hust.studentman

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface StudentDao {
    @Insert
    suspend fun addStudent(student: StudentEntity)

    @Query("SELECT * FROM students")
    suspend fun getStudents(): List<StudentEntity>

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)
}