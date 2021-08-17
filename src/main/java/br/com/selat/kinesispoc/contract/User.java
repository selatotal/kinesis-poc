package br.com.selat.kinesispoc.contract;

import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private char gender;

    public User() {
    }

    public User(UUID id, String name, char gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                '}';
    }
}
