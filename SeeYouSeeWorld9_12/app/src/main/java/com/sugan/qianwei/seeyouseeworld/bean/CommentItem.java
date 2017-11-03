package com.sugan.qianwei.seeyouseeworld.bean;

import java.io.Serializable;

/**
 * Created by QianWei on 2017/9/8.
 */

public class CommentItem implements Serializable {

    private int comment_id;
    private int dynamic_id;
    private int publisher_id;
    private String publisher_name;
    private int responder_id;
    private String responder_name;
    private String content;
    private String publisher_avatar;
    private String comment_time;

    public CommentItem(int comment_id, int dynamic_id, int publisher_id,
                       String publisher_name, int responder_id,
                       String responder_name, String content,
                       String publisher_avatar, String comment_time) {
        this.comment_id = comment_id;
        this.dynamic_id = dynamic_id;
        this.publisher_id = publisher_id;
        this.publisher_name = publisher_name;
        this.responder_id = responder_id;
        this.responder_name = responder_name;
        this.content = content;
        this.publisher_avatar = publisher_avatar;
        this.comment_time = comment_time;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getDynamic_id() {
        return dynamic_id;
    }

    public void setDynamic_id(int dynamic_id) {
        this.dynamic_id = dynamic_id;
    }

    public int getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(int publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getPublisher_name() {
        return publisher_name;
    }

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }

    public int getResponder_id() {
        return responder_id;
    }

    public void setResponder_id(int responder_id) {
        this.responder_id = responder_id;
    }

    public String getResponder_name() {
        return responder_name;
    }

    public void setResponder_name(String responder_name) {
        this.responder_name = responder_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublisher_avatar() {
        return publisher_avatar;
    }

    public void setPublisher_avatar(String publisher_avatar) {
        this.publisher_avatar = publisher_avatar;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }
}
