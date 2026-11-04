package DCS_FuzzyLab;

import java.util.ArrayList;

import Components.Activation;
import Components.Condition;
import Components.GuardMapping;
import Components.PetriNet;
import Components.PetriNetWindow;
import Components.PetriTransition;
import DataObjects.DataFuzzy;
import DataOnly.FLRS;
import DataOnly.FV;
import DataOnly.Fuzzy;
import DataOnly.PlaceNameWithWeight;
import Enumerations.FZ;
import Enumerations.LogicConnector;
import Enumerations.TransitionCondition;
import Enumerations.TransitionOperation;

/**
 * Fuzzy Lab 1 - exercise 2: FLETPN adder / subtractor.
 *
 * Built like the Inversor: two input places P1 (x1) and P2 (x2), one
 * transition T1 that fires the FLRS when both inputs hold a token, and two
 * output places P3 (x3) and P4 (x4).
 *
 * Mathematical model (all values in [-1, 1]):
 *   x3 = w1*x1 + w2*x2   clamped to [-1, 1]   (adder)
 *   x4 = w1*x1 - w2*x2   clamped to [-1, 1]   (subtractor)
 *
 * The weights w1, w2 are the arc weights P1->T1 and P2->T1. T1 multiplies each
 * input by its weight, fuzzifies it, applies the rule table below and
 * defuzzifies the result with the centre of gravity method.
 *
 * Inputs can also be sent while the net runs with GUIs.InputFuzzy
 * (place name P1 or P2, a float value, port NETWORK_PORT).
 */
public class AdderSubtractor {

	public static final int NETWORK_PORT = 1082; // the Inversor uses 1081

	/**
	 * Table 3: Adder/Subtractor FLRS. Rows are x1 = NL..PL, columns are
	 * x2 = NL..PL, every cell is FV(x3, x4).
	 */
	static FLRS adderSubtractorFlrs() {
		return new FLRS(
				// x1 = NL          x2: NL                  NM                    ZR                    PM                    PL
				new FV(FZ.NL, FZ.ZR), new FV(FZ.NL, FZ.NM), new FV(FZ.NL, FZ.NL), new FV(FZ.NM, FZ.NL), new FV(FZ.ZR, FZ.NL),
				// x1 = NM
				new FV(FZ.NL, FZ.PM), new FV(FZ.NL, FZ.ZR), new FV(FZ.NM, FZ.NM), new FV(FZ.ZR, FZ.NL), new FV(FZ.PM, FZ.NL),
				// x1 = ZR
				new FV(FZ.NL, FZ.PL), new FV(FZ.NM, FZ.PM), new FV(FZ.ZR, FZ.ZR), new FV(FZ.PM, FZ.NM), new FV(FZ.PL, FZ.NL),
				// x1 = PM
				new FV(FZ.NM, FZ.PL), new FV(FZ.ZR, FZ.PL), new FV(FZ.PM, FZ.PM), new FV(FZ.PL, FZ.ZR), new FV(FZ.PL, FZ.NM),
				// x1 = PL
				new FV(FZ.ZR, FZ.PL), new FV(FZ.PM, FZ.PL), new FV(FZ.PL, FZ.PL), new FV(FZ.PL, FZ.PM), new FV(FZ.PL, FZ.ZR));
	}

	/** Builds the FLETPN with the initial tokens x1, x2 and the arc weights w1, w2. */
	public static PetriNet build(float x1, float w1, float x2, float w2) {
		PetriNet pn = new PetriNet();
		pn.PetriNetName = "AdderSubtractor";
		pn.NetworkPort = NETWORK_PORT;

		FLRS flrs2x2 = adderSubtractorFlrs();

		DataFuzzy p1 = new DataFuzzy();
		p1.SetName("P1");
		p1.SetValue(new Fuzzy(x1));
		pn.PlaceList.add(p1);

		DataFuzzy p2 = new DataFuzzy();
		p2.SetName("P2");
		p2.SetValue(new Fuzzy(x2));
		pn.PlaceList.add(p2);

		DataFuzzy p3 = new DataFuzzy();
		p3.SetName("P3");
		pn.PlaceList.add(p3);

		DataFuzzy p4 = new DataFuzzy();
		p4.SetName("P4");
		pn.PlaceList.add(p4);

		// T1 ------------------------------------------------
		PetriTransition t1 = new PetriTransition(pn);
		t1.TransitionName = "T1";
		t1.InputPlaceName.add("P1");
		t1.InputPlaceName.add("P2");

		// guard T1: P1 != null AND P2 != null
		Condition T1Ct1 = new Condition(t1, "P1", TransitionCondition.NotNull);
		Condition T1Ct2 = new Condition(t1, "P2", TransitionCondition.NotNull);
		T1Ct1.SetNextCondition(LogicConnector.AND, T1Ct2);

		GuardMapping grdT1 = new GuardMapping();
		grdT1.condition = T1Ct1;

		ArrayList<PlaceNameWithWeight> input = new ArrayList<>();
		input.add(new PlaceNameWithWeight("P1", w1));
		input.add(new PlaceNameWithWeight("P2", w2));

		ArrayList<String> twoOutput = new ArrayList<>();
		twoOutput.add("P3"); // x3 = w1*x1 + w2*x2
		twoOutput.add("P4"); // x4 = w1*x1 - w2*x2

		grdT1.Activations.add(new Activation(t1, flrs2x2, input, TransitionOperation.FLRS, twoOutput));
		t1.GuardMappingList.add(grdT1);

		t1.Delay = 0;
		pn.Transitions.add(t1);

		pn.Delay = 3000;
		return pn;
	}

	public static void main(String[] args) {
		// exercise 3 values: x1 = 0.2, w1 = 1, x2 = 0.3, w2 = 2  ->  x3 ~ 0.8, x4 = -0.4
		PetriNet pn = build(0.2F, 1F, 0.3F, 2F);
		adderSubtractorFlrs().Print();

		System.out.println("AdderSubtractor started \n ------------------------------");

		PetriNetWindow frame = new PetriNetWindow(false);
		frame.petriNet = pn;
		frame.setVisible(true);
	}
}
