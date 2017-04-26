package org.gavaghan.json;

import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )  throws Exception
    {
        try (FileInputStream fis = new FileInputStream("C:/Users/gavmi01/Documents/Customers/McDonalds/Projects/OCE/Promotions/promo1.json");
      		 InputStreamReader isr = new InputStreamReader(fis))
        {
      	  JSONObject json = JSONObject.read(isr);
        }
        
        System.out.println("Done");
    }
}
