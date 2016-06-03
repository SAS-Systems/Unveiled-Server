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
import java.io.Writer;
import java.util.Properties;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas.systems.unveiled.server.util.PropertiesLoader;
import sas.systems.unveiled.server.util.SessionManager;

/**
 * Servlet implementation class ConnectionListener
 */
@WebServlet("/ConnectionSetup")
public class ConnectionListener extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionListener.class);
	
	@EJB
	private SessionManager sm;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOG.info("Request received! (info)");
		try {
			final Writer out = response.getWriter();
			final Properties props = PropertiesLoader.loadPropertiesFile(PropertiesLoader.MEDIA_PROPERTIES_FILE);
			final String localPath = props.getProperty(PropertiesLoader.MediaProps.SYSTEM_PATH_TO_MEDIA);
			final String urlPath = props.getProperty(PropertiesLoader.MediaProps.URL_MEDIA_PATH_PREFIX);
			
			// session information
			out.append("Sessions are running: ").append(String.valueOf(sm.isRunning())).append("\n");
			out.append("\nRTP session is running on:\t")
					.append(sm.getHost()).append(":").append(String.valueOf(sm.getDataPort())).append(" (data) and ")
					.append(sm.getHost()).append(":").append(String.valueOf(sm.getControlPort())).append(" (control)");
			out.append("\nRTSP session is running on:\t")
					.append(sm.getHost()).append(":").append(String.valueOf(sm.getSessionControlPort())).append("\n");
			
			// media locations:
			out.append("\nMedia location on server:\t").append(localPath);
			out.append("\nMedia location served as URL:\t").append(urlPath);
			
			out.flush();
			out.close();
		} catch(IOException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
	}
}
