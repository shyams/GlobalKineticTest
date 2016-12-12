/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GlobalKinetic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * REST Web Service
 *
 * @author shyam
 */
@Path("users")
public class UsersLoggedIn {

    @Context
    private HttpServletRequest request;


    /**
     * Creates a new instance of GenericResource
     */
    public UsersLoggedIn() {
    }

    /**
     * Retrieves representation of an instance of GlobalKinetic.GenericResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJSON() {

        String userid = null;
        String userData = null;
        JSONObject jso = null;

        try {

            UserData ud = new UserData();
            userData = ud.processRequest(null, null, "GET");
            System.out.println("hello" + userData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        jso = (JSONObject) JSONValue.parse(userData);
        return jso.toJSONString();
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJSON(String registrationDetails) {
        String userid = null;
        String userData = null;

        try {
            if (registrationDetails.length() > 0) {
                UserData ud = new UserData();
                userData = ud.processRequest(registrationDetails, null, "PUT");
                System.out.println("hello" + userData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    @Consumes("application/json")
    public String postJSON(String loginDetails) {
        HttpSession hs = request.getSession(true);
        String ssnId = hs.getId();
        String userid = null;
        String userData = null;
        JSONObject jso = new JSONObject();

        try {
            if (loginDetails.length() > 0) {
                UserData ud = new UserData();
                JSONObject jsonVal = (JSONObject) JSONValue.parse(loginDetails);
//                if (jsonVal.containsKey("token")) {
//                    userData = ud.processRequest(loginDetails, null, "POST");
//                } else {
                    userData = ud.processRequest(loginDetails, ssnId, "POST");
//                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userData != null && userData.length() > 0) {
            JSONObject innerObj = new JSONObject();
            innerObj.put("id", userid);
            innerObj.put("token", ssnId);
            jso = (JSONObject) JSONValue.parse(userData);

        } else {
            jso.put("msg", "Invalid Credentials");
        }
        return jso.toJSONString();
    }
}
