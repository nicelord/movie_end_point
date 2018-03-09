/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.avaje.ebean.Ebean;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.UriInfo;
import model.User;

/**
 *
 * @author asus
 */
public class JWTUtil {

    public static final String JWT_KEY = "secret";
    public static final String DOMAIN_ISSUER = "localhost:8084";

    public static String issueToken(UriInfo context, User user) {

        Key key = new SecretKeySpec(JWT_KEY.getBytes(), 0, JWT_KEY.getBytes().length, "DES");
        String jwtToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(context.getBaseUri().getHost() + ":" + context.getBaseUri().getPort())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                //.setExpiration(toDate(LocalDateTime.now().plusSeconds(15L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        return jwtToken;

    }

    public static boolean verifyJWT(String token) throws 
            ExpiredJwtException,
            ClaimJwtException,
            MalformedJwtException,
            SignatureException,
            UnsupportedJwtException,
            IllegalArgumentException {
        Key key = new SecretKeySpec(JWT_KEY.getBytes(), 0, JWT_KEY.getBytes().length, "DES");

        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token).getBody();
        
        System.out.println("ID: " + claims.getId());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Issuer: " + claims.getIssuer());
        System.out.println("Expiration: " + claims.getExpiration());
        
        User userSubject = null;
        try {
            userSubject = Ebean.find(User.class).where().eq("username", claims.getSubject()).findUnique();
        } catch (Exception e) {
            return false;
        }
        return !(userSubject == null || !claims.getIssuer().equals(DOMAIN_ISSUER));
    }
    
    
    public static boolean verifyJWT(String username, String token) throws 
            ExpiredJwtException,
            ClaimJwtException,
            MalformedJwtException,
            SignatureException,
            UnsupportedJwtException,
            IllegalArgumentException {
        Key key = new SecretKeySpec(JWT_KEY.getBytes(), 0, JWT_KEY.getBytes().length, "DES");

        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token).getBody();
        
        if(!username.equals(claims.getSubject())){
            return false;
        }
        
        System.out.println("ID: " + claims.getId());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Issuer: " + claims.getIssuer());
        System.out.println("Expiration: " + claims.getExpiration());
        
        User userSubject = null;
        try {
            userSubject = Ebean.find(User.class).where().eq("username", claims.getSubject()).findUnique();
        } catch (Exception e) {
            return false;
        }
        return !(userSubject == null || !claims.getIssuer().equals(DOMAIN_ISSUER));
    }
    

    private static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static User getUser(String token) {
        Key key = new SecretKeySpec(JWT_KEY.getBytes(), 0, JWT_KEY.getBytes().length, "DES");

        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token).getBody();
        return Ebean.find(User.class).where().eq("username", claims.getSubject()).findUnique();
    }

 

}
