package com.kushnarev.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

//КЛАСС ДЛЯ ПУБЛИКАЦИЙ ФОТО
public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView close, image_added;
    TextView post;
    EditText description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
                    //Ссылка в хранилище Storage
        storageReference = FirebaseStorage.getInstance().getReference("posts");


                //отслежиавем нажатие на крестик (и при нажатий выходим на клавную страницу)
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

            //Нажатие на кнопку Опубликовать
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Метод для публикования фото
                uploadImage();
            }
        });

            //Устанавливаем размеры выбранной области И ВРОДЕ КАК СРАЗУ ЗАПУСКАЕМ ВЫБОР ФОТО
        CropImage.activity()
                .setAspectRatio(1,1).start(PostActivity.this);

    }

            //ВРОДЕ КАК СРАЗУ ЗАПУСКАЕМ ВЫБОР ФОТО
            //НЕЗНАЮ ЧТО ЗА МЕТОД НУЖНО ПОТОМ РАЗОБРАТЬСЯ
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


         //Метод для публикования фото - отправки в бд и тд
    private void uploadImage() {
            //Загрузка анимаций загрузки
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

                //если переменная с фото не пустая
        if (imageUri != null) {
                //ДОБАВЛЯЕМ ЕЕ В СТОРЕДЖ СОЗДАЕМ ПУТЬ
            StorageReference filerefrence = storageReference.child(System.currentTimeMillis()
            + "."+ getFileExtension(imageUri));
                //вставляем фото в СТОРЕДЖ
            uploadTask = filerefrence.putFile(imageUri);
                //обновляем и добавляем фото
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    //ПРИ НЕУДАЧЕ
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                            //ВОЗВРАШАЕМ ССЫЛКУ НА ЭТО ФОТО
                    return filerefrence.getDownloadUrl();
                }
                //при успешной загрузке в сторедж
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                        //если все удачно получилось
                    if (task.isSuccessful()){
                        //берем ссылку на это фото из сторедж из предидушего метода
                        Uri downLoadUri = task.getResult();
                        myUrl = downLoadUri.toString();
                            //ДОБАВЛЯЕМ В БД
                            //пишем путь
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                            //папка поста с привязанным к нему ключем
                        String postid = reference.push().getKey();

                            //собираем данные для отправки в БД
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", myUrl);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getUid());

                              //  Вставляем данные в БД в папке посты создаем папку для каждого поста и даем ей ключ
                        reference.child(postid).setValue(hashMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(PostActivity.this, "Произошла Ошибка", Toast.LENGTH_SHORT).show();
                    }
                }
                //ПРИ ошибке постинга фото
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            //Ecли фото отсутвует
        }else {
            Toast.makeText(this, "Добавте фотографию", Toast.LENGTH_SHORT).show();
        }

    }




    @Override // При  выборе картинки из галлерий
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            //получаем ту область что выбрали
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            // и получаем uri
            imageUri = result.getUri();
            //вставляем ее в ImageView
            image_added.setImageURI(imageUri);

        }else {
            Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
        //ПРИ ИСПОЛЬЗОВАНИЙ ЭТОГО КОДА ВЫВОДИТ ОШИБКУ ТЕ НЕ ПРОХОДИТ УСЛОВИЕ
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && requestCode == RESULT_OK){

    }





}