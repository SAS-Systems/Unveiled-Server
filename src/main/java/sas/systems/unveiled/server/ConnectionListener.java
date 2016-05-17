/*
 * Copyright 2016 Sebastian Schmidl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sas.systems.unveiled.server;

import java.io.IOException;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sas.systems.unveiled.server.fileUpload.FilePOJO;
import sas.systems.unveiled.server.util.DatabaseConnector;

/**
 * Servlet implementation class ConnectionListener
 */
public class ConnectionListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB
	private SessionManager sm;
	@EJB
	private FileStream stream;
       
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
		response.getWriter().append("RTP session is running: ").append(String.valueOf(sm.isRunning())).append("\n");
		response.getWriter().append("\nsession is running on: ").append(sm.getHost()).append(":")
				.append(String.valueOf(sm.getDataPort())).append("(data) and ").append(String.valueOf(sm.getHost()))
				.append(":").append(String.valueOf(sm.getControlPort())).append("(control)").append("\n");
		response.getWriter().append("FileStream object id: ").append(String.valueOf(stream.getId())).append("\n");
		response.getWriter().append(getServletContext().getRealPath("/")).append("\n");
		DatabaseConnector connector = new DatabaseConnector();
		FilePOJO file = new FilePOJO(1, "testfile", "testfile", "testurl", "testthumbnailurl", "video/mp4", new Date(), 256, 5.6, 10.4, false, true, 23, 860, 240, "860x240");
//		response.getWriter().append(file + " succesfully inserted: " + connector.insertFile(file));
		connector.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
