package com.technovateria.loveshayari.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.technovateria.loveshayari.AllShayariActivity;
import com.technovateria.loveshayari.MainActivity;
import com.technovateria.loveshayari.Model.ShayariCategoryModel;
import com.technovateria.loveshayari.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ShayariCategoryAdapter extends RecyclerView.Adapter<ShayariCategoryAdapter.MyViewHolder> {

    List<ShayariCategoryModel> list;
    MainActivity mainActivity;
    List<Integer> gradientColorList;
    List<Integer> emojiList;
    Random random = new Random();


    public ShayariCategoryAdapter(MainActivity mainActivity, List<ShayariCategoryModel> list) {
        this.mainActivity = mainActivity;
        this.list = list;
        Collections.shuffle(list);
    }


    @NonNull
    @Override
    public ShayariCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shayari_category, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShayariCategoryAdapter.MyViewHolder holder, int position) {

        // set icon for each shayari
        emojiList = new ArrayList<>(Arrays.asList(
                R.drawable.ic_attitude_emoji,
                R.drawable.ic_birthday_emoji,
                R.drawable.ic_cry_emoji,
                R.drawable.ic_headphone_emoji,
                R.drawable.ic_hug_emoji,
                R.drawable.ic_inlove_emoji,
                R.drawable.ic_joke_emoji,
                R.drawable.ic_romantic_emjoi,
                R.drawable.ic_sad_emoji,
                R.drawable.ic_smile_emoji
        ));
        int randomEmojiIndex = random.nextInt(emojiList.size());
        int randomEmoji = emojiList.get(randomEmojiIndex);
        holder.emojiView.setBackgroundResource(randomEmoji);


        //set the shayari category name
        holder.textView.setText(list.get(position).getName());


        gradientColorList = new ArrayList<>(Arrays.asList(
                R.drawable.gradient_red,
                R.drawable.gradient_purple,
                R.drawable.gradient_green,
                R.drawable.gradient_orange,
                R.drawable.gradient_navy_blue,
                R.drawable.gradient_dracula,
                R.drawable.gradient_royal,
                R.drawable.gradient_yellow

        ));
        int randomColorIndex = random.nextInt(gradientColorList.size());
        int randomGradient = gradientColorList.get(randomColorIndex);
        holder.mainBackground.setBackgroundResource(randomGradient);


        int randomTotalShayari = random.nextInt(4000) + 1000;
        String totalShayari = String.format("%d+ Shayari", randomTotalShayari);
        holder.totalShayari.setText(totalShayari);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View emojiView;
        public TextView textView;
        public View mainBackground;

        public TextView totalShayari;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiView = (ImageView) itemView.findViewById(R.id.emoji_view);
            textView = (TextView) itemView.findViewById(R.id.shayari_categories);
            mainBackground = (LinearLayout) itemView.findViewById(R.id.mainBackground);
            totalShayari = (TextView) itemView.findViewById(R.id.total_number_of_shayari);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            Context context = v.getContext();
            //Toast.makeText(v.getContext(), "Position: " + position, Toast.LENGTH_SHORT);
            Intent intent = new Intent(context, AllShayariActivity.class);
            intent.putExtra("id", list.get(position).getId());
            intent.putExtra("name", list.get(position).getName());

            context.startActivity(intent);
        }
    }

}
