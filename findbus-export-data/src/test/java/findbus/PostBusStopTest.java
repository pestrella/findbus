package findbus;

import static java.lang.String.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;
import org.junit.*;

import java.io.*;

/**
 * @author pestrella
 */
public class PostBusStopTest {
  @Test
  public void test() throws ClientProtocolException, IOException {
    final String bus_code = "blah";
    HttpPost httpPost = new HttpPost("http://localhost:8080/bus_stop");
    httpPost.setEntity(new StringEntity(format("code=%s&name=yo&route=205&long=51.517663&lat=-0.119833", bus_code), "UTF-8"));
    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

    HttpResponse response = new DefaultHttpClient().execute(httpPost);
    System.out.println(response.getStatusLine().getStatusCode());

    HttpGet httpGet = new HttpGet(format("http://localhost:8080/bus_stop/%s", bus_code));
    response = new DefaultHttpClient().execute(httpGet);
    System.out.println(EntityUtils.toString(response.getEntity()));
  }
}