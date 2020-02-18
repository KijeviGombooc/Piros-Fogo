package com.kijevigombooc.pirosfogo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Game extends AppCompatActivity
{

    PlayerDisplays playerDisplays = null;
    PlayerScores playerScores = new PlayerScores();
    Profile[] profiles = new Profile[4];
    ScrollView scrollView;
    String date = null;
    long matchID = -1;
    Button button;

    final int REQUEST_PROFILE_1 = 1;
    final int REQUEST_PROFILE_2 = 2;
    final int REQUEST_PROFILE_3 = 3;
    final int REQUEST_PROFILE_4 = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        cacheViews();
        init();
    }

    void cacheViews(){
        button = findViewById(R.id.newLine);
        scrollView = findViewById(R.id.scrollView);

        View[] playerDisplayViews = new View[4];
        int[] ids = {R.id.p1, R.id.p2, R.id.p3, R.id.p4};
        for(int i = 0; i < 4; i++) {
            playerDisplayViews[i] = findViewById(ids[i]);
        }
        playerDisplays = new PlayerDisplays(this, (ViewGroup)findViewById(R.id.scoresHolder), playerDisplayViews);
    }

    void init(){
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                startGame();
            }
        });
        if(getIntent().getBooleanExtra("load", false)){
            loadGame();
            if(!isGameOver())
                startGame();
        }
        else{
            for(int i = 0; i < 4; i++){
                final int ii = i;
                playerDisplays.setImageOnClickListener(i, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseProfile(ii);
                    }
                });
            }
        }
    }

    private void chooseProfile(int playerIndex){
        Intent intent = new Intent(this, ProfileViewer.class);
        intent.putExtra("choose", true);
        startActivityForResult(intent, (playerIndex + 1));
    }

    private void startGame(){
        if(isGameOver()){
            endGame();
        }
        button.setText(getString(R.string.new_line));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRow();
            }
        });
        playerDisplays.addRow(new byte[]{0,0,0,0});
        playerDisplays.setImageOnClickListeners(null);
    }

    private void addRow() {
        int lastRowIndex = playerDisplays.getLastRowIndex();
        if(lastRowIndex >= 0){
            if(!playerDisplays.isRowSumEight(lastRowIndex)){
                Toast.makeText(this, "Valamit elszámoltál!", Toast.LENGTH_SHORT).show();
                return;
            }

            playerScores.addRow(playerDisplays.getRow(lastRowIndex));
            playerDisplays.lockRow(lastRowIndex);
            playerDisplays.updateAllTotalScores(playerScores.getAllTotalScores());
        }

        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        if(isGameOver()){
            endGame();
        }
        else{
            View focus = this.getCurrentFocus();
            playerDisplays.addRow(new byte[]{0, 0, 0, 0});
            scrollView.fullScroll(View.FOCUS_DOWN);
            focus.requestFocus();
        }

    }

    private void endGame() {
        playerDisplays.lockRow(playerDisplays.getLastRowIndex());
        button.setVisibility(View.GONE);
    }

    boolean isGameOver(){
        for(byte s : playerScores.getAllTotalScores())
            if(s >= 24)
                return true;

        if(playerDisplays.getLastRowIndex()>=0)
            for(int i = 0; i < 4; i++){
                byte rowVal = playerDisplays.getRow(playerDisplays.getLastRowIndex())[i];
                byte totalScore = playerScores.getRow(playerScores.getRowCount()-1)[i];
                if(rowVal+totalScore >= 24)
                    return true;
            }
        return false;
    }

    void loadGame(){
        DBHelper helper = new DBHelper(this);
        MatchData md = helper.getMatchDataByID(getIntent().getLongExtra("matchID", -1));

        date = md.date;
        matchID = md.ID;
        playerScores.setAllScores(md.scores);
        profiles = md.profiles;
        Bitmap[] imageBitmaps = new Bitmap[4];
        String[] names = new String[4];
        for(int i = 0; i < 4; i++){
            if(profiles[i] != null){
                imageBitmaps[i] = profiles[i].getImageBitmap();
                names[i] = profiles[i].getName();
            }
        }
        playerDisplays.setImages(imageBitmaps);
        playerDisplays.setNames(names);

        for(int i = 0; i < playerScores.getRowCount(); i++){
            playerDisplays.addRow(playerScores.getRow(i));
            playerDisplays.lockRow(i);
        }
        playerDisplays.updateAllTotalScores(playerScores.getAllTotalScores());
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveGame();
    }

    void saveGame(){
        if(playerScores.getRowCount() <= 0)
            return;
        DBHelper helper = new DBHelper(this);

        if(getIntent().getBooleanExtra("load", false))
            helper.updateScores(matchID, getProfileIDs(), playerScores.getAllScores());
        else
            helper.addMatch("_" + getDateTime(), getProfileIDs(), playerScores.getAllScores());
    }

    Long[] getProfileIDs(){

        Long ids[] = new Long[4];
        for(int i = 0; i < 4; i++)
            ids[i] = profiles[i] == null ? null : profiles[i].getId();
        return ids;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    boolean isProfileUsed(Long id){
        for(Profile p : profiles)
        {
            if(p!=null && p.getId() == id)
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode==REQUEST_PROFILE_1 || requestCode==REQUEST_PROFILE_2 || requestCode==REQUEST_PROFILE_3 || requestCode==REQUEST_PROFILE_4) && resultCode == RESULT_OK && data != null)
        {
            requestCode--;
            Long id = data.getLongExtra("id", -1);
            
            if(isProfileUsed(id))
                Toast.makeText(this, "Player already added!", Toast.LENGTH_SHORT).show();
            else
            {
                DBHelper helper = new DBHelper(this);
                profiles[requestCode] = helper.getProfileByID(id);
                if(profiles[requestCode].getName() != null)
                    playerDisplays.setName(requestCode, profiles[requestCode].getName());
                if(profiles[requestCode].getImageData() != null)
                    playerDisplays.setImage(requestCode, Utils.getImage(profiles[requestCode].getImageData()));
            }
        }
    }
}
