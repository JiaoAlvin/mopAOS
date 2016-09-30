/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aos.nextoperator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moeaframework.core.ParallelPRNG;
import org.moeaframework.core.Variation;

/**
 * This abstract implements the interface INextHeuristic. Classes that extend
 * this abstract class are required to have some credit repository
 *
 * @author nozomihitomi
 */
public abstract class AbstractOperatorSelector implements INextOperator {

    /**
     * Random number generator for selecting operators.
     */
    protected final ParallelPRNG pprng = new ParallelPRNG();

    /**
     * The number of times nextHeuristic() is called
     */
    private int iterations;

    /**
     * Hashmap to store the qualities of the operators
     */
    protected HashMap<Variation, Double> qualities;

    /**
     * The operators from which the selector can choose from
     */
    protected Collection<Variation> operators;

    /**
     * Constructor requires a credit repository that stores credits earned by
     * operators.
     *
     * @param operators the collection of operators used to conduct search
     */
    public AbstractOperatorSelector(Collection<Variation> operators) {
        this.iterations = 0;
        this.qualities = new HashMap<>();
        this.operators = operators;
        resetQualities();
    }

    /**
     * Method finds the operator that maximizes the function to be maximized.
     * The function to be maximized may be related to credits or a function of
     * credits. If there are two or more operators that maximize the function
     * (i.e. there is a tie) a random operator will be selected from the tied
     * maximizing operators
     *
     * @param operators the set of operators to maximize over
     * @return the operator that maximizes the function2maximize
     */
    protected Variation argMax(Collection<Variation> operators) {
        Iterator<Variation> iter = operators.iterator();
        ArrayList<Variation> ties = new ArrayList();
        Variation leadOperator = null;
        double maxVal = Double.NEGATIVE_INFINITY;
        try {
            while (iter.hasNext()) {
                Variation operator_i = iter.next();
                if (leadOperator == null) {
                    leadOperator = operator_i;
                    maxVal = function2maximize(operator_i);
                    continue;
                }
                if (function2maximize(operator_i) > maxVal) {
                    maxVal = function2maximize(operator_i);
                    leadOperator = operator_i;
                    ties.clear();
                } else if (function2maximize(operator_i) == maxVal) {
                    ties.add(operator_i);
                }
            }
        //if there are any ties in the credit score, select randomly (uniform 
            //probability)from the operators that tied at lead
            if (!ties.isEmpty()) {
                leadOperator = getRandomOperator(ties);
            }
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AbstractOperatorSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return leadOperator;
    }

    /**
     * The function to be maximized by argMax(). The function to be maximized
     * may be related to credits or a function of credits. If an
     * IHeuristicSeletor uses this method, it should be overridden
     *
     * @param operator input to the function
     * @return the value of the function with the given input
     * @throws java.lang.NoSuchMethodException If this method is used without
     * being overridden, then it throws a NoSuchMethodException
     */
    protected double function2maximize(Variation operator) throws NoSuchMethodException {
        throw new NoSuchMethodException("Need to override this method");
    }

    /**
     * Selects a random operator from a collection of operators with uniform
     * probability
     *
     * @param operators the collection to draw a random operator from
     * @return the randomly selected operator
     */
    protected Variation getRandomOperator(Collection<Variation> operators) {
        return pprng.nextItem(new ArrayList<>(operators));
    }

    /**
     * Increments the number of times nextHeuristic() has been called by one
     */
    protected void incrementIterations() {
        iterations++;
    }

    /**
     * Returns the number of times nextOperator() has been called
     *
     * @return the number of times nextOperator() has been called
     */
    @Override
    public int getNumberOfIterations() {
        return iterations;
    }

    /**
     * Resets stored qualities and iteration count
     */
    @Override
    public void reset() {
        resetQualities();
        iterations = 0;
    }

    /**
     * Clears qualities and resets them to 0.
     */
    public final void resetQualities() {
        Iterator<Variation> iter = operators.iterator();
        while (iter.hasNext()) {
            //all operators have 0 quality at the beginning
            qualities.put(iter.next(), 0.0);
        }
    }

    @Override
    public HashMap<Variation, Double> getQualities() {
        return qualities;
    }

    /**
     * Gets the operators available to the hyper-operator.
     *
     * @return
     */
    @Override
    public Collection<Variation> getOperators() {
        return operators;
    }

    /**
     * Checks the quality of the operator. If the quality becomes negative, it
     * is reset to 0.0. Only updates those operators that were just rewarded.
     */
    protected void checkQuality() {
        for (Variation operator : qualities.keySet()) {
            //if current quality becomes negative, adjust to 0
            double qual = qualities.get(operator);
            if (qual < 0.0 || Double.isNaN(qual)) {
                qualities.put(operator, 0.0);
            }
        }
    }

    /**
     * Removes an operator from the current set of operators
     *
     * @param operator operator to remove from the current set of operators
     * @return true of the specified operator was removed from the current set.
     * False if the operator was not removed or if the operator does not exist
     * in the current set
     */
    @Override
    public boolean removeOperator(Variation operator){
        qualities.remove(operator);
        return operators.remove(operator);
    }

    /**
     * Adds an operator to the current set of operators
     *
     * @param operator to add to the current set of operators
     * @return true if the operator was successfully added to the current set of
     * operators. False if the operator was not added, or if it already existed
     * in the current set
     */
    @Override
    public boolean addOperator(Variation operator){
        if(!qualities.containsKey(operator)){
            qualities.put(operator, 0.0);
            operators.add(operator);
            return true;
        }else
            return false;
    }

}
