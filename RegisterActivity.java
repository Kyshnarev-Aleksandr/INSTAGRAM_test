package com.kushnarev.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

//экран регистарций пользователя
public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);


        auth = FirebaseAuth.getInstance();

                //отслеживание нажатия на текст под кнопкой
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

            //Нажатие на кнопку зарегестриваться
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //подключаем прогресс дайлог для загрузки
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                    //получаем данные из полей ввода в стринги
                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                    //если хоть одно из полей пустое
                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                        || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegisterActivity.this, "Все поля должны быть запонены!", Toast.LENGTH_SHORT).show();
                }else if(str_password.length() < 6 ){
                    Toast.makeText(RegisterActivity.this, "Пароль должен быть больше 6 символов", Toast.LENGTH_SHORT).show();
                }else {
                    register(str_username, str_fullname, str_email, str_password);
                }

            }
        });





    }
            //метод регистраций пользователя в базе данных
    private void register(String username, String fullname, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            //при успешной регистраций
                        if (task.isSuccessful()){
                            //берем зарегестрированного пользоавтеля
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            // и берем его айди
                            String userID = firebaseUser.getUid();
                                //и указываем коренную ссылку
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                //показываем какаие данные и куда вставить
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userID);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio", "");
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/instagram-4b96f.appspot.com/o/429-4297972_search-log-in-to-your-teach-california-account.png?alt=media&token=2d73a3a2-9de5-4501-b2df-ecaf435eb833");

                                //вставляем данные о пользователе в папку с его айди
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Ошибка регистраций попробуйте снова", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}