package com.androidtest.journal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StatusBarUtil.setColor(this, resources.getColor(R.color.white),0)
        lightStatusBar(window)

        bottomNav.menu.getItem(0).isCheckable = true
        setFragment(NoteFragment())
        //底部导航的跳转
        bottomNav.setOnNavigationItemSelectedListener {menu ->
            when(menu.itemId){
                R.id.btn1 -> {
                    setFragment(NoteFragment())
                    true
                }
                R.id.btn2 -> {
                    setFragment(TodoFragment())
                    true
                }
                R.id.btn3 -> {
                    setFragment(UserFragment())
                    true
                }
                else -> false
            }
        }

    }

    //切换Fragment
    private fun setFragment(fr : Fragment){
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.fragmentContainer,fr)
        frag.commit()
    }

    private fun lightStatusBar(window: Window, isLight: Boolean = true) {
        val wic = WindowInsetsControllerCompat(window,window.decorView)
        wic.isAppearanceLightStatusBars = isLight
    }
}



































//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
//import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.setupActionBarWithNavController
//import androidx.navigation.ui.setupWithNavController
//import com.androidtest.notes.databinding.ActivityMainBinding
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//    }
//}









//import android.annotation.SuppressLint
//import android.content.Intent
//import android.graphics.Color
//import android.os.Build
//import android.os.Bundle
//import android.view.MenuItem
//import android.view.View
//import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//import android.view.WindowManager
//import android.widget.Button
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.GravityCompat
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.WindowCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.Fragment
//import androidx.navigation.findNavController
//import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.setupActionBarWithNavController
//import androidx.navigation.ui.setupWithNavController
//import com.androidtest.notes.databinding.ActivityMainBinding
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.android.material.navigation.NavigationView
//import com.jaeger.library.StatusBarUtil
//import kotlinx.android.synthetic.main.activity_main.*
//
//
//
//
//class MainActivity : AppCompatActivity() {
//
//
//    private lateinit var binding: ActivityMainBinding
//
//
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        //状态栏背景颜色与文字图标颜色
//        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_main)
//
//        StatusBarUtil.setColor(this, resources.getColor(R.color.white),0)
//        lightStatusBar(window)
//
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//
//
//
//
//
////        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
////        val toolbar: Toolbar = findViewById(R.id.toolbar)
////        val navView: NavigationView = findViewById(R.id.menu)
//
//        //ActionBar实例：toolbar
////        setSupportActionBar(toolbar)
////        supportActionBar?.let {
////            it.title = null
////            it.setDisplayHomeAsUpEnabled(true)//显示navView的home导航图标
////            it.setHomeAsUpIndicator(R.drawable.ic_menu)//设定导航图标
////        }
//
////        setSupportActionBar(topAppBar)
////        supportActionBar?.let {
////            it.title = null
////            it.setDisplayHomeAsUpEnabled(true)//显示navView的home导航图标
////            it.setHomeAsUpIndicator(R.drawable.ic_menu)//设定导航图标
////        }
//
//        //index默认选中
////        navView.setCheckedItem(R.id.index)
////        //导航抽屉跳转
////        navView.setNavigationItemSelectedListener { MenuItem ->
////            when (MenuItem.itemId) {
////                R.id.user -> {
////                    val intent = Intent(this, LoginActivity().javaClass)
////                    startActivity(intent)
////                }
////
////                R.id.todo -> {
////                    val intent = Intent(this, TodoActivity().javaClass)
////                    startActivity(intent)
////                }
////
////                R.id.note -> {
////                    val intent = Intent(this, NoteActivity().javaClass)
////                    startActivity(intent)
////                }
////
////
////            }
////            drawerLayout.closeDrawers()
////            true
////        }
//
////        topAppBar.title = null
////        topAppBar.setNavigationOnClickListener {
////            // Handle navigation icon press
////            val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
////            drawerLayout.openDrawer(GravityCompat.START)
////        }
//
////        topAppBar.setOnMenuItemClickListener { menuItem ->
////            when (menuItem.itemId) {
////                R.id.view -> {
////                    // Handle search icon press
////                    true
////                }
////                else -> false
////            }
////        }
//
//
//    }
//
//    //Toolbar 导航图标设置
////    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
////        when (item.itemId) {//点击导航图标事件：展开
////            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
////        }
////        return true
////    }
//
//}
//
