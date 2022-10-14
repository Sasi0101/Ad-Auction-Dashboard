package com.example.seg;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.chart.XYChart;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LineChartController {


    /**To Do:-
     *MAKE IT WORK FOR OTHER TIME GRAN OPTIONS
     */
    int currentMetric;
    LineChartViewer lcviewer;
    SimpleIntegerProperty groupingInterval;
    ClickLog clickLog;
    ImpressionLog impressionLog;
    ServerLog serverLog;
    static boolean isFirst = true;
    static boolean isSecond = false;
    int untilWhen = 0;
    static Date startDate2;
    static Date endDate2;
    static int minVisit=1;
    static int minTimeSpentt=60;
    static int minTimeSpentt1 = 60;
    static int minVisit1 = 1;


    public LineChartController(int interval, ClickLog clickLog, ImpressionLog impressionLog, ServerLog serverLog) {
        this.groupingInterval = new SimpleIntegerProperty();
        setGroupingInterval(interval);
        this.clickLog = clickLog;
        this.impressionLog = impressionLog;
        this.serverLog = serverLog;
    }


    public void loadGraph (int metricChoice) {
        var newSeries = getValidSeries(metricChoice);
        var yaxisname = getYAxisName(metricChoice);
        lcviewer = new LineChartViewer(newSeries, yaxisname);
        lcviewer.setSeriesName("Graph of " + yaxisname + " over time");
        System.out.println("viewer is set");
        currentMetric = metricChoice;
    }

    /**
     * Making all the files connected by their ids
     * @param impressionLog the impressiongLog from MenuScreen
     * @param clickLog the clickLog from MenuScreen
     * @param serverLog the serverLog from MenuScreen
     */
    static void clickLogWithImpressionLog (ImpressionLog impressionLog, ClickLog clickLog, ServerLog serverLog){
        HashMap<String, String> impression = new HashMap<>();
        HashMap<String, String> clicklog = new HashMap<>();
        HashMap<String, String> serverlog = new HashMap<>();
        String[] finalImpression = new String[impressionLog.length];
        String[] finalClicklog = new String[clickLog.length];
        String[] finalServerlog = new String[serverLog.length];

        for(int i=0; i < impressionLog.length; i++){
            String list = impressionLog.getCell(0,i) + "," + impressionLog.getCell(2,i) + "," + impressionLog.getCell(3,i) +
                    "," + impressionLog.getCell(4,i) + "," + impressionLog.getCell(5,i) + "," + impressionLog.getCell(6,i);
            impression.put(impressionLog.getCell(1,i), list);
        }

        for(int i=0; i< clickLog.length; i++){
            String list = clickLog.getCell(0,i) + "," + clickLog.getCell(2,i);
            clicklog.put(clickLog.getCell(1,i), list);
        }

        for(int i=0; i<serverLog.length; i++){
            String list = serverLog.getCell(0,i) + "," + serverLog.getCell(2,i) + "," + serverLog.getCell(3,i) + "," + serverLog.getCell(4,i);
            serverlog.put(serverLog.getCell(1,i), list);
        }


        //put everything to the finalclicklog
        for(int i=0; i < clickLog.length; i++){
            finalClicklog[i] = clickLog.getCell(0,i) + "," + clickLog.getCell(1,i) + "," + clickLog.getCell(2,i) + "," + impression.get(clickLog.getCell(1,i)) + "," + serverlog.get(clickLog.getCell(1,i));
        }

        //put everything to the finalserverlog
        for(int i=0; i<serverLog.length; i++){
            finalServerlog[i] = serverLog.getCell(0,i) + "," + serverLog.getCell(1,i) + "," + serverLog.getCell(2,i) + "," + serverLog.getCell(3,i)
                    + "," + serverLog.getCell(4,i) + "," + impression.get(serverLog.getCell(1,i)) + "," + clicklog.get(serverLog.getCell(1,i));
        }

        //put everything to the finalimpression
        for(int i=0; i<impressionLog.length; i++){
            finalImpression[i] = impressionLog.getCell(0,i) + "," + impressionLog.getCell(1,i) + "," + impressionLog.getCell(2,i) + "," +
                    impressionLog.getCell(3,i) + "," + impressionLog.getCell(4,i) + "," + impressionLog.getCell(5,i) + "," + impressionLog.getCell(6,i);
            if(clicklog.get(impressionLog.getCell(1,i)) != null) finalImpression[i] = finalImpression[i] + "," + clicklog.get(impressionLog.getCell(1,i));
            else finalImpression[i] = finalImpression[i] + ",n/a,0";
            if(serverlog.get(impressionLog.getCell(1,i)) != null) finalImpression[i] = finalImpression[i] + "," + serverlog.get(impressionLog.getCell(1,i));
            else finalImpression[i] = finalImpression[i] + ",n/a,n/a,n/a,n/a";
        }


        MenuScreen.finalImpression = finalImpression;
        MenuScreen.finalClicklog = finalClicklog;
        MenuScreen.finalServerlog = finalServerlog;

    }

    private String getYAxisName(int metricchoice){
        return switch (metricchoice) {
            case 1 -> "Number of Impressions";
            case 2 -> "Number of Clicks";
            case 3 -> "Number of Unique Clicks";
            case 4 -> "Number of Bounces";
            case 5 -> "Number of Conversions";
            case 6 -> "Cost of Impressions + Clicks";
            default -> null;
        };
    }

    public LineChartViewer getLcviewer() {
        return lcviewer;
    }

    public void setGroupingInterval (int choice){
        switch (choice) {
            case 1 -> //by hour
                    this.groupingInterval.set(1);
            case 2 -> //by day
                    this.groupingInterval.set(2);
            case 3 -> //by week
                    this.groupingInterval.set(3);
        }
    }

    /*private String getHour (int i, DataFile thisLog){
        return thisLog.getCell(0, i).split(" ")[1].split(":")[0];
    }

    private String getDay (int i, DataFile thisLog){
        return thisLog.getCell(0, i).split(" ")[0].split("-")[2];
    }
*/
    private XYChart.Series getValidSeries(int metricChoice) {
        switch (metricChoice) {
            case 1: // number of impressions
                if (groupingInterval.get() == 1){
                    return impressionsPerHour();
                } else if (groupingInterval.get() == 2) {
                    return impressionsPerDay();
                }
                break;
            case 2:
                if (groupingInterval.get() == 1){
                    return clicksPerHour();
                } else if (groupingInterval.get() == 2) {
                    return clicksPerDay();
                }
                break;
            case 3:
                if (groupingInterval.get() == 1){
                    return uniquesPerHour();
                } else if (groupingInterval.get() == 2) {
                    return uniquesPerDay();
                }
                break;
            case 4:
                if (groupingInterval.get() == 1){
                    return bouncesPerHour();
                } else if (groupingInterval.get() == 2) {
                    return bouncesPerDay();
                }
                break; //need to change to support changing bounce rate
            case 5:
                if (groupingInterval.get() == 1){
                    return conversionsPerHour();
                } else if (groupingInterval.get() == 2) {
                    return conversionsPerDay();
                }
                break;
            case 6:
                if (groupingInterval.get() == 1){
                    return costPerHour();
                } else if (groupingInterval.get() == 2) {
                    return costPerDay();
                }
                break;
        }
        return null; //remove
    }

    private XYChart.Series<String, Integer> impressionsPerHour() {
        return countByHour();
    }

    private XYChart.Series<String, Integer> clicksPerHour() {
        return countByHourClick();
    }

    private XYChart.Series<String, Integer> impressionsPerDay() {
        return countByDay();
    }

    private  XYChart.Series<String, Integer> clicksPerDay() {
        return countByDayClicks();
    }

    //should work for number of clicks
    public XYChart.Series<String, Integer> countByHourClick(){
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        int currentSum1 = 0;
        String startDateString = null;
        String endDateString = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[MenuScreen.finalClicklog.length];


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalClicklog);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalClicklog);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] hours = new String[untilWhen];
        String[] dates = new String[untilWhen];

        for(int i = 0; i< untilWhen; i++){
            hours[i] = filteredOne[i].split(",")[0].split(" ")[1].split(":")[0];
        }

        for(int i=0; i < untilWhen-1; i++) {
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
            assert startDateString != null;
            if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)) {
                if ((!hours[i].equals(hours[i + 1])) || (i+1 == untilWhen-1)) {
                    ser.getData().add(new XYChart.Data<>(filteredOne[i].split(",")[0].split(" ")[0] + " " + filteredOne[i].split(",")[0].split(" ")[1].split(":")[0] + ":00", currentSum1));
                    currentSum1 = 0;
                } else {
                    currentSum1++;
                }
            }
        }

        return ser;
    }

    //should work for number of clicks
    public XYChart.Series<String, Integer> countByDayClicks() {
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        int currentSum1 = 0;
        String startDateString = null;
        String endDateString = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[MenuScreen.finalClicklog.length];

        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalClicklog);
            //untilWhen = filteredOne.length;
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalClicklog);
            //untilWhen = filteredOne.length;
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] dates = new String[untilWhen];
        for(int i = 0; i< untilWhen; i++){
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
        }

        //fill up the ser
        for(int i=0; i <untilWhen-1; i++) {
            assert startDateString != null;
            if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)) {
                if ((!dates[i].equals(dates[i + 1])) || (i+1 == untilWhen-1)) {
                    ser.getData().add(new XYChart.Data<>(dates[i], currentSum1));
                    currentSum1 = 0;
                } else {
                    currentSum1++;
                }
            }
        }

        return ser;
    }

    //should work for impression
    public XYChart.Series<String, Integer> countByDay() {
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        int currentSum1 = 0;
        String startDateString = null;
        String endDateString = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[MenuScreen.finalImpression.length];

        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalImpression);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalImpression);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] dates = new String[untilWhen];
        for(int i = 0; i< untilWhen; i++){
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
        }

        //fill up the ser
        for(int i=0; i <untilWhen-1; i++) {
            assert startDateString != null;
            if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)) {
                if ((!dates[i].equals(dates[i + 1])) || (i+1 == untilWhen-1)) {
                    ser.getData().add(new XYChart.Data<>(dates[i], currentSum1));
                    currentSum1 = 0;
                } else {
                    currentSum1++;
                }
            }
        }
        //grouping
        /*int currentSum = 0;
        for (int i = 0; i < logToUse.length - 1; i++){
            Date current = new SimpleDateFormat("yyyy-MM-dd").parse(logToUse.getCell(0,i).split(" ")[0]);
            if((current.after(finalStart) || current.equals(finalStart)) && (current.before(finalEnd) || current.equals(finalEnd))){
                sbCurr.append((getDay(i, logToUse)));
                sbNext.append((getDay(i + 1, logToUse)));
                if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                    ser.getData().add(new XYChart.Data<>(logToUse.getCell(0, i).split(" ")[0], currentSum));
                    currentSum = 0;
                }else{
                    if(decider[i] > -1) currentSum+=1;
                    //currentSum+=1;
                }
                sbCurr.delete(0,sbCurr.length());
                sbNext.delete(0,sbNext.length());
            }
        }*/
        return ser;
    }

    //should work for impression
    public XYChart.Series<String, Integer> countByHour() {
        int currentSum1 = 0;
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        String startDateString = null;
        String endDateString = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[MenuScreen.finalImpression.length];


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalImpression);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalImpression);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] hours = new String[untilWhen];
        String[] dates = new String[untilWhen];

        for(int i = 0; i< untilWhen; i++){
            hours[i] = filteredOne[i].split(",")[0].split(" ")[1].split(":")[0];
        }

        for(int i=0; i < untilWhen-1; i++) {
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
            assert startDateString != null;
            if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)) {
                if ((!hours[i].equals(hours[i + 1])) || (i+1 == untilWhen-1)) {
                    ser.getData().add(new XYChart.Data<>(filteredOne[i].split(",")[0].split(" ")[0] + " " + filteredOne[i].split(",")[0].split(" ")[1].split(":")[0] + ":00", currentSum1));
                    currentSum1 = 0;
                } else {
                    currentSum1++;
                }
            }
        }


        /*System.out.println("im trying");
        System.out.println("interval is" + groupingInterval.get());
        var sbCurr = new StringBuilder();
        var sbNext = new StringBuilder();
        var ser = new XYChart.Series();
        String[] values = MenuScreen.values;
        int[] decider;
        String[] filteredOne;
        Date finalStart;
        Date finalEnd;
        if(isFirst) {filteredOne = filterImpression(values); decider = new int[5];}
        //else decider = filterImpression1(logToUse);
        if(isFirst) {
            finalStart = startDate;
            finalEnd = endDate;
        }
        else {
            finalStart = startDate1;
            finalEnd = endDate1;
        }
        //grouping
        int currentSum = 0;
        for (int i = 0; i < logToUse.length - 1; i++){
            Date current = new SimpleDateFormat("yyyy-MM-dd").parse(logToUse.getCell(0,i).split(" ")[0]);
            if((current.after(finalStart) || current.equals(finalStart)) && (current.before(finalEnd) || current.equals(finalEnd))){
                sbCurr.append((getHour(i, logToUse)));
                sbNext.append((getHour(i + 1, logToUse)));
                if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                    ser.getData().add(new XYChart.Data<>(logToUse.getCell(0, i).split(" ")[0] + " " +  logToUse.getCell(0, i).split(" ")[1].split(":")[0] + ":00", currentSum));
                    currentSum = 0;
                }else{
                    //if(decider[i] > -1) currentSum +=1;
                }

            sbCurr.delete(0,sbCurr.length());
            sbNext.delete(0,sbNext.length());
        }}*/
        return ser;
    }

    //uniquesPerDay should work
    private XYChart.Series<String, Integer> uniquesPerDay() {
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        String startDateString = null;
        String endDateString = null;
        ArrayList<String> usedIDs = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[0];


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalClicklog);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalClicklog);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] dates = new String[untilWhen];
        String[] ids = new String[untilWhen];

        for(int i=0; i < untilWhen; i++) {
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
            ids[i] = filteredOne[i].split(",")[1];
        }


        int currentSum =0;
        int checkUnique = 0;

        for (int i = 0; i < untilWhen - 1; i++){
            assert startDateString != null;
            if((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)){
                if ((!dates[i].equals(dates[i + 1])) || (i+1 == untilWhen - 1)){
                    ser.getData().add(new XYChart.Data<>(dates[i], currentSum));
                    usedIDs.clear();
                    checkUnique = checkUnique + currentSum;
                    currentSum = 0;

                }else{
                    if (!(usedIDs.contains(ids[i]))){
                        currentSum += 1;
                        usedIDs.add(ids[i]);
                    }
                }
            }}


        /*
        var sbCurr = new StringBuilder();
        var sbNext = new StringBuilder();
        var ser = new XYChart.Series();
        ArrayList<String> usedIDs = new ArrayList<String>();
        HashMap<String, Integer> usedIDs;
        Date finalStart;
        Date finalEnd;
        if(isFirst) {
            finalStart = startDate;
            finalEnd = endDate;
        }
        else {
            finalStart = startDate1;
            finalEnd = endDate1;
        }
        //grouping
        int currentSum = 0;
        for (int i = 0; i < clickLog.length - 1; i++){
            Date current = new SimpleDateFormat("yyyy-MM-dd").parse(clickLog.getCell(0,i).split(" ")[0]);
            if((current.after(finalStart) || current.equals(finalStart)) && (current.before(finalEnd) || current.equals(finalEnd))){
                sbCurr.append((getDay(i, clickLog)));
                sbNext.append((getDay(i + 1, clickLog)));
                if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                    ser.getData().add(new XYChart.Data<>(clickLog.getCell(0, i).split(" ")[0], currentSum));
                    currentSum = 0;

                }else{
                    if (!(usedIDs.contains(clickLog.getCell(1, i)))){
                        currentSum += 1;
                        usedIDs.add(clickLog.getCell(1,i));
                    }
                }
                sbCurr.delete(0,sbCurr.length());
                sbNext.delete(0,sbNext.length());
        }}*/
        return ser;
    }

    //uniquesPerHour should work
    private XYChart.Series<String, Integer> uniquesPerHour() {
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        String startDateString = null;
        String endDateString = null;
        ArrayList<String> usedIDs = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[MenuScreen.finalClicklog.length];


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalClicklog);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalClicklog);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] dates = new String[untilWhen];
        String[] ids = new String[untilWhen];
        String[] hours = new String[untilWhen];
        for(int i=0; i < untilWhen; i++) {
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
            hours[i] = filteredOne[i].split(",")[0].split(" ")[1].split(":")[0];
            ids[i] = filteredOne[i].split(",")[1];
        }


        int currentSum =0;
        int checkUnique = 0;

        for (int i = 0; i < untilWhen - 1; i++){
            assert startDateString != null;
            if((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)){
                if ((!hours[i].equals(hours[i + 1])) || (i+1 == untilWhen - 1)){
                    ser.getData().add(new XYChart.Data<>(dates[i] + " " + hours[i] + ":00", currentSum));
                    usedIDs.clear();
                    checkUnique = checkUnique + currentSum;
                    currentSum = 0;

                }else{
                    if (!(usedIDs.contains(ids[i]))){
                        currentSum += 1;
                        usedIDs.add(ids[i]);
                    }
                }
            }}


        /*
        var sbCurr = new StringBuilder();
        var sbNext = new StringBuilder();
        var ser = new XYChart.Series();
        var usedIDs = new ArrayList<String>();

        Date finalStart;
        Date finalEnd;
        if(isFirst) {
            finalStart = startDate;
            finalEnd = endDate;
        }
        else {
            finalStart = startDate1;
            finalEnd = endDate1;
        }
        //grouping
        int currentSum = 0;
        for (int i = 0; i < clickLog.length - 1; i++){
            Date current = new SimpleDateFormat("yyyy-MM-dd").parse(clickLog.getCell(0,i).split(" ")[0]);
            if((current.after(finalStart) || current.equals(finalStart)) && (current.before(finalEnd) || current.equals(finalEnd))){
                sbCurr.append((getHour(i, clickLog)));
                sbNext.append((getHour(i + 1, clickLog)));
                if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                    ser.getData().add(new XYChart.Data<>(clickLog.getCell(0, i).split(" ")[0] + " " +  clickLog.getCell(0, i).split(" ")[1].split(":")[0] + ":00", currentSum));
                    currentSum = 0;
                }else{
                    if (!(usedIDs.contains(clickLog.getCell(1, i)))){
                        currentSum += 1;
                        usedIDs.add(clickLog.getCell(1,i));
                    }
                }
                sbCurr.delete(0,sbCurr.length());
                sbNext.delete(0,sbNext.length());
        }}*/
        return ser;
    }

    //conversionsPerDay should work
    private XYChart.Series<String, Integer> conversionsPerDay() {
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        String startDateString = null;
        String endDateString = null;
        int currentSum=0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[MenuScreen.finalServerlog.length];


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalServerlog);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalServerlog);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] dates = new String[untilWhen];
        String[] conversions = new String[untilWhen];
        for(int i = 0; i< untilWhen; i++){
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
            conversions[i] = filteredOne[i].split(",")[4];
        }

        //fill up the ser
        for(int i=0; i <untilWhen-1; i++) {
            assert startDateString != null;
            if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)) {
                if ((!dates[i].equals(dates[i + 1])) || (i+1 == untilWhen-1)) {
                    ser.getData().add(new XYChart.Data<>(dates[i], currentSum));
                    currentSum = 0;
                } else {
                    if(conversions[i].equals("Yes"))
                        currentSum++;
                }
            }
        }
        /*
        var sbCurr = new StringBuilder();
        var sbNext = new StringBuilder();
        var ser = new XYChart.Series();
        Date finalStart;
        Date finalEnd;
        if(isFirst) {
            finalStart = startDate;
            finalEnd = endDate;
        }
        else {
            finalStart = startDate1;
            finalEnd = endDate1;
        }
        //grouping
        int currentSum = 0;
        for (int i = 0; i < serverLog.length - 1; i++){
            //System.out.println(serverLog.getCell(2,i));
            //Date startTime = new SimpleDateFormat("yyyy-MM-dd").parse(serverLog.getCell(0,i).split(" ")[0]);
            if (serverLog.getCell(2,i) != "n/a" && serverLog.getCell(0,i) != "n/a") {
                Date startTime = new SimpleDateFormat("yyyy-MM-dd").parse(serverLog.getCell(0,i).split(" ")[0]);
                Date endTime = new SimpleDateFormat("yyyy-MM-dd").parse(serverLog.getCell(0,i).split(" ")[0]);
                if((startTime.after(finalStart) || startTime.equals(finalStart)) && (endTime.before(finalEnd) || endTime.equals(finalEnd))){
                    sbCurr.append((getDay(i, serverLog)));
                    sbNext.append((getDay(i + 1, serverLog)));
                    if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                        ser.getData().add(new XYChart.Data<>(serverLog.getCell(0, i).split(" ")[0], currentSum));
                        currentSum = 0;
                    }else{
                        String conversion = serverLog.getCell(4, i);
                        if(conversion.equals("Yes")){
                            currentSum += 1;
                        }
                        //System.out.println(currentSum);
                    }
                    sbCurr.delete(0,sbCurr.length());
                    sbNext.delete(0,sbNext.length());
            }} else {

                    sbCurr.append((getDay(i, serverLog)));
                    sbNext.append((getDay(i + 1, serverLog)));
                    if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                        ser.getData().add(new XYChart.Data<>(serverLog.getCell(0, i).split(" ")[0], currentSum));
                        currentSum = 0;
                    }else{
                        String conversion = serverLog.getCell(4, i);
                        if(conversion.equals("Yes")){
                            currentSum += 1;
                        }
                        //System.out.println(currentSum);
                    }
                    sbCurr.delete(0,sbCurr.length());
                    sbNext.delete(0,sbNext.length());
            }
        }*/

        return ser;
    }

    //conversionsPerHour should work
    private XYChart.Series<String, Integer> conversionsPerHour() {
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        String startDateString = null;
        String endDateString = null;
        int currentSum=0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[MenuScreen.finalServerlog.length];


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalServerlog);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalServerlog);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] dates = new String[untilWhen];
        String[] hours = new String[untilWhen];
        String[] conversions = new String[untilWhen];
        for(int i = 0; i< untilWhen; i++){
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
            hours[i] = filteredOne[i].split(",")[0].split(" ")[1].split(":")[0];
            conversions[i] = filteredOne[i].split(",")[4];
        }

        //fill up the ser
        for(int i=0; i <untilWhen-1; i++) {
            assert startDateString != null;
            if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)) {
                if ((!hours[i].equals(hours[i + 1])) || (i+1 == untilWhen-1)) {
                    ser.getData().add(new XYChart.Data<>(dates[i] + " " + hours[i] + ":00", currentSum));
                    currentSum = 0;
                } else {
                    if(conversions[i].equals("Yes"))
                        currentSum++;
                }
            }
        }
        /*
        var sbCurr = new StringBuilder();
        var sbNext = new StringBuilder();
        var ser = new XYChart.Series();
        Date finalStart;
        Date finalEnd;
        if(isFirst) {
            finalStart = startDate;
            finalEnd = endDate;
        }
        else {
            finalStart = startDate1;
            finalEnd = endDate1;
        }
        //grouping
        int currentSum = 0;
        for (int i = 0; i < serverLog.length - 1; i++){
            System.out.println(serverLog.getCell(2,i));
            Date startTime = new SimpleDateFormat("yyyy-MM-dd").parse(serverLog.getCell(0,i).split(" ")[0]);
            Date endTime = new SimpleDateFormat("yyyy-MM-dd").parse(serverLog.getCell(0,i).split(" ")[0]);
            if((startTime.after(finalStart) || startTime.equals(finalStart)) && (endTime.before(finalEnd) || endTime.equals(finalEnd))){
                sbCurr.append((getHour(i, serverLog)));
                sbNext.append((getHour(i + 1, serverLog)));
                if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                    ser.getData().add(new XYChart.Data<>(serverLog.getCell(0, i).split(" ")[0] + " " +  serverLog.getCell(0, i).split(" ")[1].split(":")[0] + ":00", currentSum));
                    currentSum = 0;
                }else{
                    String conversion = serverLog.getCell(4, i);
                    if(conversion.equals("Yes")) {
                        currentSum += 1;
                    }
                }
                sbCurr.delete(0,sbCurr.length());
                sbNext.delete(0,sbNext.length());
            }}*/
        return ser;
    }

    //costPerDay should work
    private XYChart.Series<String, Double> costPerDay() {
        XYChart.Series<String, Double> ser = new XYChart.Series<>();
        String startDateString = null;
        String endDateString = null;
        double currentSum=0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[MenuScreen.finalImpression.length];


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalImpression);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalImpression);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] dates = new String[untilWhen];
        double[] clickCost = new double[untilWhen];
        for(int i = 0; i< untilWhen; i++){
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
            clickCost[i] = Double.parseDouble(filteredOne[i].split(",")[6]) + Double.parseDouble(filteredOne[i].split(",")[8]);
        }

        for(int i=0; i < untilWhen-1; i++){
            assert startDateString != null;
            if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)){
                if ((!dates[i].equals(dates[i + 1])) || (i+1 == untilWhen-1)) {
                    ser.getData().add(new XYChart.Data<>(dates[i], currentSum));
                    currentSum = 0;
                } else {
                    currentSum = currentSum + clickCost[i];
                }
            }
        }

        /*
        var sbCurr = new StringBuilder();
        var sbNext = new StringBuilder();
        var innersbCurr = new StringBuilder();
        var innersbNext = new StringBuilder();
        var ser = new XYChart.Series();

        //grouping vars
        int currentStart = 0;
        int innerStart = 0;
        boolean nextNotFound = true;
        ClickLog smallClick = null;


        //GROUP THE IMPRESSION LOG
        for (int i = 0; i < impressionLog.length - 1; i++){
            sbCurr.append((getDay(i, impressionLog)));
            sbNext.append((getDay(i + 1, impressionLog)));
            if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                var smallImpr = ImpressionLog.splitFile(currentStart, i, impressionLog);


                //NOW GROUP THE CLICK LOG
                var innerCount = innerStart;
                while (nextNotFound && innerStart < clickLog.length - 1){
                    innersbCurr.append((getDay(innerCount, clickLog)));
                    innersbNext.append((getDay(innerCount + 1, clickLog)));
                    if ((innersbCurr.charAt(1) != innersbNext.charAt(1) || innersbCurr.charAt(0) != innersbNext.charAt(0))) {
                        smallClick = ClickLog.splitFile(innerStart, innerCount, clickLog);
                        nextNotFound = false;
                    }
                    innerCount += 1;
                    innersbCurr.delete(0,innersbCurr.length());
                    innersbNext.delete(0,innersbNext.length());
                }
                innerStart = innerCount;
                nextNotFound = true;

                //COMBINE CLICK AND IMPRESSION LOGS
                var cost = KeyMetrics.getTotalCost(smallImpr, smallClick); //NEED TO CHANGE DEPENDING ON CHANGING BOUNCE RATE
                XYChart.Data newPoint = new XYChart.Data(impressionLog.getCell(0, i).split(" ")[0], cost);
                ser.getData().add(newPoint);
                currentStart = i+1;
            }
            sbCurr.delete(0,sbCurr.length());
            sbNext.delete(0,sbNext.length());
        }*/
        return ser;
    }

    //costPerHour should work
    private XYChart.Series<String, Double> costPerHour() {
        XYChart.Series<String, Double> ser = new XYChart.Series<>();
        String startDateString = null;
        String endDateString = null;
        double currentSum=0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne = new String[MenuScreen.finalImpression.length];


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalImpression);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalImpression);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
        }


        String[] dates = new String[untilWhen];
        String[] hours = new String[untilWhen];
        double[] clickCost = new double[untilWhen];
        for(int i = 0; i< untilWhen; i++){
            dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
            hours[i] = filteredOne[i].split(",")[0].split(" ")[1].split(":")[0];
            clickCost[i] = Double.parseDouble(filteredOne[i].split(",")[6]) + Double.parseDouble(filteredOne[i].split(",")[8]);
        }

        for(int i=0; i < untilWhen-1; i++){
            assert startDateString != null;
            if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)){
                if ((!hours[i].equals(hours[i + 1])) || (i+1 == untilWhen-1)) {
                    ser.getData().add(new XYChart.Data<>(dates[i] + " " + hours[i] + ":00", currentSum));
                    currentSum = 0;
                } else {
                    currentSum = currentSum + clickCost[i];
                }
            }
        }

        /*var sbCurr = new StringBuilder();
        var sbNext = new StringBuilder();
        var innersbCurr = new StringBuilder();
        var innersbNext = new StringBuilder();
        var ser = new XYChart.Series();

        //grouping vars
        int currentStart = 0;
        int innerStart = 0;
        boolean nextNotFound = true;
        ClickLog smallClick = null;


        //GROUP THE IMPRESSION LOG
        for (int i = 0; i < impressionLog.length - 1; i++){
            sbCurr.append((getHour(i, impressionLog)));
            sbNext.append((getHour(i + 1, impressionLog)));
            if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                var smallImpr = ImpressionLog.splitFile(currentStart, i, impressionLog);


                //NOW GROUP THE CLICK LOG
                var innerCount = innerStart;
                while (nextNotFound && innerStart < clickLog.length - 1){
                    innersbCurr.append((getHour(innerCount, clickLog)));
                    innersbNext.append((getHour(innerCount + 1, clickLog)));
                    if ((innersbCurr.charAt(1) != innersbNext.charAt(1) || innersbCurr.charAt(0) != innersbNext.charAt(0))) {
                        smallClick = ClickLog.splitFile(innerStart, innerCount, clickLog);
                        nextNotFound = false;
                    }
                    innerCount += 1;
                    innersbCurr.delete(0,innersbCurr.length());
                    innersbNext.delete(0,innersbNext.length());
                }
                innerStart = innerCount;
                nextNotFound = true;

                //COMBINE CLICK AND IMPRESSION LOGS
                var cost = KeyMetrics.getTotalCost(smallImpr, smallClick); //NEED TO CHANGE DEPENDING ON CHANGING BOUNCE RATE
                XYChart.Data newPoint = new XYChart.Data(impressionLog.getCell(0, i).split(" ")[0] + " " +  impressionLog.getCell(0, i).split(" ")[1].split(":")[0] + ":00", cost);
                ser.getData().add(newPoint);
                currentStart = i+1;
            }
            sbCurr.delete(0,sbCurr.length());
            sbNext.delete(0,sbNext.length());
        }*/
        return ser;
    }

    //bouncesPerDay should work
    private XYChart.Series<String, Integer> bouncesPerDay() {
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        String startDateString;
        String endDateString;
        int currentSum=0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne;


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalServerlog);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
            String[] dates = new String[untilWhen];
            int[] entrySeconds = new int[untilWhen];
            int[] exitSeconds = new int[untilWhen];
            int[] pagesViewed = new int[untilWhen];
            String check;
            //making the entrySecond the exitSecond and the pagesViewed and the dates
            for(int i = 0; i< untilWhen; i++){
                dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
                check = filteredOne[i].split(",")[0].split(" ")[1];
                entrySeconds[i] = Integer.parseInt(check.split(":")[0])*3600 + Integer.parseInt(check.split(":")[1])*60 + Integer.parseInt(check.split(":")[2]);
                if(!filteredOne[i].split(",")[2].equals("n/a")) {
                    check = filteredOne[i].split(",")[2].split(" ")[1];
                    exitSeconds[i] = Integer.parseInt(check.split(":")[0])*3600 + Integer.parseInt(check.split(":")[1])*60 + Integer.parseInt(check.split(":")[2]);
                } else {
                    exitSeconds[i] = 0;
                }
                pagesViewed[i] = Integer.parseInt(filteredOne[i].split(",")[3]);
                if(entrySeconds[i] > exitSeconds[i]){
                    exitSeconds[i] = exitSeconds[i] + 86400;
                }
            }

            //fill up the ser
            for(int i=0; i < untilWhen-1; i++){
                if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)){
                    if ((!dates[i].equals(dates[i + 1])) || (i+1 == untilWhen-1)) {
                        ser.getData().add(new XYChart.Data<>(dates[i], currentSum));
                        currentSum = 0;
                    } else {
                        if(pagesViewed[i] <= minVisit || (exitSeconds[i] - entrySeconds[i] <= minTimeSpentt))
                            currentSum++;
                        //currentSum = currentSum + clickCost[i];
                    }
                }
            }
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalServerlog);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
            String[] dates = new String[untilWhen];
            int[] entrySeconds = new int[untilWhen];
            int[] exitSeconds = new int[untilWhen];
            int[] pagesViewed = new int[untilWhen];
            String check;
            //making the entrySecond the exitSecond and the pagesViewed and the dates
            for(int i = 0; i< untilWhen; i++){
                dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
                check = filteredOne[i].split(",")[0].split(" ")[1];
                entrySeconds[i] = Integer.parseInt(check.split(":")[0])*3600 + Integer.parseInt(check.split(":")[1])*60 + Integer.parseInt(check.split(":")[2]);
                if(!filteredOne[i].split(",")[2].equals("n/a")) {
                    check = filteredOne[i].split(",")[2].split(" ")[1];
                    exitSeconds[i] = Integer.parseInt(check.split(":")[0])*3600 + Integer.parseInt(check.split(":")[1])*60 + Integer.parseInt(check.split(":")[2]);
                } else {
                    exitSeconds[i] = 0;
                }
                pagesViewed[i] = Integer.parseInt(filteredOne[i].split(",")[3]);
                if(entrySeconds[i] > exitSeconds[i]){
                    exitSeconds[i] = exitSeconds[i] + 86400;
                }
            }

            //fill up the ser
            for(int i=0; i < untilWhen-1; i++){
                if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)){
                    if ((!dates[i].equals(dates[i + 1])) || (i+1 == untilWhen-1)) {
                        ser.getData().add(new XYChart.Data<>(dates[i], currentSum));
                        currentSum = 0;
                    } else {
                        if(pagesViewed[i] <= minVisit1 || (exitSeconds[i] - entrySeconds[i] <= minTimeSpentt1))
                            currentSum++;
                        //currentSum = currentSum + clickCost[i];
                    }
                }
            }
        }




        /*
        Date finalStart;
        Date finalEnd;
        if(isFirst) {
            finalStart = startDate;
            finalEnd = endDate;
        }
        else {
            finalStart = startDate1;
            finalEnd = endDate1;
        }
        //grouping
        int currentStart = 0;
        //System.out.println(serverLog.length);
        for (int i = 0; i < serverLog.length - 1; i++) {
            Date startTime = new SimpleDateFormat("yyyy-MM-dd").parse(serverLog.getCell(0, i).split(" ")[0]);
            if (serverLog.getCell(2, i) != "n/a") {
                Date endTime = new SimpleDateFormat("yyyy-MM-dd").parse(serverLog.getCell(2, i).split(" ")[0]);
                if ((startTime.after(finalStart) || startTime.equals(finalStart)) && (endTime.before(finalEnd) || endTime.equals(finalEnd))) {
                    sbCurr.append((getDay(i, serverLog)));
                    sbNext.append((getDay(i + 1, serverLog)));
                    if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))) {
                        ServerLog smallLog = ServerLog.splitFile(currentStart, i, serverLog);
                        var bounces = KeyMetrics.getNumberOfBounces(smallLog, minVisits, minTimeSpent); //NEED TO CHANGE DEPENDING ON CHANGING BOUNCE RATE
                        XYChart.Data newPoint = new XYChart.Data(serverLog.getCell(0, i).split(" ")[0], bounces);
                        ser.getData().add(newPoint);
                        currentStart = i + 1;
                    }
                    sbCurr.delete(0, sbCurr.length());
                    sbNext.delete(0, sbNext.length());
                }
            }else {
                if ((startTime.after(finalStart) || startTime.equals(finalStart))) {
                    sbCurr.append((getDay(i, serverLog)));
                    sbNext.append((getDay(i + 1, serverLog)));
                    if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))) {
                        ServerLog smallLog = ServerLog.splitFile(currentStart, i, serverLog);
                        var bounces = KeyMetrics.getNumberOfBounces(smallLog, minVisits, minTimeSpent); //NEED TO CHANGE DEPENDING ON CHANGING BOUNCE RATE
                        XYChart.Data newPoint = new XYChart.Data(serverLog.getCell(0, i).split(" ")[0], bounces);
                        ser.getData().add(newPoint);
                        currentStart = i + 1;
                    }
                    sbCurr.delete(0, sbCurr.length());
                    sbNext.delete(0, sbNext.length());
                }
            }
        }*/
        return ser;
    }

    //bouncesPerHour should work
    private XYChart.Series<String, Integer> bouncesPerHour() {
        XYChart.Series<String, Integer> ser = new XYChart.Series<>();
        String startDateString;
        String endDateString;
        int currentSum=0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] filteredOne;


        if (isFirst){
            filteredOne = filterImpression(MenuScreen.finalServerlog);
            startDateString = dateFormat.format(startDate1);
            endDateString = dateFormat.format(endDate1);
            String[] dates = new String[untilWhen];
            int[] entrySeconds = new int[untilWhen];
            int[] exitSeconds = new int[untilWhen];
            int[] pagesViewed = new int[untilWhen];
            String[] hours = new String[untilWhen];
            String check;
            //making the entrySecond the exitSecond and the pagesViewed and the dates
            for(int i = 0; i< untilWhen; i++){
                hours[i] = filteredOne[i].split(",")[0].split(" ")[1].split(":")[0];
                dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
                check = filteredOne[i].split(",")[0].split(" ")[1];
                entrySeconds[i] = Integer.parseInt(check.split(":")[0])*3600 + Integer.parseInt(check.split(":")[1])*60 + Integer.parseInt(check.split(":")[2]);
                if(!filteredOne[i].split(",")[2].equals("n/a")) {
                    check = filteredOne[i].split(",")[2].split(" ")[1];
                    exitSeconds[i] = Integer.parseInt(check.split(":")[0])*3600 + Integer.parseInt(check.split(":")[1])*60 + Integer.parseInt(check.split(":")[2]);
                } else {
                    exitSeconds[i] = 0;
                }
                pagesViewed[i] = Integer.parseInt(filteredOne[i].split(",")[3]);
                if(entrySeconds[i] > exitSeconds[i]){
                    exitSeconds[i] = exitSeconds[i] + 86400;
                }
            }

            //fill up the ser

            for(int i=0; i < untilWhen-1; i++){
                if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)){
                    if ((!hours[i].equals(hours[i + 1])) || (i+1 == untilWhen-1)) {
                        ser.getData().add(new XYChart.Data<>(dates[i] + " " + hours[i] + ":00", currentSum));
                        currentSum = 0;
                    } else {
                        if(pagesViewed[i] <= minVisit || (exitSeconds[i] - entrySeconds[i] <= minTimeSpentt))
                            currentSum++;
                        //currentSum = currentSum + clickCost[i];
                    }
                }
            }
        }
        if(isSecond){
            filteredOne = filterImpression1(MenuScreen.finalServerlog);
            startDateString = dateFormat.format(startDate2);
            endDateString = dateFormat.format(endDate2);
            String[] dates = new String[untilWhen];
            int[] entrySeconds = new int[untilWhen];
            int[] exitSeconds = new int[untilWhen];
            int[] pagesViewed = new int[untilWhen];
            String[] hours = new String[untilWhen];
            String check;
            //making the entrySecond the exitSecond and the pagesViewed and the dates
            for(int i = 0; i< untilWhen; i++){
                hours[i] = filteredOne[i].split(",")[0].split(" ")[1].split(":")[0];
                dates[i] = filteredOne[i].split(",")[0].split(" ")[0];
                check = filteredOne[i].split(",")[0].split(" ")[1];
                entrySeconds[i] = Integer.parseInt(check.split(":")[0])*3600 + Integer.parseInt(check.split(":")[1])*60 + Integer.parseInt(check.split(":")[2]);
                if(!filteredOne[i].split(",")[2].equals("n/a")) {
                    check = filteredOne[i].split(",")[2].split(" ")[1];
                    exitSeconds[i] = Integer.parseInt(check.split(":")[0])*3600 + Integer.parseInt(check.split(":")[1])*60 + Integer.parseInt(check.split(":")[2]);
                } else {
                    exitSeconds[i] = 0;
                }
                pagesViewed[i] = Integer.parseInt(filteredOne[i].split(",")[3]);
                if(entrySeconds[i] > exitSeconds[i]){
                    exitSeconds[i] = exitSeconds[i] + 86400;
                }
            }

            //fill up the ser

            for(int i=0; i < untilWhen-1; i++){
                if ((startDateString.compareTo(dates[i]) <= 0) && (endDateString.compareTo(dates[i]) >= 0)){
                    if ((!hours[i].equals(hours[i + 1])) || (i+1 == untilWhen-1)) {
                        ser.getData().add(new XYChart.Data<>(dates[i] + " " + hours[i] + ":00", currentSum));
                        currentSum = 0;
                    } else {
                        if(pagesViewed[i] <= minVisit1 || (exitSeconds[i] - entrySeconds[i] <= minTimeSpentt1))
                            currentSum++;
                        //currentSum = currentSum + clickCost[i];
                    }
                }
            }
        }




       /* var sbCurr = new StringBuilder();
        var sbNext = new StringBuilder();
        var ser = new XYChart.Series();
        Date finalStart;
        Date finalEnd;
        if(isFirst) {
            finalStart = startDate;
            finalEnd = endDate;
        }
        else {
            finalStart = startDate1;
            finalEnd = endDate1;
        }
        //grouping
        int currentStart = 0;
        //System.out.println(serverLog.length);
        for (int i = 0; i < serverLog.length - 1; i++){
            Date startTime = new SimpleDateFormat("yyyy-MM-dd").parse(serverLog.getCell(0,i).split(" ")[0]);
            if (serverLog.getCell(2,i) != "n/a") {
                Date endTime = new SimpleDateFormat("yyyy-MM-dd").parse(serverLog.getCell(2,i).split(" ")[0]);

            if((startTime.after(finalStart) || startTime.equals(finalStart)) && (endTime.before(finalEnd) || endTime.equals(finalEnd))){
                sbCurr.append((getHour(i, serverLog)));
                sbNext.append((getHour(i + 1, serverLog)));
                if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                    ServerLog smallLog = ServerLog.splitFile(currentStart, i, serverLog);
                    var bounces = KeyMetrics.getNumberOfBounces(smallLog, minVisits,  minTimeSpent); //NEED TO CHANGE DEPENDING ON CHANGING BOUNCE RATE
                    XYChart.Data newPoint = new XYChart.Data(serverLog.getCell(0, i).split(" ")[0] + " " +  serverLog.getCell(0, i).split(" ")[1].split(":")[0] + ":00", bounces);
                    ser.getData().add(newPoint);
                    currentStart = i+1;
                }
                sbCurr.delete(0,sbCurr.length());
                sbNext.delete(0,sbNext.length());
            }} else {
                if((startTime.after(finalStart) || startTime.equals(finalStart)) ){
                    sbCurr.append((getHour(i, serverLog)));
                    sbNext.append((getHour(i + 1, serverLog)));
                    if ((sbCurr.charAt(1) != sbNext.charAt(1) || sbCurr.charAt(0) != sbNext.charAt(0))){
                        ServerLog smallLog = ServerLog.splitFile(currentStart, i, serverLog);
                        var bounces = KeyMetrics.getNumberOfBounces(smallLog, minVisits,  minTimeSpent); //NEED TO CHANGE DEPENDING ON CHANGING BOUNCE RATE
                        XYChart.Data newPoint = new XYChart.Data(serverLog.getCell(0, i).split(" ")[0] + " " +  serverLog.getCell(0, i).split(" ")[1].split(":")[0] + ":00", bounces);
                        ser.getData().add(newPoint);
                        currentStart = i+1;
                    }
                    sbCurr.delete(0,sbCurr.length());
                    sbNext.delete(0,sbNext.length());
            }}
        }*/

        return ser;
    }





    public void reload() {
        loadGraph(currentMetric);
    }



    static boolean male = true;
    static boolean female = true;
    static boolean age25 = true;
    static boolean age34 = true;
    static boolean age44 = true;
    static boolean age54 = true;
    static boolean age100= true;
    static boolean low = true;
    static boolean medium = true;
    static boolean high = true;
    static boolean news = true;
    static boolean blog = true;
    static boolean socialMedia = true;
    static boolean shopping = true;
    static boolean hobbies = true;
    static boolean travel = true;
    static Date startDate;
    static Date endDate;


    //get the filtered data in the format of [String]
    public String[] filterImpression(String[] stringValues){

        String[] toOut = new String[stringValues.length];
        int[] decider = new int[stringValues.length];
        System.out.println("Filters started");

        for (int i = 0; i < stringValues.length; i++) {
            if (!((male && stringValues[i].contains("Male")) || (female && stringValues[i].contains("Female")))
                    || !((age25 && stringValues[i].contains("<25")) || (age34 && stringValues[i].contains("25-34")) || (age44 && stringValues[i].contains("35-44"))
                    || (age54 && stringValues[i].contains("45-54")) || (age100 && stringValues[i].contains(">54")))
                    || !((low && stringValues[i].contains("Low")) || (medium && stringValues[i].contains("Medium")) || (high && stringValues[i].contains("High")))
                    || !((news && stringValues[i].contains("News")) || (blog && stringValues[i].contains("Blog")) || (socialMedia && stringValues[i].contains("Social Media"))
                    || (hobbies && stringValues[i].contains("Hobbies")) || (travel && stringValues[i].contains("Travel")) || (shopping && stringValues[i].contains("Shopping")))) {
                decider[i]--;
            }
        }

        int j=0;
        for(int i=0; i < stringValues.length; i++){
            if(decider[i] == 0){
                toOut[j] = stringValues[i];
                j++;
            }
        }

        untilWhen = j;
        return toOut;
    }


    static boolean male1 = true;
    static boolean female1 = true;
    static boolean age251 = true;
    static boolean age341 = true;
    static boolean age441 = true;
    static boolean age541 = true;
    static boolean age1001= true;
    static boolean low1 = true;
    static boolean medium1 = true;
    static boolean high1 = true;
    static boolean news1 = true;
    static boolean blog1 = true;
    static boolean socialMedia1 = true;
    static boolean shopping1 = true;
    static boolean hobbies1 = true;
    static boolean travel1 = true;
    static Date startDate1;
    static Date endDate1;



    public String[] filterImpression1(String[] stringValues) {
        String[] toOut = new String[stringValues.length];
        int[] decider = new int[stringValues.length];
        System.out.println("Filters started");

        for (int i = 0; i < stringValues.length; i++) {
            if (!((male1 && stringValues[i].contains("Male")) || (female1 && stringValues[i].contains("Female")))
                    || !((age251 && stringValues[i].contains("<25")) || (age341 && stringValues[i].contains("25-34")) || (age441 && stringValues[i].contains("35-44"))
                    || (age541 && stringValues[i].contains("45-54")) || (age1001 && stringValues[i].contains(">54")))
                    || !((low1 && stringValues[i].contains("Low")) || (medium1 && stringValues[i].contains("Medium")) || (high1 && stringValues[i].contains("High")))
                    || !((news1 && stringValues[i].contains("News")) || (blog1 && stringValues[i].contains("Blog")) || (socialMedia1 && stringValues[i].contains("Social Media"))
                    || (hobbies1 && stringValues[i].contains("Hobbies")) || (travel1 && stringValues[i].contains("Travel")) || (shopping1 && stringValues[i].contains("Shopping")))) {
                decider[i]--;
            }
        }

        int j=0;
        for(int i=0; i < stringValues.length; i++){
            if(decider[i] == 0){
                toOut[j] = stringValues[i];
                j++;
            }
        }

        untilWhen = j;
        return toOut;
    }


}
