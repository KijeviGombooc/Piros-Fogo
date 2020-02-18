package com.kijevigombooc.pirosfogo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Profile {
    private Long id;
    private byte[] imageData;
    private Bitmap imageBitmap;
    private String name;

    public Profile(Long id, String name, byte[] image){
        this.id = id;
        this.imageData = image;
        this.name = name;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public String getName() {
        return name;
    }

    public Long getId(){
        return id;
    }

    public byte[] getImageBytes(){
        return imageData;
    }

    public void setImageBitmap(int width, int height){
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        double oldWidth = bitmap.getWidth();
        double oldHeight = bitmap.getHeight();
        height = (int)((double)width / oldWidth *oldHeight);
        imageBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public Bitmap getImageBitmap()
    {
        if(imageBitmap == null)
            imageBitmap = Utils.getImage(imageData);
        return imageBitmap;
    }

    public void changeNameTo(String name){
        this.name = name;
    }
}
