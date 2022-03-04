package com.androidtest.journal
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_todo.view.*

//数据集 dataset : taskList
class TodoRvAdapter: RecyclerView.Adapter<TodoRvAdapter.ViewHolder>() {
    var todoList = ArrayList<TodoItem>()
    //接口回调：成员变量
    private var listener1:OnItemLongClickListener? = null

    //内部类ViewHolder，传入View
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val todoContent: TextView = view.findViewById(R.id.todoContent)
        val todoStatus: CheckBox = view.findViewById(R.id.todoStatus)
    }

    //重写onCreateViewHolder：返回ViewHolder实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //加载item布局到view，传入ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        val viewHolder = ViewHolder(view)

        //CheckBox的勾选监听
        viewHolder.todoStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if (viewHolder.todoStatus.isChecked) {
                Snackbar.make(view, viewHolder.todoContent.text.toString() + " 已完成", Snackbar.LENGTH_SHORT).show()
                Log.d("todoStatus.isChecked","todoStatus.isChecked")
            }else{
                Snackbar.make(view, viewHolder.todoContent.text.toString() + " 未完成", Snackbar.LENGTH_SHORT).show()
            }
        }
        return viewHolder//返回ViewHolder实例
    }

    //重写onBindViewHolder：item滚动到屏幕中时调用
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.todoContent.text = todoList[position].todoContent
        holder.todoStatus.isChecked = todoList[position].todoStatus

        //单个item的点击监听
        holder.itemView.todoItemLayout.setOnLongClickListener {
            listener1?.onLongClicked(todoList[position].id!!)
            true
        }
    }

    //重写getItemCount 计数
    override fun getItemCount() = todoList.size

    //设置Dataset
    fun setData(TodoList: ArrayList<TodoItem>){
        todoList = TodoList as ArrayList<TodoItem>
    }

    //接口回调 fun onLongClicked 重写在TodoFragment
    interface OnItemLongClickListener{
        fun onLongClicked(todoId: Int)
    }

    //接口回调 实例化 定义Listener的方法
    fun setOnLongClickListener(LongClickListener: OnItemLongClickListener){
        listener1 = LongClickListener
    }

}