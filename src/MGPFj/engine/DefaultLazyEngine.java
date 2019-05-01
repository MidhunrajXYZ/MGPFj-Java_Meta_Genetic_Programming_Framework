package MGPFj.engine;

import MGPFj.chromosome.Node;
import MGPFj.chromosome.RankedCandidate;
import MGPFj.fitness.FitnessFunction;
import MGPFj.genetic_operators.GeneticOperator;
import MGPFj.genetic_operators.crossover.GBC;
import MGPFj.genetic_operators.mutation.GBM;
import MGPFj.genetic_operators.reproduction.DirectCopy;
import MGPFj.grammar.CFG;
import MGPFj.initialize.GBIM;
import MGPFj.initialize.Initializer;
import MGPFj.select.Selector;
import MGPFj.select.TournamentSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DefaultLazyEngine {
    private final CFG cfg;
    private final Initializer initializer;
    private final FitnessFunction fitnessFunction;
    private final GeneticOperator[] operators;
    private final Integer[] probabilities;

    private final int maxDepth;
    private final int popSize;
    private final int generations;
    private final boolean terminateOnReachingMaxFitness;
    private final Random random;

    int mut = 0;

    private DefaultLazyEngine(CFG cfg,
                              Initializer initializer,
                              FitnessFunction fitnessFunction,
                              GeneticOperator[] operators,
                              Integer[] probabilities,
                              boolean terminateOnReachingMaxFitness, int maxDepth, int popSize, int generations) {
        this.cfg = cfg;
        this.initializer = initializer;
        this.fitnessFunction = fitnessFunction;
        this.operators = operators;
        this.probabilities = probabilities;
        this.terminateOnReachingMaxFitness = terminateOnReachingMaxFitness;

        this.maxDepth = maxDepth;
        this.popSize = popSize;
        this.generations = generations;

        random = new Random();
    }


    public RankedCandidate[] run(boolean loggerEnabled) {

        //creating initial population
        List<Node> currentPopulation =  initializer.generate(0, popSize, null);

        RankedCandidate[] rankedCandidates;
        int currentGen = 0;


        //iteration
        do {
            //fitness measure
            rankedCandidates = RankedCandidate.rankAndSort(currentPopulation, fitnessFunction);

            //printing the current ranked population
            if (loggerEnabled) {
                for (RankedCandidate candidate : rankedCandidates) {
                    System.out.println(candidate.getFitness() + "::: " + candidate.getNode().getTreeNotation());
                }
                System.out.println("currentGen: " + currentGen);
                System.out.println("No of individuals: " + rankedCandidates.length);
            }

            //termination check
            if (terminateOnReachingMaxFitness && rankedCandidates[0].getFitness() == 0.0) {
                break;
            }

            //selection
            Selector selector = new TournamentSelector(rankedCandidates);

            //Genetic Operators
            currentPopulation = applyGeneticOperators(selector);

            currentGen ++;

        } while (currentGen <= generations);

        System.out.println("Mutation: " + mut);

        return rankedCandidates;

    }

    /**
     * Applies genetic operators with the given probabilities and creates a new generation of size popSize.
     * @param selector the Selector which provides the nodes.
     */
    private List<Node> applyGeneticOperators(Selector selector) {
        List<Node> resultNodes = new ArrayList<Node>();

        while (resultNodes.size() < popSize) {

            //getting 2 different nodes for evolve
            List<Node> selectedNodes = new ArrayList<Node>();
            selectedNodes.add(selector.next());
            while (selectedNodes.size() != 2) {
                Node newNode = selector.next();
                if (newNode == selectedNodes.get(0)) continue;

                selectedNodes.add(newNode);
            }

            //apply genetic operators based on the probabilities.
            int p = random.nextInt(100);
            for (int i = 0; i < this.probabilities.length; i++) {

                if (p < probabilities[i]) {
                    resultNodes.addAll(this.operators[i].evolve(selectedNodes));
                    if (this.operators[i] instanceof GBM) {
                        mut++;
                    }
                }

            }

        }

        return resultNodes;
    }


    //Creator stuff

    public static DefaultEngineLazyCreator start(CFG cfg, FitnessFunction fitnessFunction, int maxDepth, int popSize, int generations) {
        return new DefaultEngineLazyCreator(cfg, fitnessFunction, maxDepth, popSize, generations);
    }

    public static DefaultEngineLazyCreator start(CFG cfg, FitnessFunction fitnessFunction) {
        return new DefaultEngineLazyCreator(cfg, fitnessFunction, 5, 100, 100);
    }


    public static class DefaultEngineLazyCreator {
        private final CFG cfg;
        private final FitnessFunction fitnessFunction;
        private final int maxDepth;
        private final int popSize;
        private final int generations;

        private Initializer initializer;
        private GeneticOperator[] operators;
        private Integer[] probabilities;
        private boolean terminateOnReachingMaxFitness; //stop iteration.


        private DefaultEngineLazyCreator(CFG cfg, FitnessFunction fitnessFunction, int maxDepth, int popSize, int generations) {
            this.cfg = cfg;
            this.fitnessFunction = fitnessFunction;
            this.maxDepth = maxDepth;
            this.popSize = popSize;
            this.generations = generations;

            this.initializer = new GBIM(cfg, maxDepth);
            //default genetic operators::
            GeneticOperator reproduction = new DirectCopy();
            GeneticOperator crossover = new GBC(cfg, maxDepth);
            GeneticOperator mutation = new GBM(cfg, maxDepth);

            this.operators = new GeneticOperator[]{reproduction, crossover, mutation};
            this.probabilities = new Integer[]{5, 90, 2};

            this.terminateOnReachingMaxFitness = false;
        }

        public DefaultEngineLazyCreator setInitializer (Initializer initializer) {
            this.initializer = initializer;
            return this;
        }

        /**
         * This will change the probabilities of the default genetic operators. <p/>
         * [NODE: do not call this method if you already specified your custom genetic operators.]
         * @param reproductionProbability probability value for default reproduction operation
         * @param crossoverProbability probability value for default crossover operation
         * @param mutationProbability probability value for default mutation operation
         */
        public DefaultEngineLazyCreator setDefaultProbabilities(int reproductionProbability, int crossoverProbability,
                                                                int mutationProbability){
            this.probabilities[0] = reproductionProbability;
            this.probabilities[1] = crossoverProbability;
            this.probabilities[2] = mutationProbability;

            return this;
        }

        /**
         * This will replace default genetic operators.
         * @param geneticOperators
         * @param probabilities
         * @return
         */
        public DefaultEngineLazyCreator setGeneticOperators(GeneticOperator[] geneticOperators,
                                                            Integer[] probabilities) {

            if (geneticOperators.length != probabilities.length) {
                throw new RuntimeException("length of GeneticOperators and probabilities are different");
            }

            this.operators = geneticOperators;
            this.probabilities = probabilities;

            return this;
        }

        public DefaultEngineLazyCreator setTerminateOnMaxFitness(boolean terminateOnMaxFitness) {
            this.terminateOnReachingMaxFitness = terminateOnMaxFitness;

            return this;
        }

        public DefaultLazyEngine finish() {
            return new DefaultLazyEngine(cfg, initializer, fitnessFunction, operators,
                    probabilities, terminateOnReachingMaxFitness, maxDepth, popSize, generations);
        }
    }


}
