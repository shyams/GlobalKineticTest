/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GlobalKinetic;

import com.google.common.collect.HashBiMap;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * REST Web Service
 *
 * @author shyam
 */
public class UsersLoggedIn_1 {

    URL pathURL = UsersLoggedIn.class.getResource("config/users.csv");
    URL copyPath = UsersLoggedIn.class.getResource("config/copy.csv");
    @Context
    private UriInfo context;
    @Context
    private HttpServletRequest request;
    private static final char SEPARATOR = ',';

    /**
     * Creates a new instance of GenericResource
     */
    public UsersLoggedIn_1() {
    }

    /**
     * Retrieves representation of an instance of GlobalKinetic.GenericResource
     *
     * @return an instance of java.lang.String
     */
//    @GET
//    @Produces("application/json")
    public String getJSON() {

        File file = new File(pathURL.getPath());
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();

        try {
            if (file.canRead() && file.exists()) {
                br = new BufferedReader(new FileReader(file.getAbsolutePath()));
                while ((line = br.readLine()) != null) {
                    if (!line.isEmpty()) {
                        String[] coloumns = line.split(cvsSplitBy);
                        if (coloumns[3].equalsIgnoreCase("yes")) {
                            JSONObject dataObject = new JSONObject();
                            dataObject.put("id", coloumns[1]);
                            dataObject.put("phone", coloumns[2]);
                            ja.add(dataObject);
                        }
                    }
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (ja.size() > 0) {
            JSONObject users = new JSONObject();
            users.put("users", ja);
            jo.put("responseBody", users);
            return jo.toJSONString();
        } else {
            jo.put("msg", "There are no users loggedIn");
            return jo.toJSONString();
        }

    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
//    @PUT
//    @Consumes("application/json")
    public void putJSON(String registrationDetails) {
        String strJSON = null;
        Writer writer = null;
        try {
            if (registrationDetails.length() > 0) {

                JSONObject reqObj = (JSONObject) JSONValue.parse(registrationDetails);
                System.out.println(reqObj.toJSONString() + " lkjdfnvkl");

                String username = (String) reqObj.get("username");
                String pass = (String) reqObj.get("password");
                String phone = (String) reqObj.get("phone");

                writer = new BufferedWriter(new FileWriter(pathURL.getPath(), true));
                writer.write("\r\n");
                writer.write(pass + "," + username + "," + phone + "," + "y," + "" + "," + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {/*ignore*/

                ex.printStackTrace();
            }
        }
    }

//    @POST
//    @Consumes("application/json")
    public String postJSON(String loginDetails) {
        String strJSON = null;
        Writer writer = null;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String ssnId = null;
        String userid = null;
        int rowCount = 0;
        List<String[]> aList = new ArrayList<>();
        try {
            UserData ud = new UserData();
//            String uderData = ud.processRequest(request);
            System.out.println(loginDetails + " lkjdfnvkl" + loginDetails.length());
            if (loginDetails.length() > 0) {
                writer = new BufferedWriter(new FileWriter(pathURL.getPath(), true));
                JSONObject reqObj = (JSONObject) JSONValue.parse(loginDetails);
                System.out.println(reqObj.toJSONString() + " lkjdfnvkl");

                String username = (String) reqObj.get("username");
                String pass = (String) reqObj.get("password");
                System.out.println(reqObj.toJSONString() + username + pass + " lkjdfnvkl");
                File file = new File(pathURL.getPath());
                if (file.canRead() && file.exists()) {
                    System.out.println(reqObj.toJSONString() + username + pass + " exits");
                    br = new BufferedReader(new FileReader(file.getAbsolutePath()));

                    while ((line = br.readLine()) != null) {

                        if (!line.isEmpty()) {
                            System.out.println(reqObj.toJSONString() + username + pass + " line");
                            String[] coloumns = line.split(cvsSplitBy);
                            aList.add(coloumns);
                            if (coloumns[0].equalsIgnoreCase(pass) && coloumns[1].equalsIgnoreCase(username)) {
                                System.out.println(reqObj.toJSONString() + username + pass + " lineexists");

//                                writer.write();
                                HttpSession hs = request.getSession(true);
                                ssnId = hs.getId();
                                aList.get(rowCount)[3] = "y";
                                aList.get(rowCount)[4] = ssnId;
                                aList.get(rowCount)[5] = Long.toString(System.currentTimeMillis());
                                userid = coloumns[1];
                            }

                            System.out.println("4325465755764534sv " + aList.get(rowCount)[3] + aList.get(rowCount)[4]);
                        }
                        rowCount++;
                    }
                    updateCsvFile(aList);
                    System.out.println("ksjdhvfkjdsfdsv " + aList.size() + aList.get(0).length);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject jso = new JSONObject();
        if (ssnId != null && userid != null) {
            JSONObject innerObj = new JSONObject();
            innerObj.put("id", userid);
            innerObj.put("token", ssnId);
            jso.put("responseBody", innerObj);

        } else {

            jso.put("msg", "Invalid Credentials");
        }
        return jso.toJSONString();
    }

    public void updateCsvFile(List alist) {

        PrintWriter writer = null;
        try {
            
            writer = new PrintWriter(pathURL.getPath());
            writer.close();
            
            Writer bfwriter = new BufferedWriter(new FileWriter(pathURL.getPath(), true));

            for (int i = 0; i < alist.size(); i++) {
                bfwriter.write("\r\n");
                String[] sa = (String[]) alist.get(i);

                StringJoiner sj = new StringJoiner(",");
                for (String s : sa) {
                    sj.add(s);
                }
                System.out.println(sj);
                 bfwriter.write(sj.toString());
                 
            }
            bfwriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
