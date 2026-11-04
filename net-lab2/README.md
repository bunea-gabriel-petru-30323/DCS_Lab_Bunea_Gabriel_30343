# Network Programming 2 – datagrams, serialization, HTTP, URLs

Lab PDF: `docs/Network Programming 2.pdf` (local only).

## Provenance

Teacher code comes from [dahliajanabi/DCS_Network_Programming](https://github.com/dahliajanabi/DCS_Network_Programming),
`Labnet/src/lab/`, commit `269cd82`.

| File | Origin | Changes vs. teacher |
|---|---|---|
| `dcs/net/datagram/QuoteServerThread.java` | teacher | quote file: Windows absolute path → `src/lab/dcs/net/datagram/one-liners.txt` |
| `dcs/net/datagram/QuoteClient.java` | teacher | host `localhost` → `10.132.87.60` |
| `dcs/net/datagram/one-liners.txt` | teacher | none |
| `scd/net/serialization/SerialTest.java` | teacher | none |
| `scd/net/serialization/Client.java` | teacher | host `localhost` → `10.132.87.60` |
| `scd/net/serialization/Pers.java` | teacher | added a comment that explains the `transient` exercise |
| `scd/net/httpexample/HttpServer.java` | teacher | `iniContext`: Windows absolute path → `src/lab/scd/net/httpexample` |
| `scd/net/httpexample/index.htm` | teacher | none |
| `scd/net/url_http/SourceViewer.java` | teacher | none |
| `scd/net/url_http/ContentDownloader.java` | teacher | none |
| `scd/net/udpchat/UdpChatTerminal.java` | **mine** | exercise 2: UDP chat |

## Exercises → files
| Exercise | Answer |
|---|---|
| 1. Datagram package: `QuoteServerThread`, then `QuoteClient`, between two PCs | `dcs/net/datagram/*` |
| 1. Serialization: uncomment `transient` in `Pers` and explain the difference | `scd/net/serialization/*`. The explanation is in `Pers.java`: a `transient` field is not serialized, so the server prints `Persoana: null vasrta: 14`. |
| 1. HTTP server: create `index.htm`, set the path, test between two PCs | `scd/net/httpexample/*`. Open `http://<server-ip>/` in a browser. |
| 1. URL classes | `scd/net/url_http/*`. `http://www.utcluj.ro` now answers 301 because the site moved to https. |
| 2. UDP chat: two identical terminals, a sender thread and a receiver thread, GUI with input, history and Send | `scd/net/udpchat/UdpChatTerminal.java` |

## Running
- **Working directory:** `QuoteServerThread` and `HttpServer` read files relative to **this folder** (`net-lab2/`).
  Use the run configurations `net-lab2 QuoteServerThread` and `net-lab2 HttpServer` from `.run/`, which set the
  working directory to the module folder.
- **HTTP server:** listens on port 80.
- **UDP chat:** run `UdpChatTerminal` twice.
  - Same PC: use the arguments `5000 localhost 5001` and `5001 localhost 5000`.
  - Two PCs: both use the same port, and each one uses the other PC's IP.
  - Without arguments, a dialog asks for these values.
