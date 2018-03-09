/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.avaje.ebean.Ebean;
import java.security.Principal;
import model.User;

/**
 *
 * @author asus
 */
public class JWTPrincipal implements Principal {

    User user;

    public JWTPrincipal(User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    
    
    

}
