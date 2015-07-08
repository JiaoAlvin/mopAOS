/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.hyperheuristics;

import hh.creditaggregation.ICreditAggregationStrategy;
import hh.creditdefinition.Credit;
import hh.creditdefinition.DecayingCredit;
import hh.creditdefinition.ICreditDefinition;
import hh.creditdefinition.ParentBasedCredit;
import hh.creditdefinition.PopulationBasedCredit;
import hh.creditdefinition.aggregate.IAggregateCredit;
import hh.credithistory.CreditHistory;
import hh.creditrepository.CreditHistoryRepository;
import hh.creditrepository.ICreditRepository;
import hh.nextheuristic.INextHeuristic;
import hh.qualityhistory.HeuristicQualityHistory;
import hh.selectionhistory.HeuristicSelectionHistory;
import hh.selectionhistory.IHeuristicSelectionHistory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.lang3.ArrayUtils;
import org.moeaframework.algorithm.EpsilonMOEA;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.ParallelPRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 *
 * @author Nozomi
 */
public class HeMOEA extends EpsilonMOEA implements IHyperHeuristic {

    /**
     * The type of heuristic selection method
     */
    private final INextHeuristic heuristicSelector;

    /**
     * The Credit definition to be used that defines how much credit to receive
     * for certain types of solutions
     */
    private final ICreditDefinition creditDef;

    /**
     * The repository that will store all the credits earned by the heuristics
     */
    private ICreditRepository creditRepo;

    /**
     * the credit aggregation scheme used to process the credits from previous
     * iterations
     */
    private final ICreditAggregationStrategy creditAgg;

    /**
     * The history that stores all the heuristics selected by the hyper
     * heuristics. History can be extracted by getSelectionHistory(). Used for
     * analyzing the results to see the dynamics of heuristics selected
     */
    private IHeuristicSelectionHistory heuristicSelectionHistory;

    /**
     * The credit history of all heuristics at every iteration. Can be extracted
     * by getCreditHistory(). Used for analyzing the results to see the dynamics
     * of the instantaneous credits received
     */
    private CreditHistoryRepository creditHistory;

    /**
     * The set of heuristics that the hyper heuristic is able to work with
     */
    private final Collection<Variation> heuristics;

    /**
     * The selection operator.
     */
    private final Selection selection;

    /**
     * The learning rate for the decaying credit value
     */
    private final double alpha;

    /**
     * The history of the heuristics' qualities over time. Used for analyzing
     * the results to see the dynamics of the heuristic qualities
     */
    private HeuristicQualityHistory qualityHistory;

    /**
     * parallel purpose random generator
     */
    private final ParallelPRNG pprng;

    /**
     * Creates an instance of a hyper heuristic e-MOEA
     *
     * @param problem the problem to solve
     * @param population the type of population
     * @param archive the type of archive to implement
     * @param selection type of selection used to select higher fitness
     * individuals
     * @param initialization the method to initialize the population
     * @param heuristicSelector the heuristic selector to use in the
     * hyper-heuristic
     * @param creditDef the credit definition to score the performance of the
     * low-level heuristics
     * @param creditRepo the repository that will store all the credits earned
     * by the heuristics
     * @param creditAgg the credit aggregation scheme used to process the
     * credits from previous iterations
     * @param alpha the decay factor used for memory in the credit history [0,1]
     */
    public HeMOEA(Problem problem, Population population,
            EpsilonBoxDominanceArchive archive, Selection selection,
            Initialization initialization, INextHeuristic heuristicSelector,
            ICreditDefinition creditDef, ICreditRepository creditRepo,
            ICreditAggregationStrategy creditAgg, double alpha) {
        super(problem, population, archive, selection, null, initialization);

        checkHeuristics(heuristicSelector, creditRepo);
        this.heuristics = heuristicSelector.getHeuristics();

        this.selection = selection;
        this.heuristicSelector = heuristicSelector;
        this.creditRepo = creditRepo;
        this.creditDef = creditDef;
        this.creditAgg = creditAgg;
        this.alpha = alpha;
        this.heuristicSelectionHistory = new HeuristicSelectionHistory(heuristics);
        this.creditHistory = new CreditHistoryRepository(heuristics, new CreditHistory());
        this.qualityHistory = new HeuristicQualityHistory(heuristics);
        this.pprng = new ParallelPRNG();
    }

    /**
     * Checks to see if the heuristics in the INextHeuristic and the credit
     * repository match
     *
     * @param heuristicSelector
     * @param creditRepo
     */
    private void checkHeuristics(INextHeuristic heuristicSelector, ICreditRepository creditRepo) {
        Iterator<Variation> iter = heuristicSelector.getHeuristics().iterator();
        Collection<Variation> repoHeuristics = creditRepo.getHeuristics();
        while (iter.hasNext()) {
            Variation heur = iter.next();
            if (!repoHeuristics.contains(heur)) {
                throw new RuntimeException("Mismatch in heuristics in INextHeuristic and ICrediRepository:" + heur);
            }
        }
    }

