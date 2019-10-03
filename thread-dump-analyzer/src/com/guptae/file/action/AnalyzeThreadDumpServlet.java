package com.guptae.file.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.guptae.thread.ThreadInfo;

/**
 * Servlet implementation class AnalyzeThreadDumpServlet
 */
@WebServlet(urlPatterns = { "/analyzeThreadDump" })
public class AnalyzeThreadDumpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String UPLOAD_DIRECTORY = "E:\\eclipse_playarea\\thread-dump-analyzer\\src\\com\\guptae\\resources";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnalyzeThreadDumpServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (null == session) {
			request.setAttribute("errorString", "Your session has been timed out!");
			response.sendRedirect(request.getContextPath());
		} else {
			try {
				String filename = (String) session.getAttribute("filename");
				if (null != filename) {
					String filePath = UPLOAD_DIRECTORY + File.separator + filename;
					if (filename.endsWith("zip") || filename.endsWith("rar")) {
						String extracetedFilesPath = unzipFiles(session, filePath);
						String destDirName = extracetedFilesPath.substring(0, extracetedFilesPath.lastIndexOf(File.separator));
						Map<String, List<String>> globalBlockedThreadsMap = new HashMap<>();
						Map<String, List<String>> commonBlockedThreadsMap = new HashMap<>();
						File destDir = new File(destDirName);
						String[] fileArray = destDir.list();
						String file = null;
						for(int i=0; i<fileArray.length; i++)
						{
							file = destDirName + File.separator + fileArray[i];
							parseThreadDumpFile(session, file);
							parseThreadbyState(session, file);
							parseDeadlockInfo(session, file);
							parseBlockedThreadsInfo(session, file, globalBlockedThreadsMap, commonBlockedThreadsMap);
						}
						session.setAttribute("commonBlockedThreads", commonBlockedThreadsMap);
						RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/blockedthreadsreport.jsp");
						dispatcher.forward(request, response);
					} else {
						parseThreadDumpFile(session, filePath);
						parseThreadbyState(session, filePath);
						parseDeadlockInfo(session, filePath);
						RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/threaddumpreportaslist.jsp");
						dispatcher.forward(request, response);
					}
				}
			} catch (Throwable t) {
				request.setAttribute("error", t);
				RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/error.jsp");
				dispatcher.forward(request, response);
			}
		}
	}

	private void parseBlockedThreadsInfo(HttpSession session, String filePath, Map<String, List<String>> globalBlockedThreadsMap, Map<String, List<String>> commonBlockedThreadsMap) {
		Map<String, Object> filePropInfoMap = (Map<String, Object>) session.getAttribute(filePath);
		if(null != filePropInfoMap) {
			Map<String, List<String>> blockedThreadMap = (Map<String, List<String>>) filePropInfoMap.get("blockedThreadMap");
			for(Entry<String, List<String>> entry : blockedThreadMap.entrySet())
			{
				ThreadInfo threadInfo = new ThreadInfo(entry.getKey());
				setThreadStackTrace(entry.getValue(), threadInfo);
				if(globalBlockedThreadsMap.get(threadInfo.getThreadName()) == null){
					globalBlockedThreadsMap.put(threadInfo.getThreadName(), threadInfo.getStackTrace());
				} else {
					if(globalBlockedThreadsMap.get(threadInfo.getThreadName()).equals(threadInfo.getStackTrace()))
					{
						commonBlockedThreadsMap.put(threadInfo.getThreadName(), threadInfo.getStackTrace());
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parseDeadlockInfo(HttpSession session, String filePath) {
		Map<String, Object> filePropInfoMap = (Map<String, Object>) session.getAttribute(filePath);
		if(null != filePropInfoMap) {
			List<String> deadlockInfoList = (List<String>) filePropInfoMap.get("deadlockInfoList");
			Map<String, String> deadlockThreadMap = new HashMap<String, String>();
			if (null != deadlockInfoList) {
				for (int i = 0; i < deadlockInfoList.size(); i++) {
					String str = deadlockInfoList.get(i).trim();
					if (str.startsWith("Java stack information")) {
						break;
					}
					if (str.startsWith("\"")) {
						String keyStr = str.substring(1, str.lastIndexOf("\""));
						i++;
						String nextStr = deadlockInfoList.get(i).trim();
						while (!nextStr.startsWith("\"")) {
							if (nextStr.startsWith("which is held by ")) {
								String valueStr = nextStr.substring("which is held by ".length() + 1,
										nextStr.lastIndexOf("\""));
								deadlockThreadMap.put(keyStr, valueStr);
								break;
							}
							i++;
							nextStr = deadlockInfoList.get(i).trim();
						}
					}
				}
				Map<String, List<String>> threadInfoMap = (Map<String, List<String>>) filePropInfoMap.get("allThreadInfoMap");
				if (deadlockThreadMap.size() == 2) {
					filePropInfoMap.put("deadlockThreadMap", deadlockThreadMap);
					Map<String, ThreadInfo> deadlockThreadInfoMap = new HashMap<String, ThreadInfo>();
					for (Map.Entry<String, String> deadlockThread : deadlockThreadMap.entrySet()) {
						List<String> threadInfoList = threadInfoMap.get(deadlockThread.getKey());
						setDeadlockThreadInfoMap(deadlockThread.getKey(), threadInfoList, deadlockThreadInfoMap);
						threadInfoList = threadInfoMap.get(deadlockThread.getValue());
						setDeadlockThreadInfoMap(deadlockThread.getValue(), threadInfoList, deadlockThreadInfoMap);
					}
					filePropInfoMap.put("deadLockThreadInfoMap", deadlockThreadInfoMap);
				} else if (deadlockThreadMap.size() > 2) {
					filePropInfoMap.put("complexDeadlockThreadMap", deadlockThreadMap);
					Map<String, ThreadInfo> deadlockThreadInfoMap = new HashMap<String, ThreadInfo>();
					for (Map.Entry<String, String> deadlockThread : deadlockThreadMap.entrySet()) {
						List<String> threadInfoList = threadInfoMap.get(deadlockThread.getKey());
						setDeadlockThreadInfoMap(deadlockThread.getKey(), threadInfoList, deadlockThreadInfoMap);
						threadInfoList = threadInfoMap.get(deadlockThread.getValue());
						setDeadlockThreadInfoMap(deadlockThread.getValue(), threadInfoList, deadlockThreadInfoMap);
					}
					filePropInfoMap.put("complexDeadLockThreadInfoMap", deadlockThreadInfoMap);
				}
				System.out.println("Parsing Deadlock info: ");
				for(Entry<String, Object> entry: filePropInfoMap.entrySet())
				{
					System.out.println("key: " + entry.getKey() + "\tvalue: " + entry.getValue());
				}
				session.setAttribute(filePath, filePropInfoMap);
			}
		}
	}

	private void setDeadlockThreadInfoMap(String threadName, List<String> threadInfoList, Map<String, ThreadInfo> deadlockThreadInfoMap) {
		ThreadInfo threadInfo = new ThreadInfo(threadInfoList.get(0));
		setThreadState(threadInfoList, threadInfo);
		setThreadStackTrace(threadInfoList, threadInfo);
		deadlockThreadInfoMap.put(threadName, threadInfo);

	}

	/*
	 * 
	 */
	private void setThreadState(List<String> threadInfoList, ThreadInfo threadInfo) {
		if (threadInfoList.size() > 1) {
			threadInfo.setThreadState(threadInfoList.get(1));
		} else {
			threadInfo.setThreadState(threadInfo.getThreadStatus());
		}
	}

	/*
	 * 
	 */
	private void setThreadStackTrace(List<String> threadInfoList, ThreadInfo threadInfo) {
		if (threadInfoList.size() > 2) {
			threadInfo.setStackTrace(threadInfoList.subList(2, threadInfoList.size() - 1));
		}
	}

	@SuppressWarnings("unchecked")
	private void parseThreadbyState(HttpSession session, String filePath) {
		int newThreadsCount = 0, runnableThreadsCount = 0, waitingThreadsCount = 0, timedWaitingThreadsCount = 0,
				blockedThreadsCount = 0, terminatedThreadsCount = 0;
		Map<String, Object> filePropInfoMap = (Map<String, Object>) session.getAttribute(filePath);
		if(null != filePropInfoMap) {
			Map<String, List<String>> threadDumpDataMap = (Map<String, List<String>>) filePropInfoMap.get("allThreadInfoMap");
			Map<String, List<String>> newThreadMap = new HashMap<String, List<String>>();
			Map<String, List<String>> runnableThreadMap = new HashMap<String, List<String>>();
			Map<String, List<String>> waitingThreadMap = new HashMap<String, List<String>>();
			Map<String, List<String>> timedWaitingThreadMap = new HashMap<String, List<String>>();
			Map<String, List<String>> blockedThreadMap = new HashMap<String, List<String>>();
			Map<String, List<String>> terminatedThreadMap = new HashMap<String, List<String>>();
			Collection<List<String>> threadInfoList = threadDumpDataMap.values();
			for (List<String> threadData : threadInfoList) {
				String str;
				if (threadData.get(1) != null && !threadData.get(1).isEmpty()) {
					str = threadData.get(1);
				} else {
					str = threadData.get(0);
				}
	
				if (str.toUpperCase().contains("NEW")) {
					newThreadsCount++;
					newThreadMap.put(threadData.get(0), threadData);
	
				} else if (str.toUpperCase().contains("RUNNABLE")) {
					runnableThreadsCount++;
					runnableThreadMap.put(threadData.get(0), threadData);
				} else if (str.toUpperCase().contains("TIMED_WAITING")) {
					timedWaitingThreadsCount++;
					timedWaitingThreadMap.put(threadData.get(0), threadData);
				} else if (str.toUpperCase().contains("WAITING")) {
					waitingThreadsCount++;
					waitingThreadMap.put(threadData.get(0), threadData);
				} else if (str.toUpperCase().contains("BLOCKED")) {
					blockedThreadsCount++;
					blockedThreadMap.put(threadData.get(0), threadData);
				} else if (str.toUpperCase().contains("TERMINATED")) {
					terminatedThreadsCount++;
					terminatedThreadMap.put(threadData.get(0), threadData);
				}
			}
			Map<String, Integer> threadStateCountMap = new HashMap<String, Integer>();
			threadStateCountMap.put("NEW", new Integer(newThreadsCount));
			threadStateCountMap.put("RUNNABLE", new Integer(runnableThreadsCount));
			threadStateCountMap.put("TIMED_WAITING", new Integer(timedWaitingThreadsCount));
			threadStateCountMap.put("WAITING", new Integer(waitingThreadsCount));
			threadStateCountMap.put("BLOCKED", new Integer(blockedThreadsCount));
			threadStateCountMap.put("TERMINATED", new Integer(terminatedThreadsCount));
			filePropInfoMap.put("threadStateCountMap", threadStateCountMap);
	
			filePropInfoMap.put("newThreadMap", newThreadMap);
			filePropInfoMap.put("runnableThreadMap", runnableThreadMap);
			filePropInfoMap.put("waitingThreadMap", waitingThreadMap);
			filePropInfoMap.put("timedWaitingThreadMap", timedWaitingThreadMap);
			filePropInfoMap.put("blockedThreadMap", blockedThreadMap);
			filePropInfoMap.put("terminatedThreadMap", terminatedThreadMap);
			
			System.out.println("Parsing Thread by State: ");
			for(Entry<String, Object> entry: filePropInfoMap.entrySet())
			{
				System.out.println("key: " + entry.getKey() + "\tvalue: " + entry.getValue());
			}
			
			session.setAttribute(filePath, filePropInfoMap);
		}
	}
	
	private String unzipFiles(HttpSession session, String filePath) {
		String path = null;
		try {
//			String filename = (String) session.getAttribute("filename");
//			String filePath = UPLOAD_DIRECTORY + File.separator + filename;
//			if(filename.endsWith("zip") || filename.endsWith("rar"))
//			{
				File destDir = new File(filePath.substring(0, filePath.lastIndexOf(".")));
				destDir.mkdir();
				byte[] buffer = new byte[1024];
				ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath));
				ZipEntry zipEntry = zis.getNextEntry();
				while(zipEntry != null)
				{
					File newFile = newFile(destDir, zipEntry);
					path = newFile.getCanonicalPath();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while((len = zis.read(buffer)) > 0)
					{
						fos.write(buffer, 0, len);
					}
					fos.close();
					zipEntry = zis.getNextEntry();
				}
				zis.closeEntry();
				zis.close();
//			} else {
//			parseThreadDumpFile(session, filePath);
//			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}

	@SuppressWarnings("resource")
	private void parseThreadDumpFile(HttpSession session, String filename) throws FileNotFoundException {
//		BufferedReader bufferedReader = new BufferedReader(new FileReader(
//				"E:\\eclipse_playarea\\thread-dump-analyzer\\src\\com\\guptae\\resources\\" + filename));
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
		List<String> dumpMetadataList = new ArrayList<String>();
		// String[] metadataArray = new String[5];
		List<String> threadInfo = null;
		String threadDataKey = null;
		Map<String, List<String>> threadDumpDataMap = new HashMap<String, List<String>>();
		Map<String, Object> filePropInfoMap = new HashMap<>();
		try {
			String currentLine = bufferedReader.readLine();
			for (; null != currentLine && !currentLine.startsWith("\"");) {
				if (currentLine.isEmpty()) {
					currentLine = bufferedReader.readLine();
					continue;
				}
				dumpMetadataList.add(currentLine);
				// metadataArray[i] = currentLine;
				currentLine = bufferedReader.readLine();
			}
			filePropInfoMap.put("dumpMetaDataList", dumpMetadataList);

			while (null != currentLine) {
				if (currentLine.isEmpty()) {
					currentLine = bufferedReader.readLine();
					continue;
				}
				if (currentLine.startsWith("\"")) {
					threadInfo = new ArrayList<String>();
					threadDataKey = currentLine.substring(1, currentLine.lastIndexOf('\"'));
					threadInfo.add(currentLine);

					for (currentLine = bufferedReader.readLine(); null != currentLine
							&& !currentLine.startsWith("\"") && !currentLine.startsWith("JNI global references:")
							&& !currentLine.contains("deadlock")
							&& !currentLine.startsWith("Heap"); currentLine = bufferedReader.readLine()) {
						threadInfo.add(currentLine);
					}
					threadDumpDataMap.put(threadDataKey, threadInfo);
				} else if (currentLine.startsWith("JNI global references:")) {
					// TODO: print only the number of JNI of global
					// references
					filePropInfoMap.put("JNI global references",
							currentLine.substring(currentLine.indexOf("JNI global references: ")));
					currentLine = bufferedReader.readLine();
				} else if (currentLine.contains("deadlock")) {
					threadInfo = new ArrayList<String>();
					threadInfo.add(currentLine);
					for (currentLine = bufferedReader.readLine(); null != currentLine
							&& !currentLine.contains("deadlock"); currentLine = bufferedReader.readLine()) {
						threadInfo.add(currentLine);
					}
					threadInfo.add(currentLine);
					filePropInfoMap.put("deadlockInfoList", threadInfo);
					currentLine = bufferedReader.readLine();
				} else if (currentLine.startsWith("Heap")) {
					threadInfo = new ArrayList<String>();
					threadInfo.add(currentLine);
					for (currentLine = bufferedReader.readLine(); null != currentLine
							&& !currentLine.isEmpty(); currentLine = bufferedReader.readLine()) {
						threadInfo.add(currentLine);
					}
					filePropInfoMap.put("Heap", threadInfo);
				}
			}
			filePropInfoMap.put("allThreadInfoMap", threadDumpDataMap);
			System.out.println("Parsing Thread Dump: ");
			for(Entry<String, Object> entry: filePropInfoMap.entrySet())
			{
				System.out.println("key: " + entry.getKey() + "\tvalue: " + entry.getValue());
			}
			session.setAttribute(filename, filePropInfoMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File newFile(File destDir, ZipEntry zipEntry) throws IOException {
		String filename = zipEntry.getName();
		String separator = filename.contains("/")?"/":null;
		if(null == separator)
			separator = filename.contains(File.separator)?File.separator:null;
		
		if(null != separator)
		{
			String[] filenameArray = filename.split(separator); 
			for(int i=0; i<filenameArray.length-1; i++)
			{
				File dir = new File(destDir + separator + filenameArray[i]);
				if(!(dir.exists()))
					dir.mkdir();
			}
		}
		File destFile = new File(destDir, zipEntry.getName());
		
		String destDirPath = destDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();
		
		if(!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of target dir: " + zipEntry.getName());
		}
		
		return destFile;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
