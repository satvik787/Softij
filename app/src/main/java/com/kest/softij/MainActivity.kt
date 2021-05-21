package com.kest.softij

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private lateinit var actionBarDrawerToggle:ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        val drawerLayout = findViewById<DrawerLayout>(R.id.main_activity_drawer);

         actionBarDrawerToggle = ActionBarDrawerToggle(this,
            drawerLayout,
            R.string.desc_open_navigation_drawer,
            R.string.desc_close_navigation_drawer)
        actionBarDrawerToggle.syncState();

        val fragment:Fragment? = supportFragmentManager.findFragmentById(R.id.main_activity_screen)
        if(fragment == null){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_activity_screen,HomeFragment())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)

    }
}