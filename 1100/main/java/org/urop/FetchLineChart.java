package org.urop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class FetchLineChart {

    public HashMap<Long,Integer> pop_over_time = new HashMap<>();
    private String getSQL(Boolean count ) {


        if(count) {

            return "SELECT FLOOR(FLOOR((start_ts+28800000)*1./(?/100)/60/60/1000)*(?/100)*60*60)-28800 as t," +
                    " count(*) as c FROM public.dwells WHERE store_id = ? and start_ts " +
                    "BETWEEN ? and ?+?*60*60*1000 GROUP BY t ORDER BY  t";
        }else{

            return "SELECT FLOOR(FLOOR((start_ts+28800000)*1./(?/100)/60/60/1000)*(?/100)*60*60)-28800 as t," +
                    " AVG(duration)/1000 as c FROM public.dwells WHERE store_id = ? and start_ts " +
                    "BETWEEN ? and ?+?*60*60*1000 GROUP BY t ORDER BY  t";
        }
    }
    public void fetchLineChart(Connection conn, Long startTime, Integer store_id, Double period_hrs,Boolean count){

        try{

            String sql = getSQL(count);



            PreparedStatement ps = conn.prepareStatement(sql);
            int index = 1;
            ps.setDouble(index++,period_hrs);
            ps.setDouble(index++,period_hrs);
            ps.setInt(index++,store_id);
            ps.setLong(index++,startTime);
            ps.setLong(index++,startTime);
            ps.setDouble(index,period_hrs);

            ResultSet rs = ps.executeQuery();



            while (rs.next()) {
                Long ts = rs.getLong("t");
                int c = rs.getInt("c");

                pop_over_time.put(ts,c);

            }
        }catch (Exception e){

            e.printStackTrace();


        }

    }


}





