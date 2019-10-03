<%@page import="com.guptae.thread.ThreadInfo"%>
<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import = "java.util.Map, java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Same State Threads Info</title>
<style type="text/css">
  div {
    border: 2px solid black;
	margin: 2px;
	font-size: 20px;
	padding: 20px
  }
</style>
</head>
<body>
  <%
  HttpSession oldSession = request.getSession(false);
  String state = (String) request.getParameter("state");
  Map<String, Integer> threadStateCountMap = null;
  Map<String, List<String>> stateThreadMap = new HashMap<String, List<String>>();
  {
	if(null != oldSession)
	{
		threadStateCountMap = (Map<String, Integer>) oldSession.getAttribute("threadStateCountMap");
		if(null != state)
		{
			out.println("<h2>" + threadStateCountMap.get(state) + " " + state + " threads </h2>");
			switch(state)
			{
				case "NEW": stateThreadMap = (Map<String, List<String>>) oldSession.getAttribute("newThreadMap");
							break;
				case "RUNNABLE": stateThreadMap = (Map<String, List<String>>) oldSession.getAttribute("runnableThreadMap");
									break;
				case "TIMED_WAITING": stateThreadMap = (Map<String, List<String>>) oldSession.getAttribute("timedWaitingThreadMap");
										break;	
				case "WAITING": stateThreadMap = (Map<String, List<String>>) oldSession.getAttribute("waitingThreadMap");
								break;
				case "BLOCKED": stateThreadMap = (Map<String, List<String>>) oldSession.getAttribute("blockedThreadMap");
								break;
				case "TERMINATED": stateThreadMap = (Map<String, List<String>>) oldSession.getAttribute("terminatedThreadMap");
									break;
			}
			for(Map.Entry<String, List<String>> threadData: stateThreadMap.entrySet())
			{
				ThreadInfo threadInfo = new ThreadInfo(threadData.getKey());
				out.println("<div>");
				//out.println("<br />");
				out.println("<h4 style=\"color: #CD5C5C\">" + threadInfo.getThreadName() + "</h4>");
				//out.println("<br />");
				out.println("priority: " + threadInfo.getThreadPriority() + " - threadId: " 
				+ threadInfo.getThreadAddress() + " - nativeId: " + threadInfo.getOsThreadId() 
				+ " - nativeId (decimal): " + Integer.parseInt(threadInfo.getOsThreadId().substring(2), 16) 
				+ " - state: " + state);
				out.println("<br />");
				if(threadData.getValue() != null && threadData.getValue().size() > 1)
				{
					threadInfo.setStackTrace(threadData.getValue().subList(1, threadData.getValue().size()-1));
				}
				
				if(threadInfo.getStackTrace() != null && threadInfo.getStackTrace().size()>0)
				{
				  out.println("Stack Trace: ");
				  for(String stackStr : threadInfo.getStackTrace())
				  {
					  out.println("<br />");
					  out.println(stackStr);
				  }
				}
				out.println("</div>");
			}
		}
	}
	else {
		out.println("Session not available :(");
	}
  }
  %>
</body>
</html>