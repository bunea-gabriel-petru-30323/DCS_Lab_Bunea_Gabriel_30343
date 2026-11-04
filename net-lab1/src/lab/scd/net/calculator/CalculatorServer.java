package lab.scd.net.calculator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP server for the network calculator.
 *
 * It waits for connections on PORT and serves every client on its own thread,
 * so several calculator clients can be connected at the same time.
 *
 * Protocol (one text line per message):
 *   client -> server : ADD|SUB|MUL|DIV <number> <number>     e.g. "DIV 7 2"
 *   server -> client : RESULT <value>        on success      e.g. "RESULT 3.5"
 *                      ERROR <message>       on bad input    e.g. "ERROR Division by zero"
 *   client -> server : END                                   closes the dialogue
 *   server -> client : BYE
 */
public class CalculatorServer {

	public static final int PORT = 1901;

	public static void main(String[] args) {
		new CalculatorServer().startServer();
	}

	void startServer() {
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("Calculator server is waiting for connections on port " + PORT + "...");
			while (true) {
				Socket socket = serverSocket.accept();
				new CalculatorHandler(socket).start();
			}
		} catch (IOException ex) {
			System.err.println("Server error: " + ex.getMessage());
		}
	}
}

/** Serves one connected client: reads request lines, writes response lines. */
class CalculatorHandler extends Thread {

	private final Socket socket;

	CalculatorHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		String client = String.valueOf(socket.getRemoteSocketAddress());
		System.out.println("Client connected: " + client);

		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(
						new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {

			String request;
			while ((request = in.readLine()) != null) {
				System.out.println("Received from " + client + ": " + request);

				if (request.trim().equals("END")) {
					out.println("BYE");
					break;
				}

				String response = compute(request);
				System.out.println("Sending to " + client + ": " + response);
				out.println(response);
			}
		} catch (IOException ex) {
			System.err.println("IO error on " + client + ": " + ex.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException ex) {
				System.err.println("Socket not closed: " + ex.getMessage());
			}
			System.out.println("Client disconnected: " + client);
		}
	}

	/** Turns one request line into the response line to send back. */
	static String compute(String request) {
		String[] parts = request.trim().split("\\s+");
		if (parts.length != 3) {
			return "ERROR Expected: <ADD|SUB|MUL|DIV> <number> <number>";
		}

		double left;
		double right;
		try {
			left = Double.parseDouble(parts[1]);
			right = Double.parseDouble(parts[2]);
		} catch (NumberFormatException ex) {
			return "ERROR Both operands must be numbers";
		}

		double result;
		switch (parts[0].toUpperCase()) {
			case "ADD" -> result = left + right;
			case "SUB" -> result = left - right;
			case "MUL" -> result = left * right;
			case "DIV" -> {
				if (right == 0) {
					return "ERROR Division by zero";
				}
				result = left / right;
			}
			default -> {
				return "ERROR Unknown operation: " + parts[0];
			}
		}
		return "RESULT " + format(result);
	}

	/** Prints whole results as 4 instead of 4.0, everything else unchanged. */
	static String format(double value) {
		if (!Double.isInfinite(value) && !Double.isNaN(value) && value == Math.rint(value)
				&& Math.abs(value) < 1e15) {
			return String.valueOf((long) value);
		}
		return String.valueOf(value);
	}
}
