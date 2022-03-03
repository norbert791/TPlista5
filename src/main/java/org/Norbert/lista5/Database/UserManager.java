package org.Norbert.lista5.Database;

public interface UserManager {
    /**
     * Used for verifying user
     * @param email user email
     * @param password user password
     * @return true iff logged in correctly
     */
    boolean logIn (String email, String password);

    /**
     * Used for registering user
     * @param email email of registered user
     * @param name name of the user to be registered
     * @param password password of registered user
     * @return true iff registered correctly
     */
    boolean register (String email, String name, String password);

    /**
     * Used for retrieving playerName
     * @return User nickName
     * @throws AuthorizationFailed if User isn't signed in
     */
    String getName () throws AuthorizationFailed;
}
