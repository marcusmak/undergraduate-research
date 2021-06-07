package org.urop;

import java.sql.*;
import java.util.HashMap;


import com.opensymphony.xwork2.ActionSupport;


public class LinechartAction extends ActionSupport {

    private HashMap<Long,Integer> pop_over_time = new HashMap<>();

    private Long startTime;
    private Integer store_id;
    private Double period_hrs;
    private Boolean type;
    public void setStartTime(Long startTime){this.startTime = startTime;}
    public void setStore_id(Integer store_id){this.store_id = store_id;}
    public void setPeriod_hrs(Double period_hrs ){this.period_hrs = period_hrs;}
    public void setType(Boolean type){this.type = type;}

    private LoginService loginService = new LoginService();
    private FetchLineChart fetchLineChart = new FetchLineChart();

    public String execute() {

        if (loginService.Login() != null){

            Connection conn = loginService.conn;

            try {

                fetchLineChart.fetchLineChart(conn,startTime,store_id,period_hrs,type);




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


    public HashMap<Long, Integer> getPop_over_time(){
        return fetchLineChart.pop_over_time;
    }



}
