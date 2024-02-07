package in.innovateria.hindishayari.Adapters;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import in.innovateria.hindishayari.Activities.AllShayariActivity;
import in.innovateria.hindishayari.Models.AllShayariModel;
import in.innovateria.hindishayari.R;

public class AllShayariAdapter extends RecyclerView.Adapter<AllShayariAdapter.ShayariViewHolder> {

    private List<AllShayariModel> list;
    AllShayariActivity allShayariActivity;
    List<Integer> imageList;
    Random random = new Random();
    public AllShayariAdapter(AllShayariActivity allShayariActivity, List<AllShayariModel> list) {
        this.allShayariActivity = allShayariActivity;
        this.list = list;
        Collections.shuffle(list);
    }

    @NonNull
    @Override
    public ShayariViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_shayari, parent, false);

        return new ShayariViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShayariViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // set Text in TextView using fromHtml() method with version check
        String data = list.get(position).getData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            holder.textView.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY));
        else
            holder.textView.setText(Html.fromHtml(data));

//        holder.textView.setText(list.get(position).getData());

        imageList = new ArrayList<>(Arrays.asList(
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3,
                R.drawable.image4,
                R.drawable.image5,
                R.drawable.image6,
                R.drawable.image7,
                R.drawable.image8,
                R.drawable.image9,
                R.drawable.image10,
                R.drawable.image11,
                R.drawable.image12,
                R.drawable.image13,
                R.drawable.image14,
                R.drawable.image15,
                R.drawable.image16,
                R.drawable.image17,
                R.drawable.image18,
                R.drawable.image19,
                R.drawable.gradient_dracula,
                R.drawable.gradient_green,
                R.drawable.gradient_green,
                R.drawable.gradient_orange,
                R.drawable.gradient_purple,
                R.drawable.gradient_red,
                R.drawable.gradient_red_light,
                R.drawable.gradient_yellow
                ));

        int randomImageIndex = random.nextInt(imageList.size());
        int randomImage = imageList.get(randomImageIndex);
        holder.shayariBackgroundImg.setBackgroundResource(randomImage);


        // set functionality for copy text button
        holder.copyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(list.get(position).getData());
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        // set functionality for download image button
        holder.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage(v, holder);
            }
        });

        // set functionality for refresh image button
        holder.refreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int randomImageIndex = random.nextInt(imageList.size());
                int randomImage = imageList.get(randomImageIndex);
                holder.shayariBackgroundImg.setBackgroundResource(randomImage);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(v, holder);
                sharePopupMenu(v, holder, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ShayariViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public View shayariBackgroundImg;
        public View copyText;
        public View downloadImage;
        public View refreshImage;
        public View share;
        public int SHARE_REQUEST_CODE = 101;

        public ShayariViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.all_shayari);
            shayariBackgroundImg = (LinearLayout) itemView.findViewById(R.id.shayari_back_img);
            copyText = (LinearLayout) itemView.findViewById(R.id.copy_text);
            downloadImage = (LinearLayout) itemView.findViewById(R.id.download_image);
            refreshImage = (LinearLayout) itemView.findViewById(R.id.refresh_image);
            share = (LinearLayout) itemView.findViewById(R.id.share);
        }
    }

    private void sharePopupMenu(View v, ShayariViewHolder holder, int position){
        PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.share);
        popupMenu.inflate(R.menu.share_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.share_text:
                        shareText(v, position);
                        return true;
                    case R.id.share_image:
                        shareImage(v, holder);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void shareText(View v, int position){
        String shareBody = list.get(position).getData();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        v.getContext().startActivity(Intent.createChooser(intent, "Share image via"));
    }
    private void shareImage(View v, ShayariViewHolder holder) {

        holder.shayariBackgroundImg.setDrawingCacheEnabled(true);
        holder.shayariBackgroundImg.buildDrawingCache();
        holder.shayariBackgroundImg.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = holder.shayariBackgroundImg.getDrawingCache();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG" + timeStamp + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Hindi Shayari");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Hindi Shayari Image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        ContentResolver contentResolver = this.allShayariActivity.getContentResolver();
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (imageUri != null) {
            try {
                OutputStream outputStream = contentResolver.openOutputStream(imageUri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/jpeg");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    v.getContext().startActivity(Intent.createChooser(shareIntent, "Share Image via"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this.allShayariActivity.getApplicationContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveImage(View v, ShayariViewHolder holder) {

        holder.shayariBackgroundImg.setDrawingCacheEnabled(true);
        holder.shayariBackgroundImg.buildDrawingCache();
        holder.shayariBackgroundImg.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = holder.shayariBackgroundImg.getDrawingCache();

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG" + timeStamp + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Hindi Shayari");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Hindi Shayari Image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        ContentResolver contentResolver = this.allShayariActivity.getContentResolver();
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (imageUri != null) {
            try {
                OutputStream outputStream = contentResolver.openOutputStream(imageUri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(this.allShayariActivity.getApplicationContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this.allShayariActivity.getApplicationContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
