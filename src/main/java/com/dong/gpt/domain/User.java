package com.dong.gpt.domain;

import javax.persistence.*;

@Entity(name = "users")
@Table(name = "users")
public class User {

    @Id @GeneratedValue
    @Column(name="user_id", updatable = false)
    private final Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "passwd")
    private String passwd;

    public User(Long id, String name, String passwd) {
        this.id = id;
        this.name = name;
        this.passwd = passwd;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
