<%@ page language = "java" contentType = "text/html; charset = ISO-8859-1"
         pageEncoding = "ISO-8859-1"%>
<%@ taglib prefix = "s" uri = "/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title>INDEX PAGE</title>
</head>

<body>
<form action = "dwelling.jsp" method = "post">
    <input type = "submit" value = "HeatMap"/>
</form>

    <form action = "return_time.jsp" method = "post">
    <input type = "submit" value = "Histogram"/>
</form>

</body>
</html>