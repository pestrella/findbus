package findbus;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.readLines;
import static org.apache.commons.lang.StringUtils.trim;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.csv.CSVUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

/**
 * @author pestrella
 */
public class ExportBusData {
  public static final String HOST = "http://find-bus.appspot.com";

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
        if (count % 100 == 0)
          System.out.println(format(" --> %d complete", count));
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