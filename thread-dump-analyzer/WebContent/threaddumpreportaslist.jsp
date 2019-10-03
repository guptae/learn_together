<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.Map,java.util.HashMap,com.guptae.thread.ThreadInfo" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
      <title>Thread Dump Report</title>
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
        <li><a style="color: yellow" href="threaddumpreportaslist.jsp">Complete Report</a></li>
      	<li><a href="summary.jsp">Thread Count Summary</a></li>
      	<li><a href="deadlockV2.jsp">Deadlock Information</a></li>
      	<li><a href="complexdeadlockV2.jsp">Complex Deadlock Information</a></li>
      </ol>
	<div>
	  <h3>Thread Count Summary</h3>
	  <%
	    int newCount = 0;
	    int runnableCount = 0;
	    int timedWaitingCount = 0;
	    int waitingCount = 0;
	    int blockedCount = 0;
	    int terminatedCount = 0;
	    Map<String, Object> filePropInfoMap = null;
	    Map<String, Integer> threadStateCountMap = null;
	    Map<String, ThreadInfo> deadlockThreadInfoMap = null;
	    Map<String, String> complexDeadlockThreadMap = null;
	    HttpSession oldSession  = request.getSession(false);
	    if(null != oldSession) 
	    {
		  String filename = (String) oldSession.getAttribute("filename");
		  if(null != filename) 
		  {
		    filePropInfoMap = (Map<String, Object>) oldSession.getAttribute("E:\\eclipse_playarea\\thread-dump-analyzer\\src\\com\\guptae\\resources\\" + filename);
		    threadStateCountMap = (Map<String, Integer>) filePropInfoMap.get("threadStateCountMap");
	  	    newCount = threadStateCountMap.get("NEW");
	  	    runnableCount = threadStateCountMap.get("RUNNABLE");
	  	    timedWaitingCount = threadStateCountMap.get("TIMED_WAITING");
	  	    waitingCount = threadStateCountMap.get("WAITING");
	  	    blockedCount = threadStateCountMap.get("BLOCKED");
	  	    terminatedCount = threadStateCountMap.get("TERMINATED");
	      }
	    }
	  %>
	  <table>
	  	<tr>
	  	  <td>New</td>
	  	  <td><a href="samestatethreadinfo.jsp?state=NEW"><%=newCount%> </a></td>
	  	</tr>
	  	<tr>
	  	  <td>Runnable</td>
	  	  <td><a href="samestatethreadinfo.jsp?state=RUNNABLE"><%=runnableCount%> </a></td>
	  	</tr>
	  	<tr>
	  	  <td>Timed Waiting</td>
	  	  <td><a href="samestatethreadinfo.jsp?state=RUNNABLE"><%=timedWaitingCount%> </a></td>
	  	</tr>
	  	<tr>
	  	  <td>Waiting</td>
	  	  <td><a href="samestatethreadinfo.jsp?state=RUNNABLE"><%=waitingCount%> </a></td>
	  	</tr>
	  	<tr>
	  	  <td>Blocked</td>
	  	  <td><a href="samestatethreadinfo.jsp?state=BLOCKED"><%=blockedCount%> </a></td>
	  	</tr>
	  	<tr>
	  	  <td>Terminated</td>
	  	  <td><a href="samestatethreadinfo.jsp?state=TERMINATED"><%=terminatedCount%> </a> </td>
	  	</tr>
	  </table>
	</div>
	<div>
	  <h3>Deadlock Information</h3>
	  <%
	  oldSession = request.getSession(false);
//	  if(null != oldSession)
//	  {
	    String filename = (String) oldSession.getAttribute("filename");
		  filePropInfoMap = (Map<String, Object>) oldSession.getAttribute("E:\\eclipse_playarea\\thread-dump-analyzer\\src\\com\\guptae\\resources\\" + filename);
		  Map<String, String> deadlockThreadMap = (Map<String, String>) filePropInfoMap.get("deadlockThreadMap");
		  if(deadlockThreadMap == null || deadlockThreadMap.isEmpty())
		  {
			  out.println("No Deadlock Found");
		  } else {
		  	for(Map.Entry deadlockThread : deadlockThreadMap.entrySet())
  		  	{
		    	out.println(deadlockThread.getKey() + " is in deadlock with " + deadlockThread.getValue());
		    	out.println("<br />");
  		  	}
		  }
//	  }
    %>
<%-- 	  <c:if test = "${empty deadlockThreadMap}" var="result" /> --%>
<%-- 	  <c:if test="result"> --%>
<!-- 	    <p>No Deadlock Found</p> -->
<%-- 	  </c:if> --%>
<%-- 	  <c:if test="${not result}"> --%>
<!-- 	    <br/> -->
<%-- 	    <c:forEach var="entry" items="${deadlockThreadMap}"> --%>
<%-- 	      ${entry.key} is in deadlock with ${entry.value} --%>
<!-- 	      <br/> -->
<%-- 	    </c:forEach> --%>
<!-- 	    <br/> -->
	    <%
	  	  
	        deadlockThreadInfoMap = (Map<String, ThreadInfo>) filePropInfoMap.get("deadLockThreadInfoMap");
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
		  
	    %> 
<%-- 	  </c:if> --%>
	</div>
    <div>
	  <h3>Complex Deadlock Information</h3>
	  <%
      oldSession = request.getSession(false);
//	  if(null != oldSession)
//	  {
//	    String filename = (String) oldSession.getAttribute("filename");
//	    if(null != filename) 
//	    {
//	      filePropInfoMap = (Map<String, Object>) oldSession.getAttribute("E:\\eclipse_playarea\\thread-dump-analyzer\\src\\com\\guptae\\resources\\" + filename);
		  complexDeadlockThreadMap = (Map<String, String>) filePropInfoMap.get("complexDeadlockThreadMap");
		  if(null == complexDeadlockThreadMap || complexDeadlockThreadMap.isEmpty())
		  {
			  out.println("No Complex Deadlocks Found");
		  } else {
			for(Map.Entry<String, String> entry : complexDeadlockThreadMap.entrySet())
			{
				out.println(entry.getKey() + " is in deadlock with " + entry.getValue());
				out.println("<br />");
			}
		  }
//	    }
//	  }
    %>
<%--  	  <c:if test = "${empty complexDeadlockThreadMap}"> --%>
<!-- 	    <p>No Complex Deadlocks Found</p> -->
<%-- 	  </c:if> --%>
<%-- 	  <c:if test = "${not empty complexDeadlockThreadMap}"> --%>
<!-- 	    <br/> -->
<%-- 	    <c:forEach var="entry" items="${complexDeadlockThreadMap}"> --%>
<%-- 	      ${entry.key} is in deadlock with ${entry.value} --%>
<!-- 	        <br/> -->
<%-- 	    </c:forEach> --%>
<!-- 	    <br/> -->
	    <%
	      Map<String, ThreadInfo> complexDeadlockThreadInfoMap = (Map<String, ThreadInfo>) filePropInfoMap.get("complexDeadLockThreadInfoMap");
	      if(null != complexDeadlockThreadInfoMap)
	      {
	        for(Map.Entry threadInfoEntry : complexDeadlockThreadInfoMap.entrySet())
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
	    %> 
<%-- 	</c:if> --%>
  </div>
  </body>
</html>