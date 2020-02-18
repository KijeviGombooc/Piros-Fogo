package com.kijevigombooc.pirosfogo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerDisplays {

    private PlayerDisplay[] playerDisplays = new PlayerDisplay[4];
    private ViewGroup scoresHolder = null;
    private Context context = null;


    PlayerDisplays(Context context, ViewGroup scoresHolder, View[] playerDisplayViews) {
        this.scoresHolder = scoresHolder;
        this.context = context;
        for(int i = 0; i < 4; i++){
            playerDisplays[i] = new PlayerDisplay();
            playerDisplays[i].nameDisplay = playerDisplayViews[i].findViewById(R.id.playerName);
            playerDisplays[i].totalScoreDisplay = playerDisplayViews[i].findViewById(R.id.playerScore);
            playerDisplays[i].imageDisplay = playerDisplayViews[i].findViewById(R.id.playerImage);
        }
    }


    void addRow(byte[] scores){
        View layout = LayoutInflater.from(context).inflate(R.layout.score_row, scoresHolder, false);
        scoresHolder.addView(layout);

        int ids[] = {R.id.score1, R.id.score2, R.id.score3, R.id.score4};
        for(int i = 0; i < 4; i++){
            EditText sd = layout.findViewById(ids[i]);
            sd.setText(String.valueOf(scores[i]));
            playerDisplays[i].scoreDisplays.add(sd);
        }
    }

    boolean isRowSumEight(int rowIndex){
        int sum = 0;
        for(int i = 0; i < 4; i++){
            EditText editText = playerDisplays[i].scoreDisplays.get(rowIndex);
            if(editText.getText().toString().isEmpty())
                editText.setText("0");
            sum += Integer.valueOf(editText.getText().toString());
        }
        return sum == 8;
    }

    void editRow(int rowIndex, Byte s1, Byte s2, Byte s3, Byte s4){

        Byte scs[] = {s1, s2, s3, s4};

        for(int i = 0; i < 4; i++){
            playerDisplays[i].scoreDisplays.get(rowIndex).setText(String.valueOf(scs[i]));
        }
    }

    void removeRow(int rowIndex){
        for(int i = 0; i < 4; i++){
            TextView tmp = playerDisplays[i].scoreDisplays.get(rowIndex);
            playerDisplays[i].scoreDisplays.remove(rowIndex);
            scoresHolder.removeView(tmp);
        }
    }

    void lockRow(int rowIndex){
        for(int i = 0; i < 4; i++) {
            playerDisplays[i].scoreDisplays.get(rowIndex).setKeyListener(null);
        }
    }

    //TODO: implement unlock. Tip: keep only 1 row unlocked at a time

    void updateTotalScore(int playerIndex, int totalScore){
        playerDisplays[playerIndex].totalScoreDisplay.setText(String.valueOf(totalScore));
    }

    void updateAllTotalScores(byte[] totalScores){
        for(int i = 0; i < 4; i++){
            playerDisplays[i].totalScoreDisplay.setText(String.valueOf(totalScores[i]));
        }
    }

    int getLastRowIndex(){
        return playerDisplays[0].scoreDisplays.size()-1;
    }

    byte[] getRow(int rowIndex){
        byte[] row = new byte[4];
        for(int i = 0; i < 4; i++){
            EditText editText = playerDisplays[i].scoreDisplays.get(rowIndex);
            if(editText.getText().toString().isEmpty())
                editText.setText("0");
            row[i] = Byte.valueOf(editText.getText().toString());
        }
        return row;
    }

    void setImage(int playerIndex, Bitmap bitmap){
        if(bitmap != null)
            playerDisplays[playerIndex].imageDisplay.setImageBitmap(bitmap);
    }

    void setImages(Bitmap[] bitmaps){
        for(int i = 0; i < 4; i++){
            if(bitmaps[i] != null)
                playerDisplays[i].imageDisplay.setImageBitmap(bitmaps[i]);
        }
    }

    void setName(int playerIndex, String name){
        playerDisplays[playerIndex].nameDisplay.setText(name);
    }

    void setNames(String[] names){
        for(int i = 0; i < 4; i++) {
            playerDisplays[i].nameDisplay.setText(names[i]);
        }
    }

    void setImageOnClickListener(int playerIndex, View.OnClickListener listener){
        playerDisplays[playerIndex].imageDisplay.setOnClickListener(listener);
    }

    void setImageOnClickListeners(View.OnClickListener listener){
        for(int i = 0; i < 4; i++){
            playerDisplays[i].imageDisplay.setOnClickListener(listener);
        }
    }

    private class PlayerDisplay{
        TextView nameDisplay;
        ImageView imageDisplay;
        ArrayList<EditText> scoreDisplays = new ArrayList<>();
        TextView totalScoreDisplay = null;
    }
}
