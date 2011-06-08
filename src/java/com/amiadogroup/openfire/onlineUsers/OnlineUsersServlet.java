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

/**
 * Servlet for serving the online users count.
 * 
 * @author Michael Weibel <michael.weibel@amiadogroup.com>
 */
public class OnlineUsersServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -329699890203170575L;

	/**
	 * Initialize servlet & add exclude to authcheck for http://yourserver:9090/plugins/onlineusers.
	 */
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		AuthCheckFilter.addExclude("onlineusers");
	}

	/**
	 * Get request on the online users plugin. Serve the number of plugins.
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		Collection<ClientSession> sessions = SessionManager.getInstance().getSessions();
        Set<String> users = new HashSet<String>(sessions.size());
        for (ClientSession session : sessions) {
            users.add(session.getAddress().toBareJID());
        }
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println(String.valueOf(users.size()));
        out.flush();
	}
	
	/**
	 * Destroy - remove the exclude filter
	 */
	@Override
	public void destroy() {
        super.destroy();
        
        // Release the excluded URL
        AuthCheckFilter.removeExclude("onlineusers");
    }
}
