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
package com.ibm.federation.fabric.user;

import java.security.PrivateKey;

import org.hyperledger.fabric.sdk.Enrollment;

/**
 * @author Rohith Ravindranath
 * @version June 29 2018
 * Class that implements the Enrollment interface
 */
public class FabricEnrollment implements Enrollment{

	private PrivateKey key;
	private String cert;

	/**
	 * Constructor
	 * @param pkey
	 * @param signedPem
	 */
	public FabricEnrollment(PrivateKey pkey, String signedPem) {
		this.key = pkey;
		this.cert = signedPem;
	}

	/**
	 * Getter method for the user's private key
	 * @return key PrivateKey
	 */
	public PrivateKey getKey() {
		return key;
	}

	/**
	 * Getter method for the certificate of the user
	 * @return certificate String
	 */
	public String getCert() {
		return cert;
	}
		
}