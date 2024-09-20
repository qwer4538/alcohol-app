package com.example.myapplication.model;

public class Travel_comment_list_item {
    private String document_id;
    private String id;
    private String name;
    private String time;
    private String comment;

    public Travel_comment_list_item(String document_id,String id, String name, String time, String comment){
        this.document_id    = document_id;
        this.id             = id;
        this.name           = name;
        this.time           = time;
        this.comment        = comment;

    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument_id() {
        return document_id;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        String str ="";

        str += "id : "+ id;
        str += " name : " + name;
        str += " time : " + time;
        str += " comment : " + comment;
        str += " document_id : " + document_id;

        return str;
    }
}
