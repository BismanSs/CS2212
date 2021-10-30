import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the API handler for the World Bank Data Repository
 * @author Paul Scoropan, Gouri Sikha, Bizman Sawhney, Owen Tjhie
 */
public class DatabaseAPIHandler {

    /**
     * This method requests data from the api based on the parameters and returns it
     * @param country the selected country
     * @param analysisType the selected analysis type, ie the repository indicator
     * @param startYear the selected start year
     * @param endYear the selected end year
     * @return a string representation of the json returned from the API
     */
    public String requestData(int country, int analysisType, int startYear, int endYear) {
        HttpURLConnection connection; // declare an http connection
        try {
            URL url = new URL(
                    "http://api.worldbank.org/v2/country/" + Util.COUNTRY_CODES[country] +
                            "/indicator/" + Util.INDICATORS[analysisType] +
                            "?date=" + startYear +
                            ":" + endYear +
                            "&format=json"); // initialize a url to access the api with the parameters concatenated within appropriately
            connection = (HttpURLConnection) url.openConnection(); // try to open a connection to the url
            connection.setRequestMethod("GET"); // set the request method to get since we are getting data
            connection.setRequestProperty("Content-Type", "application/json"); // set the content type we are receiving to JSON

            connection.setRequestProperty("Content-Language", "en-US"); //  set the content language to US english

            InputStream is = connection.getInputStream(); // initialize an input stream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is)); // initialize a buffered reader of the input stream
            StringBuilder response = new StringBuilder(); // initialize a string builder
            String line;
            while ((line = rd.readLine()) != null) { // loop until the end of the input stream
                response.append(line); // append line to response
                response.append('\n'); // append new line
            }
            rd.close(); // close the reader
            return response.toString(); // return the string builder as a string
        } catch (Exception e) { // catch any exceptions thrown while trying to access the api
            MainUI.displayError("ERROR READING API"); // display error to main UI
            try{
                Thread.sleep(1000); // sleep for 1 second so user can see the error
            } catch (InterruptedException i) {}
            System.exit(0); // terminate the program since api could not be accessed (if api returns null data that is not an exception, system only exits when calls can not be made to the api)
            return null; // this shouldn't ever run but it is required for compilation
        }
    }

    /**
     * This method formats a string of data into a map of Integer keys (years) and object values
     * @param data the data string, passed from the api response
     * @return a map of the data
     */
    public Map<Integer, Object> formatData(String data) {
        Map<Integer, Object> returnMap = new HashMap<Integer, Object>(); // initialize empty map
        String[] dateSplit = data.split("date\":\""); // split data string at [date":"]
        for (int i = 1; i < dateSplit.length; i++) { // loop though the returned string array

            if (dateSplit[i].split("value\":")[1].split(",")[0].startsWith("\"")) { // split the line at [value":], get the second string returned, split it at [,], take the first string returned and check if it starts with a ["] (ie check if the data is a string)
                String value = dateSplit[i].split("value\":")[1].split(",")[0].split("\"")[0]; // continue to split the json into further substrings to extract the necessary data
                returnMap.put(Integer.parseInt(dateSplit[i].split("\"")[0]), value); // parse the year into an int and place the string data into the map at key year
            } else {
                try {
                    Double value = Double.parseDouble(dateSplit[i].split("value\":")[1].split(",")[0]); // try to parse the data into a double
                    returnMap.put(Integer.parseInt(dateSplit[i].split("\"")[0]), value); // parse the year into an int and place the double data into the map at key year
                } catch (NumberFormatException e) { // catch exception thrown when parsing double
                    returnMap.put(Integer.parseInt(dateSplit[i].split("\"")[0]), dateSplit[i].split("value\":")[1].split(",")[0]); // parse the year into an int and place the data into the map at key year as a string (any data that is not a number will therefore be returned as a string)
                }
            }
        }
        return returnMap;
    }
}
