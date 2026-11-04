# All Petri Nets Framework (teacher's code)

- **Source:** [dahliajanabi/All_Petri_Nets_Framework](https://github.com/dahliajanabi/All_Petri_Nets_Framework),
  commit `705367faaafe5c3dff921a0d9c8e83548ae84a57` (2026-10-22), folder `All_Petri_Nets_Framework/src/`.
- **Changes:** none. Do not edit these files. A lab that needs different behaviour gets its own class in the lab
  folder.

## Packages copied
The framework packages that the FLETPN labs need, and the packages they depend on:

`Components`, `DataObjects`, `DataOnly`, `Enumerations`, `GUIs`, `Interfaces`, `Utilities`, `MetricsClasses`,
`OETPNGraphics`, `PetriDataPackage`

Not copied: the teacher's experiment packages (`DCS_FuzzyLab`, `DCS_OETPN_UVS`, `DCS_TwoLakeSystem`, `HeatPipe`,
`OETPNExperiments`, `PSO`, `RTS_LAB`, `TestFuzzy`). A lab that is based on one of them copies that class into its own
folder and lists it in its README.

## Library
`lib/jfreechart-1.5.3.jar` is the framework's only external dependency (`org.jfree:jfreechart:1.5.3`, from the
teacher's `pom.xml`). It was downloaded from Maven Central, and its SHA-1 matches the published one:
`26c6d7143d8a905a54c7e2296cea6ce4c5ecb417`.

## Updating
If the teacher changes the framework, copy the new `src/` packages over these, update the commit above and commit
that change on its own ("framework: update to <sha>").
