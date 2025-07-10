package com.mybank.backend.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gender;
    private String idNumber;
    private Date birthday;
    private String address;
    private String phone;
    private String email;
    private String photoUrl;

    // Getter 和 Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // 其他字段的 getter/setter 省略
}