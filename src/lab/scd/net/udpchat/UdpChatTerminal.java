package lab.scd.net.udpchat;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * UDP chat terminal. Two identical terminals talk to each other: start this
 * class twice, each one pointing at the other.
 *
 * Usage:  java lab.scd.net.udpchat.UdpChatTerminal [localPort peerHost peerPort]
 *   same PC : terminal A = 5000 localhost 5001,  terminal B = 5001 localhost 5000
 *   two PCs : both use the same port, peerHost = the other PC's IP address
 * Without arguments a dialog asks for the three values.
 *
 * Each terminal has one DatagramSocket bound to localPort and two threads:
 *   receiver thread : blocks in socket.receive() and shows every datagram
 *   sender thread   : takes the messages typed by the user from a queue and
 *                     sends each one as a datagram to peerHost:peerPort
 *
 * Message format: one datagram = one message, UTF-8 text, at most MAX_BYTES.
 * UDP gives no delivery guarantee, so a message can be lost if the peer is not
 * running; the history only shows what was sent and what actually arrived.
 */
public class UdpChatTerminal extends JFrame {

	static final int MAX_BYTES = 1024;
	private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

	private final DatagramSocket socket;
	private final InetAddress peerAddress;
	private final int peerPort;
	private final BlockingQueue<String> outgoing = new LinkedBlockingQueue<>();

	private final JTextArea history = new JTextArea(18, 40);
	private final JTextField input = new JTextField(30);
	private final JButton sendButton = new JButton("Send");

	public UdpChatTerminal(int localPort, InetAddress peerAddress, int peerPort) throws SocketException {
		super("UDP Chat - :" + localPort + " -> " + peerAddress.getHostAddress() + ":" + peerPort);
		this.socket = new DatagramSocket(localPort);
		this.peerAddress = peerAddress;
		this.peerPort = peerPort;

		buildUserInterface();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				socket.close(); // unblocks the receiver thread
			}
		});
		pack();
		setLocationByPlatform(true);

		startThread(this::receiveLoop, "udp-receiver");
		startThread(this::sendLoop, "udp-sender");
		append("Listening on port " + localPort + ", chatting with "
				+ peerAddress.getHostAddress() + ":" + peerPort);
	}

	private void buildUserInterface() {
		history.setEditable(false);
		history.setLineWrap(true);
		history.setWrapStyleWord(true);

		sendButton.addActionListener(event -> queueMessage());
		input.addActionListener(event -> queueMessage()); // Enter also sends

		JPanel bottom = new JPanel(new BorderLayout(6, 6));
		bottom.add(input, BorderLayout.CENTER);
		bottom.add(sendButton, BorderLayout.EAST);

		JPanel content = new JPanel(new BorderLayout(6, 6));
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		content.add(new JScrollPane(history), BorderLayout.CENTER);
		content.add(bottom, BorderLayout.SOUTH);
		setContentPane(content);
	}

	private static void startThread(Runnable task, String name) {
		Thread thread = new Thread(task, name);
		thread.setDaemon(true);
		thread.start();
	}

	/** Called on the event dispatch thread: hands the typed text to the sender thread. */
	private void queueMessage() {
		String text = input.getText().trim();
		if (text.isEmpty()) {
			return;
		}
		if (text.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
			append("! Message too long (max " + MAX_BYTES + " bytes), not sent");
			return;
		}
		outgoing.add(text);
		input.setText("");
		input.requestFocusInWindow();
	}

	/** Sender thread: sends every queued message as one datagram. */
	private void sendLoop() {
		try {
			while (!socket.isClosed()) {
				String text = outgoing.take();
				byte[] data = text.getBytes(StandardCharsets.UTF_8);
				try {
					socket.send(new DatagramPacket(data, data.length, peerAddress, peerPort));
					append("Me: " + text);
				} catch (IOException ex) {
					if (socket.isClosed()) {
						return;
					}
					append("! Could not send \"" + text + "\": " + ex.getMessage());
				}
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	/** Receiver thread: shows every datagram that arrives on the local port. */
	private void receiveLoop() {
		byte[] buffer = new byte[MAX_BYTES];
		while (!socket.isClosed()) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(packet);
			} catch (IOException ex) {
				if (!socket.isClosed()) {
					append("! Receive error: " + ex.getMessage());
				}
				continue;
			}
			// only the bytes actually received, not the whole buffer
			String text = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
			append(packet.getAddress().getHostAddress() + ":" + packet.getPort() + ": " + text);
		}
	}

	/** Thread-safe: adds a time-stamped line to the history on the event dispatch thread. */
	private void append(String line) {
		String stamped = "[" + LocalTime.now().format(TIME) + "] " + line + "\n";
		SwingUtilities.invokeLater(() -> {
			history.append(stamped);
			history.setCaretPosition(history.getDocument().getLength());
		});
	}

	public static void main(String[] args) {
		String[] config = args.length == 3 ? args : askForConfig();
		if (config == null) {
			return; // dialog cancelled
		}

		int localPort;
		int peerPort;
		InetAddress peer;
		try {
			localPort = Integer.parseInt(config[0].trim());
			peer = InetAddress.getByName(config[1].trim());
			peerPort = Integer.parseInt(config[2].trim());
		} catch (NumberFormatException ex) {
			showError("Ports must be numbers.");
			return;
		} catch (UnknownHostException ex) {
			showError("Unknown peer host: " + config[1]);
			return;
		}

		SwingUtilities.invokeLater(() -> {
			try {
				new UdpChatTerminal(localPort, peer, peerPort).setVisible(true);
			} catch (SocketException ex) {
				showError("Cannot open UDP port " + localPort + ": " + ex.getMessage());
			}
		});
	}

	/** Returns {localPort, peerHost, peerPort} or null if the user cancelled. */
	private static String[] askForConfig() {
		JTextField localPort = new JTextField("5000");
		JTextField peerHost = new JTextField("localhost");
		JTextField peerPort = new JTextField("5001");

		JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
		form.add(new JLabel("Local port:"));
		form.add(localPort);
		form.add(new JLabel("Peer host / IP:"));
		form.add(peerHost);
		form.add(new JLabel("Peer port:"));
		form.add(peerPort);

		int choice = JOptionPane.showConfirmDialog(null, form, "UDP Chat - settings",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (choice != JOptionPane.OK_OPTION) {
			return null;
		}
		return new String[] { localPort.getText(), peerHost.getText(), peerPort.getText() };
	}

	private static void showError(String message) {
		JOptionPane.showMessageDialog(null, message, "UDP Chat", JOptionPane.ERROR_MESSAGE);
	}
}
