<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Thread Dump Analyzer</title>
</head>
<body>
	<p style="color: red;">${errorString}</p>
	<h2>Upload Files</h2>
	<form action="uploadFile" method="post" enctype="multipart/form-data">
		Select file to upload:
        <br />
        <br />
        <input type="file" name="file"  />
        <br />
        <br />
        <input type="submit" value="Upload" />
	</form>
	<br />
	<br />
	
	<c:if test = "${not empty successString}">
	<div style="font-size: large;">
	<%
		String filename = null;
		/* Cookie[] cookies = request.getCookies();
		if(null != cookies)
		{
			for(Cookie cookie : cookies)
			{
				if(cookie.getName().equals("filename"))
				{
					filename = cookie.getValue();
				}
			}
		} */
		HttpSession oldSession = request.getSession(false);
		if(null!=oldSession)
		{
			filename = (String) oldSession.getAttribute("filename");
		}
	%>
	<form action="analyzeThreadDump" method="get">
		'<%=filename %>' 
		${successString} Proceed to Analyze <input type="submit" value="Analyze">
	</form>
	
	</div>
	</c:if>
	
</body>
</html>