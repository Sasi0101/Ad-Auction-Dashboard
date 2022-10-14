package com.example.seg;

/**
 * Creates a Listener interface for when an additional folder is loaded
 */
public interface AdditionalCampaignListener {

    /**
     * method for handling when an additional campaign is loaded
     * @param clickLog the ClickLog object for the additional campaign
     * @param impressionLog the ImpressionLog object
     * @param serverLog the ServerLog object
     * @param folderName the name of the folder
     */
    void additionalCampaign(ClickLog clickLog, ImpressionLog impressionLog, ServerLog serverLog, String folderName);
}
