<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.Map,com.guptae.thread.ThreadInfo" %>
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
		</style>
  </head>
  <body>
    <h1 align="center">Thread Dump Analysis Report</h1>
	<div>
	  <h3>Thread Count Summary</h3>
	  <%
	  int newCount = 0;
	  int runnableCount = 0;
	  int timedWaitingCount = 0;
	  int waitingCount = 0;
	  int blockedCount = 0;
	  int terminatedCount = 0;
	  Map<String, Integer> threadStateCountMap = null;
	  HttpSession oldSession  = request.getSession(false);
	  if(null != oldSession) {
		threadStateCountMap = (Map<String, Integer>) oldSession.getAttribute("threadStateCountMap");
	  	newCount = threadStateCountMap.get("NEW");
	  	runnableCount = threadStateCountMap.get("RUNNABLE");
	  	timedWaitingCount = threadStateCountMap.get("TIMED_WAITING");
	  	waitingCount = threadStateCountMap.get("WAITING");
	  	blockedCount = threadStateCountMap.get("BLOCKED");
	  	terminatedCount = threadStateCountMap.get("TERMINATED");
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
	  	  <td><a href="samestatethreadinfo.jsp?state=TIMED_WAITING"><%=timedWaitingCount%> </a></td>
	  	</tr>
	  	<tr>
	  	  <td>Waiting</td>
	  	  <td><a href="samestatethreadinfo.jsp?state=WAITING"><%=waitingCount%> </a></td>
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
	<%
	  	oldSession = request.getSession(false);
	  	if(null != oldSession)
	  	{
	  	  Map<String, String> deadlockThreadMap = (Map<String, String>) oldSession.getAttribute("deadlockThreadMap");
	  	}
	  %>
	<c:if test = "${not empty deadlockThreadMap}">
	<div>
	  <h3>Dead Lock</h3>
	  <br/>
	  <c:forEach var="entry" items="${deadlockThreadMap}">
	    ${entry.key} is in deadlock with ${entry.value}
	  </c:forEach>
	  <br/>
	  <%
	      Map<String, ThreadInfo> deadlockThreadInfoMap = (Map<String, ThreadInfo>) oldSession.getAttribute("deadLockThreadInfoMap");
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
	  			out.println(threadInfo.getStackTrace());
	  		}
	  		
	      }
	    %> 
	</div>
	</c:if>
	<%
    oldSession = request.getSession(false);
	if(null != oldSession)
	{
	  Map<String, String> complexDeadlockThreadMap = (Map<String, String>) oldSession.getAttribute("complexDeadlockThreadMap");
	}
  %>
  <div>
	<h3>Complex Deadlock Information</h3>
	<c:if test = "${empty complexDeadlockThreadMap}">
	  <p>No Complex Deadlocks Found</p>
	</c:if>
	<c:if test = "${not empty complexDeadlockThreadMap}">
	<div>
	  <h3>Dead Lock</h3>
	  <br/>
	  <c:forEach var="entry" items="${complexDeadlockThreadMap}">
	    ${entry.key} is in deadlock with ${entry.value}
	    <br/>
	  </c:forEach>
	  <br/>
	  <%
	    Map<String, ThreadInfo> complexDeadlockThreadInfoMap = (Map<String, ThreadInfo>) oldSession.getAttribute("complexDeadLockThreadInfoMap");
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
	  		out.println(threadInfo.getStackTrace());
	  	  }
	    }
	  %> 
	</div>
	</c:if>
  </div>
  </body>
</html>