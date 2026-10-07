package lab.scd.net.calculator;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Swing client for the network calculator.
 *
 * Start CalculatorServer first. The client opens one TCP connection when it
 * starts and keeps it open: every button press sends a request line on that
 * same socket and waits for the response line. Closing the window sends END.
 *
 * The request/response exchange runs on a SwingWorker, not on the event
 * dispatch thread, so the window stays responsive while waiting for the server.
 */
public class CalculatorClient extends JFrame {

	private static final String HOST = "localhost";
	private static final int PORT = CalculatorServer.PORT;

	private final JTextField firstNumber = new JTextField(10);
	private final JTextField secondNumber = new JTextField(10);
	private final JTextField resultField = new JTextField(10);
	private final JLabel status = new JLabel(" ");

	private final JButton[] operationButtons = {
			new JButton("+"), new JButton("-"), new JButton("*"), new JButton("/") };
	private static final String[] OPERATIONS = { "ADD", "SUB", "MUL", "DIV" };

	private final Socket socket;
	private final BufferedReader in;
	private final PrintWriter out;

	public CalculatorClient(Socket socket) throws IOException {
		super("Network Calculator - client");
		this.socket = socket;
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

		buildUserInterface();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				disconnect();
			}
		});

		pack();
		setLocationRelativeTo(null);
	}

	private void buildUserInterface() {
		resultField.setEditable(false);

		JPanel fields = new JPanel(new GridLayout(3, 2, 6, 6));
		fields.add(new JLabel("First number:"));
		fields.add(firstNumber);
		fields.add(new JLabel("Second number:"));
		fields.add(secondNumber);
		fields.add(new JLabel("Result:"));
		fields.add(resultField);

		JPanel buttons = new JPanel(new GridLayout(1, 4, 6, 6));
		for (int i = 0; i < operationButtons.length; i++) {
			String operation = OPERATIONS[i];
			operationButtons[i].addActionListener(event -> sendOperation(operation));
			buttons.add(operationButtons[i]);
		}

		JPanel content = new JPanel(new BorderLayout(6, 6));
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		content.add(fields, BorderLayout.CENTER);
		content.add(buttons, BorderLayout.SOUTH);

		status.setText("Connected to " + HOST + ":" + PORT);

		setLayout(new BorderLayout());
		add(content, BorderLayout.CENTER);
		add(status, BorderLayout.SOUTH);
	}

	/** Sends one request to the server and shows the response. */
	private void sendOperation(String operation) {
		String left = firstNumber.getText().trim();
		String right = secondNumber.getText().trim();
		String request = operation + " " + left + " " + right;

		setButtonsEnabled(false);
		status.setText("Sent: " + request);

		new SwingWorker<String, Void>() {

			@Override
			protected String doInBackground() throws Exception {
				out.println(request);
				String response = in.readLine();
				if (response == null) {
					throw new IOException("the server closed the connection");
				}
				return response;
			}

			@Override
			protected void done() {
				try {
					showResponse(get());
				} catch (Exception ex) {
					resultField.setText("");
					status.setText("Connection lost: " + ex.getMessage());
				} finally {
					setButtonsEnabled(true);
				}
			}
		}.execute();
	}

	private void showResponse(String response) {
		if (response.startsWith("RESULT ")) {
			resultField.setText(response.substring("RESULT ".length()));
			status.setText("Received: " + response);
		} else if (response.startsWith("ERROR ")) {
			resultField.setText("");
			status.setText(response.substring("ERROR ".length()));
		} else {
			resultField.setText("");
			status.setText("Unexpected response: " + response);
		}
	}

	private void setButtonsEnabled(boolean enabled) {
		for (JButton button : operationButtons) {
			button.setEnabled(enabled);
		}
	}

	/** Ends the dialogue politely, then closes the socket. */
	private void disconnect() {
		try {
			out.println("END");
			in.readLine(); // the server answers BYE
		} catch (IOException ex) {
			System.err.println("Could not say END: " + ex.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException ex) {
				System.err.println("Socket not closed: " + ex.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		Socket socket;
		try {
			InetAddress address = InetAddress.getByName(HOST);
			socket = new Socket(address, PORT);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null,
					"Cannot connect to " + HOST + ":" + PORT + "\n" + ex.getMessage()
							+ "\n\nStart CalculatorServer first.",
					"Network Calculator", JOptionPane.ERROR_MESSAGE);
			return;
		}

		SwingUtilities.invokeLater(() -> {
			try {
				new CalculatorClient(socket).setVisible(true);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Cannot open the streams: " + ex.getMessage(),
						"Network Calculator", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
