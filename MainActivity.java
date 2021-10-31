package com.kushnarev.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.kushnarev.instagram.Fragment.HomeFragment;
import com.kushnarev.instagram.Fragment.NotificationFragment;
import com.kushnarev.instagram.Fragment.ProfileFragment;
import com.kushnarev.instagram.Fragment.SearchFragment;
        //Главная страница приложения с расположенными на нем фрагментами
public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            //обозначаем наше меню
        bottomNavigationView = findViewById(R.id.bottom_navigation);
            // указания действий кнопкам в меню
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //получаем данные из комент адаптер
        //и открываем профиль фрагемент
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
                    //Переходим в профиль фрагемент
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        }else {
            //заходя в приложение открываем домашний фрагмент
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }



    }

        //метод указывающий что делать при нажатий на опреденную кнопку в меню
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){

                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_add:
                            selectedFragment = null;
                            startActivity(new Intent(MainActivity.this, PostActivity.class));
                            break;
                        case R.id.nav_heart:
                            selectedFragment = new NotificationFragment();
                            break;
                        case R.id.nav_profile:
                                //ЕСЛИ ПОЛЬЗВАОТЕЛЬ НАЖИМАЕТ НА ПРОФИЛЬ ТО ОТРЫВАЕТСЯ ЕГО СТРАНИЧКА
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                        //если фрагмент не открыт только что, то всталяем то что нажато
                    if (selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }

                    return true ;

                }

            };


}