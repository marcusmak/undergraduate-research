package org.urop;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FetchHistogram {


    public HashMap<Double,Integer> expected_return = new HashMap<>();
    public Integer time_unit;
    public Double  class_width;
    public String place;
    private String getSQL(Long startTime, Long endTime, String place, Double class_width){
        return "SELECT first.did, (MAX(t)+("+ class_width +")*3600-MIN(t)) as duration,SUM(first.c) as count from " +
                "  (SELECT did, FLOOR(FLOOR((start_ts+28800000)*1./("+class_width+")/60/60/1000)*("+class_width+")*60*60)-28800 as t," +
                " COUNT(DISTINCT did) as c FROM public.dwells LEFT JOIN stores ON dwells.store_id=stores.id WHERE stores.areaid='"+place+"'" +
                " and store_id <> 0 and did <> 0 and start_ts BETWEEN "+startTime+" and "+endTime+" GROUP BY did, t  ORDER BY  c DESC)" +
                " as first" +
                " GROUP BY first.did";

    }

    public void fetchHistogram(Connection conn, Long startTime, Long endTime, String place, Double class_width){

        try{


            Statement stmt = conn.createStatement();
            System.out.println(getSQL(startTime,endTime,place,class_width));
            ResultSet rs = stmt.executeQuery(getSQL(startTime,endTime,place,class_width));

            time_unit  = 3600;
            this.class_width = class_width;
            this.place = place;

            Double start_class = class_width;
            System.out.println(class_width);
            Double upper_bound = Math.ceil((endTime-startTime)/time_unit/1000/class_width);
            expected_return.put(-1.0,0);
            for(int i=1; i<=upper_bound;++i)
            {
                expected_return.put( Math.round(start_class * 10) / 10.0 , 0);
                start_class = start_class + class_width;

            };



            while (rs.next()) {
                Long duration = rs.getLong("duration");
                Integer count = rs.getInt("count");
                if (count <= 1) {
                    expected_return.put(-1.0, expected_return.get(-1.0) + 1);
                }else{
                    Double average = duration*1.0/(count-1);
                    Double key = Math.floor(average/(class_width*time_unit)) ;
                    System.out.print(key);
                    System.out.print("\t key:");
                    key = Math.round( key*class_width*10 )/ 10.0;
                    System.out.println(key);
                    expected_return.put(key, expected_return.get(key) + 1);
                }

            }

        }catch (Exception e){

            e.printStackTrace();


        }

    }

}
