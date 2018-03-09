/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.PathProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import static javax.ws.rs.HttpMethod.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.UserSubs;
import org.json.JSONObject;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * REST Web Service
 *
 * @author asus
 */
@Path("subs")
public class SubsResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SubsResource
     */
    public SubsResource() {
    }

    @GET
    @Path("/external_loader/{url : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response externalLoader(@PathParam("url") String urlx) {

        List<UserSubs> lisSubs = new ArrayList<>();
        try {
            URL url = new URL(urlx);
            URLConnection connection = url.openConnection();
            connection.connect();
            int fileSize = connection.getContentLength();
            if(fileSize >= 1000000){
                return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("error", "Too big..!").toString()).build();
            }
                
            if (urlx.endsWith(".srt")) {
                InputStreamReader isr = new InputStreamReader(url.openStream());
                BufferedReader reader = new BufferedReader(isr);
                String s = reader.readLine();
                StringBuilder sb = new StringBuilder();
                while (s != null) {
                    sb.append(s).append("\n");
                    s = reader.readLine();
                }
                UserSubs us = new UserSubs();
                us.setFileName(urlx.substring(urlx.lastIndexOf("/"), urlx.length()).replace("/", ""));
                us.setSignature(UUID.randomUUID().toString());
                us.setContent(sb.toString());
                Ebean.save(us);
                lisSubs.add(us);

                PathProperties pathProperties = PathProperties.parse("(fileName,signature)");
                return Response.ok(Ebean.json().toJson(lisSubs, pathProperties)).build();

            }

            ZipInputStream zin = new ZipInputStream(url.openStream());
            ZipEntry ze = zin.getNextEntry();

            while (ze != null && ze.getName().endsWith(".srt")) {
                UserSubs us = new UserSubs();
                us.setFileName(ze.getName());
                us.setSignature(UUID.randomUUID().toString());

                InputStreamReader isr = new InputStreamReader(zin);
                BufferedReader reader = new BufferedReader(isr);
                String s = reader.readLine();
                StringBuilder sb = new StringBuilder();
                while (s != null) {
                    sb.append(s).append("\n");
                    s = reader.readLine();
                }

                us.setContent(sb.toString());
                Ebean.save(us);
                lisSubs.add(us);

                ze = zin.getNextEntry();
            }
            
            if (lisSubs.size() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("error", "No sub(s) there!").toString()).build();
            }

            PathProperties pathProperties = PathProperties.parse("(fileName,signature)");
            return Response.ok(Ebean.json().toJson(lisSubs, pathProperties)).build();

        } catch (IOException ex) {
            //return Response.ok(new JSONObject().put("error", "FAILED FETCHING DATA").toString()).build();
            return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("error", "Fetch data failed.").toString()).build();
        }
    }

    @GET
    @Path("/sub_sign/{uuid : .+}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response loadUserSub(@PathParam("uuid") String uuid) {
        UserSubs us = Ebean.find(UserSubs.class).where().eq("signature", uuid).findUnique();
        if (us == null) {
            return Response.ok(new JSONObject().put("error", "NO DATA").toString()).build();
        }
        String content = us.getContent();
        Ebean.delete(us);
        return Response.ok(content).header("Content-Type", "application/x-subrip").build();
    }

    @DELETE
    @Path("/sub_sign/{uuid : .+}")
    public Response clearSub(@PathParam("uuid") String uuid) {
        try {
            UserSubs us = Ebean.find(UserSubs.class).where().eq("signature", uuid).findUnique();
            if (us != null) {
                Ebean.delete(us);
            }
            return Response.ok().build();
        } catch (OptimisticLockException e) {
            return Response.ok().build();
        }
    }

    @POST
    @Path("/external_loader/upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadUserSub(
            @HeaderParam("Content-Length") long length,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        String fileName = fileDetail.getFileName();

        if (length > 1000000) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("error", "Too big..!").toString()).build();
        }

        if (!fileDetail.getFileName().endsWith(".zip") && !fileDetail.getFileName().endsWith(".srt")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("error", "Invalid file").toString()).build();
        }

        List<UserSubs> lisSubs = new ArrayList<>();

        if (fileName.endsWith(".srt")) {
            try {
                InputStreamReader isr = new InputStreamReader(uploadedInputStream);
                BufferedReader reader = new BufferedReader(isr);
                String s = reader.readLine();
                StringBuilder sb = new StringBuilder();
                while (s != null) {
                    sb.append(s).append("\n");
                    s = reader.readLine();
                }
                UserSubs us = new UserSubs();
                us.setFileName(fileName);
                us.setSignature(UUID.randomUUID().toString());
                us.setContent(sb.toString());
                Ebean.save(us);
                lisSubs.add(us);

                PathProperties pathProperties = PathProperties.parse("(fileName,signature)");
                return Response.ok(Ebean.json().toJson(lisSubs, pathProperties)).build();
            } catch (IOException ex) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("error", "Invalid file").toString()).build();
            }

        }

        try {
            ZipInputStream zin = new ZipInputStream(uploadedInputStream);
            ZipEntry ze = zin.getNextEntry();

            while (ze != null && ze.getName().endsWith(".srt")) {
                UserSubs us = new UserSubs();
                us.setFileName(ze.getName());
                us.setSignature(UUID.randomUUID().toString());

                InputStreamReader isr = new InputStreamReader(zin);
                BufferedReader reader = new BufferedReader(isr);
                String s = reader.readLine();
                StringBuilder sb = new StringBuilder();
                while (s != null) {
                    sb.append(s).append("\n");
                    s = reader.readLine();
                }

                us.setContent(sb.toString());
                Ebean.save(us);
                lisSubs.add(us);

                ze = zin.getNextEntry();
            }

            if (lisSubs.size() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("error", "No sub(s) there!").toString()).build();
            }
            PathProperties pathProperties = PathProperties.parse("(fileName,signature)");
            return Response.ok(Ebean.json().toJson(lisSubs, pathProperties)).build();

        } catch (IOException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("error", "Invalid file").toString()).build();
        }

    }

}
