package vn.edu.hust.studentman

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
  private lateinit var students: MutableList<StudentEntity>
  private lateinit var listView: ListView
  private lateinit var adapter: StudentAdapter
  private lateinit var db: AppDatabase

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(false)

    db = AppDatabase.getDatabase(this)
    students = mutableListOf()

    adapter = StudentAdapter(this, students)
    listView = findViewById(R.id.list_view_students)
    listView.adapter = adapter

    // Load students from the database
    lifecycleScope.launch {
      students.addAll(db.studentDao().getStudents())
      adapter.notifyDataSetChanged()
    }

    findViewById<ImageView>(R.id.btn_add).setOnClickListener {
      showAddDialog()
    }

    listView.setOnItemLongClickListener { parent, view, position, id ->
      val popup = PopupMenu(this, view)
      popup.menuInflater.inflate(R.menu.context_menu, popup.menu)

      popup.setOnMenuItemClickListener { item ->
        when (item.itemId) {
          R.id.action_edit -> {
            showEditDialog(students[position])
            true
          }
          R.id.action_delete -> {
            showDeleteDialog(students[position])
            true
          }
          else -> false
        }
      }
      popup.show()
      true
    }
  }

  private fun showAddDialog() {
    val dialogView = LayoutInflater.from(this)
      .inflate(R.layout.dialog_student_input, null)

    val nameInput = dialogView.findViewById<EditText>(R.id.edit_text_name)
    val idInput = dialogView.findViewById<EditText>(R.id.edit_text_id)

    AlertDialog.Builder(this)
      .setTitle("Add New Student")
      .setView(dialogView)
      .setPositiveButton("Add") { dialog, _ ->
        val name = nameInput.text.toString()
        val id = idInput.text.toString()
        if (name.isNotEmpty() && id.isNotEmpty()) {
          val student = StudentEntity(id, name)
          lifecycleScope.launch {
            db.studentDao().addStudent(student)
            students.add(student)
            withContext(Dispatchers.Main) { adapter.notifyDataSetChanged() }
          }
        }
        dialog.dismiss()
      }
      .setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }

  private fun showEditDialog(student: StudentEntity) {
    val dialogView = LayoutInflater.from(this)
      .inflate(R.layout.dialog_student_input, null)

    val nameInput = dialogView.findViewById<EditText>(R.id.edit_text_name)
    val idInput = dialogView.findViewById<EditText>(R.id.edit_text_id)

    nameInput.setText(student.studentName)
    idInput.setText(student.studentId)
    idInput.isEnabled = false

    AlertDialog.Builder(this)
      .setTitle("Edit Student")
      .setView(dialogView)
      .setPositiveButton("Save") { dialog, _ ->
        val name = nameInput.text.toString()
        if (name.isNotEmpty()) {
          val updatedStudent = StudentEntity(student.studentId, name)
          lifecycleScope.launch {
            db.studentDao().updateStudent(updatedStudent)
            val position = students.indexOf(student)
            if (position != -1) {
              students[position] = updatedStudent
              withContext(Dispatchers.Main) { adapter.notifyDataSetChanged() }
            }
          }
        }
        dialog.dismiss()
      }
      .setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }

  private fun showDeleteDialog(student: StudentEntity) {
    AlertDialog.Builder(this)
      .setTitle("Delete Student")
      .setMessage("Are you sure?")
      .setPositiveButton("OK") { dialog, _ ->
        lifecycleScope.launch {
          db.studentDao().deleteStudent(student)
          val position = students.indexOf(student)
          if (position != -1) {
            students.removeAt(position)
            withContext(Dispatchers.Main) {
              adapter.notifyDataSetChanged()
            }
            showUndoSnackbar(student, position)
          }
        }
        dialog.dismiss()
      }
      .setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }

  private fun showUndoSnackbar(deletedStudent: StudentEntity, position: Int) {
    Snackbar.make(
      findViewById(R.id.main),
      "Student has been deleted",
      Snackbar.LENGTH_LONG
    ).setAction("UNDO") {
      lifecycleScope.launch {
        db.studentDao().addStudent(deletedStudent)
        students.add(position, deletedStudent)
        withContext(Dispatchers.Main) { adapter.notifyDataSetChanged() }
      }
    }.show()
  }
}