package com.kijevigombooc.pirosfogo;

public class Match {
    private Long id;
    private String date;

    public Match(Long id, String name){
        this.id = id;
        this.date = name;
    }


    public String getDate() {
        return date;
    }

    public Long getId(){
        return id;
    }
}