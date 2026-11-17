package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Lab 10 - Section B4, step 1: minimal ALL-DEFS suite for
 * {@code DataFlowTargets.bonus(attendance, threshold)}.
 *
 * <p>Line numbers refer to src/main/java/mn/edu/must/sqat/DataFlowTargets.java.
 * Definitions: 31 (attendance, threshold), 35 (present), 36 (streak),
 * 37 (best), 38 (a), 40 (present), 41 (streak), 43 (best),
 * 46 (streak), 49 (score), 51 (score), 54 (score).
 *
 * <p>Why two tests and not one: the def of score at line 49 can reach a use
 * only when line 50 is false (otherwise line 51 redefines score first), while
 * the def at line 51 needs line 50 to be true. One execution cannot satisfy
 * both, so all-defs needs at least two executions. These two do it.
 */
class BonusTest {

    @Test
    @DisplayName("all-defs: present 5 < threshold 6 -> line 50 F, score(49) used at 54; covers 35,36,37,38,40,41,43,46,49")
    void allDefs_belowThreshold_usesScoreDefAt49() {
        assertEquals(3, DataFlowTargets.bonus(new int[]{0, 1, 1, 1, 1, 1}, 6));
    }

    @Test
    @DisplayName("all-defs: present 5 >= threshold 5 -> line 51 def, score(51) used at 54, score(54) used at 56")
    void allDefs_atThreshold_usesScoreDefAt51() {
        assertEquals(8, DataFlowTargets.bonus(new int[]{0, 1, 1, 1, 1, 1}, 5));
    }
}
