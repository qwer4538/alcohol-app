package com.example.myapplication.model;

import java.io.Serializable;

public class Travel_list_item implements Serializable {
    private String document_id;
    private String img_url;
    private String title;
    private String content;
    private String writer;
    private String local;
    private String id;

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDocument_id() {
        return document_id;
    }

    public String getWriter() {
        return writer;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getTitle() {
        return title;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getContent() {
        return content;
    }

    public String toString(){
        String str = "";
        str += "document_id : " + document_id;
        str += "title : " + title;
        str += "content : " + content;
        str += "writer : " + writer;
        str += "img_url : " + img_url;
        str += "local : " + local;
        return str;
    }
}
