package findbus;

import static java.lang.Integer.*;
import static java.lang.String.*;
import static org.apache.commons.io.IOUtils.*;
import static org.apache.commons.lang.StringUtils.*;

import org.apache.commons.csv.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.tsccm.*;

import uk.me.jstott.jcoord.*;

import java.io.*;
import java.util.*;

/**
 * @author paolo.estrella@gmail.com
 */
public class ExportBusData {
  public static final String HOST = "http://localhost:8080";

  public HttpClient getHttpClient() {
    return new DefaultHttpClient(new ThreadSafeClientConnManager());
  }

  private InputStream getData() {
    return getClass().getClassLoader().getResourceAsStream("bus-data.csv");
  }

  private void sendData(BusStop busStop) throws ClientProtocolException, IOException {
    GeoPoint geoPoint = toGeoPoint(busStop.easting, busStop.northing);

    HttpPost httpPost = new HttpPost(HOST + "/bus_stop");
    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
    httpPost.setEntity(new StringEntity(format(
      "code=%s&name=%s&route=%s&long=%f&lat=%f",
      busStop.code,
      busStop.name,
      busStop.route,
      geoPoint.longitude,
      geoPoint.latitude
    ), "UTF-8"));

    HttpResponse response = getHttpClient().execute(httpPost);
    if (response.getStatusLine().getStatusCode() != 201)
      throw new RuntimeException("Server failed to create data: " + busStop);
  }

  private GeoPoint toGeoPoint(int easting, int northing) {
    OSRef osRef = new OSRef(easting, northing);
    LatLng latLng = osRef.toLatLng();
    latLng.toWGS84(); // convert to standard geo projection

    return new GeoPoint(latLng.getLat(), latLng.getLng());
  }

  class GeoPoint {
    double longitude;
    double latitude;

    public GeoPoint(double latitude, double longitude) {
      this.latitude = latitude;
      this.longitude = longitude;
    }
  }

  class BusStop {
    String route;
    String code;
    String name;
    int easting;
    int northing;

    public BusStop(String route, String code, String name, int easting, int northing) {
      this.route = route;
      this.code = code;
      this.name = name;
      this.easting = easting;
      this.northing = northing;
    }

    @Override
    public String toString() {
      return String.format("[route=%s, code=%s, name=%s, easting=%s, northing=%s]", route, code, name, easting, northing);
    }
  }

  @SuppressWarnings("unchecked")
  public void run() throws IOException {
    List<String> entries = readLines(getData(), "UTF-8");
    entries.remove(0); // toss the heading

    int count = 0;
    try {
      for (String entry : entries) {
        String[] groups = CSVUtils.parseLine(entry);
        if (groups.length < 9)
          throw new RuntimeException("no way!");

        sendData(new BusStop(groups[0], groups[3], trim(groups[4]), parseInt(groups[5]), parseInt(groups[6])));
        count++;

        System.out.print("*");
        if (count % 100 == 0) {
          System.out.println(format(" --> %d complete", count));
        }
      }
    } catch (Exception e) {
      System.out.println(e);
      System.out.println(format("Failed to complete export after %d records", count));
    }
    System.out.println("Export completed! Records created: " + count);
  }

  public static void main(String[] args) throws IOException {
    ExportBusData exporter = new ExportBusData();
    exporter.run();
  }
}