/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Monyo
 */
public class Util {

    private static final Logger LOG = Logger.getLogger("Util");

    /**
     * Utility method to get properties file
     *
     * @param prop Properties name
     * @return Properties
     */
    public static Properties getProperties(String prop) {
        final Properties properties = new Properties();
        InputStream is = null;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            is = loader.getResourceAsStream(prop + ".properties");
            properties.load(is);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }

        return properties;
    }

    /**
     * Performs HTTP POST operation and read reponse
     *
     * @param uri URI of HTTP service to be POST'ed to
     * @param body data to be POST'ed
     * @param headers HTTP headers as List<String, String>
     * @return reponse data (excl headers) or error response
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String doPost(String uri, String body) throws MalformedURLException, IOException {

        StringBuilder decodedString = new StringBuilder();
        URL url = new URL(uri);
        //HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        //URLConnection connection = url.openConnection();

        URLConnection connection;
        if (isHttpsUrl(uri)) {
            disableSSLCertificateChecking();
            connection = (HttpsURLConnection) url.openConnection();
            ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            });

        } else {
            connection = url.openConnection();
        }
        connection.setDoOutput(true);

        LOG.debug("writing to " + uri);
        OutputStreamWriter out = new OutputStreamWriter(
                connection.getOutputStream());

        out.write(body);

        LOG.debug("flush and close");

        out.flush();
        out.close();

        LOG.debug("done sending data");
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));

            LOG.debug("getting return");
            String line = "";
            while (line != null) {
                line = in.readLine();
                if (line != null) {
                    decodedString.append(line);
                }
                //LOG.info( line);
            }

            LOG.debug(decodedString.toString());

            in.close();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(((HttpURLConnection) connection).getErrorStream()));

            LOG.debug("getting Error return");
            String line = "";
            while (line != null) {
                line = in.readLine();
                if (line != null) {
                    decodedString.append(line);
                }
                //LOG.info( line);
            }

            LOG.debug(decodedString.toString());

            in.close();
        }
        return decodedString.toString();
    }

    /**
     * Returns true if the url is SSL enabled
     *
     * @param url URL to check
     * @return true if the url is SSL enabled
     */
    public static boolean isHttpsUrl(String url) {
        return (null != url) && (url.length() > 7) && url.substring(0, 8).equalsIgnoreCase("https://");
    }

    /**
     * Use to disable certificate checkign in SSL HTTP connections
     */
    private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Performs HTTP GET operation and read reponse
     *
     * @param uri URI of HTTP service to be called
     * @param headers HTTP headers as List<String, String>
     * @return reponse data (excl headers) or error response
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String doGet(String uri, HashMap headers) throws MalformedURLException, IOException {
        StringBuilder decodedString = new StringBuilder();
        URL url = new URL(uri);
        //HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        Iterator it = headers.keySet().iterator();
        LOG.debug("------------begin------------");

        while (it.hasNext()) {
            Object key = it.next();
            Object value = headers.get(key);
            connection.setRequestProperty(key.toString(), value.toString());
            LOG.debug("key = " + key + " | value = " + value);
        }

        LOG.debug("------------ end ------------");

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));

            LOG.debug("getting return");
            String line = "";
            while (line != null) {
                line = in.readLine();
                if (line != null) {
                    decodedString.append(line);
                }
                //LOG.info( line);
            }

            //LOG.debug(decodedString.toString());
            in.close();
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(((HttpURLConnection) connection).getErrorStream()));

            LOG.debug("getting Error return");
            String line = "";
            while (line != null) {
                line = in.readLine();
                if (line != null) {
                    decodedString.append(line);
                }
                //LOG.info( line);
            }

            LOG.debug(decodedString.toString());

            in.close();
        }
        return decodedString.toString();
    }

    /**
     * Performs HTTP GET operation and read reponse
     *
     * @param uri URI of HTTP service to be called
     * @param headers HTTP headers as List<String, String>
     * @return reponse data (excl headers) or error response
     * @throws MalformedURLException
     * @throws IOException
     */
    public static InputStream doGetStream(String uri, HashMap headers) throws MalformedURLException, IOException {
        
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        Iterator it = headers.keySet().iterator();
        LOG.debug("------------begin------------");
        while (it.hasNext()) {
            Object key = it.next();
            Object value = headers.get(key);
            connection.setRequestProperty(key.toString(), value.toString());
            LOG.debug("key = " + key + " | value = " + value);
        }
        LOG.debug("------------ end ------------");

        try {
            return connection.getInputStream();
        } catch (IOException ex) {
            return ((HttpURLConnection) connection).getErrorStream();
        }
    }

}
