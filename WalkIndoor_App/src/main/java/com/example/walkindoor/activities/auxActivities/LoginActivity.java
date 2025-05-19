package com.example.walkindoor.activities.auxActivities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.walkindoor.R;
import com.example.walkindoor.activities.MainActivity;
import com.example.walkindoor.api.ApiClient;
import com.example.walkindoor.api.ApiService;
import com.example.walkindoor.api.UserDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText editTextEmail = findViewById(R.id.etUsername);
        EditText editTextPassword = findViewById(R.id.etPassword);

        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> authenticateUser(editTextEmail, editTextPassword));
    }

    private void authenticateUser(EditText editTextEmail, EditText editTextPassword) {
        String userInput = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (userInput.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getApiService();
        Call<UserDTO> call = apiService.findUser(userInput); // userInput puede ser un email o username

        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO user = response.body();

                    if (user.getPassword().equals(password)) {
                        saveUserSession(user.getId());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("LoginActivity", "Error de conexión", t);
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserSession(Long userId) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        Log.d("LoginActivity", "ID de usuario guardado: " + userId);
        editor.putLong("userId", userId);
        editor.apply();
        Log.d("LoginActivity", "Sesión guardada exitosamente.");
    }
}
