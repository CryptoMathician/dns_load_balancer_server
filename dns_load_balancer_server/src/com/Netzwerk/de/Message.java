package com.Netzwerk.de;

/**
 * This class describe a encrpyted message with salt and the parameters to decrypt it later
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */
public class Message extends Object
{
	/**
	 * Store the cipher text as byte array
	 */
	private byte[] ciphertext = null;
	
	/**
	 * Store the parameter from the encryption of the plain text as byte array
	 */
	private byte[] iv = null;
	
	/**
	 * Store the salt as byte array
	 */
	private byte[] salt = null;
	
	/**
	 * Public constructor of this class
	 * 
	 * @param pbaCiphertext as byte array
	 * @param pbaIv as byte array
	 * @param pbaSalt as byte array
	 */
	public Message(byte[] pbaCiphertext, byte[] pbaIv, byte[] pbaSalt)
	{
		this.ciphertext = pbaCiphertext;
		this.iv = pbaIv;
		this.salt = pbaSalt;
	}

	/**
	 * Return the cipher text
	 * 
	 * @return the ciphertext as byte array
	 */
	public byte[] getCiphertext() 
	{
		return this.ciphertext;
	}

	/**
	 * Return the parameter from the encryption
	 * 
	 * @return the iv as byte array
	 */
	public byte[] getIv() 
	{
		return this.iv;
	}

	/**
	 * Return the salt 
	 * 
	 * @return the salt as byte array
	 */
	public byte[] getSalt() 
	{
		return this.salt;
	}
}