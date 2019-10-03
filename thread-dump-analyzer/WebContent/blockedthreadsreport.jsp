<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.Map,java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
      <title>Blocked Thread Report</title>
		<style type="text/css">
		  div {
		    border: 1px solid black;
		  	margin: 2px;
		  	font-size: 20px;
		  	padding: 20px
		  }
		  table, td, tr {
		  border: 1px solid black;
		  border-collapse: collapse;
		  background-color: #f2f2d1;
		  }
		  th {
		  border: 1px solid black;
		  border-collapse: collapse;
		  padding: 20px;
		  background-color: #D3D3D3;
		  }
		  td {
 		  padding: 20px;
		  }
		  ol {
		    list-style-type: none;
		    margin: 0;
		    padding: 0;
		    overflow: hidden;
		    background-color: #333333
		  }
		  li {
		    float: left;
		  }
		  li a {
		    display: block;
		    color: white;
		    text-align: center;
		    padding: 16px;
		    text-decoration: none;
		  }
		  li a:HOVER {
			background-color: #111111
		  }
		</style>
  </head>
  <body>
    <h1 align="center">Thread Dump Analysis Report</h1>
    <ol>
      <li><a href="launchpage.jsp">Home</a></li>
      <li><a style="color: yellow" href="blockedthreadsreport.jsp">Blocked Threads</a></li>
<!--      <li><a href="threaddumpreportaslist.jsp">Complete Report</a></li> -->
<!--      <li><a href="summary.jsp">Thread Count Summary</a></li> -->
<!--      <li><a href="deadlock.jsp">Deadlock Information</a></li> -->
<!--      <li><a href="complexdeadlock.jsp">Complex Deadlock Information</a></li> -->
    </ol>
    <div>
	  <h3>Blocked Threads</h3>
 	  <% 
 	    Map<String, List<String>> blockedThreadsMap = null; 
 	    HttpSession oldSession  = request.getSession(false); 
	    if(null != oldSession)  
 	    { 
 		  blockedThreadsMap = (Map<String, List<String>>) oldSession.getAttribute("commonBlockedThreads");
		  if(null == blockedThreadsMap || blockedThreadsMap.isEmpty())
		  {
		    out.println("No BLocked Threads Found");	  
		  } else {
			  out.println("<table><th>Blocking Code</th><th>Hung Threads</th>");
			  out.println("<tr>");
			  out.println("<td>");
			  for(Map.Entry<String, List<String>> entry : blockedThreadsMap.entrySet())
			  {
				  String blockedInfo = entry.getValue().get(0).trim();
				  String className = blockedInfo.substring(3, blockedInfo.indexOf('.'));
				  String methodName = blockedInfo.substring(blockedInfo.indexOf('.')+1, blockedInfo.indexOf('('));
				  String lineNum = blockedInfo.substring(blockedInfo.indexOf(':')+1, blockedInfo.indexOf(')'));
				  out.println("line# " + lineNum + " of " + className + " file");
				  out.println("<br />");
				  out.println("in " + methodName + " method");
				  break;
			  }
			  out.println("</td>");
			  out.println("<td>" + blockedThreadsMap.size() + " threads are blocked.");
			  out.println("<br />");
			  for(Map.Entry<String, List<String>> entry : blockedThreadsMap.entrySet())
			  {
				  out.println(entry.getKey());
				  out.println("<br />");
			  }
			  out.println("</td></tr></table>");
		  }
 	    }
 	  %>
	</div>
  </body>
</html>