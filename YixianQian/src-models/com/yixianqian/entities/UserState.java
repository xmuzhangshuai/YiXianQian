package com.yixianqian.entities;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table USER_STATE.
 */
public class UserState {

    private Long id;
    private String userStateName;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public UserState() {
    }

    public UserState(Long id) {
        this.id = id;
    }

    public UserState(Long id, String userStateName) {
        this.id = id;
        this.userStateName = userStateName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserStateName() {
        return userStateName;
    }

    public void setUserStateName(String userStateName) {
        this.userStateName = userStateName;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
