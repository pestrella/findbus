package findbus;

import org.junit.Test;

import uk.me.jstott.jcoord.*;

/**
 * @author paolo.estrella@gmail.com
 */
public class GeoConvertTest {
  @Test
  public void convertUTM() {
    OSRef osRef = new OSRef(530237, 181523);
    LatLng latLng = osRef.toLatLng();
    latLng.toWGS84(); // convert to standard geo projection

    System.out.println("" + latLng.getLat() + ", " + latLng.getLng());
  }
}