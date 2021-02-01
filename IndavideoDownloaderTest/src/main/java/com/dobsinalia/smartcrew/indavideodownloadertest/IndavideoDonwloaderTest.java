/*
* MIT License
*
* Copyright (c) 2021 Robert Hutter
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
package com.dobsinalia.smartcrew.indavideodownloadertest;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

/**
 * Attempts to send and receive an HTTP request.
 * 
 * @author Robert Hutter
 */
public abstract class IndavideoDonwloaderTest
{
    static HttpURLConnection connection;
    static String apiurl = "https://indavideo.nxu.hu/url";
    
    static String key = "url";
    static String value = "https://indavideo.hu/video/Egy_steaket_legyen_szives";
    
    static String url360;
    static String url720;
    static String url1080;
    
    public static void main(String[] args) throws Exception
    {
        connection = (HttpURLConnection) new URL(apiurl).openConnection();
        
        // Setup POST request
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        // Connect
        System.out.println("Connecting to " + apiurl + "\n");
        connection.connect();
        
        // Prepare and send reqest data to send
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(key+"="+value);
        outputStream.flush();
        outputStream.close();
        
        // Check responce code
        System.out.println("Response: " + connection.getResponseCode() + " "
            + connection.getResponseMessage() + "\n");
        
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            InputStream is = connection.getInputStream();
            JsonParser parser = Json.createParser(is);
            while (parser.hasNext())
            {
                Event e = parser.next();

                if (e == Event.KEY_NAME)
                {
                    switch (parser.getString())
                    {
                        case "360":
                            if (parser.next() == Event.VALUE_STRING)
                                url360 = parser.getString();
                        break;
                        
                        case "720":
                            if (parser.next() == Event.VALUE_STRING)
                                url720 = parser.getString();
                        break;
                        
                        case "1080":
                            if (parser.next() == Event.VALUE_STRING)
                                url1080 = parser.getString();
                        break;
                    }
                }
            }
        }
        else
        {
            System.out.println("Error connecting to website...");
            System.exit(1);
        }
        
        // Disconnect
        connection.disconnect();
        
        // Retrieved data
        System.out.println("Retrieved data: ");
        System.out.println("360p: "+url360);
        System.out.println("720p: "+url720);
        System.out.println("1080p: "+url1080+"\n");
        
        // Download video
        System.out.println("Downloading video...");
        BufferedInputStream in = new BufferedInputStream(new URL(url360).openStream());
        FileOutputStream fos = new FileOutputStream("video.mp4");
        
        byte data[] = new byte[1024];
        int byteContent;
        
        while ((byteContent = in.read(data, 0, 1024)) != -1)
        {
            fos.write(data, 0, byteContent);
        }
        
        fos.close();
        
        System.out.println("Done.");
    }
}
