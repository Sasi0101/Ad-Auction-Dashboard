import com.example.seg.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsTest {

    Functions functions;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        functions = new Functions();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void binPdBoundary() {
        //testing large n
        assertEquals(BigDecimal.valueOf(3.6437351549E-4).round(new MathContext(11, RoundingMode.HALF_UP)),
                this.functions.binomialPdCalc(1000,5000, BigDecimal.valueOf(0.185)).round(new MathContext(11, RoundingMode.HALF_UP)));
        //testing small n, x and p
        assertEquals(BigDecimal.valueOf(1.0).round(new MathContext(11, RoundingMode.HALF_UP)),
                this.functions.binomialPdCalc(0,1,BigDecimal.valueOf(0.000000001)).round(new MathContext(2, RoundingMode.HALF_UP)));
        //testing invalid variables (x > n)
        assertEquals(0,this.functions.binomialPdCalc(1000, 4, BigDecimal.valueOf(1)).intValue());
    }

    @org.junit.jupiter.api.Test
    void poisPdBoundary() {
        //large values of x, l
        assertEquals(BigDecimal.valueOf(0).round(new MathContext(11, RoundingMode.HALF_UP)),
                this.functions.poissonPdCalc(5000,BigDecimal.valueOf(5000.0)).round(new MathContext(11, RoundingMode.HALF_UP)));
        //small values of x, l
        assertEquals(BigDecimal.valueOf(1.0).round(new MathContext(11, RoundingMode.HALF_UP)),
                this.functions.binomialPdCalc(0,1,BigDecimal.valueOf(0.000000001)).round(new MathContext(2, RoundingMode.HALF_UP)));
        //testing x >> l
        assertEquals(0,this.functions.poissonPdCalc(1000, BigDecimal.valueOf(0.0)).intValue());
    }

}
