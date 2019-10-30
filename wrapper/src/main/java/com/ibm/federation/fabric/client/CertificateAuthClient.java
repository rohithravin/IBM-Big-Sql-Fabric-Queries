package com.ibm.federation.fabric.client;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Properties;

import com.ibm.federation.fabric.user.FabricUser;
import com.ibm.federation.fabric.utils.Utilities;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class CertificateAuthClient {
    String caUrl;
    Properties caProperties;

    HFCAClient instance;

    FabricUser adminUser;

    public FabricUser getAdminFabricUser() {
        return adminUser;
    }

    /**
     * Set the admin user context for registering and enrolling users.
     *
     * @param userContext
     */
    public void setAdminFabricUser(FabricUser userContext) {
        this.adminUser = userContext;
    }

    /**
     * Constructor
     *
     * @param caUrl
     * @param caProperties
     * @throws MalformedURLException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InvalidArgumentException
     * @throws CryptoException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public CertificateAuthClient(String caUrl, Properties caProperties) throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
        this.caUrl = caUrl;
        this.caProperties = caProperties;
        init();
    }

    public void init() throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        instance = HFCAClient.createNewInstance(caUrl, caProperties);
        instance.setCryptoSuite(cryptoSuite);
    }

    public HFCAClient getInstance() {
        return instance;
    }

    /**
     * Enroll admin user.
     *
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public FabricUser enrollAdminUser(String username, String password) throws Exception {
        FabricUser userContext = Utilities.readFabricUser(adminUser.getAffiliation(), username);
        if (userContext != null) {
            return userContext;
        }
        Enrollment adminEnrollment = instance.enroll(username, password);
        adminUser.setEnrollment(adminEnrollment);
        Utilities.writeFabricUser(adminUser);
        return adminUser;
    }

    /**
     * Register user.
     *
     * @param username
     * @param organization
     * @return
     * @throws Exception
     */
    public String registerUser(String username, String organization) throws Exception {
        FabricUser userContext = Utilities.readFabricUser(adminUser.getAffiliation(), username);
        if (userContext != null) {
            return null;
        }
        RegistrationRequest rr = new RegistrationRequest(username, organization);
        String enrollmentSecret = instance.register(rr, adminUser);
        return enrollmentSecret;
    }

    /**
     * Enroll user.
     *
     * @param user
     * @param secret
     * @return
     * @throws Exception
     */
    public FabricUser enrollUser(FabricUser user, String secret) throws Exception {
        FabricUser userContext = Utilities.readFabricUser(adminUser.getAffiliation(), user.getName());
        if (userContext != null) {
            return userContext;
        }
        Enrollment enrollment = instance.enroll(user.getName(), secret);
        user.setEnrollment(enrollment);
        Utilities.writeFabricUser(user);
        return user;
    }
}
