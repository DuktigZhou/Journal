package com.androidtest.journal
import android.content.*
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_user.*
//import kotlinx.android.synthetic.main.activity_todo.*
//import kotlinx.android.synthetic.main.activity_todo.recyclerView
import kotlin.collections.ArrayList

class NoteFragment : Fragment() {

    var noteList = ArrayList<NoteItem>()//dataset
    var noteRvAdapter: NoteRvAdapter = NoteRvAdapter()
    private val dbHelper = DatabaseHelper(MyApplication.context,"Note.db",1)
    private val dbHelper1 = DatabaseHelper(MyApplication.context,"RecyclerViewLayout.db",1)//recyclerviewLayout存放于RecyclerViewLayout.db
    private val broadcastManager = LocalBroadcastManager.getInstance(MyApplication.context)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        broadcastManager.unregisterReceiver(mRefreshReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerReceiver()
        super.onViewCreated(view, savedInstanceState)

        noteRvAdapter.setOnClickListenerNote(onClickedNote)
        noteRvAdapter.setOnLongClickListenerNote(onLongClickedNote)

    //recyclerView实现
        val recyclerViewNote : RecyclerView = view.findViewById(R.id.recyclerViewNote)
        recyclerViewNote.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        queryRecyclerViewLayout()

        topAppBarNote.menu.findItem(R.id.view2).isVisible = false
        topAppBarNote.menu.findItem(R.id.view1).isVisible = true

        topAppBarNote.setOnMenuItemClickListener {
            val menu = topAppBarNote.menu
            when (it.itemId) {
                R.id.view2 -> {
                    currentRecyclerViewLayout("2")
                    queryRecyclerViewLayout()
                    Toast.makeText(MyApplication.context,"两列布局", Toast.LENGTH_SHORT).show()
                    refreshNoteList()
                    menu.findItem(R.id.view2).isVisible = false
                    menu.findItem(R.id.view1).isVisible = true
                    true
                }

                R.id.view1 -> {
                    currentRecyclerViewLayout("1")
                    queryRecyclerViewLayout()
                    Toast.makeText(MyApplication.context,"单列布局", Toast.LENGTH_SHORT).show()
                    refreshNoteList()
                    menu.findItem(R.id.view2).isVisible = true
                    menu.findItem(R.id.view1).isVisible = false
                    true
                }
                else -> false
            }
        }



        //设置适配器实例
        queryNote()
        noteRvAdapter.setData(noteList)
        recyclerViewNote.adapter = noteRvAdapter

        //Fab点击事件：切换到NoteEditFragment
        val btnFabAddNote: Button = view.findViewById(R.id.fabAddNote)
        btnFabAddNote.setOnClickListener {
            replaceFragment(NoteEditFragment.newInstance())
        }

        //下拉刷新
        val swipeRefreshNote : SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshNote)
        swipeRefreshNote.setOnRefreshListener {
            refreshNoteList()//强制刷新recyclerView
            swipeRefreshNote.isRefreshing = false
        }

        topAppBarNote.title = null
        topAppBarNote.setNavigationOnClickListener {}
    }


    //注册广播接收器
    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("refreshNoteList")
        broadcastManager.registerReceiver(mRefreshReceiver, intentFilter)
    }

    //接受广播
    private val mRefreshReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val receive = intent.getStringExtra("conform")
            if (receive == "yes") {
                refreshNoteList()
//                Toast.makeText(context,"添加成功", Toast.LENGTH_SHORT).show()
            }
        }
    }



    //设定recyclerView要切换的布局
    private fun currentRecyclerViewLayout(recyclerViewLayout: String){
        val db = dbHelper1.writableDatabase
        val values = ContentValues().apply {
            put("recyclerViewLayout", recyclerViewLayout)
        }
        db.insert("RecyclerViewLayout", null, values) //插入数据
    }

    //查找recyclerView布局
    private fun queryRecyclerViewLayout(){
        val db = dbHelper1.writableDatabase
        val cursor = db.rawQuery("select recyclerViewLayout from RecyclerViewLayout", null)
        if (cursor.moveToFirst()) {
            do {
                val recyclerViewLayout = cursor.getString(cursor.getColumnIndex("recyclerViewLayout"))
                setRecyclerViewLayout(recyclerViewLayout)
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    //设定recyclerView布局
    private fun setRecyclerViewLayout(recyclerViewLayout: String){
        if(recyclerViewLayout == "1"){
            recyclerViewNote.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        }
        if(recyclerViewLayout == "2") {
            recyclerViewNote.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }



    //查询db中数据
    private fun queryNote(){
        val db = dbHelper.writableDatabase
        // 查询表中所有的数据
        val cursor1 = db.query(
            "Note",
            null,
            null,
            null,
            null,
            null,
            null
        )
        if (cursor1.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                val nid = cursor1.getInt(cursor1.getColumnIndex("id"))
                val noteTitle = cursor1.getString(cursor1.getColumnIndex("noteTitle"))
                val noteContent = cursor1.getString(cursor1.getColumnIndex("noteContent"))
                //Log.d(c.toString(),c.toString())
                val n=NoteItem(nid, noteTitle, noteContent)
                noteList.add(n)//填充到 todoList
            } while (cursor1.moveToNext())
        }
        cursor1.close()
    }

    //删除特定的note
    private fun deleteSpecificNote(i: Int){
        val db = dbHelper.writableDatabase
        db.execSQL(
            "delete from Note where id = ?",
            arrayOf(i)
        )
        Toast.makeText(MyApplication.context,"id = $i 的笔记已删除", Toast.LENGTH_SHORT).show()
    }

    //强制刷新recyclerView
    private fun refreshNoteList() {
        noteList.clear()    //清除todoList,避免数据重复
        queryNote()         //再查询全部数据填充到todoList
        noteRvAdapter.setData(noteList)
        recyclerViewNote.adapter = noteRvAdapter
    }


    //删除确认对话框
    fun deleteNoteAlertDialog(noteId: Int){
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("删除笔记")//resources.getString(R.string.)
            .setMessage("确定要永久删除该条笔记？")
            .setNegativeButton("取消") { dialog, which -> }
            .setPositiveButton("确认") { dialog, which ->
                deleteSpecificNote(noteId)
                refreshNoteList()
            }
            .show()
    }


    //Adapter中item点击的方法
    //实现接口回调方法 将点击事件从NoteRvAdapter转移到外部 把noteId传进NoteEditFragment
    //点击：
    private val onClickedNote = object :
        NoteRvAdapter.OnNoteItemClickListener{
            override fun onClicked(noteId: Int) {
                val fragment: Fragment
                val bundle = Bundle()
                bundle.putInt("noteId",noteId)
                fragment = NoteEditFragment.newInstance()
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
    }


    //长按：
    private val onLongClickedNote = object :
        NoteRvAdapter.OnNoteItemLongClickListener{
            override fun onLongClicked(noteId: Int) {
                deleteNoteAlertDialog(noteId)
            }
    }


    //fragment切换
    private fun replaceFragment(fragment: Fragment){
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransition.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_in_left)
        fragmentTransition.replace(R.id.Container,fragment).addToBackStack(fragment.javaClass.simpleName).commit()
    }

}





