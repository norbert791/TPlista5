package org.Norbert.lista5.Database;

public interface UserManager {
    /**
     * Used for verifying user
     * @param email user email
     * @param password user password
     * @throws AuthorizationFailed thrown if password does not match email or user does not exist
     */
    String logIn (String email, String password) throws AuthorizationFailed;

    /**
     * Used for registering user
     * @param email email of registered user
     * @param name name of the user to be registered
     * @param password password of registered user
     * @throws AuthorizationFailed Thrown if email was already used for registration or
     * name/password don't fulfill requirements (e.g. minimum length)
     */
    void register (String email, String name, String password) throws AuthorizationFailed;
}
