/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hh.nextheuristic;

import hh.creditdefinition.Credit;
import hh.creditrepository.ICreditRepository;
import java.util.HashMap;
import org.moeaframework.core.Variation;

/**
 * Interface to control methods used to select or generate next heuristic(s) to 
 * be used in hyper-heuristic
 * @author nozomihitomi
 */
public interface INextHeuristic{
    
    /**
     * Method to select or generate the next heuristic based on some selection 
     * or generation method
     * @return the next heuristic to be applied
     */
    public Variation nextHeuristic();
    
    /**
     * Method to update the selector's or generator's internal probabilities
     * @param heuristic the heuristic who just received credit
     * @param credit the credit received by the heuristic
     */
    public void update(Variation heuristic, Credit credit);
    
    /**
     * Method to replace the selector's or generator's credit repository 
     * @param creditRepo the new credit repository
     */
    public void update(ICreditRepository creditRepo);
    
    /**
     * Resets all stored history, qualities and credits
     */
    public void reset();
    
    /**
     * Gets the current quality of each heuristic stored
     * @return the current quality for each heuristic stored
     */
    public HashMap<Variation,Double> getQualities();
    
    /**
     * Returns the latest credit received by each heuristic
     * @return the latest credit received by each heuristic
     */
    public HashMap<Variation,Credit> getLatestCredits();
    
    /**
     * Returns the number of times nextHeuristic() has been called
     * @return the number of times nextHeuristic() has been called
     */
    public int getNumberOfIterations();
}
