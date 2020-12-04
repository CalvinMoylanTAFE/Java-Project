//Calvin Moylan 30018702
//Hashing Class
package javaproject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hashing {
    private String HashedPassword;
    private byte[] Salt;

    //Constructor
    public Hashing(String password) {
        this.Salt = generateSalt();
        this.HashedPassword = hashPassword(password, this.Salt);
    }
    
    //hashed password getter
    public String getHashedPassword() {
        return HashedPassword;
    }

    //salt getter
    public byte[] getSalt() {
        return Salt;
    }
    
    //this method hashes that password
    public String hashPassword(String password, byte[] Salt) {
        String HashedPassword = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            md.update(Salt);
            
            byte[] passwordBytes = md.digest(password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< passwordBytes.length ;i++)
            {
                sb.append(Integer.toString((passwordBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            HashedPassword = sb.toString();
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Hashing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return HashedPassword;
    }
    
    //this method generates a salt for the hash
    private byte[] generateSalt() {
        byte[] Salt = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            sr.nextBytes(Salt);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Hashing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Hashing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Salt;
    }
    
    //This method compares two passwords to see if they are the same.
    public static boolean comparePasswords(byte[] Salt, String ExistingPassword, String Password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            md.update(Salt);
            
            byte[] passwordBytes = md.digest(Password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< passwordBytes.length ;i++)
            {
                sb.append(Integer.toString((passwordBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            String HashedPassword = sb.toString();
            
            if (ExistingPassword.equals(HashedPassword)) 
                return true;
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Hashing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
