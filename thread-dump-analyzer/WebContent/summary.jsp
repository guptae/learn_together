<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.Map" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Thread Count Summary</title>
<style type="text/css">
  div {
    border: 2px solid black;
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
  <ol>
    <li><a href="launchpage.jsp">Home</a></li>
    <li><a href="threaddumpreportaslist.jsp">Complete Report</a></li>
    <li><a style="color: yellow" href="summary.jsp">Thread Count Summary</a></li>
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
	  Map<String, Integer> threadStateCountMap = null;
	  HttpSession oldSession  = request.getSession(false);
	  if(null != oldSession) {
		String filename = (String) oldSession.getAttribute("filename");
		threadStateCountMap = (Map<String, Integer>)((Map<String, Object>) oldSession.getAttribute("E:\\eclipse_playarea\\thread-dump-analyzer\\src\\com\\guptae\\resources\\" + filename)).get("threadStateCountMap");
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
</body>
</html>