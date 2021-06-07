package org.urop;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.opensymphony.xwork2.ActionSupport;


public class HeatmapAction extends ActionSupport {



//  private List<Store_count> store_list = new ArrayList<Store_count>();

    private Long startTime;
    private Long endTime;
    private String place;
    private Boolean type;
    public void setStartTime(Long startTime){this.startTime = startTime;System.out.println(startTime);}
    public void setEndTime(Long endTime){this.endTime = endTime;}
    public void setPlace(String place){this.place = place;}
    public void setType(Boolean type){this.type = type;}

    private LoginService loginService = new LoginService();
    private FetchHeatMap fetchHeatMap = new FetchHeatMap();
    public String execute() {

        if (loginService.Login() != null){

            Connection conn = loginService.conn;

            try {

                fetchHeatMap.fetchHeatMap(conn,startTime,endTime,place,type);

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


    };



    public HashMap<Integer, String> getStore_name(){
        return fetchHeatMap.store_name;
    }
    public HashMap<Long,HashMap<Integer,Integer>> getPop_over_time(){
        return fetchHeatMap.pop_over_time;
    }
    public Double getPeriod_hrs(){return fetchHeatMap.hours;}



}