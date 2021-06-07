package org.urop;

import com.opensymphony.xwork2.ActionSupport;

import java.sql.Connection;
import java.sql.DriverManager;


public class LoginService {

        public Connection conn;
        public Connection Login() {
            conn = null;
            try {
                String URL = "jdbc:postgresql://eek123.ust.hk:7023/lanecrawford";
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection(URL, "root", "v1mZo48q2VdjqSn");
                System.out.println("sucess");


            } catch (Exception e) {
                e.printStackTrace();

            }
            return conn;
        }
}
