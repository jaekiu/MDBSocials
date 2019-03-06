/** Represents the components of a Social.
 * @author: Jacqueline Zhang
 * @date: 02/23/2019
 */

package com.jackie.mdbsocials;

import java.io.Serializable;

public class Social implements Serializable {
    /** Represents the ID associated with the social in Firebase Realtime Database. */
    private String _uid;

    /** Represents the event name of the social. */
    private String _eventName;

    /** Represents the description of the social. */
    private String _description;

    /** Represents the date of when the social will take place. */
    private String _date;

    /** Represents the email of the person who created the event. */
    private String _email;

    /** Represents the number of interested people for the Social. */
    private int _interested = 0;

    /** Constructs a Social and initializes all variables. */
    public Social(String uid, String eventName, String description, String date, String email, int interested) {
        _uid = uid;
        _eventName = eventName;
        _description = description;
        _date = date;
        _email = email;
        _interested = interested;
    }

    /** Increments the number of interested users. */
    public void incrementInterested() {
        _interested += 1;
    }

    /** Decrements the number of interested users.
     * Only occurs when they deselect the interested button. Cannot decrement without having incremented in the past. */
    public void decrementInterested() {
        _interested -= 1;
    }

    /** <-------- GETTER METHODS -------> */
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
