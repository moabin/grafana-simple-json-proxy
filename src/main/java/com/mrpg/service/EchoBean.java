package com.mrpg.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import org.apache.camel.Body;
import java.security.cert.X509Certificate;
import java.util.UUID;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

/**
 *
 * @author moabi
 */
public class EchoBean {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger("EchoBean");

    /**
     * Process USSD inbound data
     *
     * @param body incoming data
     * @return Response XML
     */
    public String echo(@Body String body) {
        LOG.debug("@@start@@\n" + body + "\n@@end@@");
        LOG.info("echo");
        return body;
    }
    
    
    public String search(@Body String body){
        LOG.debug("@@start@@\n" + body + "\n@@end@@");
        LOG.info("search");
        String response = "[\"upper_25\",\"upper_50\",\"upper_75\",\"upper_90\",\"upper_95\"]";
        
        return response;
    }
    
    public String query(@Body String body) {
        LOG.debug("@@start@@\n" + body + "\n@@end@@");
        LOG.info("query");
        
        
        String newResponse = "[{\n" +
"	\"columns\": [{\n" +
"		\"text\": \"Time\",\n" +
"		\"type\": \"time\"\n" +
"	}, {\n" +
"		\"text\": \"Country\",\n" +
"		\"type\": \"string\"\n" +
"	}, {\n" +
"		\"text\": \"Number\",\n" +
"		\"type\": \"number\"\n" +
"	}],\n" +
"	\"rows\": [\n" +
"		[1509107737817, \"SE\", 123],\n" +
"		[1509106737817, \"DE\", 231],\n" +
"		[1509105737817, \"US\", 321]\n" +
"	],\n" +
"	\"type\": \"table\"\n" +
"}]";
                
        LOG.info(newResponse);
        return newResponse;
    }

    

}
