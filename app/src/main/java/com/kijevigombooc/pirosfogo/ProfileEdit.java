package com.kijevigombooc.pirosfogo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static androidx.core.content.FileProvider.getUriForFile;

public class ProfileEdit extends AppCompatActivity
{
    final int REQUEST_CAMERA = 0;
    final int REQUEST_GALLERY = 1;
    final int REQUEST_CAMERA_REQUEST = 2;
    final int REQUEST_WRITE_REQUEST = 3;
    ImageView image;
    ImageView rotateLeft;
    ImageView rotateRight;
    ProgressBar loadingCircleLeft;
    ProgressBar loadingCircleRight;
    TextView totalReds;
    TextView totalMatches;
    TextView averageReds;
    EditText name;
    long id = -69;
    String pathToFile = null;
    boolean processing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setResult(RESULT_CANCELED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        rotateLeft = findViewById(R.id.rotateLeft);
        rotateRight = findViewById(R.id.rotateRight);
        image = findViewById(R.id.profileImage);
        name = findViewById(R.id.profileName);
        loadingCircleLeft = findViewById(R.id.loadingCircleLeft);
        loadingCircleRight = findViewById(R.id.loadingCircleRight);
        Button btSaveProfile = findViewById(R.id.saveProfile);
        totalMatches = findViewById(R.id.totalMatches);
        totalReds = findViewById(R.id.totalReds);
        averageReds = findViewById(R.id.averageReds);

        image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                chooseImageOption();
            }
        });
        btSaveProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveAndFinish();
            }
        });
        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!processing)
                {
                    ImageRotateTask task = new ImageRotateTask(true);
                    task.setBitmap(((BitmapDrawable)image.getDrawable()).getBitmap());
                    task.execute();
                }
            }
        });
        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!processing)
                {
                    ImageRotateTask task = new ImageRotateTask(false);
                    task.setBitmap(((BitmapDrawable)image.getDrawable()).getBitmap());
                    task.execute();
                }
            }
        });

        loadData();
        setStats();
    }

    public void setLoading(boolean left, boolean loading){
        if(left){
            if(loading){
                loadingCircleLeft.setVisibility(View.VISIBLE);
                rotateLeft.setVisibility(View.GONE);
                rotateRight.setVisibility(View.INVISIBLE);
            }
            else{
                loadingCircleLeft.setVisibility(View.GONE);
                rotateLeft.setVisibility(View.VISIBLE);
                rotateRight.setVisibility(View.VISIBLE);
            }
        }
        else{
            if(loading){
                loadingCircleRight.setVisibility(View.VISIBLE);
                rotateRight.setVisibility(View.GONE);
                rotateLeft.setVisibility(View.INVISIBLE);
            }
            else{
                loadingCircleRight.setVisibility(View.GONE);
                rotateRight.setVisibility(View.VISIBLE);
                rotateLeft.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setImage(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }

    private class ImageRotateTask extends AsyncTask<Void, Void, Void> {

        private boolean left = false;
        ImageRotateTask(boolean left){
            this.left = left;
        }

        private Bitmap bitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processing = true;
            setLoading(left, true);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Matrix matrix = new Matrix();
            matrix.postRotate(left ? -90 : 90);
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);

            bitmap = rotated;
            return null;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setImage(bitmap);
            processing = false;
            setLoading(left, false);
        }
    }

    void askForPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA},2);
    }

    void saveAndFinish(){
        DBHelper dbHelper = new DBHelper(this);
        String nameString = name.getText().toString().trim();
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

        if(getIntent().getBooleanExtra("load", false))
            dbHelper.updateProfile(id, nameString, Utils.getBytes(bitmap));
        else
            id = dbHelper.addProfile(nameString, Utils.getBytes(bitmap));

        Intent intent = new Intent();
        intent.putExtra("id", id);
        setResult(RESULT_OK, intent);
        finish();
    }

    void loadData(){
        DBHelper dbHelper = new DBHelper(this);
        if(getIntent().getBooleanExtra("load", false)){
            id = getIntent().getLongExtra("id", id);
            Profile p = dbHelper.getProfileByID(id);
            if(p != null)
            {
                if(p.getName() != null)
                    name.setText(p.getName());
                if(p.getImageData()!=null)
                    image.setImageBitmap(Utils.getImage(p.getImageData()));
            }
        }
    }

    void setStats(){
        if(getIntent().getBooleanExtra("load", false)){
            DBHelper helper = new DBHelper(this);

            int matchCount = helper.getProfileMatchCount(id);
            int redCount = helper.getProfileTotalReds(id);
            double average = (double)redCount / (double)matchCount;

            totalMatches.setVisibility(View.VISIBLE);
            totalMatches.setText("Összes meccs: " + matchCount);
            totalReds.setVisibility(View.VISIBLE);
            totalReds.setText("Összes piros: " + redCount);
            averageReds.setVisibility(View.VISIBLE);
            averageReds.setText("Átlag piros: " + new DecimalFormat("##.##").format(average));
        }
    }

    void takePhoto(){

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,},REQUEST_CAMERA_REQUEST);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},REQUEST_WRITE_REQUEST);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            return;


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null)
        {
                File photoFile = createPhotoFile();
                if(photoFile != null){
                    pathToFile = photoFile.getAbsolutePath();
                    Uri photoURI = getUriForFile(this, this.getPackageName() + ".fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            }

    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try{
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch(IOException e){
            Log.d("myLog", "Except: " + e.toString());
        }
        return image;
    }

    void loadFromGallery(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 110);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        }

    }

    void chooseImageOption(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEdit.this, R.style.AlertDialog);
        builder.setTitle("Choose:");
        String items[] = {"Gallery", "Camera"};
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(which == 0)
                    loadFromGallery();
                else if(which == 1)
                    takePhoto();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
            image.setImageBitmap(bitmap);
        }
        else if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();
            image.setImageURI(imageUri);
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
            image.setImageBitmap(bitmap);
        }
    }
}
