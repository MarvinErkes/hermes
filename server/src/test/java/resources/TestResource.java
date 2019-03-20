/*
 * Copyright (c) 2017 "Marvin Erkes"
 *
 * This file is part of Hermes.
 *
 * Hermes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package resources;

import de.progme.hermes.server.http.Request;
import de.progme.hermes.server.http.annotation.FormParam;
import de.progme.hermes.server.http.annotation.Path;
import de.progme.hermes.server.http.annotation.PathParam;
import de.progme.hermes.server.http.annotation.Produces;
import de.progme.hermes.server.http.annotation.method.GET;
import de.progme.hermes.server.http.annotation.method.POST;
import de.progme.hermes.shared.ContentType;
import de.progme.hermes.shared.http.Response;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marvin Erkes on 30.01.2016.
 */
@Path("/master")
public class TestResource {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @POST
    @Path("/info")
    @Produces(ContentType.TEXT_HTML)
    public Response info(Request httpRequest, @FormParam("name") String name, @FormParam("password") String password) {

        System.out.println("Name: " + name);
        System.out.println("Password: " + password);

        return Response.ok().content("<H1>info " + atomicInteger.getAndIncrement() + "</H1>").header("X-Test", "Hello :D").build();
    }

    @GET
    @Path("/test")
    public Response test(Request httpRequest) {

        return Response.ok().content("<H1>test</H1>").build();
    }

    @GET
    @Path("/hello/{name}")
    public Response hello(Request httpRequest, @PathParam String name) {


        return Response.ok().content("<H1>Hello " + name + "!</H1>").build();
    }

    @GET
    @Path("/bye/{name}/{name2}")
    public Response bye(Request httpRequest, @PathParam String name, @PathParam String name2) {


        return Response.ok().content("<H1>Bye " + name + " " + name2 + "!</H1>").build();
    }

    @GET
    @Path("/download")
    @Produces(ContentType.APPLICATION_OCTET_STREAM)
    public Response download(Request httpRequest) {

        return Response.file("");
        //return Response.file(new File("C:\\Hermes.txt"));
    }
}
