//Calvin Moylan 30018702
//User Class
package javaproject;

public class User {
    private String Username;
    private byte[] Salt;
    private String HashedPassword;
    
    //constructor
    public User(String Username, String Password) {
        this.Username = Username;
        hashPassword(Password);
    }
    
    //This hashes the user password using the Hashing class
    private void hashPassword(String password) {
        Hashing hashing = new Hashing(password);
        this.Salt = hashing.getSalt();
        this.HashedPassword = hashing.getHashedPassword();
    }
    
    //username getter
    public String getUsername() {
        return Username;
    }

    //Salt getter
    public byte[] getSalt() {
        return Salt;
    }

    //HashedPassword getter
    public String getHashedPassword() {
        return HashedPassword;
    }
}
