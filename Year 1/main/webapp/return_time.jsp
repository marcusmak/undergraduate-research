<%--
  Created by IntelliJ IDEA.
  User: Marcus
  Date: 04/14/19
  Time: 12:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<script src = "https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/histogram-bellcurve.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
<head>
    <title>Expected Return Time</title>
</head>
<body>
<input type="text" name="datetimes" />

<select name="location" type="text" id="location" >
    <option value="LaneCrawford_3">LaneCrawford_3</option>
    <option value="LaneCrawford_4">LaneCrawford_4</option>
    <option value="LaneCrawford_5">LaneCrawford_5</option>
</select>

<input type="number" name="class_width" min="0" step="0.25" value="24" maxlength="5" width="10px"> hour

<input type="button" value = submit name="submit" onclick="fetch_histogram()" id="submit"/>

<div id="container"></div>

<script language = "JavaScript">
    var startTime, endTime;


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
    });


    function fetch_histogram() {
        var location = $("#location").val();
        console.log(location);
        $.ajax({
            url: "http://localhost:8080/DBConnect_war_exploded/HisAction?startTime=" + startTime + "&endTime=" + endTime
                + "&place=" + location + "&class_width=" + $('input[name="class_width"]').val(),
            type: 'GET',
            dataType: 'json',
            // timeout: 10000,
            error: function (json) {
                alert('Histogram Failed'); //當json讀取失敗
            },
            success: function (json) {
                console.log(json);
                var strkey = Object.keys(json['expected_return']);
                var size = strkey.length - 1;
                var data = new Array(size);
                var classes = new Array (size);

                var index = 0;
                var class_width = json['class_width'];

                var time_unit  = json['time_unit'];
                var keys = new Array(size);
                for (var i = 0; i<strkey.length ; ++i){
                    keys[i] = parseFloat(strkey[i]);
                }
                console.log(keys);
                keys.sort(function(a, b){return a-b});
                console.log(keys);
                var unit = "hour(s)";

                keys.forEach(function(label) {
                    //var temp = new Array(2);
                    //temp[0] = device;
                    //temp[1] = json['expected_return'][device] / 1000;
                    if (label == -1) {
                        // classes[index] = "Never Return";

                    } else {

                        var temp = label + class_width;
                        temp = temp.toFixed(1);
                        classes[index] = label + " to " + temp + " " + unit + " ";
                        label = label.toFixed(1);
                        data[index++] = json['expected_return'][label.toString()];
                    }
                 });

                console.log(data);


                drawHistogram(classes,data);


            }
        });

    }



    function drawHistogram(classes,data){
        Highcharts.chart('container', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Histogram using a column chart'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: classes,
                crosshair: true
            },
            yAxis: {
                min: 0,
                title: {
                    text: ''
                }
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                    '<td style="padding:0"><b>{point.y:.1f} ppl.</b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    pointPadding: 0,
                    borderWidth: 0,
                    groupPadding: 0,
                    shadow: false
                }
            },
            series: [{
                name: 'Data',
                data: data

            }]
        });

    }

</script>


</body>
</html>
