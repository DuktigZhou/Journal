package com.androidtest.journal
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.item_note.view.*

//class NoteRvAdapter(private var noteList: ArrayList<NoteItem>) : RecyclerView.Adapter<NoteRvAdapter.ViewHolder>() {

class NoteRvAdapter: RecyclerView.Adapter<NoteRvAdapter.ViewHolder>() {

    private var listener: OnNoteItemClickListener? = null
    private var listener1: OnNoteItemLongClickListener? = null
    var noteList = ArrayList<NoteItem>() //dataset

    //内部类ViewHolder，传入View
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvNoteTitle: TextView = view.findViewById(R.id.tvNoteTitle)
        val tvNoteContent: TextView = view.findViewById(R.id.tvNoteContent)
    }

    //重写onCreateViewHolder：返回ViewHolder实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //加载item布局到view，传入ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    //重写onBindViewHolder：绑定数据
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvNoteTitle.text = noteList[position].noteTitle
        holder.tvNoteContent.text = noteList[position].noteContent
        //点击时返回被点击item的id
        holder.itemView.noteCardView.setOnClickListener {
                listener?.onClicked(noteList[position].id!!)
        }

        holder.itemView.noteCardView.setOnLongClickListener {
            listener1?.onLongClicked(noteList[position].id!!)
            true
        }

    }

    //重写getItemCount 计数
    override fun getItemCount() = noteList.size

    //设置Dataset
    fun setData(NoteList: ArrayList<NoteItem>){
        noteList = NoteList as ArrayList<NoteItem>
    }

    //点击监听 接口回调
    interface OnNoteItemClickListener{
        fun onClicked(noteId: Int)
    }

    interface OnNoteItemLongClickListener{
        fun onLongClicked(noteId: Int)
    }

    fun setOnClickListenerNote(ClickListener: NoteRvAdapter.OnNoteItemClickListener){
        listener = ClickListener
    }

    fun setOnLongClickListenerNote(LongClickListener: NoteRvAdapter.OnNoteItemLongClickListener){
        listener1 = LongClickListener
    }


}
