/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.rewarddefinition.fitnessindicator;

import java.util.ArrayList;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 *
 * @author nozomihitomi
 */
public class BinaryR2Indicator implements IBinaryIndicator {

    protected ArrayList<WtVector> wtVecs;

    protected final Solution referencePt;

    public BinaryR2Indicator(Solution referencePt, int numVecs) {
        this.referencePt = referencePt;
        initializeWts(numVecs);
    }

    /**
     *
     * @param popA can be the reference population
     * @param popB
     * @return
     */
    @Override
    public double compute(NondominatedPopulation popA, NondominatedPopulation popB) {
        double val = 0.0;
        for (WtVector vec : wtVecs) {
            val += popUtility(vec, popA) - popUtility(vec, popB);
        }
        return val / wtVecs.size();
    }

    /**
     * Returns the maximum value over all the solution utilities wrt to a weight
     * vector
     *
     * @param vec weight vector
     * @param pop
     * @return the utility of the nondominated population
     */
    protected double popUtility(WtVector vec, NondominatedPopulation pop) {
        double popUtil = Double.NEGATIVE_INFINITY;
        for (Solution solution : pop) {
            popUtil = Math.max(popUtil, solnUtility(vec, solution));
        }
        return popUtil;
    }

    /**
     * Computes the utility of a solution wrt to a weight vector using a
     * Tchebycheff function. Tchebycheff function: u_w(z) = -max{w_j*|z'_j -
     * z_j|} where w_j is the jth component of the weight vector, z' is the
     * reference point and z is the objective value.
     *
     * @param vec weight vector
     * @param solution
     * @return utility of a solution wrt to a weight vector
     */
    private double solnUtility(WtVector vec, Solution solution) {
        double solnUtil = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
            solnUtil = Math.max(solnUtil, vec.get(i) * Math.abs(referencePt.getObjective(i) - solution.getObjective(i)));
        }
        return -solnUtil;
    }

    @Override
    public double computeWRef(NondominatedPopulation popA, NondominatedPopulation refPop) {
        return compute(refPop, popA);
    }

    private void initializeWts(int numVecs) {
        // creates full factorial matrix. Code is based on 2013a Matlab 
        //fullfact(levels). Eliminate rows with sum != the number of vectors.
        int numObj = referencePt.getNumberOfObjectives();
        int numExp = (int)Math.pow(numVecs, numObj);
        int[][] experiments = new int[numExp][numObj];

        int ncycles = numExp;

        for (int k = 0; k < numObj; k++) {
            int numLevels4kthFactor = numObj;
            int nreps = numExp / ncycles;
            ncycles = ncycles / numLevels4kthFactor;
            int[] settingReps = new int[nreps * numLevels4kthFactor];
            int index = 0;
            for (int j = 0; j < numLevels4kthFactor; j++) {
                for (int i = 0; i < nreps; i++) {
                    settingReps[index] = j;
                    index++;
                }
            }
            index = 0;
            for (int j = 0; j < ncycles; j++) {
                for (int i = 0; i < settingReps.length; i++) {
                    experiments[index][k] = settingReps[i];
                    index++;
                }
            }
        }
        
        wtVecs = new ArrayList<>();
        //Find valid row vectors (ones that add up to numVecs) 
        for(int i=0;i<numExp;i++){
            double sum = 0.0;
            for(int j=0; j<numObj; j++){
                sum+=experiments[i][j];
            }
            if(sum==numVecs){
                double[] wts = new double[numObj];
                for(int k=0; k<numObj; k++){
                    wts[k] = ((double)experiments[i][k])/((double)numObj);
                }
                wtVecs.add(new WtVector(wts));
            }
        }
        
        
        
    }

    @Override
    public String toString() {
        return "BIR2";
    }

    protected class WtVector {

        /**
         * Weights for vector
         */
        private final double[] weights;

        public WtVector(double[] weights) {
            this.weights = weights;
        }

        public double get(int i) {
            return weights[i];
        }
    }
}
