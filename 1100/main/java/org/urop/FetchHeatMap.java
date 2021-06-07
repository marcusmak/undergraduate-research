package org.urop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;



public class FetchHeatMap {


    public HashMap<Long,HashMap<Integer,Integer>> pop_over_time = new HashMap<>();
    public HashMap<Integer,String> store_name = new HashMap<>();

    public Double hours ;

    private void setStore_name(Connection conn, String place) {
        try {

            String sql = "SELECT name, id FROM public.stores WHERE areaid =?" ;
            PreparedStatement ps  = conn.prepareStatement(sql);
            ps.setString(1,place);
            System.out.println("--------------------------------------");
            System.out.println(place);
            System.out.println(ps);
            ResultSet rs = ps.executeQuery();



            while (rs.next()) {
                store_name.put(rs.getInt("id"), rs.getString("name"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }






    }
    private void returnResult(ResultSet rs){
        try {
            HashMap<String, Integer> pop_in_period = null;
            while (rs.next()) {
                Long time = rs.getLong("t");
                if (pop_over_time.get(time) == null) {
                    pop_over_time.put(time, new HashMap<Integer, Integer>());
                }
                Integer store_id = rs.getInt("store_id");
                Integer count = rs.getInt("c");
                pop_over_time.get(time).put(store_id, count);
//                    System.out.println(store_id);
//                    System.out.println(count);
//                    System.out.println(time);
//                    System.out.println(pop_over_time);

            }
        }catch (Exception e){

            e.printStackTrace();

        };


        System.out.println("here");
    }

    private void preHours(Long startTime, Long endTime){
        Long temp = (endTime - startTime)/3600/1000;
        if (temp <= 3){
            hours = 0.25;
        }else if( temp <=6){
            hours = 0.5;
        }else if ( temp <= 12){
            hours = 0.75;
        }else if ( temp <= 24){
            hours = 1.0;
        }else if ( temp <= 48){
            hours = 2.0;
        }else {
            hours = 24.0;
        }

    }




    private String genSQL(Boolean count){
        if(count){
            return "SELECT FLOOR(FLOOR((start_ts+28800000)*1./?/60/60/1000)*(?)*60*60)-28800 as t, store_id, COUNT(DISTINCT did) as c FROM public.dwells " +
                "WHERE store_id <> 0 and start_ts BETWEEN ? and ? GROUP BY t, store_id ORDER BY  t,store_id";
            //SELECT FLOOR(FLOOR((start_ts+28800000)*1./1/60/60/1000)*(1)*60*60)-28800 as t, store_id,
            //       SUM(CASE WHEN st art_ts/1000 >= FLOOR(FLOOR((start_ts+28800000)*1./(6.0/100)/60/60/1000)*(1.0/100)*60*60)-28800
            //         OR end_ts/1000 <=FLOOR(FLOOR((start_ts+28800000)*1./(6.0/100)/60/60/1000)*(6.0/100)*60*60)-28800 + (1.0/100)*60*60
            //                  THEN 1 ELSE 0 END) as c FROM public.dwells
            //WHERE store_id <> 0 and start_ts BETWEEN 1554048000000 and 1554134340000 GROUP BY t, store_id ORDER BY  t,store_id

        }else{
            return "SELECT FLOOR(FLOOR((start_ts+28800000)*1./?/60/60/1000)*(?)*60*60)-28800 as t, store_id, AVG((duration)/1000) as c FROM public.dwells " +
                    "WHERE store_id <> 0 and start_ts BETWEEN ? and ? GROUP BY t, store_id ORDER BY  t,store_id";
        }
    }

    public void fetchHeatMap(Connection conn, Long startTime, Long endTime, String place, Boolean count) {
        //c

        try {

            String sql = genSQL(count);


            PreparedStatement ps = conn.prepareStatement(sql);

            if (true)
            {
                preHours(startTime, endTime);
                ps.setDouble(1, hours);
                ps.setDouble(2, hours);
                ps.setLong(3, startTime);
                ps.setLong(4, endTime);
            }
                ResultSet rs = ps.executeQuery();



            returnResult(rs);
            setStore_name(conn, place);



        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}





// String sql = "SELECT floor(start_ts/24/60/60/1000)*24*60*60 as t, store_id, count(id) FROM public.dwells " +
//       "WHERE store_id <> 0 and start_ts BETWEEN ? and ? GROUP BY t, store_id ORDER BY  t,store_id";
//  String sql = "SELECT stores.name, count(public.dwells.store_id)" +
//       "FROM public.dwells , public.stores" +
//     "GROUP BY stores.store_id ";

//                String sql = "SELECT store_id, count(store_id) FROM public.dwells " +
//                        "WHERE store_id<>0 GROUP BY store_id ORDER BY store_id";

//                Statement stmt = conn.createStatement();
//                ResultSet rs = stmt.executeQuery(sql);


/*

                while (rs.next()) {
                    String store_id = rs.getString("store_id");
                    int count = rs.getInt("count");
                    Store_count store_count = new Store_count();
                    store_count.setStore_id(store_id);
                    store_count.setCount(count);
                    store_list.add(store_count);

                }
*/
              /*  String time = "";
                HashMap<String,Integer> pop_in_period = null;
                while (rs.next()) {
                    if ( time != rs.getString("t") ){
                        System.out.println("old"+time);
                        time = rs.getString("t");
                        pop_in_period = new HashMap<>();
                        pop_over_time.put(time,pop_in_period);
                        System.out.println(time);
                    }
                    String store_id = rs.getString("store_id");
                    Integer count   = rs.getInt("count");
                    pop_over_time.get(time).put(store_id,count);


                }
*/

// sql = "SELECT name, id FROM public.stores WHERE areaid ='LaneCrawford_4' " ;








