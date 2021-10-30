import java.util.Map;

/**
 * This class represents an analysis of the currently selected parameters
 * @author Paul Scoropan, Gouri Sikha, Bizman Sawhney, Owen Tjhie
 */
public class Analysis {

    private int currentCountry; // the current country parameter
    private int currentAnalysis; // the current analysis type parameter
    private int currentStartYear; // the current start year parameter
    private int currentEndYear; // the current end year parameter
    private Map<Integer, Object> mapForest; // data map of the Forest Area dataset, where the key is a year
    private Map<Integer, Object> mapVar; // data map of the variable second dataset, where the key is a year
    private boolean validFlag = false;

    /**
     * Constructor for the analysis class, initializes instance variables, ie the chosen analysis parameters
     */
    public Analysis() {
        currentCountry = 0; // init to 0 since current country stores an index
        currentAnalysis = 0; // init to 0 since current analysis stores an index
        currentStartYear = Util.getYearsInRange(Util.MIN_YEAR, Util.MAX_YEAR)[0]; // init to the value at index 0 of the returned year array
        currentEndYear = Util.getYearsInRange(Util.MIN_YEAR,Util.MAX_YEAR)[0];
    }

    /**
     * This method handles the calculation or recalculation of the analysis based on passed parameters
     * @param currentCountry index of the selected country
     * @param currentAnalysis index of the selected analysis type
     * @param currentStartYear the selected start year
     * @param currentEndYear the selected end year
     */
    public void recalculate(int currentCountry, int currentAnalysis, int currentStartYear, int currentEndYear) {
        this.currentCountry = currentCountry; // store parameters in corresponding instance variables
        this.currentAnalysis = currentAnalysis;
        this.currentStartYear = currentStartYear;
        this.currentEndYear = currentEndYear;

        DatabaseAPIHandler apiHandler = new DatabaseAPIHandler(); // initialize a DatabaseApiHandler
        String responseForest = apiHandler.requestData(currentCountry, 0, currentStartYear, currentEndYear); // request data for the forest area dataset
        mapForest = apiHandler.formatData(responseForest); // format the api response into a map
        if (mapVar != null) mapVar.clear(); // clear the variable map if it has been initialized

        if (currentAnalysis != 0) { // if the chosen analysis type is not the first type, then we need to get a second variable dataset
            String responseVar = apiHandler.requestData(currentCountry, currentAnalysis, currentStartYear, currentEndYear); // request data for the variable dataset of the chosen analysis type
            mapVar = apiHandler.formatData(responseVar); // format api response into a map

        }
    }

    /**
     * This method checks whether the current analysis object is valid
     * @return true if the analysis is valid and false if its not
     */
    public boolean validateAnalysis() {
        MainUI.removeError(); // remove any errors being displayed
        if (currentEndYear < currentStartYear) { // check if start year is less than end year
            MainUI.displayError("End year must be greater than or equal to start year"); // display error if its not
            validFlag = false;
            return false;
        }
        for (int i = currentStartYear; i <= currentEndYear; i++) { // loop through the range of years
            if((mapForest == null || mapForest.get(i) == null || mapForest.get(i).equals("null"))||(currentAnalysis!=0 && (mapVar==null || mapVar.get(i) == null || mapVar.get(i).equals("null")))) {
                MainUI.displayError("There is missing data for the chosen analysis type and country in the date range"); // display error if any data collected is null
                validFlag = false;
                return false;
            }
        }
        validFlag = true;
        return true;
    }

    /**
     * This method gets the current analysis type
     * @return index of the current analysis type
     */
    public int getCurrentAnalysis() {
        return currentAnalysis;
    }

    /**
     * This method gets the current start year
     * @return the current start year
     */
    public int getCurrentStartYear() {
        return currentStartYear;
    }

    /**
     * This method gets the current end year
     * @return the current end year
     */
    public int getCurrentEndYear() {
        return currentEndYear;
    }

    /**
     * This method gets the current forest area data map
     * @return the current forest data map
     */
    public Map<Integer, Object> getMapForest() {
        return mapForest;
    }

    /**
     * This method gets the current variable area data map
     * @return the current variable data map
     */
    public Map<Integer, Object> getMapVar() {
        return mapVar;
    }

    /**
     * This method gets the current country
     * @return index of the current country
     */
    public int getCurrentCountry() {
        return currentCountry;
    }

    public boolean isValid() {
        return validFlag;
    }
}
