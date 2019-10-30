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

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Set;
import com.ibm.federation.fabric.utils.Utilities;

/**
 * @author Rohith Ravindranath
 * @version June 29 2018
 * Class the implements the user Interface from the SDK
 */
public class FabricUser implements User, Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private Enrollment enrollment;
    private String mspId;

    /**
     * Constructor
     * @param name
     * @param affiliation
     * @param certFilePath
     * @param mspId
     * @param pkFilePath
     */
    public FabricUser(String name, String affiliation, String mspId, String pkFilePath, String certFilePath ) throws IOException, GeneralSecurityException, CryptoException {
        this.name = name;
        this.affiliation = affiliation;
        this.mspId = mspId;
        this.enrollment = Utilities.createEnrollment(Utilities.findFileSk(pkFilePath), Utilities.findFilePm(certFilePath));
    }

    public FabricUser(){
        this.name = null;
        this.affiliation = null;
        this.mspId = null;
        this.enrollment = null;
    }

    public FabricUser(String name, String affiliation, String mspId){
        this.name = name;
        this.affiliation = affiliation;
        this.mspId = mspId;
        this.enrollment = null;
    }

    /**
     * Getter method user's name. Implemented from User interface
     * @return name String
     */
    @Override
    public String getName() {
        return name;
    }


    /**
     * Getter method user's roles. Implemented from User interface
     * @return roles Set<String>
     */
    @Override
    public Set<String> getRoles() {
        return roles;
    }


    /**
     * Getter method user's account. Implemented from User interface
     * @return account String
     */
    @Override
    public String getAccount() {
        return account;
    }

    /**
     * Getter method user's affiliation. Implemented from User interface
     * @return affiliation String
     */
    @Override
    public String getAffiliation() {
        return affiliation;
    }

    /**
     * Getter method user's enrollment. Implemented from User interface
     * @return enrollment Enrollment
     */
    @Override
    public Enrollment getEnrollment() {
        return this.enrollment;
        
    }

    /**
     * Setter method user's enrollment. Implemented from User interface
     * @param enrollment
     */
    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
        
    }

    /**
     * Getter method user's MSPID. Implemented from User interface
     * @return mspid String
     */
    @Override
    public String getMspId() {
        return mspId;
    }


}