package gt.app.config.security;

import java.util.UUID;

public class CurrentUserToken   { //this is our principal object

    private AppUserDetails user;

    public AppUserDetails getUser() {
        return user;
    }

    public UUID getUserId() {
        return user.getUserId();
    }


    public String getUsername() {
        return user.getUsername();
    }

}
