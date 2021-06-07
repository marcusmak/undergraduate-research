package org.urop;

import com.opensymphony.xwork2.ActionSupport;

import java.sql.Connection;
import java.util.HashMap;

public class HisAction extends ActionSupport {
    private Long startTime, endTime;
    private String place;
    private Double class_width;

    public void setStartTime(Long startTime){this.startTime = startTime;}
    public void setEndTime(Long endTime){this.endTime = endTime;}
    public void setPlace(String place){this.place = place; System.out.println(place);}
    public void setClass_width(Double class_width){this.class_width = class_width;}


    private LoginService loginService = new LoginService();
    private FetchHistogram fetchHistogram = new FetchHistogram();
    public String execute() {

        if (loginService.Login() != null){

            Connection conn = loginService.conn;

            try {

                fetchHistogram.fetchHistogram(conn,startTime,endTime,place,class_width);




                return SUCCESS;

            }catch(Exception e){
                e.printStackTrace();
                return ERROR;
            }finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Exception e) {
                    }
                }
            }

        }else {

            return ERROR;

        }


    }


    public HashMap<Double, Integer> getExpected_return(){
        return fetchHistogram.expected_return;
    }

    public Integer getTime_unit(){
        return fetchHistogram.time_unit;
    }

    public Double getClass_width(){
        return fetchHistogram.class_width;
    }
}
