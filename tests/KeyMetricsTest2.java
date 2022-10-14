import com.example.seg.ClickLog;
import com.example.seg.ImpressionLog;
import com.example.seg.KeyMetrics;
import com.example.seg.ServerLog;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class KeyMetricsTest2 {
    ImpressionLog impressionLog;
    ClickLog clickLog;
    ServerLog serverLog;
    boolean[] filters;
    String start_date;
    String end_date;

    @BeforeEach
    void setUp() throws IOException, CsvException {
        this.impressionLog = new ImpressionLog("C:\\Users\\jmboy\\IdeaProjects\\SEGProject\\SEG\\src\\main\\tests\\impression_log_test.csv");
        this.clickLog = new ClickLog("C:\\Users\\jmboy\\IdeaProjects\\SEGProject\\SEG\\src\\main\\tests\\click_log_test.csv");
        this.serverLog = new ServerLog("C:\\Users\\jmboy\\IdeaProjects\\SEGProject\\SEG\\src\\main\\tests\\server_log_test.csv");
        this.filters = new boolean[16];
        this.filters[0] = false;
        this.filters[1] = true;
        for(int i = 2; i < this.filters.length; i++) {
            this.filters[i] = true;
        }
        this.start_date = "2015-01-04";
        this.end_date = "2015-01-11";
    }

    @Test
    void makeDateFromString() {
        /*Defect Testing
           ---Strings that are not a valid format
           ---A day that is over 31 or under 1
           ---A month that is over 12 or under 1
           ---A date that doesn't exist

        */
        assertNull(KeyMetrics.makeDateFromString("Not-a-date"));
        assertNull(KeyMetrics.makeDateFromString("NotADate"));
        assertNull(KeyMetrics.makeDateFromString("2d015-01-01"));

        assertNull(KeyMetrics.makeDateFromString("2015-01-32"));
        assertNull(KeyMetrics.makeDateFromString("2015-01-00"));

        assertNull(KeyMetrics.makeDateFromString("2015-13-01"));
        assertNull(KeyMetrics.makeDateFromString("2015-00-01"));

        assertNull(KeyMetrics.makeDateFromString("2015-02-29"));
        assertNull(KeyMetrics.makeDateFromString("2015-06-31"));


        /*Partitions
           ---Before 1970       => "1966-04-03" => False
           ---1970-Presnet Day  => "2015-01-01" => True
           ---After Present Day => "2027-07-21" => False
         */
        assertNull(KeyMetrics.makeDateFromString("1966-04-03"));
        assertNotNull(KeyMetrics.makeDateFromString("2015-01-01"));
        assertNull(KeyMetrics.makeDateFromString("2027-07-21"));

        /*Boundaries
           ---Before 1970
              ---"1969-12-31" => False
              ---"1970-01-01" => True
              ---"1970-01-02" => True
           ---After Present Day
              ---"Day-Before-Present-Day" => True
              ---"Present Day"            => True
              ---"Day-After-Present-Day"  => False
         */
        assertNull(KeyMetrics.makeDateFromString("1969-12-31"));
        assertNotNull(KeyMetrics.makeDateFromString("1970-01-01"));
        assertNotNull(KeyMetrics.makeDateFromString("1970-01-02"));
        assertNotNull(KeyMetrics.makeDateFromString("2022-03-22"));
        assertNotNull(KeyMetrics.makeDateFromString("2022-03-23"));
        assertNull(KeyMetrics.makeDateFromString("2022-03-24"));
    }

    @Test
    void getNumberOfImpressionsFiltered() {
        assertEquals(8, KeyMetrics.getNumberOfImpressionsFiltered(this.impressionLog, this.filters, this.start_date, this.end_date));
    }

    @Test
    void getNumberOfClicksFiltered() {
        assertEquals(12, KeyMetrics.getNumberOfClicksFiltered(this.clickLog, this.impressionLog, this.filters, this.start_date, this.end_date));
    }

    @Test
    void getNumberOfUniquesFiltered() {
        assertEquals(10, KeyMetrics.getNumberOfUniquesFiltered(this.clickLog, this.impressionLog, this.filters, this.start_date, this.end_date));
    }

    @Test
    void getNumberOfBouncesFiltered() {
        assertEquals(2, KeyMetrics.getNumberOfBouncesFiltered(this.serverLog, 1, 60, this.impressionLog, this.filters, this.start_date, this.end_date));
    }

    @Test
    void getNumberOfConversionsFiltered() {
        assertEquals(2, KeyMetrics.getNumberOfConversionsFiltered(this.serverLog, this.impressionLog, this.filters, this.start_date, this.end_date));
    }

    @Test
    void getTotalCostFiltered() {
        assertEquals(41.61, (Math.round(KeyMetrics.getTotalCostFiltered(this.impressionLog, this.clickLog, this.filters, this.start_date, this.end_date) *100) * 1.0) / 100);
    }

    @Test
    void getCTRFiltered() {
        assertEquals(1.5, (Math.round(KeyMetrics.getCTRFiltered(this.impressionLog, this.clickLog, this.filters, this.start_date, this.end_date) *1000) * 1.0) / 1000);
    }

    @Test
    void getCPAFiltered() {
        assertEquals(20.805, (Math.round(KeyMetrics.getCPAFiltered(this.impressionLog, this.clickLog, this.serverLog, this.filters, this.start_date, this.end_date) *1000) * 1.0) / 1000);
    }

    @Test
    void getCPCFiltered() {
        assertEquals(3.468, (Math.round(KeyMetrics.getCPCFiltered(this.impressionLog, this.clickLog, this.filters, this.start_date, this.end_date) *1000) * 1.0) / 1000);
    }

    @Test
    void getCPMFiltered() {
        assertEquals(5201.25, (Math.round(KeyMetrics.getCPMFiltered(this.impressionLog, this.clickLog, this.filters, this.start_date, this.end_date) *1000) * 1.0) / 1000);
    }

    @Test
    void getBounceRateFiltered() {
        assertEquals(0.667, (Math.round(KeyMetrics.getBounceRateFiltered(this.clickLog, this.serverLog, this.impressionLog, 1, 60, this.filters, this.start_date, this.end_date) *1000) * 1.0) / 1000);
    }
}