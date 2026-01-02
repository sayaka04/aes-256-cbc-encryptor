package aes256cbc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class AES256CBCGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPasswordField passphraseField;
	private JTextArea plaintextArea;
	private JTextArea ciphertextArea;
	private JButton toggleButton;
	private boolean passwordVisible = false;

	public AES256CBCGUI() {
		setTitle("AES-256-CBC Encrypt / Decrypt");
		setSize(720, 520);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		initUI();

		try {
			// Load icon from resources
			InputStream iconStream = getClass().getResourceAsStream("/aes256cbc/pc1.png");
			if (iconStream != null) {
				Image icon = ImageIO.read(iconStream);
				setIconImage(icon); // this sets the icon for tab and taskbar
			} else {
				System.err.println("Icon not found!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initUI() {
		JPanel root = new JPanel(new BorderLayout(10, 10));
		root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Passphrase panel
		JPanel passPanel = new JPanel(new BorderLayout(5, 5));
		passPanel.add(new JLabel("Passphrase:"), BorderLayout.WEST);

		passphraseField = new JPasswordField("00000000000000000000000000000000");
		passphraseField.setEchoChar('•');

		toggleButton = new JButton("Show");
		toggleButton.addActionListener(e -> togglePasswordVisibility());

		passPanel.add(passphraseField, BorderLayout.CENTER);
		passPanel.add(toggleButton, BorderLayout.EAST);

		root.add(passPanel, BorderLayout.NORTH);

		// Text areas
		plaintextArea = new JTextArea();
		plaintextArea.setLineWrap(true);
		plaintextArea.setWrapStyleWord(true);

		ciphertextArea = new JTextArea();
		ciphertextArea.setLineWrap(true);
		ciphertextArea.setWrapStyleWord(true);

		JScrollPane plainScroll = new JScrollPane(plaintextArea);
		plainScroll.setBorder(BorderFactory.createTitledBorder("Plaintext (UTF-8)"));

		JScrollPane cipherScroll = new JScrollPane(ciphertextArea);
		cipherScroll.setBorder(BorderFactory.createTitledBorder("Ciphertext (HEX)"));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, plainScroll, cipherScroll);
		splitPane.setResizeWeight(0.5);

		root.add(splitPane, BorderLayout.CENTER);

		// Buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

		JButton encryptBtn = new JButton("Encrypt → HEX");
		JButton decryptBtn = new JButton("Decrypt ← HEX");

		encryptBtn.addActionListener(e -> encryptAction());
		decryptBtn.addActionListener(e -> decryptAction());

		buttonPanel.add(encryptBtn);
		buttonPanel.add(decryptBtn);

		root.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(root);
	}

	private void togglePasswordVisibility() {
		passwordVisible = !passwordVisible;
		passphraseField.setEchoChar(passwordVisible ? (char) 0 : '•');
		toggleButton.setText(passwordVisible ? "Hide" : "Show");
	}

	private void encryptAction() {
		try {
			String passphrase = new String(passphraseField.getPassword());
			String plaintext = plaintextArea.getText();

			if (plaintext.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Plaintext is empty");
				return;
			}

			byte[] encrypted = AES256CBC.encrypt(plaintext, passphrase);
			ciphertextArea.setText(AES256CBC.bytesToHex(encrypted));

		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void decryptAction() {
		try {
			String passphrase = new String(passphraseField.getPassword());
			String hex = ciphertextArea.getText().trim();

			byte[] data = AES256CBC.hexToBytes(hex);

			try {
				String decrypted = AES256CBC.decrypt(data, passphrase);
				plaintextArea.setText(decrypted);
			} catch (javax.crypto.BadPaddingException | javax.crypto.IllegalBlockSizeException e) {
				JOptionPane.showMessageDialog(this, "Decryption failed.\nWrong passphrase or corrupted ciphertext.",
						"Decryption Error", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				// Catch-all for other unexpected errors
				JOptionPane.showMessageDialog(this, "An unexpected error occurred during decryption.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}

		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showError(Exception ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Crypto Error", JOptionPane.ERROR_MESSAGE);
	}
}
