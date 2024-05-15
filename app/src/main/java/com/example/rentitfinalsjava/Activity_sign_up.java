package com.example.rentitfinalsjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Activity_sign_up extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText, usernameEditText, passwordEditText;
    private RadioGroup userTypeRadioGroup, genderRadioGroup;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initializeDB();

        firstNameEditText = findViewById(R.id.signup_firstname);
        lastNameEditText = findViewById(R.id.signup_lastname);
        emailEditText = findViewById(R.id.signup_email);
        usernameEditText = findViewById(R.id.signup_username);
        passwordEditText = findViewById(R.id.signup_password);
        userTypeRadioGroup = findViewById(R.id.rb_usertype);
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        signUpButton = findViewById(R.id.signup_button);

        signUpButton.setOnClickListener(view -> insertUsers());


    }

    public void insertUsers(){

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Get selected user type
        RadioButton userTypeRadioButton = findViewById(userTypeRadioGroup.getCheckedRadioButtonId());
        String userType = userTypeRadioButton.getText().toString();

        // Get selected gender
        RadioButton genderRadioButton = findViewById(genderRadioGroup.getCheckedRadioButtonId());
        String gender = genderRadioButton.getText().toString();

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.insertUser(firstName, lastName, gender, email, username, password);
    }

}
