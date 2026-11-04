# DCS_Lab_Bunea_Gabriel_30343

Distributed Control Systems labs, one folder per lab. Each folder is its own IntelliJ module.

| Lab | Folder | Topic | Status |
|---|---|---|---|
| Network Programming 1 | [`net-lab1/`](net-lab1/README.md) | TCP sockets, multithreaded server, NIO, network calculator | approved (tag `net-lab1`) |
| Network Programming 2 | [`net-lab2/`](net-lab2/README.md) | UDP, serialization, HTTP server, URLs, UDP chat | done |
| Fuzzy Lab 1 | [`fuzzy-lab1/`](fuzzy-lab1/README.md) | FLETPN: inversor, adder/subtractor | done |

Shared code:

| Folder | What it is |
|---|---|
| [`framework/all-petri-nets/`](framework/all-petri-nets/ORIGIN.md) | The teacher's Petri net framework, copied once and never edited. The fuzzy labs depend on it. |

## Conventions
- Every lab README has a **provenance table**: for each file, whether it is the teacher's code (with the repo and
  commit it came from, and what was changed) or written by me.
- Teacher code is copied with the smallest changes needed to run it, such as IP addresses, ports and file paths.
- One commit per lab. A lab is tagged (`git tag <lab>`) once the teacher approves it.
- The lab PDFs are kept locally in `docs/`, which is not committed.

## Running
Open the project folder in IntelliJ. Every lab is a module, so right-click a class with `main` and choose Run.
Shared run configurations are in `.run/`, for the classes that read files relative to their module folder.

From the terminal (JDK 21+):

```
# a network lab
javac -d out/net-lab2 $(find net-lab2/src -name '*.java')
cd net-lab2 && java -cp ../out/net-lab2 lab.dcs.net.datagram.QuoteServerThread

# a fuzzy lab (needs the framework and JFreeChart)
FW=framework/all-petri-nets
javac -d out/framework -cp $FW/lib/jfreechart-1.5.3.jar $(find $FW/src -name '*.java')
javac -d out/fuzzy-lab1 -cp out/framework $(find fuzzy-lab1/src -name '*.java')
java -cp out/fuzzy-lab1:out/framework:$FW/lib/jfreechart-1.5.3.jar DCS_FuzzyLab.AdderSubtractor
```
