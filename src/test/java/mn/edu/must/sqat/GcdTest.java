package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Lab 10 - Section A: all-DU-pairs tests for {@code DataFlowTargets.gcd(x, y)}.
 *
 * <p>Line numbers refer to src/main/java/mn/edu/must/sqat/DataFlowTargets.java:
 * 13 (def x, y), 15 (p-use y), 16 (c-use x, y / def tmp),
 * 17 (c-use y / def x), 18 (c-use tmp / def y), 20 (c-use x).
 *
 * <p>Covered DU pairs (def line -> use line):
 * x:   13-16, 13-20, 17-16, 17-20
 * y:   13-15, 13-16, 13-17, 18-15, 18-16, 18-17
 * tmp: 16-18
 */
class GcdTest {

    @Test
    @DisplayName("gcd(12,8)=4: loop runs 2x, covers x(13,16)(17,16)(17,20), y(13,15T)(13,16)(13,17)(18,15T)(18,16)(18,17), tmp(16,18)")
    void gcd_12_8_returns4() {
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }

    @Test
    @DisplayName("gcd(7,0)=7: loop body skipped, only pair x(13,20) and y(13,15F)")
    void gcd_7_0_returns7() {
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    @Test
    @DisplayName("gcd(0,5)=5: one iteration, x(13,16)(17,20), y(13,15T)(13,16)(13,17)(18,15F), tmp(16,18)")
    void gcd_0_5_returns5() {
        assertEquals(5, DataFlowTargets.gcd(0, 5));
    }

    @Test
    @DisplayName("gcd(0,0)=0: loop body skipped, x(13,20), y(13,15F)")
    void gcd_0_0_returns0() {
        assertEquals(0, DataFlowTargets.gcd(0, 0));
    }
}
