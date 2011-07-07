/**
 * Openfire online users plugin
 * Copyright (C) 2011 Amiado Group AG
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.amiadogroup.openfire.onlineUsers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.util.JiveGlobals;

/**
 * Servlet for serving the online users count and the list of online users
 * 
 * @author Michael Weibel <michael.weibel@amiadogroup.com>
 */
public class OnlineUsersServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -329699890203170575L;

	/**
	 * Initialize servlet & add exclude to authcheck for
	 * http://yourserver:9090/plugins/onlineusers.
	 * If plugin.onlineUsers.list.disableAuth is set, also add an exclude to authcheck for
	 * http://yourserver:9090/plugins/onlineusers/list.
	 */
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		AuthCheckFilter.addExclude("onlineusers");
		if (JiveGlobals.getBooleanProperty("plugin.onlineUsers.list.disableAuth", false)) {
			AuthCheckFilter.addExclude("onlineusers/list");
		}
	}

	/**
	 * Get request on the online users plugin. Serve the number of users or the list of users.
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Set<String> users = getOnlineUsers();
		if (request.getPathInfo().indexOf("/list") != -1) {
			displayUserList(response, users);
		} else {
			displayUserCount(response, users);
		}
	}

	/**
	 * Displays the users lists as a JSON array
	 * @param response
	 * @param users
	 * @throws IOException
	 */
	private void displayUserList(HttpServletResponse response, Set<String> users) throws IOException {
		StringBuilder userList = new StringBuilder();
		userList.append('[');
		if (users.size() > 0) {
			for (String user : users) {
				userList.append("\"" + user +"\",");
			}
			userList.deleteCharAt(userList.length() - 1);
		}
		userList.append(']');
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(userList);
		out.flush();
	}

	/**
	 * Display user count
	 * @param response
	 * @param users
	 * @throws IOException
	 */
	private void displayUserCount(HttpServletResponse response,
			Set<String> users) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(String.valueOf(users.size()));
		out.flush();
	}

	/**
	 * Get online users list
	 * @return online users list as a set
	 */
	private Set<String> getOnlineUsers() {
		Collection<ClientSession> sessions = SessionManager.getInstance()
				.getSessions();
		Set<String> users = new HashSet<String>(sessions.size());
		for (ClientSession session : sessions) {
			users.add(session.getAddress().toBareJID());
		}
		return users;
	}

	/**
	 * Destroy - remove the exclude filters
	 */
	@Override
	public void destroy() {
		super.destroy();

		// Release the excluded URL
		AuthCheckFilter.removeExclude("onlineusers");
		AuthCheckFilter.removeExclude("onlineusers/list");
	}
}
