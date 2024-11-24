package vn.edu.hust.studentman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
  private lateinit var students: MutableList<StudentModel>
  private lateinit var listView: ListView
  private lateinit var adapter: StudentAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(false)

    students = mutableListOf(
      StudentModel("Nguyễn Văn An", "SV001"),
      StudentModel("Trần Thị Bảo", "SV002"),
      StudentModel("Lê Hoàng Cường", "SV003"),
      StudentModel("Phạm Thị Dung", "SV004"),
      StudentModel("Đỗ Minh Đức", "SV005"),
      StudentModel("Vũ Thị Hoa", "SV006"),
      StudentModel("Hoàng Văn Hải", "SV007"),
      StudentModel("Bùi Thị Hạnh", "SV008"),
      StudentModel("Đinh Văn Hùng", "SV009"),
      StudentModel("Nguyễn Thị Linh", "SV010"),
      StudentModel("Phạm Văn Long", "SV011"),
      StudentModel("Trần Thị Mai", "SV012"),
      StudentModel("Lê Thị Ngọc", "SV013"),
      StudentModel("Vũ Văn Nam", "SV014"),
      StudentModel("Hoàng Thị Phương", "SV015"),
      StudentModel("Đỗ Văn Quân", "SV016"),
      StudentModel("Nguyễn Thị Thu", "SV017"),
      StudentModel("Trần Văn Tài", "SV018"),
      StudentModel("Phạm Thị Tuyết", "SV019"),
      StudentModel("Lê Văn Vũ", "SV020")
    )

    adapter = StudentAdapter(this, students)
    listView = findViewById(R.id.list_view_students)
    listView.adapter = adapter

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

    registerForContextMenu(listView)

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
          students.add(StudentModel(name, id))
          adapter.notifyDataSetChanged()
        }
        dialog.dismiss()
      }
      .setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }

  private fun showEditDialog(student: StudentModel) {
    val dialogView = LayoutInflater.from(this)
      .inflate(R.layout.dialog_student_input, null)

    val nameInput = dialogView.findViewById<EditText>(R.id.edit_text_name)
    val idInput = dialogView.findViewById<EditText>(R.id.edit_text_id)

    nameInput.setText(student.studentName)
    idInput.setText(student.studentId)

    AlertDialog.Builder(this)
      .setTitle("Edit Student")
      .setView(dialogView)
      .setPositiveButton("Save") { dialog, _ ->
        val name = nameInput.text.toString()
        val id = idInput.text.toString()
        if (name.isNotEmpty() && id.isNotEmpty()) {
          val position = students.indexOf(student)
          if (position != -1) {
            students[position] = StudentModel(name, id)
            adapter.notifyDataSetChanged()
          }
        }
        dialog.dismiss()
      }
      .setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }

  private fun showDeleteDialog(student: StudentModel) {
    AlertDialog.Builder(this)
      .setTitle("Delete Student")
      .setMessage("Are you sure?")
      .setPositiveButton("OK") { dialog, _ ->
        val position = students.indexOf(student)
        if (position != -1) {
          students.removeAt(position)
          adapter.notifyDataSetChanged()
//          Toast.makeText(this, "Student has been deleted", Toast.LENGTH_SHORT).show()
          showUndoSnackbar(student, position)
        }
        dialog.dismiss()
      }
      .setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }

  private fun showUndoSnackbar(deletedStudent: StudentModel, position: Int) {
    Snackbar.make(
      findViewById(R.id.main),
      "Student has been deleted",
      Snackbar.LENGTH_LONG
    ).setAction("UNDO") {
      students.add(position, deletedStudent)
      adapter.notifyDataSetChanged()
    }.show()
  }
}