# Fuzzy Lab 1 – FLETPN (Fuzzy Logic Enhanced Time Petri Nets)

- **Lab PDF:** `docs/Fuzzy Lab 1.pdf` (local only).
- **Solutions:** [SOLUTIONS.md](SOLUTIONS.md) has the test tables, the class diagram, the hand calculation and the
  answers to the verification questions.
- **Depends on:** [`framework/all-petri-nets`](../framework/all-petri-nets/ORIGIN.md).

## Provenance

| File | Origin | Changes vs. teacher |
|---|---|---|
| `src/DCS_FuzzyLab/Inversor.java` | teacher, [All_Petri_Nets_Framework](https://github.com/dahliajanabi/All_Petri_Nets_Framework) `src/DCS_FuzzyLab/Inversor.java`, commit `705367f` | none |
| `src/DCS_FuzzyLab/AdderSubtractor.java` | **mine** | exercise 2, built on the Inversor's structure, with the FLRS from Table 3 |
| `SOLUTIONS.md` | **mine** | exercises 1–3 and the verification questions |

## Exercises → files
| Exercise | Answer |
|---|---|
| 1. Test the Inversor for different x1, x2, w1, w2 | `Inversor.java` and the table in SOLUTIONS.md §1 |
| 2. Adder/subtractor FLETPN, class diagram, tests | `AdderSubtractor.java` and SOLUTIONS.md §2 |
| 3. Hand calculation: x1 = 0.2, w1 = 1, x2 = 0.3, w2 = 2 | SOLUTIONS.md §3 (x3 = 0.76 ≈ 0.8, x4 = −0.4) |
| Verification of knowledge 1–5 | SOLUTIONS.md, last section |

## Running
- **Inversor:** runs on port 1081. Run `DCS_FuzzyLab.Inversor` and press Start.
- **AdderSubtractor:** runs on port 1082. Run `DCS_FuzzyLab.AdderSubtractor` and press Start.
- **New inputs at run time:** run `GUIs.InputFuzzy` (it is in the framework module). Enter the place name
  (`P1` or `P2`), a value and the net's port.
