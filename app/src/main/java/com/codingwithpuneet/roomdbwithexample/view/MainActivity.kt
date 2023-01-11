package com.codingwithpuneet.roomdbwithexample.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.codingwithpuneet.room_db_with_example.R
import com.codingwithpuneet.room_db_with_example.databinding.ActivityMainBinding
import com.codingwithpuneet.roomdbwithexample.room.UserDatabase
import com.codingwithpuneet.roomdbwithexample.room.entity.User
import kotlinx.coroutines.*

/*
Created by Puneet for "CodingWithPuneet"
 */

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    // database object
    private val database by lazy { UserDatabase.getDatabase(this, scope) }

    private val userDao by lazy { database.userDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        activityMainBinding.btnSubmit.setOnClickListener {
            // here we have to insert data (input data by user - name, and address) in Room DB
            val name = activityMainBinding.etName.text.toString()
            val address = activityMainBinding.etAddress.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                userDao.insert(User(name = name, address = address))
            }
        }

        // Get Data from Database
        // No need of Coroutine to execute the query here
        userDao.getAllUsers().observe(this) { usersData ->
            if (usersData.isNullOrEmpty()) {
                activityMainBinding.tvWelcome.isVisible = false
                activityMainBinding.tvWelcome.text = ""
            } else {
                usersData.forEach { user ->
                    val name = user.name
                    val address = user.address
                    val data = "Welcome $name From $address"
                    activityMainBinding.tvWelcome.isVisible = true
                    activityMainBinding.tvWelcome.text = data
                }
            }
        }

        // Delete data from database
        // Need to call this query in coroutine
        activityMainBinding.btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                userDao.deleteAll()
            }
        }
        activityMainBinding.btnShow.setOnClickListener {
            showAllData()
        }
    }

    /*
     Here we have to use Coroutines for Select Query
     */
    private fun showAllData() {
        CoroutineScope(Dispatchers.IO).launch {
            val usersList = userDao.getUsers()
            withContext(Dispatchers.Main) {
                if (usersList.isNullOrEmpty()) {
                    activityMainBinding.tvWelcome.isVisible = false
                    activityMainBinding.tvWelcome.text = ""
                } else {
                    usersList.forEach { user ->
                        val name = user.name
                        val address = user.address
                        val data = "Welcome $name To CodingWithPuneet"
                        activityMainBinding.tvWelcome.isVisible = true
                        activityMainBinding.tvWelcome.text = data
                    }
                }
            }
        }
    }
}