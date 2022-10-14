import com.example.seg.ClickLog;
import com.example.seg.ImpressionLog;
import com.example.seg.ServerLog;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ClickLogCleanDataTest {
    ImpressionLog impressionLog;
    ClickLog clickLog;

    @BeforeEach
    void setUp() throws IOException, CsvException {
        this.impressionLog = new ImpressionLog("C:\\Users\\jmboy\\IdeaProjects\\segagain\\SEG\\src\\main\\tests\\impression_log_test2.csv");
        this.clickLog = new ClickLog("C:\\Users\\jmboy\\IdeaProjects\\segagain\\SEG\\src\\main\\tests\\click_log_test2.csv");
    }

    @Test
    void clean_data() {
        clickLog.clean_data(impressionLog);
        assertEquals(13, clickLog.getLength());
    }
}