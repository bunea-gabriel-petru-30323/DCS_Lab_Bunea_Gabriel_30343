# Network Programming 1 – TCP sockets

Lab PDF: `docs/Network Programming 1.pdf` (local only). Status: **approved by the teacher**.

## Provenance

Teacher code comes from [dahliajanabi/DCS_Network_Programming](https://github.com/dahliajanabi/DCS_Network_Programming),
`Labnet/src/lab/scd/net/`, commit `269cd82`.

| File | Origin | Changes vs. teacher |
|---|---|---|
| `socket/SimpleServer.java` | teacher | none (whitespace only) |
| `socket/SimpleClient.java` | teacher | host `localhost` → `10.132.87.60` (the partner PC) |
| `socket/MultiThreadedServer.java` | teacher | none |
| `nonblocking/NonBlockingServer.java` | teacher | port 8000 → 8003 |
| `nonblocking/NonBlockingClient.java` | teacher | host → `10.132.87.60`, port 8000 → 8003 |
| `calculator/CalculatorServer.java` | **mine** | exercise: multithreaded TCP calculator server |
| `calculator/CalculatorClient.java` | **mine** | exercise: Swing calculator client |

## Exercises → files
| Exercise | Answer |
|---|---|
| Test the socket examples between two PCs | `socket/*` |
| Test the non-blocking (NIO) example | `nonblocking/*` |
| Network calculator (client/server with GUI) | `calculator/*`; the protocol is documented in `CalculatorServer`'s Javadoc |

## Running
- Start the server class first, then the client.
- For a two-PC test, set the clients' host to the server PC's IP address.
- Calculator: run `CalculatorServer` (port 1901), then `CalculatorClient`.
