package com.example.sign;

import java.util.Date;

//멤버 정보
public class Message {

    private String send;
    private String recv;
    private String msg;
    private Date time;

    public Message() {

    }

    public Message(String send, String recv, String msg, Date time) {
        this.send = send;
        this.recv = recv;
        this.msg = msg;
        this.time =time;
    }

    public String getSend() {
        return send;
    }
    public void setSend(String send) {
        this.send = send;
    }

    public String getRecv() {
        return recv;
    }

    public void setRecv(String recv) {
        this.recv = recv;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
