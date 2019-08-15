package com.amit.groupsprojectmvc;

import com.google.firebase.database.DataSnapshot;

import java.net.URL;

public class User {
    private String _name;
    private URL _image;
    private String _id;
    private String token;
    private String _phone;



    public User() {
    }

    /**
     * constructor for database snapshot
     *
     * @param dataSnapshot
     */
    public User(DataSnapshot dataSnapshot) {
        setName((String) dataSnapshot.child("name").getValue());
        setId((String) dataSnapshot.child("id").getValue());
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        if (name != null)
            this._name = name;
    }

    public URL getImage() {
        return _image;
    }

    public void setImage(URL image) {
        this._image = image;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        if (id != null)
            this._id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return _phone;
    }

    public void setPhone(String _phone) {
        this._phone = _phone;
    }


}
