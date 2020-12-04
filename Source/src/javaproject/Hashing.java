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

    Hashing(String password) {
        this.Salt = GenerateSalt();
        this.HashedPassword = HashPassword(password, this.Salt);
    }

    public String getHashedPassword() {
        return HashedPassword;
    }

    public byte[] getSalt() {
        return Salt;
    }
    
    public String HashPassword(String password, byte[] Salt) {
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
    
    private byte[] GenerateSalt() {
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
    
    public static boolean ComparePasswords(byte[] Salt, String ExistingPassword, String Password) {
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
