/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.util.Set;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 * @author asus
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
 //features
        //this will register Jackson JSON providers
        //resources.add(org.glassfish.jersey.jackson.JacksonFeature.class);
        //we could also use this:
        //resources.add(com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider.class);
        resources.add(MultiPartFeature.class);
        addRestResourceClasses(resources);
        return resources;
    } 

    private void addRestResourceClasses(Set<Class<?>> resources) {
        
        resources.add(api.APIService.class);
        resources.add(api.CORSFilter.class);
        resources.add(api.JWTAuthFilter.class);
        resources.add(api.MovieResource.class);
        resources.add(api.SubsResource.class);
    }
    
}
