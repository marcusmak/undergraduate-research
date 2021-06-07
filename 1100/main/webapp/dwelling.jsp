<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Customer population</title>
    <script src = "https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src = "https://code.highcharts.com/highcharts.js"></script>
    <script src="https://code.highcharts.com/modules/heatmap.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
</head>
<body>
<div id="title"></div>
<input type="text" name="datetimes" />

<select name="location" type="text" id="location" >
    <option value="LaneCrawford_3">LaneCrawford_3</option>
    <option value="LaneCrawford_4">LaneCrawford_4</option>
    <option value="LaneCrawford_5">LaneCrawford_5</option>
</select>

<select name="Map_mean" type="text" id="type_of_map" >
    <option value="dwell_time">average dwell time</option>
    <option value="dwell_ppl">No. of People</option>
</select>

<input type="button" value="submit" onclick="fetch_heatmap()">
<div id = "container" style = "width: 1000px; height: 600px; margin: 0 auto"></div>
<div id = "container2" style = "width: 550px; height: 400px; margin: 0 auto"></div>
<script language = "JavaScript">
    var startTime,endTime;
    var timeOffset = 28800000;
    // var period= 24;
    startTime = 0;
    endTime   = 0;



    $(function() {
        $('input[name="datetimes"]').daterangepicker({
            timePicker: true,
            startDate: moment().startOf('day'),
            endDate: moment().startOf('day').add(23, 'hour').add(59,'minute'),
            locale: {
                format: 'M/DD hh:mm A'
            }}, function(start, end, label) {
                console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
                startTime = parseInt(start.unix()) * 1000;
                endTime = parseInt(end.unix()) * 1000;
                console.log(startTime);
                console.log(endTime);
                // var temp = endTime-startTime;
                   // if (temp < 3600 * 24 * 1000){
                //     period = 1;
                // };

            }
        )
    })


    function fetch_heatmap() {
            var dwell_ppl = $("#type_of_map").val() == "dwell_ppl";
            console.log(dwell_ppl)

           $.ajax({
               url: 'http://localhost:8080/DBConnect_war_exploded/HeatmapAction.action?startTime='
                   + startTime + '&endTime=' + endTime + '&place=' + $("#location").val()
                   +'&type=' + dwell_ppl,
               type: 'GET',
               dataType: 'json',
               // timeout: 10000,
               error: function (json) {
                   alert('failed');
               },
               success: function (json) {
                   var size = json['store_name'].length;
                   var store_names = new Array(size);
                   var store_ids = new Array(size);
                   var period_hrs = json['period_hrs'];
                   var i = 0;
                   for (store_id in json['store_name']) {
                       store_ids[i] = store_id;
                       store_names[i] = json['store_name'][store_id];
                       i++;

                   }


                   console.log(store_names);
                   console.log(store_ids);


                   var time_periods = Object.keys(json['pop_over_time']);
                   var coor = [];
                   time_periods.forEach(function (time_period) {
                       store_ids.forEach(function (value, index) {

                               // console.log(json['pop_over_time'][time_period]);
                               var count = json['pop_over_time'][time_period][value]; //store_ids

                               if (count != null) {
                                   coor.push([time_period * 1000, index, count]);

                               } else {

                                   coor.push([time_period * 1000, index, 0]);

                               }

                               // console.log(time_period);

                           }
                       )


                   });

                   Highcharts.setOptions({
                       time: {
                           timezoneOffset: -8 * 60
                       }
                   });


                   console.log(store_names);
                   console.log(coor);
                   Highcharts.chart('container', {

                       chart: {
                           type: 'heatmap',
                           marginTop: 40,
                           marginBottom: 80,
                           plotBorderWidth: 0
                       },


                       title: {
                           text: 'HeatMap'
                       },

                       xAxis: {
                           type: 'datetime'
                       },

                       yAxis: {
                           categories: store_names,
                           title: null
                       },

                       colorAxis: {
                           min: 0,
                           minColor: '#FFFFFF',
                           maxColor: Highcharts.getOptions().colors[0]
                       },

                       legend: {
                           align: 'right',
                           layout: 'vertical',
                           margin: 0,
                           verticalAlign: 'top',
                           y: 25,
                           symbolHeight: 500
                       },

                       tooltip: {
                           formatter: function () {
                               var currentDate = new Date(this.point.x);

                               var date = currentDate.getDate();
                               var month = currentDate.getMonth(); //Be careful! January is 0 not 1
                               var year = currentDate.getFullYear();
                               var hours = currentDate.getHours();
                               var minutes = "0" + currentDate.getMinutes();
                               var seconds = "0" + currentDate.getSeconds();

                               var dateString = date + "-" + (month + 1) + "-" + year + " at "
                                   + hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
                               if (dwell_ppl){
                                   return '<b>' + dateString + '</b>  <br><b>' +
                                   this.point.value + '</b> people dwell <br><b>' + this.series.yAxis.categories[this.point.y] + '</b>';
                               }else{
                                   return '<b>' + dateString + '</b>  <br><b>' +
                                       this.point.value + '</b> second on average <br><b>' + this.series.yAxis.categories[this.point.y] + '</b>';

                               }

                           }
                       },

                       plotOptions: {
                           series: {
                               events: {
                                   click: function (e) {
                                       var store_id;
                                       var store_name = this.yAxis.categories[e.point.y].toString();
                                       var indexTemp = store_names.findIndex(function (element){return (element == store_name) });


                                       fetch_linechart(e.point.x,store_ids[indexTemp],period_hrs );

                                   }
                               }
                           }
                       },

                       series: [{
                           name: 'Sales per employee',
                           borderWidth: 1,
                           colsize: period_hrs * 60 * 60 * 1000,
                           data: coor,
                           dataLabels: {
                               enabled: true,
                               color: '#000000'
                           }
                       }]

                   });


               }
           });
    };




    function fetch_linechart(ts,store_id,period_hrs){
        var dwell_ppl = $("#type_of_map").val() == "dwell_ppl";
        $.ajax({
            url:"http://localhost:8080/DBConnect_war_exploded/LinechartAction.action?startTime=" + ts + "&store_id="+store_id+
            "&period_hrs=" + period_hrs + "&type=" + dwell_ppl,
            type: 'GET',
            dataType: 'json',//資料型態可以不設定，且此型態不可是text或html
            // timeout: 10000,
            error: function (json) {
                alert('Line Chart Failed'); //當json讀取失敗
            },
            success: function (json) {
                console.log(json);
                var size = json['pop_over_time'].length;
                var data = new Array(size);

                var index = 0;
                for (var element in json['pop_over_time']){
                    var temp = new Array(2);
                    temp[0] = element*1000;
                    temp[1] = json['pop_over_time'][element];
                    data[index++] = temp;

                }

                console.log(data);


                drawLineChart(data);


            }
        });






    }



    function drawLineChart(data){
        var dwell_ppl = $("#type_of_map").val() == "dwell_ppl";
        Highcharts.setOptions({
            time: {
                timezoneOffset: -8 * 60
            }
        });
        var output_info = 'No. of people';
        if (!dwell_ppl){ output_info = 'Dwell time on avgerage';}
        Highcharts.chart('container2', {
            chart: {
                zoomType: 'x'
            },
            title: {
                text: 'Dwelling Detail over time'
            },
            subtitle: {
                text: document.ontouchstart === undefined ?
                    'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
            },
            xAxis: {
                type: 'datetime'
            },
            yAxis: {
                title: {
                    text: 'Number of customers'

                },
                floor: 0
            },
            legend: {
                enabled: false
            },
            plotOptions: {
                area: {
                    fillColor: {
                        linearGradient: {
                            x1: 0,
                            y1: 0,
                            x2: 0,
                            y2: 1
                        },
                        stops: [
                            [0, Highcharts.getOptions().colors[0]],
                            [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                        ]
                    },
                    marker: {
                        radius: 2
                    },
                    lineWidth: 1,
                    states: {
                        hover: {
                            lineWidth: 1
                        }
                    },
                    threshold: null
                }
            },

            series: [{
                type: 'area',
                name: output_info,
                data: data
            }]
        });







    }



    // $(document).ready(fetch_heatmap());
</script>
<s:property value="store_list.get(0).getCount()"/>

</body>
</html>


<%--TODO::--%>
        <%--https://api.highcharts.com/highmaps/plotOptions.heatmap.events.click--%>