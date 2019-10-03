package com.guptae.file.action;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 * Servlet implementation class FileValidator
 */
@WebServlet(urlPatterns = { "/uploadFile"})
@MultipartConfig(fileSizeThreshold = 1024*1024*2, //2 MB
				maxFileSize = 1024*1024*10,		// 10 MB
				maxRequestSize = 1024*1024*50)	// 50 MB
public class UploadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String UPLOAD_DIRECTORY = "E:\\eclipse_playarea\\thread-dump-analyzer\\src\\com\\guptae\\resources";
	private boolean isFailed;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		File fileSaveDir = new File(UPLOAD_DIRECTORY);
		String filename = "";
		if(!fileSaveDir.exists())
		{
			fileSaveDir.mkdir();
		}
		
		for(Part part : request.getParts())
		{
			filename = extractFileName(part);
			if(null != filename && filename.length() > 0 && (filename.contains("stack") || (filename.contains("thread")) || (filename.contains("dump"))))
			{
				String filePath = UPLOAD_DIRECTORY + File.separator + filename;
				System.out.println("Write the uploaded file to " + filePath);
				part.write(filePath);
			} else {
				isFailed = true;
				break;
			}
		}
		if("".equals(filename) || isFailed)
		{
			request.setAttribute("errorString", "Error while uploading the file. Please upload a proper file.");
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/launchpage.jsp");
			dispatcher.forward(request, response);
		}
		else{
			// delete or invalidate the old session if any.
			HttpSession oldSession = request.getSession(false);
			if(null != oldSession)
			{
				oldSession.invalidate();
			}
			HttpSession newSessuion = request.getSession(true);
			// set max idle time as 2 hrs
			newSessuion.setMaxInactiveInterval(2*60*60);

			newSessuion.setAttribute("filename", filename);
			/*Cookie fileCookie = new Cookie("filename", filename);
			response.addCookie(fileCookie);*/
			
			request.setAttribute("successString", "file uploaded successfully!");
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/launchpage.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	private String extractFileName(Part part)
	{
		// form-data; name="file"; filename="E:\eclipse_playarea\thread-dump-creator\deadlock_stacktrace.log"
		String contentDisplay = part.getHeader("content-disposition");
		String[] items = contentDisplay.split(";");
		String filename = null;
		for(String item : items)
		{
			if(item.trim().startsWith("filename"))
			{
				filename = item.substring(item.indexOf("=")+2, item.lastIndexOf("\""));
			}
		}
		return filename;
	}

}
