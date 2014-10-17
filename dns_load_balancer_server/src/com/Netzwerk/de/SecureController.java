package com.Netzwerk.de;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

/**
 * This class describe a object which handle security things like<br>
 * <br>
 * - generation of hash from a password<br>
 * - generate of a salt<br>
 * - decryption and encryption of messages<br>
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */
public class SecureController extends Object
{

	/**
	 * The singleton object of this class
	 */
	private static SecureController sc = new SecureController();

	/**
	 * The logger object of this class
	 */
	private static Logger logger = Logger.getLogger(SecureController.class);

	/**
	 * The private constructor of this class
	 */
	private SecureController()
	{
		Field field;
		try
		{
			field = Class.forName("javax.crypto.JceSecurity").getDeclaredField(
					"isRestricted");
			field.setAccessible(true);
			field.set(null, java.lang.Boolean.FALSE);
		}
		catch (NoSuchFieldException | SecurityException
				| ClassNotFoundException | IllegalArgumentException
				| IllegalAccessException e)
		{
			logger.fatal("security load failed: " + e.getMessage());
		}
	}

	/**
	 * generate a password hash with a salt
	 * 
	 * @param pcaPassword as char array
	 * @param pbaSalt as byte array
	 * @return password hash as SecretKey
	 */
	public SecretKey passwordToHash(char[] pcaPassword, byte[] pbaSalt)
	{
		/* Derive the key, given password and salt. */
		SecretKeyFactory factory = null;
		KeySpec spec = null;
		SecretKey tmp2 = null;
		try
		{
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			spec = new PBEKeySpec(pcaPassword, pbaSalt, 65536, 256);
			tmp2 = factory.generateSecret(spec);
		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException e)
		{
			logger.fatal("security password to hash failed: " + e.getMessage());
		}
		return tmp2;
	}

	/**
	 * decryption of a message with a user password
	 * 
	 * @param pbaPassword as byte array
	 * @param pbaCiphertext as byte array
	 * @param pbaIv as byte array
	 * @param pbaSalt as byte array
	 * @return message as String
	 */
	public String decryptMessage(byte[] pbaPassword, byte[] pbaCiphertext,
			byte[] pbaIv, byte[] pbaSalt)
	{
		/*
		 * local variables
		 */
		Cipher cipherDec = null;
		String plaintext = null;

		/*
		 * build secretKey from an byte array
		 */
		SecretKey tmp = new SecretKeySpec(pbaPassword, 0, pbaPassword.length,
				"AES");
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

		/*
		 * from cipher text to plain text
		 */
		try
		{
			cipherDec = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipherDec.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(
					pbaIv));
			plaintext = new String(cipherDec.doFinal(pbaCiphertext), "UTF-8");
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException | InvalidAlgorithmParameterException
				| UnsupportedEncodingException | IllegalBlockSizeException e)
		{
			logger.fatal("Security decrypt message failed: " + e.getMessage());
		}
		catch (BadPaddingException e)
		{
			logger.debug("Security: password is false!");
			plaintext = "";
		}

		return plaintext;
	}

	/**
	 * generate a salt for the password
	 * 
	 * @return salt as byte array
	 * @throws NoSuchAlgorithmException as Exception
	 */
	public byte[] generateSalt() throws NoSuchAlgorithmException
	{
		/*
		 * secure random
		 */
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		/*
		 * 32 Byte (256 Bit) random Salt
		 */
		byte[] salt = new byte[32];
		random.nextBytes(salt);

		return salt;
	}

	/**
	 * encrypt a text message with the user password
	 * 
	 * @param pcaPassword as char array
	 * @param psMessage as String
	 * @return a Message object
	 */
	public Message encryptMessage(char[] pcaPassword, String psMessage)
	{
		/*
		 * local variables
		 */
		SecretKey secret = null;
		SecretKey tmp = null;
		Cipher cipher = null;
		AlgorithmParameters params = null;
		byte[] iv = null;
		byte[] ciphertext = null;
		byte[] salt = null;

		/*
		 * generate salt, build password hash, and than build a cipher text with
		 * AES encryption
		 */
		try
		{
			salt = this.generateSalt();
			tmp = this.passwordToHash(pcaPassword, salt);
			secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			params = cipher.getParameters();
			iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			ciphertext = cipher.doFinal(psMessage.getBytes("UTF-8"));
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException | InvalidParameterSpecException
				| IllegalBlockSizeException | BadPaddingException
				| UnsupportedEncodingException e)
		{
			logger.fatal("security encrypt message failed: " + e.getMessage());
		}

		/*
		 * return the message with encrypted text, parameter and the salt
		 */
		return new Message(ciphertext, iv, salt);
	}

	public static SecureController getInstance()
	{
		return sc;
	}
}
