/* Copyright 2009-2015 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.core.operator.real;

import java.io.Serializable;
import org.moeaframework.core.ParallelPRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;

/**
 * 
 * Differential evolution (DE) variation operator.  Differential evolution
 * works by randomly selecting three distinct individuals from a population.  A
 * difference vector is calculated between the first two individuals (shown as
 * the left-most arrow in the figure below), which is subsequently applied to
 * the third individual (shown as the right-most arrow in the figure below).
 * <p>
 * <img src="doc-files/DifferentialEvolution-1.png" 
 *      alt="Example DifferentialEvolution operator distribution" />
 * <p>
 * The scaling factor parameter adjusts the magnitude of the difference vector,
 * allowing the user to decrease or increase the magnitude in relation to the
 * actual difference between the individuals.  The crossover rate parameter 
 * controls the fraction of decision variables which are modified by the DE 
 * operator.  
 * <p>
 * References:
 * <ol>
 * <li>Storn and Price. "Differential Evolution - A Simple and Efficient
 * Heuristic for Global Optimization over Continuous Spaces." Journal of Global
 * Optimization, 11:341-359, 1997.
 * </ol>
 */
public class DifferentialEvolution4 implements Variation,Serializable {
    private static final long serialVersionUID = -1725319928392071901L;

	/**
	 * The crossover rate.
	 */
	private final double CR;

	/**
	 * The scaling factor or step size.
	 */
	private final double F;
        
        /**
	 * Secondary scaling factor or step size.
	 */
	private final double K;
        
        /**
         * parallel purpose random generator
         */
        private final ParallelPRNG pprng;

	/**
	 * Constructs a differential evolution operator with the specified crossover
	 * rate and scaling factor.
	 * 
	 * @param CR the crossover rate
	 * @param F the scaling factor
         * @param K secondary scaling factor
	 */
	public DifferentialEvolution4(double CR, double F,double K) {
		this.CR = CR;
		this.F = F;
                this.K = K;
                this.pprng = new ParallelPRNG();
	}

	/**
	 * Returns the crossover rate of this differential evolution operator.
	 * 
	 * @return the crossover rate
	 */
	public double getCrossoverRate() {
		return CR;
	}

	/**
	 * Returns the scaling factor of this differential evolution operator.
	 * 
	 * @return the scaling factor
	 */
	public double getScalingFactor() {
		return F;
	}

	@Override
	public int getArity() {
		return 5;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result = parents[0].copy();

		int jrand = pprng.nextInt(result.getNumberOfVariables());

		for (int j = 0; j < result.getNumberOfVariables(); j++) {
			if ((pprng.nextDouble() <= CR) || (j == jrand)) {
				RealVariable v0 = (RealVariable)result.getVariable(j);
				RealVariable v1 = (RealVariable)parents[1].getVariable(j);
				RealVariable v2 = (RealVariable)parents[2].getVariable(j);
				RealVariable v3 = (RealVariable)parents[3].getVariable(j);
                                RealVariable v4 = (RealVariable)parents[4].getVariable(j);

				double y = v4.getValue() + K * (v4.getValue() - v1.getValue()) + F * (v2.getValue() - v3.getValue());

				if (y < v0.getLowerBound()) {
					y = v0.getLowerBound();
				}

				if (y > v0.getUpperBound()) {
					y = v0.getUpperBound();
				}

				v0.setValue(y);
			}
		}

		return new Solution[] { result };
	}

    @Override
    public String toString() {
        return "DifferentialEvolution{" + "CR=" + CR + ", F=" + F + '}';
    }

}
