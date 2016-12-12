/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GlobalKinetic;

import com.google.common.collect.HashBiMap;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.apache.catalina.mapper.Mapper;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author shyam
 */
public class UserData extends HttpServlet {

    URL pathURL = UsersLoggedIn.class.getResource("config/users.csv");
    @Context
    private HttpServletRequest request;

    public static Connection getConnection() {
        Properties props = new Properties();
        FileReader output = null;
        Connection con = null;
        try {
            output = new FileReader("C:\\GlobalKineticTest\\db.properties");
            props.load(output);

            // load the Driver Class
            Class.forName("org.postgresql.Driver");

            // create the connection now
            con = DriverManager.getConnection(props.getProperty("DB_URL"),
                    props.getProperty("DB_USERNAME"),
                    props.getProperty("DB_PASSWORD"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return con;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected String processRequest(String jsonStr, String sessionId, String requestType) {
        Connection myConn = null;
        Statement myStatement = null;
        ResultSet myRS = null;
        String userName = null;
        String sesssionid = null;
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        try {

            String user = null;
            String pass = null;
            String rowId = null;
            String userPhone = null;
            String token = null;
            String loginStatus = null;
            org.json.JSONObject jsonData = null;
            if (jsonStr != null) {
                System.out.println("ijrjdgjre"+jsonStr);
                jsonData = new org.json.JSONObject(jsonStr);              

                     if (jsonData.has("username")) {
                        user = jsonData.get("username").toString();
                    }
                    if (jsonData.has("password")) {
                        pass = jsonData.get("password").toString();
                    }
                    if (jsonData.has("token")) {
                        token = jsonData.get("token").toString();
                    }
                    if (jsonData.has("id")) {
                        user = jsonData.get("id").toString();
                    }
               
  
            }

            // 1.0 Get a connection
            myConn = getConnection();

            // 2.0 Create a statement
            myStatement = myConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.HOLD_CURSORS_OVER_COMMIT);

            // 3.0 Execute SQL query
            if (requestType.equalsIgnoreCase("POST")) {
                if (token != null) {
                    System.out.println("");
                    myRS = myStatement.executeQuery("SELECT * FROM users "
                            + "WHERE key = '" + token + "'");
                } else {
                    myRS = myStatement.executeQuery("SELECT * FROM users "
                            + "WHERE username = '" + user + "'" + " AND password = '"
                            + pass + "'");
                }

            } else if (requestType.equalsIgnoreCase("PUT")) {
                System.out.println("PUT lkjdnhfgvjkdfnjkgvn " + user);
                userPhone = jsonData.get("phone").toString();
                myRS = myStatement.executeQuery("SELECT * FROM users "
                        + "WHERE username = '" + user + "'");
            } else if (requestType.equalsIgnoreCase("GET")) {
                myRS = myStatement.executeQuery("SELECT * FROM users");
            }
            System.out.println("mrs closed " + myRS.isClosed());
//            if (!myRS.isClosed()) {
            // 4. Process the result set
            while (myRS.next()) {
                String id = myRS.getString("id");
                String uName = myRS.getString("username");
                String password = myRS.getString("password");
                String phone = myRS.getString("phone");
                String status = myRS.getString("status");
                String key = myRS.getString("key");
                String time = myRS.getString("logintime");

                if (sessionId != null && uName != null
                        && uName.equalsIgnoreCase(user)
                        && password != null
                        && password.equalsIgnoreCase(pass)) {
                    System.out.println("hello 1");
                    userName = uName;
                    rowId = id;
                    if (userName != null) {
                        String currentTime = String.valueOf(System.currentTimeMillis());
                        jo.put("id", userName);
                        jo.put("time", currentTime);
                        if (sessionId != null) {
                            System.out.println("key " + key);
                            jo.put("token", sessionId);
                            myStatement.executeUpdate("UPDATE users SET status ='y', key='" + sessionId + "', logintime='" + currentTime + "' WHERE id =" + rowId);
                        }
                    }

                }
                //checks post request to logout the user
                if (token != null) {
                    System.out.println("hello 2");
                    if (token.equalsIgnoreCase(key)) {
                        myStatement.executeUpdate("UPDATE users SET status ='n', key='" + null + "', logintime='" + null + "' WHERE id =" + id);
                    } else if (user.equalsIgnoreCase(uName)) {
                        myStatement.executeUpdate("UPDATE users SET status ='n', key='" + null + "', logintime='" + null + "' WHERE id =" + id);
                    }
                }
                //checks put request if user exists or not
                if (sessionId == null && token == null && uName != null && uName.equalsIgnoreCase(user)) {
                    userName = uName;
                    if (userName != null) {
                        jo.put("error", "Username Already Exists");
                    }
                }
                if (sessionId == null && jsonStr == null && status.equalsIgnoreCase("y")) {
                    JSONObject dataObject = new JSONObject();
                    dataObject.put("id", uName);
                    dataObject.put("phone", phone);
                    dataObject.put("time", time);
                    ja.add(dataObject);
                }
            }
//            }
            if (userName == null && user != null && pass != null && userPhone != null) {
                myStatement.executeUpdate("INSERT INTO users (username, password, phone,status,key,logintime) "
                        + "VALUES('" + user + "', '" + pass + "', '" + userPhone + "', '" + "n" + "', '" + null + "', '" + null + "')");
            }
            if (ja.size() > 0) {
                jo.put("users", ja);
            }
        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (myRS != null) {
                    myRS.close();
                }
                if (myStatement != null) {
                    myStatement.close();
                }
                if (myConn != null) {
                    myConn.close();
                }
            } catch (Exception ex) {
//                logger.log(Level.WARNING, ex.getMessage(), ex);
                ex.printStackTrace();
            }
        }
        return jo.toJSONString();
    }
}
