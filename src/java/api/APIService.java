/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.PathProperties;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import model.User;
import org.json.JSONObject;
;

/**
 * REST Web Service
 *
 * @author asus
 */
@Path("api")
public class APIService {

    @Context
    private UriInfo context;

    @Context
    SecurityContext sctx;

    /**
     * Creates a new instance of APIService
     */
    public APIService() {
    }

    @GET
    @Path("/ok")
    @JWTAuthFilter.Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response secure() {
        return Response.ok(new JSONObject().put("message", "secured area").toString()).build();
    }

    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(String data) {
        User u = Ebean.json().toBean(User.class, data);
        User user = Ebean.find(User.class).where().eq("username", u.getUsername())
                .where().eq("password", u.getPassword()).findUnique();
        if (user == null) {
            JSONObject json = new JSONObject();
            json.put("error", "invalid credential");
            return Response.status(Response.Status.FORBIDDEN).entity(json.toString()).build();
        }

        JSONObject json = new JSONObject();
        json.put("token", JWTUtil.issueToken(context, user));

        return Response.ok().entity(json.toString()).build();
    }
    
    
//    @GET
//    @Path("/products")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response products() {
//        
//        List<Product> products = Ebean.find(Product.class).setMaxRows(10).findList();
//        PathProperties pathProperties = PathProperties.parse("(name,imgUrl,description)");
//        return Response.ok(Ebean.json().toJson(products, pathProperties)).build();
//    }
    
    @GET
    @Path("/protected")
    @JWTAuthFilter.Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response protectedResources() {
        List<User> products = Ebean.find(User.class).setMaxRows(10).findList();
        PathProperties pathProperties = PathProperties.parse("(name,imgUrl,description)");
        return Response.ok(Ebean.json().toJson(products)).build();
    }
    
    @GET
    @Path("/verify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyToken(@QueryParam("username") String username, @QueryParam("token") String token) {
        try{
            return Response.ok(new JSONObject().put("result", JWTUtil.verifyJWT(username,token)).toString()).build();
        }catch(Exception e){
            return Response.ok(new JSONObject().put("result", "false").toString()).build();
        }
    }
    
    
//    @GET
//    @Path("/users")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getUsers() {
//         
//        Product p = Ebean.find(Product.class,1);
//        PathProperties pp = PathProperties.parse(""
//                + "("
//                + "name,"
//                + "description"
//                + ")"
//                + "");
//        return Ebean.json().toJson(p,pp);
//    }
    
    

    //    @GET
//    @Path("/TTSS")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getTTSS() {
//        List<TTSS> listTTSS = Ebean.find(TTSS.class).setMaxRows(10)
//                .where().eq("tipe", "masuk").findList();
//        JsonContext jc = Ebean.createJsonContext();
//        return Response.status(200).entity(jc.toJson(listTTSS)).build();
//    }
//    @GET
//    @Path("/TTSS2")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getTTSS2() {
//        List<TTSS> listTTSS = Ebean.find(TTSS.class).setMaxRows(10)
//                .where().eq("tipe", "masuk").findList();
//
//        PathProperties pathProperties = PathProperties.parse("(*,userLogin(nama,akses,defPrinter(namaPrinter)))");
//
//        return Response.ok(Ebean.json().toJson(listTTSS, pathProperties)).build();
//    }
//    @GET
//    @Path("/echofree")
//    public Response echo(@QueryParam("message") String message) {
//        return Response.ok().entity(message == null ? "no message" : message).build();
//    }
}
