/*
 * Copyright 2018, 2018 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ibm.federation.fabric.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;


import javax.xml.bind.DatatypeConverter;
import com.ibm.federation.fabric.user.FabricUser;

import com.ibm.federation.fabric.network.FabricConnection;
import com.ibm.federation.fabric.user.FabricEnrollment;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import java.io.FileReader;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

/**
 * @author Rohith Ravindranath
 * @version June 29 2018
 * Util class for common functions throughout the package
 */
public class Utilities {

	/**
	 * Serialize user
	 *
	 * @param userContext
	 * @throws Exception
	 */
	public static void writeFabricUser(FabricUser userContext) throws Exception {
		String directoryPath = "users/" + userContext.getAffiliation();
		String filePath = directoryPath + "/" + userContext.getName() + ".ser";
		File directory = new File(directoryPath);
		if (!directory.exists())
			directory.mkdirs();

		FileOutputStream file = new FileOutputStream(filePath);
		ObjectOutputStream out = new ObjectOutputStream(file);

		// Method for serialization of object
		out.writeObject(userContext);

		out.close();
		file.close();
	}

	/**
	 * Deserialize user
	 *
	 * @param affiliation
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public static FabricUser readFabricUser(String affiliation, String username) throws Exception {
		String filePath = "users/" + affiliation + "/" + username + ".ser";
		File file = new File(filePath);
		if (file.exists()) {
			// Reading the object from a file
			FileInputStream fileStream = new FileInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(fileStream);

			// Method for deserialization of object
			FabricUser uContext = (FabricUser) in.readObject();

			in.close();
			fileStream.close();
			return uContext;
		}

		return null;
	}
	
	
	/**
	 * Method creates and enrollment object based on the key and cert file path given.
	 * Both params should correspond to one user.
	 * @param certFilePath
	 * @param keyFilePath
	 */
	public static FabricEnrollment createEnrollment(File keyFilePath,  File certFilePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		PrivateKey key = null;
		String certificate = null;
		InputStream isKey = null;
		BufferedReader brKey = null;
		isKey = new FileInputStream(keyFilePath);
		brKey = new BufferedReader(new InputStreamReader(isKey));
		StringBuilder keyBuilder = new StringBuilder();
		for (String line = brKey.readLine(); line != null; line = brKey.readLine()) {
			if (line.indexOf("PRIVATE") == -1) {
				keyBuilder.append(line);
			}
		}
		certificate = new String(Files.readAllBytes(Paths.get(certFilePath.getPath())));
		byte[] encoded = DatatypeConverter.parseBase64Binary(keyBuilder.toString());
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		KeyFactory kf = KeyFactory.getInstance("EC");
		key = kf.generatePrivate(keySpec);
		isKey.close();
		brKey.close();
		FabricEnrollment enrollment = new FabricEnrollment(key, certificate);
		return enrollment;
	}

	/**
	 * Will find the private key file within the directory given.
	 * Private key should end with "_sk"
	 * @param directorys
	 */
	public static File findFileSk(String directorys) {
	        File directory = new File(directorys);
	        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));
	        return matches[0];
	}

	/**
	 * Will find the user certificate file within the directory given.
	 * Private key should end with "pem"
	 * @param directorys
	 */
	public static File findFilePm(String directorys) {
	        File directory = new File(directorys);
	        File[] matches = directory.listFiles((dir, name) -> name.endsWith(".pem"));
	        return matches[0];
	}

	/**
	 * Converst the given hash value (byte []) into a readable string
	 * @param hash
	 */
	 public static String hashToString(byte [] hash) {
			return Hex.encodeHexString(hash);
	}

	/**
	 * Converst the given string into the hash value
	 * @param str
	 */
	 public static byte[] stringToHash(String str) {
		 char[] hash = str.toCharArray();
		 try {
			return Hex.decodeHex(hash);
		} catch (DecoderException e) {
			return null;
		}
	 }

	/**
	 * Converts the given string, which is encoded with symbols and characters into a readable string for the user
	 * @param string
	 */
	 public static String printableString(final String string) {
	        int maxLogStringLength = 64;
	        if (string == null || string.length() == 0) {
	            return string;
	        }
	        String ret = string.replaceAll("[^\\p{Print}]", "?");
	        ret = ret.substring(0, Math.min(ret.length(), maxLogStringLength)) + (ret.length() > maxLogStringLength ? "..." : "");
	        return ret;

	}

	/**
	 * Given the file of the configuration json file, then method will parse through
	 * the json file and read the necessary information to create a fabric connection
	 * @param file
	 * @return connection FabricConnection
	 */
	public static FabricConnection parseConfigFile(File file){
		if(!com.google.common.io.Files.getFileExtension(file.getPath()).equals("json")){
			System.err.println("JSON FILE REQUIRED.");
			System.exit(206);
		}
		Object obj = null;
		try {
			obj = new JSONParser().parse(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		// typecasting obj to JSONObject
		JSONObject jo = (JSONObject) obj;
		Map userConfigs = ((Map)jo.get("fabricUser"));
		Map networkConfigs = ((Map)jo.get("network"));
		String userAffiliation = null;
		String userPKFilePath = null;
		String userMSPID = null;
		String userCertFilePath = null;
		String username = null;
		String peerName = null;
		String IPAdress = null;
		String channelName = null;
		String ordererName = null;
		String ordererURLPort = null;
		String peerURLPort = null;
		String password = null;
		String caURLPort = null;
		try {
			userAffiliation = userConfigs.get("userAffiliation").toString();
			userPKFilePath = userConfigs.get("userPKFilePath").toString();
			userMSPID = userConfigs.get("userMSPID").toString();
			userCertFilePath = userConfigs.get("userCertFilePath").toString();
			username = userConfigs.get("username").toString();
			peerName = networkConfigs.get("peerName").toString();
			IPAdress = networkConfigs.get("IPAdress").toString();
			channelName = networkConfigs.get("channelName").toString();
			ordererName = networkConfigs.get("ordererName").toString();
			ordererURLPort = networkConfigs.get("ordererURLPort").toString();
			peerURLPort = networkConfigs.get("peerURLPort").toString();
			password = userConfigs.get("password").toString();
			caURLPort = networkConfigs.get("caURLPort").toString();
		}catch (NullPointerException e){
			System.err.println("ERROR READING JSON FILE.");
			System.exit(207);
		}

		String peerURL = "grpc://"+IPAdress+":" + peerURLPort;
		String ordererURL = "grpc://"+IPAdress+":" + ordererURLPort;
		String caUrl  =  "http://"+IPAdress+":" + caURLPort;
		FabricConnection connection = null;

		try {
			connection = new FabricConnection(username,password, userAffiliation, userMSPID,channelName, peerName, peerURL, caUrl ,ordererName, ordererURL);
			System.out.println("FabricConnection Created.");
			System.out.println("Channel Created.");
		}catch (Exception e){
			System.err.println("ERROR CONNECTION TO BLOCKCHAIN NETWORK.");
			System.exit(207);
		}
		return connection;
	}

	public static void cleanUp() {
		String directoryPath = "users";
		File directory = new File(directoryPath);
		deleteDirectory(directory);
	}

	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(children[i]);
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

}