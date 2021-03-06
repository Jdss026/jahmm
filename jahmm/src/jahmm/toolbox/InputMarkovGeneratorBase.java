package jahmm.toolbox;

import jahmm.InputHmm;
import jahmm.observables.InputObservationTuple;
import jahmm.observables.Observation;
import jahmm.observables.Opdf;
import jahmm.observables.OpdfDiscrete;
import jahmm.observables.TypedObservation;
import java.util.logging.Logger;

/**
 *
 * @author kommusoft
 * @param <TObs> The type of observations regarding the Hidden Markov Model.
 * @param <TIn> The type of interactions regarding the Hidden Markov Model.
 */
public class InputMarkovGeneratorBase<TObs extends Observation, TIn, THmm extends InputHmm<TObs, TIn, THmm>> extends MarkovGeneratorBase<TObs, InputObservationTuple<TIn, TObs>, THmm> implements InputMarkovGenerator<TObs, TIn, THmm> {

    private static final Logger LOG = Logger.getLogger(InputMarkovGeneratorBase.class.getName());

    private final Opdf<? extends TypedObservation<TIn>> inputDistribution;

    /**
     * Creates a new instance of an InputMarkovGenerator base with a given
     * Hidden Markov model and distribution on the inputs.
     *
     * @param hmm The given hidden Markov Model to generate a list of
     * observations from.
     */
    public InputMarkovGeneratorBase(THmm hmm) {
        this(hmm, new OpdfDiscrete<>(hmm.getRegisteredInputs()));
    }

    /**
     * Creates a new instance of an InputMarkovGenerator base with a given
     * Hidden Markov model and distribution on the inputs.
     *
     * @param hmm The given hidden Markov Model to generate a list of
     * observations from.
     * @param inputDistribution Gets the input distribution of the input values.
     */
    public InputMarkovGeneratorBase(THmm hmm, Opdf<? extends TypedObservation<TIn>> inputDistribution) {
        super(hmm);
        this.inputDistribution = inputDistribution;
    }

    /**
     * Generates a new (pseudo) random observation.
     *
     * @return The generated observation.
     */
    @Override
    public InputObservationTuple<TIn, TObs> interaction() {
        TIn input = this.getInputDistribution().generate().getTag();
        THmm ihmm = this.getHmm();
        int inputIndex = ihmm.getInputIndex(input);
        TObs o = ihmm.getOpdf(stateNb, inputIndex).generate();
        double rand = Math.random();
        for (int j = 0; j < ihmm.nbStates() - 1; j++) {
            if ((rand -= ihmm.getAixj(stateNb, inputIndex, j)) < 0) {
                stateNb = j;
                return new InputObservationTuple<>(input, o);
            }
        }
        stateNb = ihmm.nbStates() - 1;
        return new InputObservationTuple<>(input, o);
    }

    @Override
    public Opdf<? extends TypedObservation<TIn>> getInputDistribution() {
        return this.inputDistribution;
    }

}
