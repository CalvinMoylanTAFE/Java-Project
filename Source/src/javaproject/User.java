package javaproject;

public class User {
    private String Username;
    private byte[] Salt;
    private String HashedPassword;
    
    public User(String Username, String Password) {
        this.Username = Username;
        HashPassword(Password);
    }
    
    private void HashPassword(String password) {
        Hashing hashing = new Hashing(password);
        this.Salt = hashing.getSalt();
        this.HashedPassword = hashing.getHashedPassword();
    }

    public String getUsername() {
        return Username;
    }

    public byte[] getSalt() {
        return Salt;
    }

    public String getHashedPassword() {
        return HashedPassword;
    }
}
