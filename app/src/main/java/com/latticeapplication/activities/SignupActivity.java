package com.latticeapplication.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.latticeapplication.R;
import com.latticeapplication.helpers.DatabaseClient;
import com.latticeapplication.models.User;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEdit, emailEdit, numberEdit, passwordEdit, addressEdit, countryCode;
    private Button signupBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        nameEdit = findViewById(R.id.name);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        numberEdit = findViewById(R.id.number);
        addressEdit = findViewById(R.id.address);
        countryCode = findViewById(R.id.code);
        signupBtn = findViewById(R.id.signup);
        signupBtn.setOnClickListener(v -> {
            if (validateUserDetails()) {
                saveUser();
            } else {

            }
        });
    }

    private boolean validateUserDetails() {
        if (nameEdit.getText().length() < 4) {
            nameEdit.setError("should be at least 4 characters");
            nameEdit.requestFocus();
            return false;
        } else if (!isValidEmailAddress(emailEdit.getText().toString())) {
            emailEdit.setError("should be a valid email address");
            emailEdit.requestFocus();
            return false;
        } else if (!isValidPassword(passwordEdit.getText().toString())) {
            passwordEdit.setError("must contain one upper character, one lower character and a number. Max length\n" +
                    "15 and min length 8");
            passwordEdit.requestFocus();
            return false;
        } else if (countryCode.getText().length() < 3) {
            countryCode.setError("Invalid Country Code");
            countryCode.requestFocus();
            return false;
        } else if (numberEdit.getText().length() < 10) {
            numberEdit.setError("Invalid Number");
            numberEdit.requestFocus();
            return false;
        } else if (addressEdit.getText().length() < 10) {
            addressEdit.setError("should be at least 10 characters");
            addressEdit.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void saveUser() {
        String userName = nameEdit.getText().toString().trim();
        String userEmail = emailEdit.getText().toString().trim();
        String userNumber = numberEdit.getText().toString().trim();
        String userAddress = addressEdit.getText().toString().trim();
        String userPassword = passwordEdit.getText().toString().trim();

        class SaveUser extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                User user = new User();
                user.setUserName(userName);
                user.setUserEmail(userEmail);
                user.setUserAddress(userAddress);
                user.setUserNumber(userNumber);
                user.setUserPassword(userPassword);

                // getting database instance and insertng new user;
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .userDao()
                        .insertUser(user);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }

        SaveUser sUser = new SaveUser();
        sUser.execute();

    }
}