import com.example.seg.Functions;
import com.example.seg.PoissonCD;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class PoissonCDTest {

    Functions functions;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        functions = new Functions();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    void poissonCdCalc() {
        //large values of x, l
        Assertions.assertEquals(BigDecimal.valueOf(0).round(new MathContext(7, RoundingMode.HALF_UP)),
                PoissonCD.poissonCdCalc(5000,BigDecimal.valueOf(5000.0)).round(new MathContext(7, RoundingMode.HALF_UP)));
        //small values of x, l
        assertEquals(BigDecimal.valueOf(0.3678794).round(new MathContext(7, RoundingMode.HALF_UP)),
                PoissonCD.poissonCdCalc(0,BigDecimal.valueOf(1.0)).round(new MathContext(7, RoundingMode.HALF_UP)));
        //testing x >> l
        assertEquals(1,PoissonCD.poissonCdCalc(1000, BigDecimal.valueOf(0.0)).intValue());
    }
}