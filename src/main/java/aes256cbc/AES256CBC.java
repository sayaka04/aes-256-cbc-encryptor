package aes256cbc;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public class AES256CBC {

	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

	public static byte[] encrypt(String plaintext, String passphrase) throws Exception {
		byte[] keyBytes = deriveKey(passphrase);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = deriveIV(keyBytes);

		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

		return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
	}

	public static String decrypt(byte[] ciphertext, String passphrase) throws Exception {
		byte[] keyBytes = deriveKey(passphrase);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = deriveIV(keyBytes);

		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		byte[] decrypted = cipher.doFinal(ciphertext);
		return new String(decrypted, StandardCharsets.UTF_8);
	}

	private static byte[] deriveKey(String passphrase) throws Exception {
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		return sha256.digest(passphrase.getBytes(StandardCharsets.UTF_8));
	}

	private static IvParameterSpec deriveIV(byte[] key) {
		return new IvParameterSpec(Arrays.copyOfRange(key, 0, 16));
	}

	// Helper methods for hex conversion
	public static String bytesToHex(byte[] data) {
		StringBuilder sb = new StringBuilder(data.length * 2);
		for (byte b : data) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	public static byte[] hexToBytes(String hex) {
		if (hex == null || hex.isEmpty()) {
			throw new IllegalArgumentException("Ciphertext is empty");
		}

		if ((hex.length() & 1) != 0) {
			throw new IllegalArgumentException("Hex string length must be even");
		}

		if (!hex.matches("[0-9a-fA-F]+")) {
			throw new IllegalArgumentException("Ciphertext contains non-hex characters");
		}

		byte[] data = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			data[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
		}
		return data;
	}

}
