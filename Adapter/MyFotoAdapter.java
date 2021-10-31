package com.kushnarev.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kushnarev.instagram.Model.Post;
import com.kushnarev.instagram.R;

import java.util.List;

//АДАПТЕР ДЛЯ ОТОБРАЖЕНИЯ ВСЕХ ПОТСОВ ПОЛЬЗОВАТЕЛЯ
public class MyFotoAdapter extends RecyclerView.Adapter<MyFotoAdapter.ViewHolder> {

    private Context context;
    private List<Post> mPost;

    public MyFotoAdapter(Context context, List<Post> mPost) {
        this.context = context;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                //указываем наш файл макет
        View view = LayoutInflater.from(context).inflate(R.layout.fotos_item, viewGroup, false);
        return new MyFotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Post post = mPost.get(i);

        Glide.with(context).load(post.getPostimage()).into(viewHolder.post_image);

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView post_image;

                //СОЗДАЛИ ВЬЮ ХОЛДЕР И ПОКАЗАЛИ КУДА ИМЕННО ВСТАВЛЯТЬ ФОТО
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);

        }
    }



}
