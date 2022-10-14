package com.example.seg;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.security.Key;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class KeyMetrics {

    /**
     * Gets the total number of impressions in this campaign
     * @param impressionLog the impression log file
     * @return the total number of impressions
     */
    public static int getNumberOfImpressions(ImpressionLog impressionLog) { return impressionLog.getData().size(); }

    /**
     * Gets the total number of clicks in this campaign
     * @param clickLog the click log file
     * @return the total number of clicks
     */
    public static int getNumberOfClicks(ClickLog clickLog) { return clickLog.getData().size(); }

    /**
     * Gets the total number of unique clicks in this campaign
     * It goes through each row on the file, and counts it if it is a new ID
     * @param clickLog the click log file
     * @return the total number of unique clicks
     */
    public static int getNumberOfUniques(ClickLog clickLog) {
        List<String> store = new ArrayList<>();
        int count = 0;
        for(int i = 0; i < clickLog.getData().size(); i++) {
            String currentRecord = clickLog.getCell(1, i);
            if(!store.contains(currentRecord)) {
                store.add(currentRecord);
                count += 1;
            }
        }
        return count;
    }

    /**
     * Gets the total number of bounces in this campaign
     * First checks if the page visits is greater than the bounce_page_visits
     * Then checks if the time spent is greater than the bounce_time_spent
     * if both of those are true, it is not a bounce, otherwise it is a bounce
     * @param serverLog the server log file
     * @param bounce_page_visits the specific page visits to define a bounce
     * @param bounce_time_spent the specific time spent to define a bounce in seconds
     * @return the total number of bounces
     */
    public static int getNumberOfBounces(ServerLog serverLog, int bounce_page_visits, int bounce_time_spent) {
        int count = 0;
        for(int i = 0; i < serverLog.getData().size(); i++) {
            String pagesViewed = serverLog.getCell(3, i);
            //If the pagesViewed is greater than the bounce_page_visits set, it then checks the time spent
            //Else it adds one to the number of bounces
            if(Integer.parseInt(pagesViewed) > bounce_page_visits) {
                String exitDate = serverLog.getCell(2, i);
                if(!exitDate.equals("n/a")) {
                    String entryDate = serverLog.getCell(0, i);
                    String[] start = entryDate.split(" ");
                    String[] end = exitDate.split(" ");
                    String [] startTime = start[1].split(":");
                    String [] endTime = end[1].split(":");

                    int startSeconds = (Integer.parseInt(startTime[0]) * 3600) + (Integer.parseInt(startTime[1]) * 60) + Integer.parseInt(startTime[2]);
                    int endSeconds = (Integer.parseInt(endTime[0]) * 3600) + (Integer.parseInt(endTime[1]) * 60) + Integer.parseInt(endTime[2]);
                    if(startSeconds > endSeconds) {
                        endSeconds += 86400;
                    }
                    int totalTimeSpent = endSeconds - startSeconds;
                    if(totalTimeSpent <= bounce_time_spent) {
                        count += 1;
                    }
                }
            } else {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Gets the total number of conversions in this campaign
     * @param serverLog the server log file
     * @return the total number of conversions
     */
    public static int getNumberOfConversions(ServerLog serverLog) {
        int count = 0;
        for(int i = 0; i < serverLog.getData().size(); i++) {
            String conversion = serverLog.getCell(4, i);
            if(conversion.equals("Yes")){
                count += 1;
            }
        }
        return count;
    }

    /**
     * Gets the total cost of click costs and impression costs
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @return the total cost
     */
    public static float getTotalCost(ImpressionLog impressionLog, ClickLog clickLog) {
        float total = 0;
        for(int i = 0; i < impressionLog.getData().size(); i++) {
            //System.out.print(".");
            String impressionAmount = impressionLog.getCell(6, i);
            total += Float.parseFloat(impressionAmount);
        }
        for(int i = 0; i < clickLog.getData().size(); i++) {
            String clickAmount = clickLog.getCell(2, i);
            total += Float.parseFloat(clickAmount);
        }
        return (float) ((Math.round(total * 100) * 1.0) / 100);
    }

    /**
     * Calculates the click-through-rate in this campaign
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @return the overall click-through-rate
     */
    public static float getCTR(ImpressionLog impressionLog, ClickLog clickLog) {
        return (float) Math.round(((getNumberOfClicks(clickLog)*1.0) / getNumberOfImpressions(impressionLog))*1000)/1000;
    }

    /**
     * Gets the cost-per-acquisition in this campaign
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @param serverLog the server log file
     * @return the cost-per-acquisition
     */
    public static float getCPA(ImpressionLog impressionLog, ClickLog clickLog, ServerLog serverLog) {
        return (float) Math.round(getTotalCost(impressionLog, clickLog) / getNumberOfConversions(serverLog)*1000)/1000;
    }

    /**
     * Gets the cost-per-click in this campaign
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @return the cost-per-click
     */
    public static float getCPC(ImpressionLog impressionLog, ClickLog clickLog) {
        return (float) Math.round(getTotalCost(impressionLog, clickLog) / getNumberOfClicks(clickLog)*1000)/1000;
    }

    /**
     * Gets the cost-per-thousand impressions in this campaign
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @return the cost-per-thousand impressions
     */
    public static float getCPM(ImpressionLog impressionLog, ClickLog clickLog) {
        return (float) Math.round((1000 * (getTotalCost(impressionLog, clickLog)) / getNumberOfImpressions(impressionLog))*1000)/1000;
    }

    /**
     * Calculates the bounce rate in this campaign
     * @param clickLog the click log file
     * @param serverLog the server log file
     * @param bounce_page_visits the specific page visits to define a bounce
     * @param bounce_time_spent the specific time spent to define a bounce
     * @return the overall bounce rate
     */
    public static float getBounceRate(ClickLog clickLog, ServerLog serverLog, int bounce_page_visits, int bounce_time_spent) {
        return (float) Math.round((float) ((getNumberOfBounces(serverLog, bounce_page_visits, bounce_time_spent) * 1.0) / getNumberOfClicks(clickLog))*1000)/1000;
    }

    /**
     * This will check if one set of data is accepted by the filtered collection
     * It checks first with gender
     * Then with Age
     * Then with Income
     * Then with Context
     * If it doesn't fail with any of those it passes
     * @param details is the row of data that is being checked
     * @param filters is the list of filters applied to it
     * @return true if the list of filters are accepted with this row of data
     */
    public static boolean checkRowAgainstFilters(String[] details, boolean[] filters) {
        /*
        -Gender
        ---Male
        ---Female
         */
        if(!(filters[0] && filters[1])) {
            switch (details[2]) {
                case "Male":
                    if (!filters[0]) {
                        return false;
                    }
                    break;
                case "Female":
                    if (!filters[1]) {
                        return false;
                    }
                    break;
            }
        }
        /*
        -Age
        ---<25
        ---25-34
        ---35-44
        ---45-54
        --->54
         */
        if(!(filters[2] && filters[3] && filters[4] && filters[5] && filters[6])) {
            switch (details[3]) {
                case "<25":
                    if (!filters[2]) {
                        return false;
                    }
                    break;
                case "25-34":
                    if (!filters[3]) {
                        return false;
                    }
                    break;
                case "35-44":
                    if (!filters[4]) {
                        return false;
                    }
                    break;
                case "45-54":
                    if (!filters[5]) {
                        return false;
                    }
                    break;
                case ">54":
                    if (!filters[6]) {
                        return false;
                    }
                    break;
            }
        }
        /*
        -Income
        ---Low
        ---Medium
        ---High
         */
        if(!(filters[7] && filters[8] && filters[9])) {
            switch (details[4]) {
                case "Low":
                    if (!filters[7]) {
                        return false;
                    }
                    break;
                case "Medium":
                    if (!filters[8]) {
                        return false;
                    }
                    break;
                case "High":
                    if (!filters[9]) {
                        return false;
                    }
                    break;
            }
        }
        /*
        -Context
        ---News
        ---Shopping
        ---Social Media
        ---Blog
        ---Hobbies
        ---Travel
         */
        if(!(filters[10] && filters[11] && filters[12] && filters[13] && filters[14] && filters[15])) {
            switch (details[5]) {
                case "News":
                    if (!filters[10]) {
                        return false;
                    }
                    break;
                case "Shopping":
                    if (!filters[11]) {
                        return false;
                    }
                    break;
                case "Social Media":
                    if (!filters[12]) {
                        return false;
                    }
                    break;
                case "Blog":
                    if (!filters[13]) {
                        return false;
                    }
                    break;
                case "Hobbies":
                    if (!filters[14]) {
                        return false;
                    }
                    break;
                case "Travel":
                    if (!filters[15]) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * This will transfer a string into Date format
     * @param date the String to be converted in form YYYY-MM-DD
     * @return the Date object of the date given
     */
    public static Date makeDateFromString(String date){
        String[] parts = date.split("-");
        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1])-1;
            int day = Integer.parseInt(parts[2]);
            Date today_date = new Date();
            if (year < 1970){
                throw new Exception();
            }
            if (month > 11 || month < 0) {
                throw new Exception();
            }
            if (day > 31 || day < 1) {
                throw new Exception();
            }
            if (month == 3 || month == 5 || month == 8 || month == 10) {
                if(day > 30){
                    throw new Exception();
                }
            }
            if(month == 1 && year % 4 == 0){
                if(day > 29) {
                    throw new Exception();
                }
            } else if (month == 1) {
                if(day > 28) {
                    throw new Exception();
                }
            }
            Date test = new Date(year-1900, month, day);
            if(test.after(today_date)){
                throw new Exception();
            }
        } catch (Exception e) {
            return null;
        }
        int year = Integer.parseInt(parts[0])-1900;
        int month = Integer.parseInt(parts[1])-1;
        int day = Integer.parseInt(parts[2]);
        return new Date(year, month, day);

    }

    /**
     * Gets the total number of impressions in this campaign with the filters and date range
     * @param impressionLog the impression log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the total calculated number of impressions
     */
    public static int getNumberOfImpressionsFiltered(ImpressionLog impressionLog, boolean[] filters, String start_date, String end_date) {
        int total = 0;
        Date startDate = makeDateFromString(start_date);
        Date endDate = makeDateFromString(end_date);
        for(int i = 0; i < impressionLog.getData().size(); i++) {
            String[] details = impressionLog.getRow(i);
            String date_time = details[0];
            String[] parts = date_time.split(" ");
            Date testDate = makeDateFromString(parts[0]);
            if(startDate.equals(testDate) || endDate.equals(testDate) || (endDate.after(testDate) && startDate.before(testDate))) {
                if(checkRowAgainstFilters(details, filters)) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * Gets the total number of clicks in this campaign with the filters and date range
     * @param clickLog the click log file
     * @param impressionLog the impression log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the total calculated number of clicks
     */
    public static int getNumberOfClicksFiltered(ClickLog clickLog, ImpressionLog impressionLog, boolean[] filters, String start_date, String end_date) {
        int total = 0;
        Date startDate = makeDateFromString(start_date);
        Date endDate = makeDateFromString(end_date);
        int startIndex = 0;
        for(int a = 0; a < clickLog.getLength(); a++) {
            String[] info = clickLog.getRow(a);
            String date_time = info[0];
            String[] parts = date_time.split(" ");
            Date testDate = makeDateFromString(parts[0]);
            if(startDate.equals(testDate) || endDate.equals(testDate) || (endDate.after(testDate) && startDate.before(testDate))) {
                String id = clickLog.getCell(1, a);
                for (int b = startIndex; b < impressionLog.getLength(); b++) {
                    if (id.equals(impressionLog.getCell(1, b))) {
                        startIndex = b;
                        String[] details = impressionLog.getRow(b);
                        if (checkRowAgainstFilters(details, filters)) {
                            total++;
                        }
                        break;
                    }
                }
            }
        }

        return total;
    }

    /**
     * Gets the total number of unique clicks in this campaign with the filters and date range
     * @param clickLog the click log file
     * @param impressionLog the impression log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the total calculated number of uniques
     */
    public static int getNumberOfUniquesFiltered(ClickLog clickLog, ImpressionLog impressionLog, boolean[] filters, String start_date, String end_date) {
        List<String> store = new ArrayList<>();
        int count = 0;
        Date startDate = makeDateFromString(start_date);
        Date endDate = makeDateFromString(end_date);
        int startIndex = 0;
        for (int a = 0; a < clickLog.getData().size(); a++) {
            String[] info = clickLog.getRow(a);
            String date_time = info[0];
            String[] parts = date_time.split(" ");
            Date testDate = makeDateFromString(parts[0]);
            if (startDate.equals(testDate) || endDate.equals(testDate) || (endDate.after(testDate) && startDate.before(testDate))) {
                String id = clickLog.getCell(1, a);
                for (int b = startIndex; b < impressionLog.getLength(); b++) {
                    if (id.equals(impressionLog.getCell(1, b))) {
                        startIndex = b;
                        String[] details = impressionLog.getRow(b);
                        if (checkRowAgainstFilters(details, filters)) {
                            if (!store.contains(id)) {
                                store.add(id);
                                count += 1;
                            }
                        }
                        break;
                    }
                }

            }
        }
        return count;
    }

    /**
     * Gets the total number of bounces in this campaign with the filters and date range
     * First it checks with all of the filters and date range
     * Next checks if the page visits is greater than the bounce_page_visits
     * Then checks if the time spent is greater than the bounce_time_spent
     * if both of those are true, it is not a bounce, otherwise it is a bounce
     * @param serverLog the server log file
     * @param bounce_page_visits the specific page visits to define a bounce
     * @param bounce_time_spent the specific time spent to define a bounce in seconds
     * @param impressionLog the impression log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the total calculated number of bounces
     */
    public static int getNumberOfBouncesFiltered(ServerLog serverLog, int bounce_page_visits, int bounce_time_spent, ImpressionLog impressionLog, boolean[] filters, String start_date, String end_date) {
        int count = 0;
        Date startDate = makeDateFromString(start_date);
        Date endDate = makeDateFromString(end_date);
        int startIndex = 0;
        for(int a = 0; a < serverLog.getData().size(); a++) {
            String[] info = serverLog.getRow(a);
            String date_time = info[0];
            String[] parts = date_time.split(" ");
            Date testDate = makeDateFromString(parts[0]);
            if (startDate.equals(testDate) || endDate.equals(testDate) || (endDate.after(testDate) && startDate.before(testDate))) {
                String id = serverLog.getCell(1, a);
                for (int b = startIndex; b < impressionLog.getLength(); b++) {
                    if (id.equals(impressionLog.getCell(1, b))) {
                        startIndex = b;
                        String[] details = impressionLog.getRow(b);
                        if (checkRowAgainstFilters(details, filters)) {
                            String pagesViewed = serverLog.getCell(3, a);
                            if (Integer.parseInt(pagesViewed) > bounce_page_visits) {
                                String exitDate = serverLog.getCell(2, a);
                                if (!exitDate.equals("n/a")) {
                                    String entryDate = serverLog.getCell(0, a);
                                    String[] start = entryDate.split(" ");
                                    String[] end = exitDate.split(" ");
                                    String[] startTime = start[1].split(":");
                                    String[] endTime = end[1].split(":");

                                    int startSeconds = (Integer.parseInt(startTime[0]) * 3600) + (Integer.parseInt(startTime[1]) * 60) + Integer.parseInt(startTime[2]);
                                    int endSeconds = (Integer.parseInt(endTime[0]) * 3600) + (Integer.parseInt(endTime[1]) * 60) + Integer.parseInt(endTime[2]);
                                    if (startSeconds > endSeconds) {
                                        endSeconds += 86400;
                                    }
                                    int totalTimeSpent = endSeconds - startSeconds;
                                    if (totalTimeSpent <= bounce_time_spent) {
                                        count += 1;
                                    }
                                }
                            } else {
                                count += 1;
                            }
                        }
                        break;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Gets the total number of conversions in this campaign with the filters and date range
     * @param serverLog the server log file
     * @param impressionLog the impression log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the total calculated number of conversions
     */
    public static int getNumberOfConversionsFiltered(ServerLog serverLog, ImpressionLog impressionLog, boolean[] filters, String start_date, String end_date){
        int count = 0;
        Date startDate = makeDateFromString(start_date);
        Date endDate = makeDateFromString(end_date);
        int startIndex = 0;
        for(int a = 0; a < serverLog.getData().size(); a++) {
            String[] info = serverLog.getRow(a);
            String date_time = info[0];
            String[] parts = date_time.split(" ");
            Date testDate = makeDateFromString(parts[0]);
            if (startDate.equals(testDate) || endDate.equals(testDate) || (endDate.after(testDate) && startDate.before(testDate))) {
                String id = serverLog.getCell(1, a);
                for (int b = startIndex; b < impressionLog.getLength(); b++) {
                    if (id.equals(impressionLog.getCell(1, b))) {
                        startIndex = b;
                        String[] details = impressionLog.getRow(b);
                        if (checkRowAgainstFilters(details, filters)) {
                            String conversion = serverLog.getCell(4, a);
                            if (conversion.equals("Yes")) {
                                count += 1;
                            }
                        }
                        break;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Gets the total cost of click costs and impression costs with the filters and date range
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the total calculated costs
     */
    public static float getTotalCostFiltered(ImpressionLog impressionLog, ClickLog clickLog, boolean[] filters, String start_date, String end_date) {
        float total = 0;
        Date startDate = makeDateFromString(start_date);
        Date endDate = makeDateFromString(end_date);
        int startIndex = 0;
        for(int i = 0; i < impressionLog.getData().size(); i++) {
            String[] details = impressionLog.getRow(i);
            String date_time = details[0];
            String[] parts = date_time.split(" ");
            Date testDate = makeDateFromString(parts[0]);
            if (startDate.equals(testDate) || endDate.equals(testDate) || (endDate.after(testDate) && startDate.before(testDate))) {
                if (checkRowAgainstFilters(details, filters)) {
                    String impressionAmount = impressionLog.getCell(6, i);
                    total += Float.parseFloat(impressionAmount);
                }
            }
        }
        for(int a = 0; a < clickLog.getData().size(); a++) {
            String[] info = clickLog.getRow(a);
            String date_time = info[0];
            String[] parts = date_time.split(" ");
            Date testDate = makeDateFromString(parts[0]);
            if(startDate.equals(testDate) || endDate.equals(testDate) || (endDate.after(testDate) && startDate.before(testDate))) {
                String id = clickLog.getCell(1, a);
                for (int b = startIndex; b < impressionLog.getLength(); b++) {
                    if (id.equals(impressionLog.getCell(1, b))) {
                        startIndex = b;
                        String[] details = impressionLog.getRow(b);
                        if (checkRowAgainstFilters(details, filters)) {
                            String clickAmount = clickLog.getCell(2, a);
                            total += Float.parseFloat(clickAmount);
                        }
                        break;
                    }
                }
            }

        }
        return (float) ((Math.round(total * 100) * 1.0) / 100);
    }

    /**
     * Calculates the click-through-rate in this campaign with the filters and date range
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the overall calculated click-through-rate
     */
    public static float getCTRFiltered(ImpressionLog impressionLog, ClickLog clickLog, boolean[] filters, String start_date, String end_date) {
        return (float) Math.round(((getNumberOfClicksFiltered(clickLog, impressionLog, filters, start_date, end_date)*1.0) / getNumberOfImpressionsFiltered(impressionLog, filters, start_date, end_date))*1000)/1000;
    }

    /**
     * Gets the cost-per-acquisition in this campaign with the filters and date range
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @param serverLog the server log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the calculated cost-per-acquisition
     */
    public static float getCPAFiltered(ImpressionLog impressionLog, ClickLog clickLog, ServerLog serverLog, boolean[] filters, String start_date, String end_date) {
        return (float) Math.round(getTotalCostFiltered(impressionLog, clickLog, filters, start_date, end_date) / getNumberOfConversionsFiltered(serverLog, impressionLog, filters, start_date, end_date)*1000)/1000;
    }

    /**
     * Gets the cost-per-click in this campaign with the filters and date range
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the calculated cost-per-click
     */
    public static float getCPCFiltered(ImpressionLog impressionLog, ClickLog clickLog, boolean[] filters, String start_date, String end_date) {
        return (float) Math.round(getTotalCostFiltered(impressionLog, clickLog, filters, start_date, end_date) / getNumberOfClicksFiltered(clickLog, impressionLog, filters, start_date, end_date)*1000)/1000;
    }

    /**
     * Gets the cost-per-thousand impressions in this campaign with the filters and date range
     * @param impressionLog the impression log file
     * @param clickLog the click log file
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the calculated cost-per-thousand impressions
     */
    public static float getCPMFiltered(ImpressionLog impressionLog, ClickLog clickLog, boolean[] filters, String start_date, String end_date) {
        return (float) Math.round((1000 * (getTotalCostFiltered(impressionLog, clickLog, filters, start_date, end_date)) / getNumberOfImpressionsFiltered(impressionLog, filters, start_date, end_date))*1000)/1000;
    }

    /**
     * Gets the bounce rate in this campaign with the filters and date range
     * @param clickLog the click log file
     * @param serverLog the server log file
     * @param impressionLog the impression log file
     * @param bounce_page_visits the specific page visits to define a bounce
     * @param bounce_time_spent the specific time spent to define a bounce
     * @param filters the list of booleans for the check-boxes
     * @param start_date the first date of the date range
     * @param end_date the last date of the date range
     * @return the calculated bounce rate
     */
    public static float getBounceRateFiltered(ClickLog clickLog, ServerLog serverLog, ImpressionLog impressionLog, int bounce_time_spent, int bounce_page_visits, boolean[] filters, String start_date, String end_date) {
        return (float) Math.round((float) ((getNumberOfBouncesFiltered(serverLog, bounce_page_visits, bounce_time_spent, impressionLog, filters, start_date, end_date) * 1.0) / getNumberOfClicksFiltered(clickLog, impressionLog, filters, start_date, end_date))*1000)/1000;
    }

    public static void main (String[] args) throws IOException, CsvException {
        ImpressionLog impressionLog = new ImpressionLog("C:\\Users\\jmboy\\IdeaProjects\\SEGProject\\SEG\\src\\main\\tests\\impression_log_test.csv");
        ClickLog clickLog = new ClickLog("C:\\Users\\jmboy\\IdeaProjects\\SEGProject\\SEG\\src\\main\\tests\\click_log_test.csv");
        ServerLog serverLog = new ServerLog("C:\\Users\\jmboy\\IdeaProjects\\SEGProject\\SEG\\src\\main\\tests\\server_log_test.csv");
        boolean[] filters = new boolean[16];
        filters[0] = false;
        filters[1] = true;
        for(int i = 2; i < filters.length; i++) {
            filters[i] = true;
        }
        String start_date = "2015-01-04";
        String end_date = "2015-01-11";
        System.out.println(KeyMetrics.getNumberOfImpressionsFiltered(impressionLog, filters, start_date, end_date));
    }


}
