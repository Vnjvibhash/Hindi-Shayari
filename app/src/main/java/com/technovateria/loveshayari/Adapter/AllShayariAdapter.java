package com.technovateria.loveshayari.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.technovateria.loveshayari.AllShayariActivity;
import com.technovateria.loveshayari.Model.AllShayariModel;
import com.technovateria.loveshayari.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AllShayariAdapter extends RecyclerView.Adapter<AllShayariAdapter.ShayariViewHolder> {

    private List<AllShayariModel> list;
    AllShayariActivity allShayariActivity;
    List<Integer> imageList;
    Random random = new Random();
    public int CLICK_COUNT = 0;
    public InterstitialAd mInterstitialAd;

    public int SHARE_REQUEST_CODE = 101;

    public AllShayariAdapter(AllShayariActivity allShayariActivity, List<AllShayariModel> list) {
        this.allShayariActivity = allShayariActivity;
        this.list = list;
        Collections.shuffle(list);
    }

    @NonNull
    @Override
    public AllShayariAdapter.ShayariViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_shayari, parent, false);

        return new ShayariViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllShayariAdapter.ShayariViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //set shayari text
        Log.d("TAG", list.toString());
        holder.textView.setText(list.get(position).getData());


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

                if(CLICK_COUNT >= 10){
                    loadInterstitialAd(v);
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(allShayariActivity);

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                mInterstitialAd = null;
                            }
                        });

                        CLICK_COUNT = 0;
                    } else {
                        Log.d("InterstitialAd", "The interstitial ad wasn't ready yet.");
                    }
                } else {
                    CLICK_COUNT += 1;
                }
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                shareImage(v, holder);
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

    private void sharePopupMenu(View v, AllShayariAdapter.ShayariViewHolder holder, int position){
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
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        v.getContext().startActivity(Intent.createChooser(intent, "Share image via"));
    }
    private void shareImage(View v, AllShayariAdapter.ShayariViewHolder holder) {

//        //convert any layout as image
//        holder.shayariBackgroundImg.setDrawingCacheEnabled(true);
//        holder.shayariBackgroundImg.buildDrawingCache();
//        holder.shayariBackgroundImg.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//
//        //create bitmap image
//        Bitmap bitmap = holder.shayariBackgroundImg.getDrawingCache();
//
//        //save in external storage of app cache folder
//        File directory = new File(v.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
//
//        System.out.println(v.getContext().getFilesDir());
//        if (!directory.exists()) {
//            boolean created = directory.mkdirs();
//            if (!created) {
//                Log.e("Drectory Creation", "Failed to create directory");
//            }
//        }
//
//        String fname = String.format("%d.jpg", System.currentTimeMillis());
//        File file = new File("/storage/emulated/0/Android/data/com.technovateria.loveshayari/files/Download", fname);
//        Log.i("TAG", "" + file);
//
//        if (file.exists())
//            file.delete();
//
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//            fileOutputStream.flush();
//            fileOutputStream.close();
//            Toast.makeText(v.getContext(), "Wait..", Toast.LENGTH_SHORT).show();
//
//            holder.shayariBackgroundImg.setDrawingCacheEnabled(false);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(v.getContext(), "Error in Saving", Toast.LENGTH_SHORT).show();
//        }
//
//        Uri fileUri = Uri.parse("/storage/emulated/0/Android/data/com.technovateria.loveshayari/files/Download/" + fname);
//
//        // Create the share intent and set the type and data
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType("image/jpeg");
//        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//        // Add flags to grant temporary permission to external apps to read the content URI
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        // Start the share activity
//        v.getContext().startActivity(Intent.createChooser(shareIntent, "Share image via"));

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

    private void saveImage(View v, AllShayariAdapter.ShayariViewHolder holder) {
        //convert any layout as image
//        holder.shayariBackgroundImg.setDrawingCacheEnabled(true);
//        holder.shayariBackgroundImg.buildDrawingCache();
//        holder.shayariBackgroundImg.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//
//        //create bitmap image
//        Bitmap bitmap = holder.shayariBackgroundImg.getDrawingCache();
//
//        //String directoryName = "HindiShayari";
//
//        File directory = new File(v.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
//
//        System.out.println(v.getContext().getFilesDir());
//        if (!directory.exists()) {
//            boolean created = directory.mkdirs();
//            if (!created) {
//                System.out.println(v.getContext().getFilesDir());
//                System.out.println("==========================Failed to create directory==================");
//                Log.e("Drectory Creation", "Failed to create directory");
//            }
//        }
//
//
//        String fname = String.format("%d.jpg", System.currentTimeMillis());
//        File file = new File("/storage/emulated/0/Android/data/com.technovateria.loveshayari/files/Download", fname);
//        Log.i("TAG", "" + file);
//
//        if (file.exists())
//            file.delete();
//
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//            fileOutputStream.flush();
//            fileOutputStream.close();
//            Toast.makeText(v.getContext(), "Saved", Toast.LENGTH_SHORT).show();
//
//            holder.shayariBackgroundImg.setDrawingCacheEnabled(false);
//
//            String filePath = "/storage/emulated/0/Android/data/com.technovateria.loveshayari/files/Download";
//            MediaScannerConnection.scanFile(v.getContext(), new String[]{filePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
//                @Override
//                public void onScanCompleted(String path, Uri uri) {
//                    // Scan complete callback
//                    Log.i("TAG", "Scanned " + path);
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(v.getContext(), "Error in Saving", Toast.LENGTH_SHORT).show();
//        }

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

    private void loadInterstitialAd(View v){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(v.getContext(),v.getContext().getString(R.string.Interstitial_ad_unit), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("InterstitialAd", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("InterstitialAd", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }
}
