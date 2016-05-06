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
package sas_systems.unveiled.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = -6308606465526504820L;

	public FileUploadServlet() {
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// read parameters:
		String filename = "undefined";
		String suffix = "unv";
		String author = "no_author";
		if(request.getParameter("filename") != null)
			filename = request.getParameter("filename");
		if(request.getParameter("suffix") != null)
			suffix = request.getParameter("suffix");
		if(request.getParameter("author") != null)
			author = request.getParameter("author");
		
		// create and write to file
		final long startTime = System.nanoTime();
		final String location = getServletContext().getRealPath("/") + "media\\";
		final FileWriter writer = new FileWriter(author, location, filename, suffix);
		writer.writeToFile(request.getInputStream());
		writer.close();
				
		// send result
		final long endTime = System.nanoTime();
		final long elapsedTimeNs = endTime - startTime;
		final double elapsedTimeS = elapsedTimeNs/(1e9);
		response.getWriter().println(filename + "." + suffix + " from " + author + " was succefully uploaded in " + elapsedTimeS + " seconds!");
	}
}
