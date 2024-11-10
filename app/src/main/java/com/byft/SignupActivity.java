package com.byft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private EditText inputName, inputEmail, inputPhone, inputPassword, inputConfirmPassword;
    private Button registerButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPhone = findViewById(R.id.inputPhone);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        registerButton = findViewById(R.id.registerButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Get input values
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // Validate input fields
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone number format (Sri Lankan format)
        if (!isValidPhone(phone)) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password strength (at least 6 characters, 1 uppercase, 1 number)
        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 6 characters, with at least one uppercase letter and one number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert user into the database
        boolean success = databaseHelper.insertUser(name, email, phone, password);
        if (success) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            // Redirect to another activity (e.g., LoginActivity)
            Intent intent = new Intent(this,SignInActivity.class); // Change LoginActivity to your desired next screen
            startActivity(intent);
            finish(); // Optionally finish the current activity
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to validate email format
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailPattern, email);
    }

    // Helper method to validate Sri Lankan phone number format (+94 or 0 followed by 9 digits)
    private boolean isValidPhone(String phone) {
        // Regex to match Sri Lankan phone numbers with format +94 7X XXXX XXX or 07X XXXX XXX
        String sriLankanPhonePattern = "^(\\+94|0)7\\d{8}$";
        return Pattern.matches(sriLankanPhonePattern, phone);
    }

    // Helper method to validate password strength
    private boolean isValidPassword(String password) {
        // Password should have at least one uppercase letter, one number, and be at least 6 characters long
        String passwordPattern = "(?=.*[A-Z])(?=.*\\d).{6,}";
        return password.matches(passwordPattern);
    }
}
