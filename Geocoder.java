/*
 * The MIT License
 *
 * Copyright 2019 Robert Hutter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A class handling the geocoding of geographical names. 
 * 
 * @author rohu7
 */
public class Geocoder {
    protected Table database;
    protected Table list;
    protected int cityCol = -1;
    protected int nameCol = -1;
    protected int latCol = -1;
    protected int lonCol = -1;
    protected char delim;
    
    public Geocoder (String src, String table, char delim) throws FileNotFoundException, IOException
    {
        database = Table.parseFile(src, delim);
        list = Table.parseFile(table, delim);
        this.delim = delim;
    }
    
    public void geocode() throws IOException
    {
        // Parse files
        /*System.out.println(Arrays.toString(list.getHeader()));
        
        for (int i = 0; i < list.getData().length; i++)
        {
            System.out.print(Arrays.toString(list.getData()[i])+",");
        }*/
        
        // Find columns
        for (int i = 0; i < database.getHeader().length; i++)
        {
            switch(database.getHeader()[i].toLowerCase())
            {
                case "name":
                    nameCol = i;
                    break;
                    
                case "city":
                    cityCol = i;
                    break;
                    
                case "lat":
                    latCol = i;
                    break;
                    
                case "lon":
                    lonCol = i;
                    break;
            }
        }
        
        // Make sure we found the columns
        if (cityCol == -1)
        {
            System.out.println("Database does not contain a city column.");
            System.exit(1);
        }
        
        if (nameCol == -1)
        {
            System.out.println("Database does not contain a name column.");
            System.exit(1);
        }
        
        if (latCol == -1)
        {
            System.out.println("Database does not contain a lat column.");
            System.exit(1);
        }
        
        if (lonCol == -1)
        {
            System.out.println("Database does not contain a lon column.");
            System.exit(1);
        }
        
        // Create output table contents
        String[] outH = new String[list.getHeader().length + 2]; // Lat & lon
        String[][] outD = new String[list.getData().length][outH.length];
        
        // Setup output header
        outH[0] = "Name";
        outH[1] = "City";
        outH[2] = "Lat";
        outH[3] = "Lon";
        
        // Iterate through the name that need to be geocoded
        for (int i = 0; i < list.getData().length; i++)
        {
            String[] query = find(list.getData()[i][0], list.getData()[i][1]);
            if (query == null)
            {
                outD[i][0] = list.getData()[i][0];
                outD[i][1] = list.getData()[i][1];
                System.out.println("Record #" + i + " not found in database.");
                continue;
            }
            
            outD[i][0] = list.getData()[i][0];
            outD[i][1] = list.getData()[i][1];
            outD[i][2] = query[0];
            outD[i][3] = query[1];
            
        }
        
        File out = new File("output.xsv");
        Table r = new Table(outH, outD);
        r.export(out, delim);
        
    }
    
    private String[] find(String name, String city)
    {
        String[] ret = new String[2];
        
        // linear search
        for (String[] data : database.getData()) {
            if (data[nameCol].toLowerCase().equals(name.toLowerCase())) {
                if (data[cityCol].toLowerCase().equals(city.toLowerCase())) {
                    ret[0] = data[latCol];
                    ret[1] = data[lonCol];
                    return ret;
                }
            }
        }
        
        return null;
    }
    
}
