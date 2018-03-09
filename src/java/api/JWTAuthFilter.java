/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Priority;
import javax.ws.rs.NameBinding;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.json.JSONObject;

/**
 *
 * @author asus
 */
@JWTAuthFilter.Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        

        // Get the HTTP Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            JSONObject json = new JSONObject();
            json.put("error", "Forbidden");
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(json.toString()).type(MediaType.APPLICATION_JSON).build());
            return;
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {

            // Validate the token
            if (!JWTUtil.verifyJWT(token)) {
                JSONObject json = new JSONObject();
                json.put("error", "invalid");
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(json.toString()).type(MediaType.APPLICATION_JSON).build());
            }else{
                
                requestContext.setSecurityContext(new Authorizer(JWTUtil.getUser(token)));
            }

        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException e) {
            JSONObject json = new JSONObject();
            json.put("error", e.getMessage());
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(json.toString()).type(MediaType.APPLICATION_JSON).build());
        }

    }

    @NameBinding
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Secured {
    }

}
