/** Represents the components of a Social.
 * @author: Jacqueline Zhang
 * @date: 02/23/2019
 */

package com.jackie.mdbsocials;

import java.io.Serializable;

public class Social implements Serializable {
    private String _uid;
    private String _eventName;
    private String _description;
    private String _date;
    private String _email;
    private int _interested = 0;

    public Social(String uid, String eventName, String description, String date, String email, int interested) {
        _uid = uid;
        _eventName = eventName;
        _description = description;
        _date = date;
        _email = email;
        _interested = interested;
    }

    public void incrementInterested() {
        _interested += 1;
    }

    // Only occurs when they deselect the interested button. Cannot decrement without having incremented in the past.
    public void decrementInterested() {
        _interested -= 1;
    }

    /** <-------- GETTER METHODS ------->*/
    public String getEventName() {
        return _eventName;
    }

    public String getUID() {
        return _uid;
    }

    public String getDescription() {
        return _description;
    }

    public String getDate() {
        return _date;
    }

    public int getInterested() {
        return _interested;
    }

    public String getEmail() {
        return _email;
    }
}
