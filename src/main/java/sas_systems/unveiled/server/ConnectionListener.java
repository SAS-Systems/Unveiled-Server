package sas_systems.unveiled.server;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ConnectionListener
 */
public class ConnectionListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB
	private SessionManager sm;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConnectionListener() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath()).append("\n");
		response.getWriter().append("RTP session is running: ").append(String.valueOf(sm.isRunning()));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
