package com.LuDik.EvoAI;

public class Neuron {
	double[] weights;
	double bias;

	Neuron(int heightPreviousLayer) {
		// geeft elke input een random weight
		weights = new double[heightPreviousLayer];
		for (int i = 0; i < heightPreviousLayer; i++) {
			weights[i] = Math.random() * 2 - 1;
		}
		bias = Math.random() * 2 - 1;
	}

	Neuron(Neuron neuron, double deviation /* van 0 tot 1 waar 1 een volledig random weight maakt */) {
		this.weights = neuron.getWeights();
		for (int i = 0; i < this.weights.length; i++) {
			double random = Math.random() * 2 - 1;
			if (random < deviation) {
				weights[i] += (Math.random() * 2 - 1) * deviation;
			}
		}
		double random =Math.random();
		if (random < deviation) {
			deviation = (Math.random() * 2 - 1) * deviation;
			this.bias += deviation;
		}
	}

	// genereert de output van de neuron voor de gegeven inputs
	double processNeuron(double[] inputs) {
		double weightedSum = this.bias;
		for (int i = 0; i < inputs.length; i++) {

			weightedSum += inputs[i] * weights[i];

		}
		return Neuron.sigmoid(weightedSum);
	}

	// zorgt dat de neuron waardes tussen de -1 en 1 heeft
	// Dit wordt ook wel de activation function genoemd
	static double sigmoid(double input) {
		if (input < -10) {
			return -1;
		} else if (input > 10) {
			return 1;
		} else {
			return (input / (1 + Math.abs(input)));
		}
	}

	public double[] getWeights() {
		return weights;
	}

	public void setWeights(double[] weights) {
		this.weights = weights;
	}

	public double getBias() {
		return bias;
	}

	public void setBias(double bias) {
		this.bias = bias;
	}

}