package aes256cbc;

import javax.swing.SwingUtilities;

public class AES256CBCEncryptorMain {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new AES256CBCGUI().setVisible(true));
	}
}
