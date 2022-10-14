import com.example.seg.BinomialCD;
import com.example.seg.Functions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class BinomialCDTest {

    Functions functions;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        functions = new Functions();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    void binomialCdCalc() {
        //testing large n
        Assertions.assertEquals(BigDecimal.valueOf(0.99679135889).round(new MathContext(11, RoundingMode.HALF_UP)),
                BinomialCD.binomialCdCalc(1000,5000, BigDecimal.valueOf(0.185)).round(new MathContext(11, RoundingMode.HALF_UP)));
        //testing small n x and p
        assertEquals(BigDecimal.valueOf(0.999999999).round(new MathContext(11, RoundingMode.HALF_UP)),
                BinomialCD.binomialCdCalc(0,1,BigDecimal.valueOf(0.000000001)).round(new MathContext(9, RoundingMode.HALF_UP)));
        //testing invalid variables (x > n)
        assertEquals(0,this.functions.binomialPdCalc(1000, 4, BigDecimal.valueOf(1)).intValue());
    }
}