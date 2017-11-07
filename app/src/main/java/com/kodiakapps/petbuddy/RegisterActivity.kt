package com.kodiakapps.petbuddy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var et_fname = findViewById(R.id.et_fname) as EditText
        var et_lname = findViewById(R.id.et_lname) as EditText
        var et_email = findViewById(R.id.et_email) as EditText
        var et_pwd = findViewById(R.id.et_pwd) as EditText
        var but_submit = findViewById(R.id.reg_submit_btn) as Button

        but_submit.setOnClickListener {

            val firstname = et_fname.text
            val lastname = et_lname.text
            val emailadd = et_email.text
            val pwd = et_pwd.text

            when {
                firstname.isEmpty() -> Toast.makeText(this@RegisterActivity,
                        "Enter First Name", Toast.LENGTH_LONG).show()
                lastname.isEmpty() -> Toast.makeText(this@RegisterActivity,
                        "Enter Last Name", Toast.LENGTH_LONG).show()
                emailadd.isEmpty() -> Toast.makeText(this@RegisterActivity,
                        "Enter e-mail address", Toast.LENGTH_LONG).show()
                pwd.isEmpty() -> Toast.makeText(this@RegisterActivity,
                        "Choose a password", Toast.LENGTH_LONG).show()

                else -> {
                    et_fname.setText("")
                    et_lname.setText("")
                    et_email.setText("")
                    et_pwd.setText("")

                    println("firstname = " + firstname)
                    println("lastname = " + lastname)
                    println("email = " + emailadd)
                    println("pwassword = " + pwd)

                    Toast.makeText(this@RegisterActivity, "logged in as: "+ firstname, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}
