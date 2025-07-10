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
<<<<<<< HEAD
    private Long userId;

    // Getter 和 Setter
    public Long getUserId(){return userId;}

    public void setUserId(Long userId){this.userId = userId;}

    public Long getId() {
        return id;
    }

=======

    // Getter 和 Setter
    public Long getId() {
        return id;
    }
>>>>>>> bbb1d15 (Initial commit)
    public void setId(Long id) {
        this.id = id;
    }

<<<<<<< HEAD
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
=======
    // 其他字段的 getter/setter 省略
>>>>>>> bbb1d15 (Initial commit)
}