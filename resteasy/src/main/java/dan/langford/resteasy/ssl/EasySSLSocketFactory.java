package dan.langford.resteasy.ssl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * This socket factory will create ssl socket that accepts self signed
 * certificate
 * 
 * @author olamy
 * @version $Id: EasySSLSocketFactory.java 765355 2009-04-15 20:59:07Z evenisse
 *          $
 * @since 1.2.3
 */
public class EasySSLSocketFactory implements LayeredSchemeSocketFactory {

	private SSLContext sslcontext = null;

	private static SSLContext createEasySSLContext() throws IOException {
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[] { new EasyX509TrustManager(
					null) }, null);
			return context;
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	private SSLContext getSSLContext() throws IOException {
		if (this.sslcontext == null) {
			this.sslcontext = createEasySSLContext();
		}
		return this.sslcontext;
	}

	/**
	 * @see SchemeSocketFactory#createSocket(HttpParams)
	 */
	//OVERRIDE
	public Socket createSocket(HttpParams params) throws IOException {
		// hopefuly i dont need params
		return getSSLContext().getSocketFactory().createSocket();
	}

	/**
	 * @see SchemeSocketFactory#connectSocket(Socket, InetSocketAddress, 
	 * InetSocketAddress, HttpParams)
	 */
	//OVERRIDE
	public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress,
			InetSocketAddress localAddress, HttpParams params)
			throws IOException, UnknownHostException, ConnectTimeoutException {
		
		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);

		SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket(null));

//		if (localAddress != null) {
//			sslsock.bind(localAddress);
//		}

		sslsock.connect(remoteAddress, connTimeout);
		sslsock.setSoTimeout(soTimeout);
		return sslsock;
		
	}
	
	/**
	 * @see SchemeSocketFactory#isSecure(Socket)
	 */
	//OVERRIDE
	public boolean isSecure(Socket socket) throws IllegalArgumentException {
		return true;
	}

	/**
	 * @see LayeredSchemeSocketFactory#createLayeredSocket(Socket, String, int, boolean)
	 */
	// OVERRIDE
	public Socket createLayeredSocket(Socket socket, String target, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return getSSLContext().getSocketFactory().createSocket(socket, target, port, autoClose);
	}

}