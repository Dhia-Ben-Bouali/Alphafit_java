package services;

import entite.user;

public class LoggedInUserManager {
    private static LoggedInUserManager instance;
    private user loggedInUser;

    private LoggedInUserManager() {
        // Private constructor to prevent instantiation
    }

    public static LoggedInUserManager getInstance() {
        if (instance == null) {
            instance = new LoggedInUserManager();
        }
        return instance;
    }

    public user getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(user user) {
        loggedInUser = user;
    }
    public void updateLoggedInUser(user user) {
        loggedInUser = user;
    }
}
