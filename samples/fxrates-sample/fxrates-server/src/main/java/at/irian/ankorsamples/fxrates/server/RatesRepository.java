package at.irian.ankorsamples.fxrates.server;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @author Thomas Spiegl
 */
public class RatesRepository {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FXRatesRepository.class);

    public List<Rate> getRates() {
        BufferedReader in = null;
        XMLStreamReader reader = null;
        try {
            URL url = new URL("http://rates.fxcm.com/RatesXML");
            URLConnection conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            reader = XMLInputFactory.newInstance().createXMLStreamReader(in);

            return parseRates(reader);

        } catch (IOException | XMLStreamException e) {
            throw new IllegalStateException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException ignored) {
                }
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {            }
        }
    }

    private List<Rate> parseRates(XMLStreamReader reader) throws XMLStreamException {
        String currentElement = null;
        Map<String, Object> currentRate = null;
        List<Rate> rates = new ArrayList<>(100);
        while(reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if (reader.getName().toString().equals("Rate")) {
                    if (currentRate != null) {
                        rates.add(getRate(currentRate));
                    }
                    currentRate = new HashMap<>(5);
                    String symbol = reader.getAttributeValue(0);
                    if (symbol.length() == 6) {
                        symbol = symbol.substring(0,3) + "/" + symbol.substring(3,6);
                    }
                    currentRate.put("Symbol", symbol);
                    currentElement = null;
                } else {
                    currentElement = reader.getName().toString();
                }
            } else if (reader.getEventType() == XMLStreamConstants.ATTRIBUTE) {
                currentElement = null;
            } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                currentElement = null;
            } else if (reader.getEventType() == XMLStreamConstants.CHARACTERS
                    && currentRate != null && currentElement != null) {
                //noinspection ConstantConditions
                currentRate.put(currentElement, Converter.convert(currentElement, reader.getText()));
            }
        }
        if (currentRate != null) {
            rates.add(getRate(currentRate));
        }
        Collections.sort(rates);
        return rates;
    }

    private Rate getRate(Map<String, Object> currentRate) {
        return new Rate(
                (String) currentRate.get("Symbol"),
                (BigDecimal) currentRate.get("Bid"),
                (BigDecimal) currentRate.get("Ask"),
                (BigDecimal) currentRate.get("High"),
                (BigDecimal) currentRate.get("Low"),
                (Short) currentRate.get("Direction"),
                (String) currentRate.get("Last"));
    }

    private static class Converter {

        public static Object convert(String elementName, String value) {
            switch (elementName) {
                case "Bid":
                case "Ask":
                case "High":
                case "Low":
                    return new BigDecimal(value);
                case "Direction":
                    return new Short(value);
                default:
                    return value;
            }
        }

    }

}
