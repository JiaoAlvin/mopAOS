/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aos.operator;

import aos.creditassigment.AbstractSetContribution;
import java.util.Map;
import aos.operatorselectors.OperatorSelector;

/**
 * AOS strategy for set-contribution type credit assignment strategies.
 *
 * @author nozomihitomi
 */
public class AOSVariationSC extends AbstractAOSVariation {
   
    public AOSVariationSC(OperatorSelector operatorSelector, AbstractSetContribution creditAssignment, int initialNFE) {
        super(operatorSelector, creditAssignment, initialNFE);
    }

    @Override
    protected Map<String, Double> computeCredits() {
        AbstractSetContribution creditAssignment = (AbstractSetContribution)getCreditAssignment();
        return creditAssignment.compute(getOperatorSelector().getOperatorNames());
    }

    @Override
    protected void internalReset() {
        //nothing to reset
    }

}