    /**
     * An iterations of this algorithm will select the next heuristic to be
     * applied, select the parents for that heuristic and select one child to
     * get evaluated. For heuristics that create more than one offspring, a
     * random offspring will be selected with uniform probability
     */
    @Override
    public void iterate() {
        //select next heuristic
        Variation heuristic = heuristicSelector.nextHeuristic();

        Solution[] parents;

        if (archive.size() <= 1) {
            parents = selection.select(heuristic.getArity(), population);
        } else {
            parents = ArrayUtils.add(
                    selection.select(heuristic.getArity() - 1, population),
                    archive.get(pprng.nextInt(archive.size())));
        }

        pprng.shuffle(parents);

        Solution[] children = heuristic.evolve(parents);

        //RECONSIDER some other policy other than taking one random offspring
        Solution randChild = children[pprng.nextInt(children.length)];
        children = new Solution[]{randChild};

        if (creditDef.isImmediate()) {
            for (Solution child : children) {
                evaluate(child);

                double creditValue;
                switch (creditDef.getType()) {
                    case PARENT:
                        creditValue = ((ParentBasedCredit) creditDef).compute(child, parents, heuristic);
                        break;
                    case POPULATION:
                        creditValue = ((PopulationBasedCredit) creditDef).compute(child, population, heuristic);
                        break;
                    case ARCHIVE:
                        creditValue = ((PopulationBasedCredit) creditDef).compute(child, archive, heuristic);
                        break;
                    default:
                        throw new NullPointerException("Credit definition not "
                                + "recognized. Used " + creditDef.getType() + ".");
                }
                creditRepo.update(heuristic, new DecayingCredit(this.getNumberOfEvaluations(), creditValue, alpha));
                heuristicSelector.update(creditRepo, creditAgg);
                heuristicSelectionHistory.add(heuristic);
                updateCreditHistory();
                updateQualityHistory();
            }
        }

        //add all solutions to the population/archive
        for (Solution child : children) {
            addToPopulation(child);
            archive.add(child);
        }

        if (!creditDef.isImmediate()) {
            for (Solution child : children) {
                evaluate(child);
//                Map<String,Object> attribute = new HashMap();
                child.setAttribute("iteration", new SerializableVal(this.getNumberOfEvaluations()));
                child.setAttribute("heuristic", new SerializableVal(heuristic.getClass().getSimpleName()));
                child.setAttribute("alpha", new SerializableVal(alpha));
//                child.addAttributes(attribute);
            }

            HashMap<Variation,Credit> aggCredits;
            switch (creditDef.getType()) {
//                case PARENT:
//                    newCreditRepo = ((IAggregateCredit)creditDef).computeAll(children, parents,heuristic).get(0);
//                    break;
                case POPULATION:
                    aggCredits = ((IAggregateCredit) creditDef).compute(population, heuristics, this.getNumberOfEvaluations());
                    break;
                case ARCHIVE:
                    aggCredits = ((IAggregateCredit) creditDef).compute(archive, heuristics, this.getNumberOfEvaluations());
                    break;
                default:
                    throw new NullPointerException("Credit definition not "
                            + "recognized. Used " + creditDef.getType() + ".");
            }
            creditRepo.update(aggCredits);
            heuristicSelector.update(creditRepo, creditAgg);
            heuristicSelectionHistory.add(heuristic);
            updateCreditHistory();
            updateQualityHistory();
        }
    }

    /**
     * Updates the credit history every iteration for each heuristic according
     * to the INextHeuristic class used
     */
    private void updateCreditHistory() {
        for (Variation heuristic : heuristics) {
            creditHistory.update(heuristic, creditRepo.getLatestCredit(heuristic));
        }
    }

    /**
     * Updates the quality history every iteration for each heuristic according
     * to the INextHeuristic class used
     */
    private void updateQualityHistory() {
        HashMap<Variation, Double> currentQualities = heuristicSelector.getQualities();
        for (Variation heuristic : heuristics) {
            qualityHistory.add(heuristic, currentQualities.get(heuristic));
        }
    }

    /**
     * Returns the ordered history of heuristics that were selected
     *
     * @return The ordered history of heuristics that were selected
     */
    @Override
    public IHeuristicSelectionHistory getSelectionHistory() {
        return heuristicSelectionHistory;
    }

    /**
     * Returns the entire history of credits for each heuristic.
     *
     * @return the entire history of credits for each heuristic.
     */
    @Override
    public CreditHistoryRepository getCreditHistory() {
        return creditHistory;
    }

    /**
     * gets the quality history stored for each heuristic in the hyper-heuristic
     *
     * @return
     */
    @Override
    public HeuristicQualityHistory getQualityHistory() {
        return qualityHistory;
    }

    /**
     * Reset the hyperheuristic. Clear all selection history and the credit
     * repository
     */
    @Override
    public void reset() {
        heuristicSelectionHistory.clear();
        heuristicSelector.reset();
        numberOfEvaluations = 0;
        creditHistory.clear();
        qualityHistory.clear();
    }

    @Override
    public ICreditDefinition getCreditDefinition() {
        return creditDef;
    }

    @Override
    public INextHeuristic getNextHeuristicSupplier() {
        return heuristicSelector;
    }

    /**
     * Returns the latest credit received by each heuristic
     *
     * @return the latest credit received by each heuristic
     */
    @Override
    public HashMap<Variation, Credit> getLatestCredits() {
        HashMap<Variation, Credit> out = new HashMap<>();
        Iterator<Variation> iter = creditRepo.getHeuristics().iterator();
        while (iter.hasNext()) {
            Variation heuristic = iter.next();
            out.put(heuristic, creditRepo.getLatestCredit(heuristic));
        }
        return out;
    }

}
