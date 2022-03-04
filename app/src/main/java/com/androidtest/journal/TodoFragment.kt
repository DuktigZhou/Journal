package com.androidtest.journal
import android.content.ContentValues
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_todo.*
import kotlin.collections.ArrayList
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_todo_add.*

class TodoFragment : Fragment() {

    private var todoList = ArrayList<TodoItem>()
    var todoRvAdapter: TodoRvAdapter = TodoRvAdapter()
    private val dbHelper = DatabaseHelper(MyApplication.context, "Todo.db", 1)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //实现监听方法
        todoRvAdapter.setOnLongClickListener(onLongClicked)

        //recyclerView 实现
        recyclerView.layoutManager = LinearLayoutManager(MyApplication.context)

        //设置数据、adapter实例
        queryTodo()//查询数据库中的全部数据并填充到数据集
        todoRvAdapter.setData(todoList)
        recyclerView.adapter = todoRvAdapter

        //item分割线
        val divider = DividerItemDecoration(MyApplication.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divider)

        //添加待办
        val btnTodoAdd: Button = view.findViewById(R.id.todoAdd)
        btnTodoAdd.setOnClickListener {
            addTodo()
        }

        topAppBarTodo.title = null
        topAppBarTodo.setNavigationOnClickListener {}
    }


    //从etTodoContent中获得文本并插入db
    private fun addTodo(){
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("todoContent", todoContentInput.text.toString())
            put("todoStatus",false)
        }
        db.insert("Todo", null, values) //插入数据
        //在todoList顶端插入新数据并实时更新
        todoRvAdapter.notifyItemInserted(0)
        todoList.add(0, TodoItem(0, todoContentInput.text.toString(), false))
        todoRvAdapter.notifyItemRangeChanged(0, todoRvAdapter.itemCount)

        Toast.makeText(MyApplication.context, "添加成功", Toast.LENGTH_SHORT).show()
        todoContentInput.setText("")//etTodoContent置空
        //todoRvAdapter.notifyItemRangeChanged(0, todoList.size)
    }


    //查询db中全部数据
    private fun queryTodo(){
        val db = dbHelper.writableDatabase
        // 查询表中所有的数据
        val cursor = db.rawQuery(
            "select id, todoContent, todoStatus from Todo order by id DESC",//倒序显示
            null
        )
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                val tId = cursor.getInt(cursor.getColumnIndex("id"))
                val todoText = cursor.getString(cursor.getColumnIndex("todoContent"))
                //val todoStatus = cursor.getInt(cursor.getColumnIndex("todoStatus"))
                //cursor不能查询bool类型的数据，只能读取int类型判断bool值
                val todoStatus: Boolean = cursor.getInt(cursor.getColumnIndex("todoStatus")) > 0
                val t=TodoItem(tId, todoText, todoStatus)
                todoList.add(t)//填充到 todoList
            } while (cursor.moveToNext())
        }
        cursor.close()
    }


    //删除特定的todo
    private fun deleteSpecificTodo(todoId: Int){
        val db = dbHelper.writableDatabase
        db.execSQL(
            "delete from Todo where id = ?",
            arrayOf(todoId)
        )
//        Toast.makeText(MyApplication.context,"id = $todoId 的待办已删除", Toast.LENGTH_SHORT).show()
    }

    //强制刷新recyclerView
    private fun refreshTodoList() {
        todoList.clear()    //清除todoList,避免数据重复
        queryTodo()         //再查询全部数据填充到todoList
        todoRvAdapter.setData(todoList)
        recyclerView.adapter = todoRvAdapter
//        Toast.makeText(context,"刷新todo列表", Toast.LENGTH_SHORT).show()
    }


    //删除确认对话框
    fun deleteTodoAlertDialog(todoId: Int){
        MaterialAlertDialogBuilder(requireActivity())//使用requireActivity()或者activity!!来提供context，
            .setTitle("删除任务")//resources.getString(R.string.)
            .setMessage("确定要永久删除此项任务？")
            .setNegativeButton("取消") { dialog, which -> }
            .setPositiveButton("确认") { dialog, which ->
                deleteSpecificTodo(todoId)
                refreshTodoList()
            }
            .show()
    }


    //Adapter中item点击的方法
    //实现接口回调方法 将点击事件从Adapter转移出来
    private val onLongClicked = object :
        TodoRvAdapter.OnItemLongClickListener{
        override fun onLongClicked(todoId: Int) {
            deleteTodoAlertDialog(todoId)
            refreshTodoList()
        }
    }
}
