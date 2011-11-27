/*
 * $Copyright: copyright(c) 2007-2011 kuwata-lab.com all rights reserved. $
 * $License: Creative Commons Attribution (CC BY) $
 */
package teb;
 
import java.util.List;
import java.util.ArrayList;

public class Stock {

    private String _name;
    private String _name2;
    private String _url;
    private String _symbol;
    private double _price;
    private double _change;
    private double _ratio;

    public Stock(String name, String name2, String url, String symbol, double price, double change, double ratio) {
        _name   = name;
        _name2  = name2;
        _url    = url;
        _symbol = symbol;
        _price  = price;
        _change = change;
        _ratio  = ratio;
    }

    public String getName()   { return _name; }
    public String getName2()  { return _name2; }
    public String getUrl()    { return _url; }
    public String getSymbol() { return _symbol; }
    public double getPrice()  { return _price; }
    public double getChange() { return _change; }
    public double getRatio()  { return _ratio; }

    public static List<Stock> dummyItems() {
        List<Stock> items = new ArrayList<Stock>();
        items.add(new Stock("Adobe Systems", "Adobe Systems Inc.", "http://www.adobe.com", "ADBE", 39.26, 0.13, 0.33));
        items.add(new Stock("Advanced Micro Devices", "Advanced Micro Devices Inc.", "http://www.amd.com", "AMD", 16.22, 0.17, 1.06));
        items.add(new Stock("Amazon.com", "Amazon.com Inc", "http://www.amazon.com", "AMZN", 36.85, -0.23, -0.62));
        items.add(new Stock("Apple", "Apple Inc.", "http://www.apple.com", "AAPL", 85.38, -0.87, -1.01));
        items.add(new Stock("BEA Systems", "BEA Systems Inc.", "http://www.bea.com", "BEAS", 12.46, 0.09, 0.73));
        items.add(new Stock("CA", "CA, Inc.", "http://www.ca.com", "CA", 24.66, 0.38, 1.57));
        items.add(new Stock("Cisco Systems", "Cisco Systems Inc.", "http://www.cisco.com", "CSCO", 26.35, 0.13, 0.5));
        items.add(new Stock("Dell", "Dell Corp.", "http://www.dell.com/", "DELL", 23.73, -0.42, -1.74));
        items.add(new Stock("eBay", "eBay Inc.", "http://www.ebay.com", "EBAY", 31.65, -0.8, -2.47));
        items.add(new Stock("Google", "Google Inc.", "http://www.google.com", "GOOG", 495.84, 7.75, 1.59));
        items.add(new Stock("Hewlett-Packard", "Hewlett-Packard Co.", "http://www.hp.com", "HPQ", 41.69, -0.02, -0.05));
        items.add(new Stock("IBM", "International Business Machines Corp.", "http://www.ibm.com", "IBM", 97.45, -0.06, -0.06));
        items.add(new Stock("Intel", "Intel Corp.", "http://www.intel.com", "INTC", 20.53, -0.07, -0.34));
        items.add(new Stock("Juniper Networks", "Juniper Networks, Inc", "http://www.juniper.net/", "JNPR", 18.96, 0.5, 2.71));
        items.add(new Stock("Microsoft", "Microsoft Corp", "http://www.microsoft.com", "MSFT", 30.6, 0.15, 0.49));
        items.add(new Stock("Oracle", "Oracle Corp.", "http://www.oracle.com", "ORCL", 17.15, 0.17, 1.0));
        items.add(new Stock("SAP", "SAP AG", "http://www.sap.com", "SAP", 46.2, -0.16, -0.35));
        items.add(new Stock("Seagate Technology", "Seagate Technology", "http://www.seagate.com/", "STX", 27.35, -0.36, -1.3));
        items.add(new Stock("Sun Microsystems", "Sun Microsystems Inc.", "http://www.sun.com", "SUNW", 6.33, -0.01, -0.16));
        items.add(new Stock("Yahoo", "Yahoo! Inc.", "http://www.yahoo.com", "YHOO", 28.04, -0.17, -0.6));
        return items;
    }

}
