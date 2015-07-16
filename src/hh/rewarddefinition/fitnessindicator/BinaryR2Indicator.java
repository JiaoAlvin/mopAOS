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


    /**
     * Constructor to initialize the weight vectors
     * @param numbObjs number of objectives
     * @param numVecs number of vectors
     */
    public BinaryR2Indicator(int numbObjs,int numVecs) {
        initializeWts(numbObjs,numVecs);
    }

    /**
     *
     * @param popA can be the reference population
     * @param popB
     * @return
     */
    @Override
    public double compute(NondominatedPopulation popA, NondominatedPopulation popB, Solution refPt) {
        double val = 0.0;
        for (WtVector vec : wtVecs) {
            val += popUtility(vec, popA,refPt) - popUtility(vec, popB,refPt);
        }
        return val / wtVecs.size();
    }

    /**
     * In this implementation the order of the inputs matter. formula based on
     * "R2-IBEA: R2 Indicator Based Evolutionary Algorithm for Multiobjective
     * Optimization"
     *
     * @param solnA
     * @param solnB
     * @return
     */
    @Override
    public double compute(Solution solnA, Solution solnB, Solution refPt) {
        NondominatedPopulation singlePop = new NondominatedPopulation();
        singlePop.add(solnA);
        NondominatedPopulation doublePop = new NondominatedPopulation();
        doublePop.add(solnA);
        double valA = 0.0;
        double valB = 0.0;
        for (WtVector vec : wtVecs) {
            valA += popUtility(vec, singlePop, refPt);
            valB += popUtility(vec, doublePop, refPt);
        }
        return valA-valB;
    }

    /**
     * Returns the maximum value over all the solution utilities wrt to a weight
     * vector
     *
     * @param vec weight vector
     * @param pop
     * @param refPt reference point
     * @return the utility of the nondominated population
     */
    protected double popUtility(WtVector vec, NondominatedPopulation pop,Solution refPt) {
        double popUtil = Double.NEGATIVE_INFINITY;
        for (Solution solution : pop) {
            popUtil = Math.max(popUtil, solnUtility(vec, solution, refPt));
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
     * @param refPt reference point
     * @return utility of a solution wrt to a weight vector
     */
    private double solnUtility(WtVector vec, Solution solution, Solution refPt) {
        double solnUtil = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
            solnUtil = Math.max(solnUtil, vec.get(i) * Math.abs(refPt.getObjective(i) - solution.getObjective(i)));
        }
        return -solnUtil;
    }

    @Override
    public double computeWRef(NondominatedPopulation popA, NondominatedPopulation refPop, Solution refPt) {
        return compute(refPop, popA, refPt);
    }

    private void initializeWts(int numObj,int numVecs) {
        // creates full factorial matrix. Code is based on 2013a Matlab 
        //fullfact(levels). Eliminate rows with sum != the number of vectors.
        int numExp = (int) Math.pow(numVecs, numObj);
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
        for (int i = 0; i < numExp; i++) {
            double sum = 0.0;
            for (int j = 0; j < numObj; j++) {
                sum += experiments[i][j];
            }
            if (sum == numVecs) {
                double[] wts = new double[numObj];
                for (int k = 0; k < numObj; k++) {
                    wts[k] = ((double) experiments[i][k]) / ((double) numObj);
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
