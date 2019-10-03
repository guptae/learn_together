<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Map,com.guptae.thread.ThreadInfo" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Deadlock Information</title>
<style type="text/css">
   div {
    border: 2px solid black;
	margin: 2px;
	font-size: 20px;
	padding: 20px
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
  <ol>
    <li><a href="launchpage.jsp">Home</a></li>
    <li><a href="threaddumpreportaslist.jsp">Complete Report</a></li>
    <li><a href="summary.jsp">Thread Count Summary</a></li>
    <li><a style="color: yellow" href="deadlockV2.jsp">Deadlock Information</a></li>
    <li><a href="complexdeadlockV2.jsp">Complex Deadlock Information</a></li>
  </ol>
  <div>
	<h3>Deadlock Information</h3>
    <%
      HttpSession oldSession = request.getSession(false);
	  if(null != oldSession)
	  {
        String filename = (String) oldSession.getAttribute("filename");
	    Map<String, String> deadlockThreadMap = (Map<String, String>)((Map<String, Object>) oldSession.getAttribute("E:\\eclipse_playarea\\thread-dump-analyzer\\src\\com\\guptae\\resources\\" + filename)).get("deadlockThreadMap");
	    if(deadlockThreadMap == null || deadlockThreadMap.isEmpty())
	    {
		  out.println("No Deadlock Found");
	    } else {
	      out.println("<h3>Dead Lock</h3>");
	  	  for(Map.Entry deadlockThread : deadlockThreadMap.entrySet())
		  {
	        out.println(deadlockThread.getKey() + " is in deadlock with " + deadlockThread.getValue());
	    	out.println("<br />");
		  }
	    }
	    Map<String, ThreadInfo> deadlockThreadInfoMap = (Map<String, ThreadInfo>)((Map<String, Object>) oldSession.getAttribute("E:\\eclipse_playarea\\thread-dump-analyzer\\src\\com\\guptae\\resources\\" + filename)).get("deadLockThreadInfoMap");
	    if(null != deadlockThreadInfoMap)
	    {
	      for(Map.Entry threadInfoEntry : deadlockThreadInfoMap.entrySet())
	  	  {
	        out.println("<br />");
	  		out.println("<h4 style=\"color: #CD5C5C\">" + threadInfoEntry.getKey() + "</h4>");
	  		out.println("<br />");
	  		ThreadInfo threadInfo = (ThreadInfo) threadInfoEntry.getValue();
	  		out.println("Priority: " + threadInfo.getThreadPriority() 
	  				+ " - threadId: " + threadInfo.getThreadId() 
	  				+ " - nativeId: " + threadInfo.getOsThreadId()
	  				+ " - state: " + threadInfo.getThreadState());
	  		out.println("<br />");
	  		out.println("Stack trace: ");
	  		out.println("<br />");
	  		for(int i=0; i< threadInfo.getStackTrace().size(); i++)
  			{
  				out.println(threadInfo.getStackTrace().get(i));
  				out.println("<br />");
  			}
	  	  }
	    }
	  }
    %>

  </div>
</body>
</html>