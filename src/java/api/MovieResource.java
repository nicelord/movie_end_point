/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.text.PathProperties;
import com.avaje.ebean.text.json.JsonElement;
import com.avaje.ebean.text.json.JsonElementArray;
import com.fasterxml.jackson.core.JsonParser;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import com.jaunt.util.HandlerForText;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Genre;
import model.Movie;
import model.Movie2;
import model.Playlist;
import model.Quality;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author asus
 */
@Path("movie")
public class MovieResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MovieResource
     */
    public MovieResource() {
    }

    @GET
    @Path("/ok")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() {
        JSONObject json = new JSONObject();
        json.put("ok", "ok");
        return Response.status(Response.Status.OK).entity(json.toString()).build();

    }

    @GET
    @Path("/latest/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovies(@PathParam("type") String type) {

        List<Movie2> movies = Ebean.find(Movie2.class).where().eq("publish", true)
                .where().eq("type", type)
                .orderBy("postDate desc")
                .setFirstRow(0)
                .setMaxRows(21)
                .findList();
        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "type,"
                + "lastUpdate,"
                + "views,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes(StandardCharsets.UTF_8))));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {

        List<Movie2> movies = Ebean.find(Movie2.class).findList();
        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "type,"
                + "lastUpdate,"
                + "views,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes())));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();
    }

    @GET
    @Path("/title/{title}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByTitle(@PathParam("title") String title, @PathParam("page") String page,
            @QueryParam("sort") String sort) {

        PagedList<Movie2> paged = Ebean.find(Movie2.class)
                .where().eq("publish", true)
                .where().like("title", "%" + title + "%")
                .order().desc("lastUpdate")
                .findPagedList(Integer.parseInt(page) - 1, 21);

        if (sort != null) {
            if (sort.equals("recent_post")) {
                paged = Ebean.find(Movie2.class)
                        .where().eq("publish", true)
                        .where().like("title", "%" + title + "%")
                        .order().desc("postDate")
                        .findPagedList(Integer.parseInt(page) - 1, 21);
            }
        }

        List<Movie2> movies = paged.getList();

        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "lastUpdate,"
                + "views,"
                + "type,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("totalPage", paged.getTotalPageCount());
        json.put("curPage", Integer.parseInt(page));
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes())));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();

    }

    @GET
    @Path("/genre/{genre}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByGenre(@PathParam("genre") String genre, @PathParam("page") String page,
            @QueryParam("sort") String sort) {

        PagedList<Movie2> paged = Ebean.find(Movie2.class)
                .where().eq("genres.name", genre)
                .where().eq("publish", true)
                .order().desc("lastUpdate")
                .findPagedList(Integer.parseInt(page) - 1, 21);

        if (sort != null) {
            if (sort.equals("recent_post")) {
                paged = Ebean.find(Movie2.class)
                        .where().eq("publish", true)
                        .where().eq("genres.name", genre)
                        .order().desc("postDate")
                        .findPagedList(Integer.parseInt(page) - 1, 21);
            }
        }

        int totalPage = paged.getTotalPageCount();
        List<Movie2> movies = paged.getList();
        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "lastUpdate,"
                + "views,"
                + "type,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("totalPage", totalPage);
        json.put("curPage", Integer.parseInt(page));
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes(StandardCharsets.UTF_8))));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();
//        return Response.ok(json.toString()).build();
    }

    @GET
    @Path("/quality/{quality}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByQuality(@PathParam("quality") String quality, @PathParam("page") String page,
            @QueryParam("sort") String sort) {

        PagedList<Movie2> paged = Ebean.find(Movie2.class)
                .where().eq("publish", true)
                .where().eq("streamLinks.quality.quality", quality)
                .order().desc("lastUpdate")
                .findPagedList(Integer.parseInt(page) - 1, 21);

        if (sort != null) {
            if (sort.equals("recent_post")) {
                paged = Ebean.find(Movie2.class)
                        .where().eq("publish", true)
                        .where().eq("streamLinks.quality.quality", quality)
                        .order().desc("postDate")
                        .findPagedList(Integer.parseInt(page) - 1, 21);
            }
        }

        int totalPage = paged.getTotalPageCount();
        List<Movie2> movies = paged.getList();

        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "lastUpdate,"
                + "views,"
                + "type,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("totalPage", totalPage);
        json.put("curPage", Integer.parseInt(page));
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes())));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();

    }

    @GET
    @Path("/resolution/{resolution}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByResolution(@PathParam("resolution") String resolution, @PathParam("page") String page,
            @QueryParam("sort") String sort) {

        PagedList<Movie2> paged = Ebean.find(Movie2.class)
                .where().eq("publish", true)
                .where().eq("streamLinks.resolution.resolution", resolution)
                .order().desc("lastUpdate")
                .findPagedList(Integer.parseInt(page) - 1, 21);

        if (sort != null) {
            if (sort.equals("recent_post")) {
                paged = Ebean.find(Movie2.class)
                        .where().eq("publish", true)
                        .where().eq("streamLinks.resolution.resolution", resolution)
                        .order().desc("postDate")
                        .findPagedList(Integer.parseInt(page) - 1, 21);
            }
        }

        int totalPage = paged.getTotalPageCount();
        List<Movie2> movies = paged.getList();

        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "lastUpdate,"
                + "views,"
                + "type,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("totalPage", totalPage);
        json.put("curPage", Integer.parseInt(page));
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes())));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();
    }

    @GET
    @Path("/country/{country}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByCountry(@PathParam("country") String country, @PathParam("page") String page,
            @QueryParam("sort") String sort) {

        PagedList<Movie2> paged = Ebean.find(Movie2.class)
                .where().eq("publish", true).where()
                .eq("countries.countryName", country)
                .order().desc("lastUpdate")
                .findPagedList(Integer.parseInt(page) - 1, 21);

        if (sort != null) {
            if (sort.equals("recent_post")) {
                paged = Ebean.find(Movie2.class)
                        .where().eq("publish", true).where()
                        .eq("countries.countryName", country)
                        .order().desc("postDate")
                        .findPagedList(Integer.parseInt(page) - 1, 21);
            }
        }

        int totalPage = paged.getTotalPageCount();
        List<Movie2> movies = paged.getList();

        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "lastUpdate,"
                + "views,"
                + "type,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("totalPage", totalPage);
        json.put("curPage", Integer.parseInt(page));
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes())));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();
    }

    @GET
    @Path("/year/{year}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByYear(@PathParam("year") String year, @PathParam("page") String page,
            @QueryParam("sort") String sort) {

        PagedList<Movie2> paged = Ebean.find(Movie2.class)
                .where().eq("publish", true).where()
                .eq("releaseYear", year).order()
                .desc("lastUpdate")
                .findPagedList(Integer.parseInt(page) - 1, 21);

        if (sort != null) {
            if (sort.equals("recent_post")) {
                paged = Ebean.find(Movie2.class)
                        .where().eq("publish", true).where()
                        .eq("releaseYear", year).order()
                        .desc("postDate")
                        .findPagedList(Integer.parseInt(page) - 1, 21);
            }
        }

        List<Movie2> movies = paged.getList();

        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "lastUpdate,"
                + "views,"
                + "type,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("totalPage", paged.getTotalPageCount());
        json.put("curPage", Integer.parseInt(page));
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes())));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();
    }

    @GET
    @Path("/tag/{tag}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByTag(@PathParam("tag") String tag, @PathParam("page") String page,
            @QueryParam("sort") String sort) {

        PagedList<Movie2> paged = Ebean.find(Movie2.class)
                .where().eq("publish", true)
                .where().eq("tags.tagName", tag)
                .order().desc("lastUpdate")
                .findPagedList(Integer.parseInt(page) - 1, 21);

        if (sort != null) {
            if (sort.equals("recent_post")) {
                paged = Ebean.find(Movie2.class)
                        .where().eq("publish", true)
                        .where().eq("tags.tagName", tag)
                        .order().desc("postDate")
                        .findPagedList(Integer.parseInt(page) - 1, 21);
            }
        }

        List<Movie2> movies = paged.getList();

        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "lastUpdate,"
                + "views,"
                + "type,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("totalPage", paged.getTotalPageCount());
        json.put("curPage", Integer.parseInt(page));
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes())));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();
    }

    @GET
    @Path("/actor/{actor}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByActor(@PathParam("actor") String actor, @PathParam("page") String page,
            @QueryParam("sort") String sort) {

        PagedList<Movie2> paged = Ebean.find(Movie2.class)
                .where().eq("publish", true)
                .where().eq("cast.actorName", actor)
                .order().desc("lastUpdate")
                .findPagedList(Integer.parseInt(page) - 1, 21);

        if (sort != null) {
            if (sort.equals("recent_post")) {
                paged = Ebean.find(Movie2.class)
                        .where().eq("publish", true)
                        .where().eq("cast.actorName", actor)
                        .order().desc("postDate")
                        .findPagedList(Integer.parseInt(page) - 1, 21);
            }
        }

        List<Movie2> movies = paged.getList();

        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "lastUpdate,"
                + "views,"
                + "type,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("totalPage", paged.getTotalPageCount());
        json.put("curPage", Integer.parseInt(page));
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes())));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();
    }

    @GET
    @Path("/type/{type}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByType(@PathParam("type") String type, @PathParam("page") String page,
            @QueryParam("sort") String sort) {

        PagedList<Movie2> paged = Ebean.find(Movie2.class)
                .where().eq("publish", true)
                .where().eq("type", type)
                .order().desc("lastUpdate")
                .findPagedList(Integer.parseInt(page) - 1, 21);

        if (sort != null) {
            if (sort.equals("recent_post")) {
                paged = Ebean.find(Movie2.class)
                        .where().eq("publish", true)
                        .where().eq("type", type)
                        .order().desc("postDate")
                        .findPagedList(Integer.parseInt(page) - 1, 21);
            }
        }

        List<Movie2> movies = paged.getList();

        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "releaseYear,"
                + "trailer,"
                + "posterLink,"
                + "rating,"
                + "lastUpdate,"
                + "views,"
                + "type,"
                + "season,"
                + "episode,"
                + "countries((*)countryCode,countryName),"
                + "genres((*)name)"
                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
                + ")");

        JSONArray arr = new JSONArray(Ebean.json().toJson(movies, pathProperties));
        JSONObject json = new JSONObject();
        json.put("totalPage", paged.getTotalPageCount());
        json.put("curPage", Integer.parseInt(page));
        json.put("movies", arr);

        JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
        kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(json.toString()).reverse().toString()).getBytes())));
        kc.put("ts", new Date().getTime());
        kc.put("token", UUID.randomUUID().toString());

        return Response.ok(kc.toString()).build();
    }

    @GET
    @Path("/detail/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        String mId = null;
        try {
            mId = id.substring(id.lastIndexOf(".") + 1);
            Movie2 movie = Ebean.find(Movie2.class, mId);
            if (movie == null) {
                JSONObject json = new JSONObject();
                json.put("error", "not found");
                return Response.status(Response.Status.NOT_FOUND).entity(json.toString()).build();
            }
            PathProperties pathProperties = PathProperties.parse("("
                    + "movieId,"
                    + "title,"
                    + "synopsis,"
                    + "releaseYear,"
                    + "imdbId,"
                    + "trailer,"
                    + "posterLink,"
                    + "bigPosterLink,"
                    + "fileSize,"
                    + "rating,"
                    + "duration,"
                    + "type,"
                    + "postDate,"
                    + "lastUpdate,"
                    + "user(name),"
                    + "views,"
                    + "season,"
                    + "episode,"
                    + "playlist((*)playlistId,name),"
                    + "countries((*)countryCode,countryName),"
                    + "genres((*)name),"
                    + "streamLinks((*)streamLinkId,serverSource(serverName),quality(qualityId,quality),resolution(resolution),link,isIframe)),"
                    + "cast((*)actorName),"
                    + "tags((*)tagName)"
                    + ")");

            JSONObject kc = new JSONObject();
//        json2.put("kc", rot13(Base64.getEncoder().encodeToString(new StringBuilder(rot13(json.toString())).reverse().toString().getBytes())));
            kc.put("kc", rot13(Base64.getEncoder().encodeToString(rot13(new StringBuilder(Ebean.json().toJson(movie, pathProperties)).reverse().toString()).getBytes(StandardCharsets.UTF_8))));
            kc.put("ts", new Date().getTime());
            kc.put("token", UUID.randomUUID().toString());

            return Response.ok(kc.toString()).build();
//            return Response.ok(Ebean.json().toJson(movie, pathProperties)).build();

        } catch (Exception e) {
            JSONObject json = new JSONObject();
            json.put("error", "not implemeted");
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(json.toString()).build();
        }
    }

    //menus
    @GET
    @Path("/genres")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGenres() {
        List<Genre> genres = Ebean.find(Genre.class).orderBy("genreId desc").findList();
        PathProperties pathProperties = PathProperties.parse("(name)");
        return Response.ok(Ebean.json().toJson(genres, pathProperties)).build();
    }

    @GET
    @Path("/years")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllYears() {
        List<Movie2> year = Ebean.find(Movie2.class).select("releaseYear").orderBy("releaseYear desc").setDistinct(true).findList();
        PathProperties pathProperties = PathProperties.parse("(releaseYear)");
        return Response.ok(Ebean.json().toJson(year, pathProperties)).build();
    }

    @GET
    @Path("/qualities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllQualities() {
        List<Quality> quality = Ebean.find(Quality.class).select("quality").setDistinct(true).findList();
        PathProperties pathProperties = PathProperties.parse("(quality)");
        return Response.ok(Ebean.json().toJson(quality, pathProperties)).build();
    }

    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterCriteria(
            @QueryParam("from") int from,
            @QueryParam("to") int to,
            @QueryParam("orderBy") List<String> orderBy) {

        List<Quality> quality = Ebean.find(Quality.class).select("quality").setDistinct(true).findList();
        PathProperties pathProperties = PathProperties.parse("(quality)");
        return Response.ok(Ebean.json().toJson(quality, pathProperties)).build();
    }

    @GET
    @Path("/gostream_token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response gosTreamToken() {

        UserAgent userAgent = new UserAgent();
        userAgent.settings.defaultRequestHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

        HandlerForText handler = new HandlerForText();
        userAgent.setHandler("application/javascript", handler);
        try {
            userAgent.sendGET("http://gostream.is/ajax/movie_token?eid=1101069&mid=22762&_=" + System.currentTimeMillis());
            String x = handler.getContent().split(",")[0].split("'")[1];
            String y = handler.getContent().split(",")[1].split("'")[1];

            handler = new HandlerForText();
            userAgent.setHandler("text/html", handler);
            userAgent.sendGET("http://gostream.is/ajax/movie_sources/1101069?x=" + x + "&y=" + y);

        } catch (ResponseException ex) {
            Logger.getLogger(MovieResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(handler.getContent()).build();
    }

    @GET
    @Path("/playlist/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByPlaylist(@PathParam("id") String id) {

        List<Movie2> movies = Ebean.find(Movie2.class).where().eq("playlist.playlistId", id).order().desc("lastUpdate").findList();

        PathProperties pathProperties = PathProperties.parse("("
                + "movieId,"
                + "title,"
                + "season,"
                + "episode,"
                + ")");
        return Response.ok(Ebean.json().toJson(movies, pathProperties)).build();
    }

    public static String rot13(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c >= 'a' && c <= 'm') {
                c += 13;
            } else if (c >= 'A' && c <= 'M') {
                c += 13;
            } else if (c >= 'n' && c <= 'z') {
                c -= 13;
            } else if (c >= 'N' && c <= 'Z') {
                c -= 13;
            }
            sb.append(c);
        }
        return sb.toString();
    }

}
