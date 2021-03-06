package jahmm.calculators;

import jahmm.RegularHmmBase;
import jahmm.observables.ObservationEnum;
import jahmm.observables.Opdf;
import jahmm.observables.OpdfEnum;
import java.util.List;
import jutils.testing.AssertExtensions;
import jutlis.lists.ListArray;
import jutlis.tuples.Tuple3;
import org.junit.Test;

/**
 *
 * @author kommusoft
 */
public class ForwardBackwardCalculatorTest {

    public ForwardBackwardCalculatorTest() {
    }

    /**
     * Test of computeAll method, of class ForwardBackwardCalculator. Based on
     * the Umbrella world of Russell & Norvig 2010 Chapter 15 pp. 566
     */
    @Test
    public void testComputeAll() {
        double[][] trans = {{0.7d, 0.3d}, {0.3d, 0.7d}};
        double[][] exhaust = {{0.9d, 0.1d}, {0.2d, 0.8d}};
        Opdf<ObservationEnum<Events>> state0 = new OpdfEnum<>(Events.class, exhaust[0x00]);
        Opdf<ObservationEnum<Events>> state1 = new OpdfEnum<>(Events.class, exhaust[0x01]);
        double[] pi = {0.5d, 0.5d};
        @SuppressWarnings("unchecked")
        RegularHmmBase<ObservationEnum<Events>> hmm = new RegularHmmBase<>(pi, trans, state0, state1);
        @SuppressWarnings("unchecked")
        List<ObservationEnum<Events>> sequence = new ListArray<>(new ObservationEnum<>(Events.Umbrella), new ObservationEnum<>(Events.Umbrella), new ObservationEnum<>(Events.NoUmbrella), new ObservationEnum<>(Events.Umbrella), new ObservationEnum<>(Events.Umbrella));
        Tuple3<double[][], double[][], Double> abp = RegularForwardBackwardCalculatorBase.Instance.computeAll(hmm, sequence);
        double[][] a = abp.getItem1();
        double[][] b = abp.getItem2();
        double p = abp.getItem3();
        double[] expecteda = {0.8182, 0.8834, 0.1907, 0.7308, 0.8673};
        double[] expectedb = {0.5923, 0.3763, 0.6533, 0.6273};
        AssertExtensions.pushEpsilon(0.01);
        for (int t = 0x00; t < expecteda.length; t++) {
            AssertExtensions.assertEquals(a[t][0x00] * (1.0d - expecteda[t]), a[t][0x01] * expecteda[t]);
        }
        for (int t = 0x00; t < expectedb.length; t++) {
            AssertExtensions.assertEquals(b[t][0x00] * (1.0d - expectedb[t]), b[t][0x01] * expectedb[t]);
        }
        AssertExtensions.assertEquals(1.0d, b[expectedb.length][0x00]);
        AssertExtensions.assertEquals(1.0d, b[expectedb.length][0x01]);
        AssertExtensions.popEpsilon();
    }

    public enum Events {

        Umbrella,
        NoUmbrella
    }

}
