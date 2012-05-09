package dan.langford;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;

public class FunServer {

	public enum Up {
		TEXT,JSON,XML
	}

	public static class Ctrl {
		private Server server;
		private ServletContextHandler context;
		
		Ctrl(int port) {
			this.server = new Server(port);
			this.context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			this.context.setContextPath("/");
			this.server.setHandler(context);
		}
		
		public Ctrl basicAuth(String username, String password) {

	        String realm = "Private!";
	        
	    	HashLoginService l = new HashLoginService();
	        l.putUser(username, Credential.getCredential(password), new String[] {"user"});
	        l.setName(realm);
	        
	        Constraint constraint = new Constraint();
	        constraint.setName(Constraint.__BASIC_AUTH);
	        constraint.setRoles(new String[]{"user"});
	        constraint.setAuthenticate(true);
	         
	        ConstraintMapping cm = new ConstraintMapping();
	        cm.setConstraint(constraint);
	        cm.setPathSpec("/*");
	        
	        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
	        csh.setAuthenticator(new BasicAuthenticator());
	        csh.setRealmName("myrealm");
	        csh.addConstraintMapping(cm);
	        csh.setLoginService(l);
	        
	        context.setSecurityHandler( csh );
	        return this;
	        
	    }

		public Brake start() {
			try {
				server.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new Brake(server);
		}
		
		public Ctrl serve(Up type) {
			switch (type) {
			case TEXT:
				serveUpText();
				break;
			}
			return this;
		}
		
		@SuppressWarnings("serial")
		private void serveUpText() {
			context.addServlet(new ServletHolder(new HttpServlet() {
				@Override
			    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			            throws ServletException, IOException {
					resp.setContentType("text/plain");
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().println("hello world");
			    }
			}),"/*");

		}
		
	}
	
	public static class Brake {
		private Server server;
		Brake(Server server){
			this.server = server;
		}
		
		public void stop() {
			try {
				server.stop();
				server.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	public static Ctrl onPort(int port) {
		return new Ctrl(port);
	}


	public static void main(String[] args) throws Exception {

		FunServer.onPort(3210).serve(Up.TEXT).basicAuth("scott","tiger").start();
		
	}
}
