package com.example.appquanlighichi;

import java.io.Serializable;

public class Note implements Serializable {
    private int id;
    private String title;
    private String content;
    private String createdAt;  // Thời gian tạo
    private String updatedAt;  // Thời gian cập nhật

    // Constructor dùng khi lấy dữ liệu từ DB (đã có id và thời gian)
    public Note(int id, String title, String content, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor dùng khi tạo mới ghi chú (chưa có id, chưa có thời gian)
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
