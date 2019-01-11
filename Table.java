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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for parsing a file containing organized data.
 * Compatible file types: .CSV, .TSV, etc...
 * 
 * @author Robert Hutter
 */
public class Table
{
    String[] header;
    String[][] data;
    
    /**
     * Constructor of class Table.
     * 
     * @param header The header row of the table
     * @param data The data part of the table
     */
    public Table(String[] header, String[][] data)
    {
        this.header = header;
        this.data = data;
    }
    
    /**
     * Creates a completely empty table
     */
    public Table()
    {
        header = null;
        data = null;
    }
    
    /**
     * Parses a file and returns a Table
     *
     * @param fileName The name of the file to parse
     * @param delim The delimeter used in the file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Table parseFile(String fileName, char delim) throws FileNotFoundException, IOException
    {
        return parseFile(new File(fileName), delim);
    }
    
    /**
     *  Parses a file and returns a Table
     * 
     * @param in File to parse
     * @param delim The delimeter used in the file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Table parseFile(File in, char delim) throws FileNotFoundException, IOException
    {
        Table t = new Table();
        // Read header
        try (BufferedReader fw = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(in), "utf-8")
        )
        //FileReader fw = new FileReader(in);
        ) {
            // Read header
            t.setHeader(parseLine(readLine(fw), delim));
            final int colNum = t.getHeader().length;
            
            // Read data
            ArrayList<String[]> cols = new ArrayList<>();
            
            String line = readLine(fw);
            
            while (!line.equals(""))
            {
                cols.add(parseLine(line, delim));
                line = readLine(fw);
            }
            
            t.setData(cols.toArray(new String[cols.size()][colNum]));
        }
        
        return t;
    }
    
    private static String[] parseLine(String line, char delim)
    {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("");
        
        int i = 0;
        for (char c : line.toCharArray())
        {
            if (c == delim)
            {
                cols.add("");
                i++;
                continue;
            }
            
            cols.set(i, cols.get(i) + c);
        }
        
        return cols.toArray(new String[cols.size()]);
    }
    
    private static String readLine(BufferedReader fw) throws IOException
    {
        StringBuilder sb = new StringBuilder("");
        
        char c = (char) fw.read();
        
        while (c != '\n' && c != -1 && c != '\r' && c != '\uffff')
        {
            sb.append(c);
            c = (char) fw.read();
        }
        
        if (c == '\r')
        {
            fw.skip(1);
        }
        
        //System.out.println(sb.toString());
        
        return sb.toString();
    }
    
    /**
     *  Export the Table to a file
     * 
     * @param file
     * @param delim
     * @throws IOException
     */
    public void export(String file, char delim) throws IOException
    {
        try {
            export(new File(file), delim);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *  Export the Table to a file
     * 
     * @param file
     * @param delim
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void export(File file, char delim) throws FileNotFoundException, IOException
    {
        // Write header
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            // Write header
            for (int i = 0; i < header.length; i++)
            {
                writer.append(header[i]);
                
                // Last col should not have a delim at the end of it
                if (i != header.length - 1)
                {
                    writer.append(delim);
                }
            }
            
            writer.append('\n');
            
            // Write contents
            for (int y = 0; y < data.length; y++)
            {
                for (int x = 0; x < data[y].length; x++)
                {
                    writer.append(data[y][x]);
                    
                    // Last col should not have a delim at the end of it
                    if (x != data[y].length - 1)
                    {
                        writer.append(delim);
                    }
                }
                
                // Last row should not have a new line at the end of it
                if (y != data.length - 1)
                {
                    writer.append('\n');
                }
            }
        }
    }
    
    /**
     * Flip two columns in the table
     * 
     * @param col1 The index of the first column
     * @param col2 The index of the second column
     * @param updateHeader Switch the header labels too?
     */
    public void flipCols(int col1, int col2, boolean updateHeader)
    {
        // Create temp block
        String temp;
        
        // Perform switch
        for (String[] data1 : data) {
            temp = data1[col1];
            data1[col1] = data1[col2];
            data1[col2] = temp;
        }
                       
        // Update header
        if (updateHeader)
        {
            // Create temp string for header
            String temp2 = header[col1];

            // Perform switch
            header[col1] = header[col2];
            header[col2] = temp2;
        }
    }
    
    /**
     *
     * @return
     */
    public String[] getHeader()
    {
        return header;
    }
    
    /**
     *
     * @param header
     */
    public void setHeader(String[] header)
    {
        this.header = header;
    }
    
    /**
     *
     * @return
     */
    public String[][] getData()
    {
        return data;
    }
    
    /**
     *
     * @param data
     */
    public void setData(String[][] data)
    {
        this.data = data;
    }
}
