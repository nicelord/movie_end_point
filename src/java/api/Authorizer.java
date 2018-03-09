/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;


import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import model.User;

/**
 *
 * @author asus
 */
public class Authorizer implements SecurityContext {
    User user;

    public Authorizer(User user) {
        this.user = user;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return new JWTPrincipal(user);
    }

    @Override
    public boolean isUserInRole(String string) {
        return true;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }

}
