package com.kest.softij

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.kest.softij.vm.MainViewModel

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    ProductFragment.ToProductFragment,
    AddressFragment.ToAddressFragment
{

    private lateinit var actionBarDrawerToggle:ActionBarDrawerToggle
    private lateinit var drawerLayout:DrawerLayout

    private val viewModel: MainViewModel by lazy{
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            findViewById<ImageButton>(R.id.search_btn).visibility = View.GONE
            findViewById<EditText>(R.id.search).visibility = View.GONE
        }else if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            findViewById<ImageButton>(R.id.search_btn).visibility = View.VISIBLE
            findViewById<EditText>(R.id.search).visibility = View.VISIBLE
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        viewModel.title?.let {
            updateTitle()
        }
        drawerLayout = findViewById(R.id.main_activity_drawer)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,
            drawerLayout,
            R.string.desc_open_navigation_drawer,
            R.string.desc_close_navigation_drawer).apply { syncState() }


        supportFragmentManager.addOnBackStackChangedListener {
            if(supportFragmentManager.backStackEntryCount < viewModel.titleStack.size){
                viewModel.titleStack.pop()
            }
            val titleId = if(viewModel.titleStack.empty()) R.string.app_name else viewModel.titleStack.peek()
            viewModel.title = getString(titleId)
            updateTitle()
        }

        findViewById<NavigationView>(R.id.navigation_drawer).setNavigationItemSelectedListener(this)

        val fragment:Fragment? = supportFragmentManager.findFragmentById(R.id.main_activity_screen)
        if(fragment == null){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_activity_screen,ListFragment.init(ListFragment.LIST_PRODUCTS))
                .commit()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            findViewById<ImageButton>(R.id.search_btn).visibility = View.GONE
            findViewById<EditText>(R.id.search).visibility = View.GONE
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            findViewById<ImageButton>(R.id.search_btn).visibility = View.VISIBLE
            findViewById<EditText>(R.id.search).visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.navbar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true
        }else if(item.itemId == R.id.cart){
            Toast.makeText(this,"Cart",Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_home -> {
                while (supportFragmentManager.backStackEntryCount > 0){
                    supportFragmentManager.popBackStackImmediate()
                }
            }
            R.id.my_wishlist -> {
                replaceFragment(
                    ListFragment.init(ListFragment.LIST_WISHLIST),
                    R.string.title_my_wishlist)
            }
            R.id.my_order -> {
                replaceFragment(
                    ListFragment.init(ListFragment.LIST_ORDERS),
                    R.string.title_my_order
                )
            }
            R.id.my_account -> {
                replaceFragment(
                    AccountFragment(),R.string.title_my_acc
                )
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    private fun replaceFragment(fragment: Fragment,id:Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_activity_screen, fragment)
            .addToBackStack(null)
            .commit()
        viewModel.titleStack.add(id)
    }

    private fun updateTitle(){
        supportActionBar?.title = viewModel.title
    }

    override fun launchProduct(productFragment: ProductFragment) {
        replaceFragment(productFragment,R.string.title_product)
    }

    override fun launchAddress() {
        replaceFragment(AddressFragment(),R.string.btn_address)
    }


}