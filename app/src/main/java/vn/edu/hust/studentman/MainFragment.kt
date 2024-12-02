package vn.edu.hust.studentman

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var param3: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getInt(ARG_PARAM3)
        }
        if(param1 != null && param2 != null){
            if(param3 > -1){
                students[param3].studentName = param1 as String
                students[param3].studentId = param2 as String
            } else {
                students.add(0, StudentModel(param1!!, param2!!))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        registerForContextMenu(view.findViewById(R.id.student_list))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_main, container, false)
        val studentList = view.findViewById<ListView>(R.id.student_list)
        studentList.adapter = studentAdapter

        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.button_add_student -> {
                val arguments = Bundle()
                arguments.putInt("param3", param3)
                findNavController().navigate(R.id.action_mainFragment_to_addAndEditFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu( menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo? ) {
        requireActivity().menuInflater.inflate(R.menu.context_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val pos = (item.menuInfo as AdapterContextMenuInfo).position
        when(item.itemId) {
            R.id.action_edit -> {
//                Toast.makeText(context, "edit student", Toast.LENGTH_LONG).show()
                val arguments = Bundle()
                arguments.putString("param1", students[pos].studentName)
                arguments.putString("param2", students[pos].studentId)
                arguments.putInt("param3", pos)
                findNavController().navigate(R.id.action_mainFragment_to_addAndEditFragment, arguments)
            }
            R.id.action_delete-> {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Delete student")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Ok") { _, _ ->
                        val studentName = students[pos].studentName
                        val studentId = students[pos].studentId
                        students.removeAt(pos)
                        studentAdapter.notifyDataSetChanged()
                        Snackbar.make(requireActivity().findViewById(R.id.main), "Deleted",  Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                students.add(pos, StudentModel(studentName, studentId))
                                studentAdapter.notifyDataSetChanged()
                            }
                            .show()
                    }
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .show()
            }
        }
        return super.onContextItemSelected(item)
    }

    companion object {
        private val students = mutableListOf(
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
        private val studentAdapter = StudentAdapter(students)

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: Int) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putInt(ARG_PARAM3, param3)
                }
            }
    }
}