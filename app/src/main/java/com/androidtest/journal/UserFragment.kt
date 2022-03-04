package com.androidtest.journal
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_user.*
import android.view.LayoutInflater
import android.widget.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UserFragment : Fragment() {

    private val dbHelper = DatabaseHelper(MyApplication.context,"User.db",1)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userCard1 : CardView = view.findViewById(R.id.userCard1)
        val userCard2 : CardView = view.findViewById(R.id.userCard2)
        val btnLogout : Button = view.findViewById(R.id.btnLogout)
        val btnUserLogin : Button = view.findViewById(R.id.btnUserLogin)
        val btnUserSignUp : Button = view.findViewById(R.id.btnUserSignUp)

        //卡片初始状态
        userCard1.visibility = View.VISIBLE
        userCard2.visibility = View.GONE

        //查询用户登录状态
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery(
            "select status from User",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val status = cursor.getString(cursor.getColumnIndex("status"))
                cardViewVisibility(status)//判断卡片可见性
            } while (cursor.moveToNext())
        }
        cursor.close()

        //退出登录：更新卡片可见性
        btnLogout.setOnClickListener {
            userCard1.visibility = View.VISIBLE
            userCard2.visibility = View.GONE
            userStatus("false")
        }

        //登录
        btnUserLogin.setOnClickListener {
            loginDialog()
        }

        //注册
        btnUserSignUp.setOnClickListener {
            signupDialog()
        }
    }

    //根据登录状态确定卡片可见性
    private fun cardViewVisibility(status: String){
        if(status == "true"){
            userCard1.visibility = View.GONE
            userCard2.visibility = View.VISIBLE
        }
        if(status == "false") {
            userCard1.visibility = View.VISIBLE
            userCard2.visibility = View.GONE
        }
    }

    //在数据库中保存用户登录状态：true 已登陆 false 未登录
    private fun userStatus(status: String){
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("status", status)
        }
        db.insert("User", null, values) //插入数据
    }

    //登录Dialog
    private fun loginDialog() {
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        val userAccount: EditText = dialogView.findViewById(R.id.userAccount)
        val userPassword: EditText = dialogView.findViewById(R.id.userPassword)
        val userRememberPw : CheckBox = dialogView.findViewById(R.id.rememberPassword)
        val prefs = activity?.getSharedPreferences("user",MODE_PRIVATE)
        val isRememberPw = prefs?.getBoolean("remember password",false)
        //勾选记住密码
        if(isRememberPw == true){
            //填充账号密码
            val account = prefs.getString("account","")
            val password = prefs.getString("password","")
            userAccount.setText(account)
            userPassword.setText(password)
            userRememberPw.isChecked = true
        }

        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("登录")
            .setView(dialogView)
            .setNegativeButton("取消") { dialog, which -> }
            .setPositiveButton("登录") { dialog, which ->
                val inputAccount = userAccount.text.toString()
                val inputPassword = userPassword.text.toString()
                val prefsAccount = prefs?.getString("account","")
                val prefsPassword = prefs?.getString("password","")
                if (inputAccount == prefsAccount && inputPassword == prefsPassword) {
                    val editor = prefs.edit()
                    if (userRememberPw.isChecked){
                        Toast.makeText(MyApplication.context, "已记住密码", Toast.LENGTH_SHORT).show()
                        editor?.putBoolean("remember password",true)
                        editor?.putString("account",inputAccount)
                        editor?.putString("password",inputPassword)
                    }else{
                        editor?.clear()
                    }
                    editor?.apply()
                    userStatus("true")//用户登录状态true
                    Toast.makeText(
                        MyApplication.context,
                        userAccount.text.toString() + " 登录成功 ",
                        Toast.LENGTH_SHORT
                    ).show()
                    userCard2.visibility = View.VISIBLE//更新卡片可见性
                    userCard1.visibility = View.GONE
                }else{
                    Toast.makeText(MyApplication.context, "账号或密码错误", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    //注册Dialog
    private fun signupDialog() {
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        val userAccount: EditText = dialogView.findViewById(R.id.userAccount)
        val userPassword: EditText = dialogView.findViewById(R.id.userPassword)
        val userRememberPw : CheckBox = dialogView.findViewById(R.id.rememberPassword)
        val prefs = activity?.getSharedPreferences("user",MODE_PRIVATE)
        userRememberPw.visibility = View.GONE//注册界面不显示记住密码
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("注册")
            .setView(dialogView)
            .setNegativeButton("取消") { dialog, which -> }
            .setPositiveButton("注册") { dialog, which ->
                val inputAccount = userAccount.text.toString()
                val inputPassword = userPassword.text.toString()
                    val editor = prefs?.edit()
                    editor?.putString("account",inputAccount)
                    editor?.putString("password",inputPassword)
                    editor?.apply()
                    userStatus("true")
                    Toast.makeText(
                        MyApplication.context,
                        userAccount.text.toString() + " 注册成功，已自动登录",
                        Toast.LENGTH_SHORT
                    ).show()
                    userCard2.visibility = View.VISIBLE
                    userCard1.visibility = View.GONE
            }
            .show()
    }
}