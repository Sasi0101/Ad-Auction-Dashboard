package com.example.seg;

/**
 * Listener interface for when the bounce definition is changed
 */
public interface ChangeBounceDefListener {

    /**
     * method to pass the new page visit number and bounce time number through the listener
     * @param pageVisit new number
     * @param bounceTime new time period
     */
    void changeBounceDef(int pageVisit, int bounceTime);
}
