import com.example.seg.ClickLog;
import com.example.seg.ImpressionLog;
import com.example.seg.KeyMetrics;
import com.example.seg.ServerLog;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class KeyMetricsTest {
    ImpressionLog impressionLog;
    ClickLog clickLog;
    ServerLog serverLog;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws IOException, CsvException {
        this.impressionLog = new ImpressionLog("C:\\Users\\jmboy\\IdeaProjects\\SEGProject\\SEG\\src\\main\\tests\\impression_log_test.csv");
        this.clickLog = new ClickLog("C:\\Users\\jmboy\\IdeaProjects\\SEGProject\\SEG\\src\\main\\tests\\click_log_test.csv");
        this.serverLog = new ServerLog("C:\\Users\\jmboy\\IdeaProjects\\SEGProject\\SEG\\src\\main\\tests\\server_log_test.csv");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void getNumberOfImpressions() {
        assertEquals(15, KeyMetrics.getNumberOfImpressions(this.impressionLog));
    }

    @org.junit.jupiter.api.Test
    void getNumberOfClicks() {
        assertEquals(19, KeyMetrics.getNumberOfClicks(this.clickLog));
    }

    @org.junit.jupiter.api.Test
    void getNumberOfUniques() {
        assertEquals(15, KeyMetrics.getNumberOfUniques(this.clickLog));
    }

    @org.junit.jupiter.api.Test
    void getNumberOfBounces() {
        assertEquals(6, KeyMetrics.getNumberOfBounces(this.serverLog, 1, 1));
    }

    @org.junit.jupiter.api.Test
    void getNumberOfConversions() {
        assertEquals(2, KeyMetrics.getNumberOfConversions(this.serverLog));
    }

    @org.junit.jupiter.api.Test
    void getTotalCost() {
        assertEquals(99.53, (Math.round(KeyMetrics.getTotalCost(this.impressionLog, this.clickLog) *100) * 1.0) / 100);
    }

    @org.junit.jupiter.api.Test
    void getCTR() {
        assertEquals(1.267, (Math.round(KeyMetrics.getCTR(this.impressionLog, this.clickLog) *1000) * 1.0) / 1000);
    }

    @org.junit.jupiter.api.Test
    void getCPA() {
        assertEquals(49.765, (Math.round(KeyMetrics.getCPA(this.impressionLog, this.clickLog, this.serverLog) *1000) * 1.0) / 1000);
    }

    @org.junit.jupiter.api.Test
    void getCPC() {
        assertEquals(5.238, (Math.round(KeyMetrics.getCPC(this.impressionLog, this.clickLog) *1000) * 1.0) / 1000);
    }

    @org.junit.jupiter.api.Test
    void getCPM() {
        assertEquals(6635.334, (Math.round(KeyMetrics.getCPM(this.impressionLog, this.clickLog) *1000) * 1.0) / 1000);
    }

    @org.junit.jupiter.api.Test
    void getBounceRate() {
        assertEquals(0.316, (Math.round(KeyMetrics.getBounceRate(this.clickLog, this.serverLog, 1, 1) *1000) * 1.0) / 1000);
    }
}