package org.rit.classmap.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class JsonUtil {

	public String readQueryStream(InputStream is)
	{
		String result = "";
	    try{
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                    
            }
            is.close();
            result=sb.toString();
            reader.close();
            return result;
	    }catch(Exception e){
	            Log.e("log_tag", "Error converting result "+e.toString());
	    }
	    return null;
	} 
	
	
	
	
}
