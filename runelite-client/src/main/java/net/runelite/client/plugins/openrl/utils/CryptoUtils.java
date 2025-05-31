/*
 * Copyright (c) 2022, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.utils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoUtils
{
	private static final int KEY_SIZE = 256; // AES key size
	private static final int GCM_IV_LENGTH = 12; // GCM IV size
	private static final int GCM_TAG_LENGTH = 128; // GCM tag size
	private static final int ITERATIONS = 65536; // PBKDF2 iterations
	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int SALT_SIZE = 16; // Size of the salt
	private static final SecureRandom random = new SecureRandom(); // Random instance

	/**
	 * Encrypt String
	 *
	 * @param str, string to encrypt
	 * @param password, password to encrypt string with
	 * @return encrypted string
	 */
	public static String encrypt(String str, String password)
	{
		try
		{
			// Generate a random IV
			final byte[] iv = new byte[GCM_IV_LENGTH];
			random.nextBytes(iv);

			// Generate a random salt
			final byte[] salt = new byte[SALT_SIZE];
			random.nextBytes(salt);

			// Derive key from password
			final SecretKey secretKey = getSecretKey(password, salt);

			// Initialize cipher
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			final GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

			// Encrypt
			final byte[] encryptedBytes = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));

			// Combine salt, IV, and encrypted bytes
			final byte[] combined = new byte[salt.length + iv.length + encryptedBytes.length];
			System.arraycopy(salt, 0, combined, 0, salt.length);
			System.arraycopy(iv, 0, combined, salt.length, iv.length);
			System.arraycopy(encryptedBytes, 0, combined, salt.length + iv.length, encryptedBytes.length);

			return Base64.getEncoder().encodeToString(combined);
		}
		catch (Exception e)
		{
			log.error("Failed to encrypt", e);
		}
		return null;
	}

	/**
	 * Decrypt string
	 *
	 * @param str, string to decrypt
	 * @param password, password to decrypt encrypted string
	 * @return decrypted string
	 */
	public static String decrypt(String str, String password)
	{
		try
		{
			final byte[] combined = Base64.getDecoder().decode(str);

			// Extract salt, IV, and encrypted bytes from the combined array
			final byte[] salt = new byte[SALT_SIZE];
			System.arraycopy(combined, 0, salt, 0, SALT_SIZE);
			final byte[] iv = new byte[GCM_IV_LENGTH];
			System.arraycopy(combined, SALT_SIZE, iv, 0, GCM_IV_LENGTH);
			final byte[] encryptedBytes = new byte[combined.length - SALT_SIZE - GCM_IV_LENGTH];
			System.arraycopy(combined, SALT_SIZE + GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

			// Derive key from password using the extracted salt
			final SecretKey secretKey = getSecretKey(password, salt);

			// Initialize cipher
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			final GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

			// Decrypt
			final byte[] original = cipher.doFinal(encryptedBytes);
			return new String(original, StandardCharsets.UTF_8);
		}
		catch (Exception e)
		{
			log.error("Failed to decrypt", e);
		}
		return null;
	}

	/**
	 * Derives a SecretKey from the given password and salt.
	 *
	 * @param password the password to derive the key from
	 * @param salt the salt used for key derivation
	 * @return the derived SecretKey
	 * @throws Exception if any errors occur during key derivation
	 */
	private static SecretKey getSecretKey(String password, byte[] salt) throws Exception
	{
		final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		final PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
		final SecretKey tmp = factory.generateSecret(spec);
		return new SecretKeySpec(tmp.getEncoded(), "AES");
	}

	public static void main(String[] args)
	{
		String enc = encrypt("text", "password");
		log.debug("Encrypted string: {}", enc);

		String dec = decrypt("text", "password");
		log.debug("Decrypted string: {}", dec);
	}
}