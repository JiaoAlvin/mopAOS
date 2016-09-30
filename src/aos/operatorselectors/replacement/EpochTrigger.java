/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aos.operatorselectors.replacement;

import java.util.HashMap;
import org.moeaframework.core.Variation;

/**
 * This replacement trigger will detect when an epoch has passed. Each epoch is
 * defined with a fixed number of function evaluations
 *
 * @author nozomihitomi
 */
public class EpochTrigger implements ReplacementTrigger {

    /**
     * The number of evaluations that define one epoch
     */
    private final int epochLength;

    /**
     * The evaluation count when the epochtrigger was triggered last
     */
    private int lastTriggeredEpoch;

    /**
     * The constructor to create a new EpochTrigger that detects if an epoch has
     * elapsed.
     *
     * @param epochLength
     */
    public EpochTrigger(int epochLength) {
        this.epochLength = epochLength;
        this.lastTriggeredEpoch = 0;
    }

    /**
     * Returns true if the current number of evaluations belongs to the next
     * epoch
     *
     * @param nevals
     * @param qualities
     * @return
     */
    @Override
    public boolean checkTrigger(int nevals, HashMap<Variation, Double> qualities) {
        if (Math.floorDiv(nevals, epochLength) > lastTriggeredEpoch) {
            this.lastTriggeredEpoch = nevals;
            return true;
        } else {
            return false;
        }
    }
}
