package com.androidtest.journal

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_note_edit.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import android.content.Intent

class NoteEditFragment : Fragment(){
    private var nId = -1
    private val dbHelper = DatabaseHelper(MyApplication.context,"Note.db",1)//用MyApplication.context全局获得Context

    //onCreate
    //取传进来的noteId
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nId = requireArguments().getInt("noteId",-1)
    }

    //onDetach
    override fun onDetach() {
        super.onDetach()
//        Toast.makeText(MyApplication.context,"onDetach()", Toast.LENGTH_SHORT).show()
        //发送刷新列表的广播
        val intent = Intent("refreshNoteList")
        intent.putExtra("conform", "yes")
        LocalBroadcastManager.getInstance(MyApplication.context).sendBroadcast(intent)
    }

    //onCreateView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note_edit, container, false)
    }

    //伴生对象，类似于Java静态方法
    //新建NoteEditFragment实例：NoteEditFragment.newInstance()
    companion object {
        fun newInstance() =
            NoteEditFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    //onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //直接从列表打开的情况：从数据库获得数据并填充数据
        if (nId != -1){
            querySpecificNote(nId)
        }

        //点击返回，应当和点击done操作一样，确保不会丢数据
        val ivBack2note: ImageView = view.findViewById(R.id.ivBack2note)
        ivBack2note.setOnClickListener {
            if (nId != -1){
                updateNote()
            }else{
                saveNote()
            }
            requireActivity().supportFragmentManager.popBackStack() //Fragment出栈
        }

        //点击done，更新或新增note
        val ivDone: ImageView = view.findViewById(R.id.ivDone)
        ivDone.setOnClickListener {
            if (nId != -1){
                updateNote()
//                Toast.makeText(MyApplication.context,"更新笔记", Toast.LENGTH_SHORT).show()
            }else{
                saveNote()
//                Toast.makeText(MyApplication.context,"新增笔记", Toast.LENGTH_SHORT).show()
            }
            requireActivity().supportFragmentManager.popBackStack() //Fragment出栈
        }

        //点击delete, 已有笔记：删除；新建笔记：不对db做任何操作，Fragment直接出栈
        val ivDeleteNote: ImageView = view.findViewById(R.id.ivDeleteNote)
        ivDeleteNote.setOnClickListener {
            if (nId != -1){
                deleteNote()
            }else{
                requireActivity().supportFragmentManager.popBackStack() //Fragment出栈
            }
        }
    }


    //根据id查找特定note
    private fun querySpecificNote(i:Int){
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery(
            "select * from Note where id =$i",
            null
        )
        if (cursor == null) {
            Toast.makeText(MyApplication.context, "查询失败", Toast.LENGTH_SHORT).show()
        }
        while (cursor.moveToNext()) {
            val noteTitle = cursor.getString(cursor.getColumnIndex("noteTitle"))
            val noteContent = cursor.getString(cursor.getColumnIndex("noteContent"))
            etNoteTitle.setText(noteTitle.toString())
            etNoteContent.setText(noteContent.toString())
//            Toast.makeText(MyApplication.context, "id = $i，noteTitle=$noteTitle", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }


    //保存
    private fun saveNote(){
        //空判断
        if (etNoteTitle.text.isNullOrEmpty()){
            Toast.makeText(context,"笔记标题不能为空",Toast.LENGTH_SHORT).show()
        }
        else if (etNoteContent.text.isNullOrEmpty()){
            Toast.makeText(context,"笔记内容不能为空",Toast.LENGTH_SHORT).show()
        }
        else{
            val db = dbHelper.writableDatabase
            val value = ContentValues().apply {
                put("noteTitle", etNoteTitle.text.toString())
                put("noteContent", etNoteContent.text.toString())//组装数据
            }
            db.insert("Note", null, value) //插入数据
            etNoteTitle.setText("")//EditText置空
            etNoteContent.setText("")
            Log.d("Note添加","Note添加")
//            Toast.makeText(MyApplication.context,"Database添加成功", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    //更新
    private fun updateNote(){
        val db = dbHelper.writableDatabase
        db.execSQL(
            "update Note set noteTitle = ? where id = ?",
            arrayOf(etNoteTitle.text.toString(), nId)
        )
        db.execSQL(
            "update Note set noteContent = ? where id = ?",
            arrayOf(etNoteContent.text.toString(), nId)
        )
        etNoteTitle.setText("")
        etNoteContent.setText("")
        Log.d("Note更新","Note更新")
//        Toast.makeText(MyApplication.context,"id = $nId 的笔记已更新", Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.popBackStack()

    }


    //删除
    private fun deleteNote(){
        val db = dbHelper.writableDatabase
        db.execSQL(
            "delete from Note where id = ?",
            arrayOf(nId)
        )
//        Toast.makeText(MyApplication.context,"id = $nId 的笔记已删除", Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.popBackStack()

    }




}

/*
    private val dbHelper = DatabaseHelper(MyApplication.context,"Note.db",1)
    //error
    private val dbHelper = DatabaseHelper(Application(),"Note.db",2)
    -> java.io.File android.content.Context.getDatabasePath(java.lang.String) on a null object reference
    private val dbHelper = DatabaseHelper(view!!.context,"Note.db",2)
    -> NullPointerException()
*/



//实现NoteEditFragment出栈时刷新NoteActivity中的RecyclerView
//在Fragment添加回调接口，让NoteActivity继承并实现

//interface RefreshListener{
//    fun refresh()
//}



/*在协程中访问DAO

            GlobalScope.launch {
                context?.let {
                    val note = NoteDatabase.getDatabase(it).noteDaoDatabaseFun().getSpecificNote(nId)
                    etNoteTitle.setText(note.noteTitle)
                    etNoteContent.setText(note.noteContent)
                }
            }


            GlobalScope.launch {
                val note = NoteItem()
                note.noteTitle = etNoteTitle.text.toString()
                note.noteContent = etNoteContent.text.toString()
                context?.let {
                    NoteDatabase.getDatabase(it).noteDaoDatabaseFun().insertNote(note)
                    etNoteTitle.setText("")
                    etNoteContent.setText("")
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }


        GlobalScope.launch {
            context?.let {
                val note = NoteDatabase.getDatabase(it).noteDaoDatabaseFun().getSpecificNote(nId)
                note.noteTitle = etNoteTitle.text.toString()
                note.noteContent = etNoteContent.text.toString()
                NoteDatabase.getDatabase(it).noteDaoDatabaseFun().updateNote(note)
                etNoteTitle.setText("")
                etNoteContent.setText("")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }


        GlobalScope.launch {
            context?.let {
                NoteDatabase.getDatabase(it).noteDaoDatabaseFun().deleteSpecificNote(nId)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

 */