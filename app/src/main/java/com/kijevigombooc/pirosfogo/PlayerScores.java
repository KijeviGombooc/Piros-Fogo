package com.kijevigombooc.pirosfogo;

import java.util.ArrayList;

public class PlayerScores {


    private ArrayList<Byte>[] scores;
    private byte[] totalScores = new byte[4];

    public PlayerScores() {
        scores = new ArrayList[4];
        for(int i = 0; i < 4; i++){
            scores[i] = new ArrayList<Byte>();
            totalScores[i] = 0;
        }
    }

    void addRow(byte[] scs){
        for(int i = 0; i < 4; i++) {
            scores[i].add(scs[i]);
            totalScores[i] += scs[i];
        }
    }

    void setRow(int rowIndex, byte[] scs){
        for(int i = 0; i < 4; i++) {
            totalScores[i] -= scores[i].get(rowIndex);
            scores[i].set(rowIndex, scs[i]);
            totalScores[i] += scs[i];
        }
    }

    void removeRow(int rowIndex){
        for(int i = 0; i < 4; i++) {
            totalScores[i] -= scores[i].get(rowIndex);
            scores[i].remove(rowIndex);
        }
    }

    byte[] getRow(int rowIndex){
        byte[] row = new byte[4];
        for(int i = 0; i < 4; i++){
            row[i] = scores[i].get(rowIndex);
        }
        return row;
    }

    void setColumn(int playerIndex, byte[] column){
        scores[playerIndex].clear();
        totalScores[playerIndex] = 0;
        for(int i = 0; i < column.length && column[i] != -1; i++){
            scores[playerIndex].add(column[i]);
            totalScores[playerIndex] += column[i];
        }
    }

    byte[] getColumn(int playerIndex){
        byte[] sc = new byte[scores[playerIndex].size()];
        for(int i = 0; i < sc.length; i++){
            sc[i] = scores[playerIndex].get(i).byteValue();
        }
        return sc;
    }

    byte getTotalScore(int playerIndex){
        return totalScores[playerIndex];
    }

    int getRowCount(){
        return scores[0].size();
    }

    byte[] getAllTotalScores(){
        byte[] allTotalScores = new byte[4];
        for(int i = 0; i < 4; i++){
            allTotalScores[i] = totalScores[i];
        }
        return totalScores;
    }

    void setAllScores(byte[][] allScores){
        for(int i = 0; i < 4; i++) {
            setColumn(i, allScores[i]);
        }
    }

    byte[][] getAllScores(){
        byte[][] sc = new byte[4][];
        for(int i = 0; i < 4; i++){
            sc[i] = getColumn(i);
        }
        return sc;
    }
}
