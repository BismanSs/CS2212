import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class represents the Main UI of the application
 * @author Paul Scoropan, Gouri Sikha, Bizman Sawhney, Owen Tjhie
 */
public class MainUI {

    private static String windowTitle = "Country Statistics";   // title of JFrame window
    private static int windowWidth = 1000;
    private static int windowHeight = 750;

    private static JComboBox countryCombo;                      // JComboBoxes for the different drop downs in the Main UI
    private static JComboBox startYearCombo;
    private static JComboBox endYearCombo;
    private static JComboBox viewsCombo;
    private static JComboBox analysisCombo;

    private static JLabel errorLabel;   // JLabel for the error message under the recalculate button
    private static JPanel chartsPanel;  // JPanel for holding the JFreeChart objects

    private static Analysis analysis;   // the analysis object

    private static GridBagConstraints c; // constraint variable for GridBagLayouts

    private static ArrayList<GraphViewer> viewers = new ArrayList<>(); // An arraylist of the graphs being displayed

    private static JFrame mainFrame; // the JFrame window

    private static GridLayout gl = new GridLayout(2,3);

    /**
     * The main method of the program
     * @param args command line args
     */
    public static void main(String[] args) {
        LoginUI loginUI = new LoginUI(); // initialize a login window on start-up
        while(!loginUI.isLogin()) {
            try {
                Thread.sleep(500); // wait and sleep in intervals of 500ms until user has logged in
            } catch (InterruptedException e){}
        }
        loginUI.setVisible(false);
        loginUI.dispose(); // after the user is logged in, dispose of the loginUI


        analysis = new Analysis(); // initialize the analysis object
        mainFrame = new JFrame(windowTitle); // initialize the window
        mainFrame.setPreferredSize(new Dimension(windowWidth, windowHeight));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // terminate program on window close
        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null); // place in middle of the screen
        mainFrame.setLayout(new BorderLayout()); // set layout to a border layout

        JPanel topPanel = new JPanel(); // init JPanel for top row of components
        JPanel bottomPanel = new JPanel(); // init JPanel for the bottom row of components
        topPanel.setBounds(0,0,1000,50); // set locations and sizes
        bottomPanel.setBounds(0,0,1000,80);
        topPanel.setLayout(new GridBagLayout()); // use a grab bag layout format
        bottomPanel.setLayout(new GridBagLayout());
        c = new GridBagConstraints(); // init the constraints variable

        JLabel chooseCountry = new JLabel("Choose a country:    ");

        countryCombo = new JComboBox(Util.COUNTRIES); // load country array into country combo box
        countryCombo.setSelectedIndex(0); // set to display first index by default
        countryCombo.setBounds(0,0,50, 30);

        JLabel startLabel = new JLabel("    From:    ");

        startYearCombo = new JComboBox(Util.getYearsInRange(Util.MIN_YEAR,Util.MAX_YEAR)); // call getYears to get an array of years
        startYearCombo.setSelectedIndex(0);
        startYearCombo.setBounds(0,0,50, 30);

        JLabel endLabel = new JLabel("    To:    ");

        endYearCombo = new JComboBox(Util.getYearsInRange(Util.MIN_YEAR,Util.MAX_YEAR));
        endYearCombo.setSelectedIndex(0);
        endYearCombo.setBounds(0,0,50, 30);

        topPanel.add(chooseCountry); // add components to top panel
        topPanel.add(countryCombo);
        topPanel.add(startLabel);
        topPanel.add(startYearCombo);
        topPanel.add(endLabel);
        topPanel.add(endYearCombo);

        JLabel viewsLabel = new JLabel("Available views:  ");

        viewsCombo = new JComboBox(Util.VIEW_TYPES); // put view types (types of chart) into the views combo box
        viewsCombo.setSelectedIndex(0);
        viewsCombo.setBounds(0,0,50, 30);

        JButton addView = new JButton("+"); // add the add and remove viewer buttons
        JButton removeView = new JButton("-");

        JLabel analysisLabel = new JLabel("         Choose analysis type:  ");

        analysisCombo = new JComboBox(Util.ANALYSIS_TYPES); // put the analysis types into the anaylsis combo box
        analysisCombo.setSelectedIndex(0);
        analysisCombo.setBounds(0,0,50,30);

        JButton recalculateButton = new JButton("Recalculate"); // create the recalculate button

        c.gridy = 0; // set grid location to 0,0 with the constraint variable
        c.gridx = 0;
        bottomPanel.add(viewsLabel); // adds viewsLabel relative to c

        c.gridx = 1;
        bottomPanel.add(viewsCombo);

        c.gridx = 2;
        bottomPanel.add(addView);

        c.gridx = 3;
        bottomPanel.add(removeView);

        c.gridx = 4;
        bottomPanel.add(analysisLabel);

        c.gridx = 5;
        bottomPanel.add(analysisCombo);

        c.gridx = 6;
        bottomPanel.add(recalculateButton);

        errorLabel = new JLabel(""); // init error label to empty string

        c.anchor = GridBagConstraints.PAGE_END; // add an anchor to the bottom of the page
        c.gridy = 1;
        c.ipady = 20;
        c.gridx = 5;
        bottomPanel.add(errorLabel, c); // add error label relative to c

        mainFrame.add(topPanel, BorderLayout.NORTH); // add the top and bottom panels
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);

        chartsPanel = new JPanel(); // init the charts panel
        chartsPanel.setLayout(gl);
        mainFrame.add(chartsPanel, BorderLayout.CENTER); // add the charts panel starting on the left

        mainFrame.pack(); // pack the main frame
//        mainFrame.getRootPane().setDefaultButton(recalculateButton); // when you press enter the recalculate button is pressed

        // Add event listeners
        recalculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) { // called on recalculate button pressed
                removeError(); // remove whatever is displayed in the error label
                int prevCountry = analysis.getCurrentCountry(); // remember the previous year
                analysis.recalculate(
                        countryCombo.getSelectedIndex(),
                        analysisCombo.getSelectedIndex(),
                        Util.getYearsInRange(Util.MIN_YEAR,Util.MAX_YEAR)[startYearCombo.getSelectedIndex()],
                        Util.getYearsInRange(Util.MIN_YEAR,Util.MAX_YEAR)[endYearCombo.getSelectedIndex()]
                ); // call recalculate with the selected indexes of the combo boxes
                if (prevCountry != analysis.getCurrentCountry()) {
                    removeAllViewers(); // remove all the viewers being displayed if the chosen country changed
                }
                if (analysis.validateAnalysis()) {
                    updateViewers(); // otherwise update the viewers
                }
            }
        });

        addView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) { // called on add viewer button pressed
                removeError(); // remove any errors being displayed
                int viewType = viewsCombo.getSelectedIndex(); // get selected view type
                boolean flag = false; // set a flag to false
                for (int i = 0; i < viewers.size(); i++) { // loop through viewer array list
                    if (viewers.get(i).getViewType() == viewType) {
                        displayError("This viewer has already been added"); // throw an error if the user tries to add a view already being displayed
                        flag = true;
                    }
                }
                if (!flag && analysis.isValid()) addViewer(new GraphViewer(viewType, Util.ANALYSIS_TYPES[getAnalysisType()])); // if the flag was never set true then add the viewer
                else if (!analysis.isValid()){
                    displayError("Invalid analysis, have you run the analysis at least once before trying to display?");
                }
            }
        });

        removeView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) { // called on remove viewer button pressed
                removeError();
                int viewType = viewsCombo.getSelectedIndex();
                boolean flag = false;
                for (int i = 0; i < viewers.size(); i++) {
                    if (viewers.get(i).getViewType() == viewType) {
                        viewers.remove(viewers.get(i)); // remove if the selected view is in the array

                        updateViewers();
                        flag = true;
                    }
                }
                if (!flag) displayError("Viewer already removed"); // if no view was removed display error
            }
        });

    }

    /**
     * This method gets the start year from the analysis object
     * @return the current start year from analysis
     */
    public static int getStartYear() {
        return analysis.getCurrentStartYear(); // call getCurrentStartYear() and return it
    }

    /**
     * This method gets the end year from the analysis object
     * @return the current end year from analysis
     */
    public static int getEndYear() {
        return analysis.getCurrentEndYear(); // call getCurrentEndYear() and return it
    }

    /**
     * This method gets the analysis type from the analysis object
     * @return the current analysis type from analysis
     */
    public static int getAnalysisType() {
        return analysis.getCurrentAnalysis(); // call getCurrentAnalysis() and return it
    }

    /**
     * This method displays an error in selections to the user through the errorLabel
     * @param error string message defining the error
     */
    public static void displayError(String error) {
        errorLabel.setForeground(Color.red);
        errorLabel.setText(error); // set the text of errorLabel to error
    }

    /**
     * This method removes any error being displayed in errorLabel
     */
    public static void removeError() {
        errorLabel.setText(""); // set the text to empty
    }

    /**
     * This method updates the viewers in the viewer array list
     */
    private static void updateViewers() {
        removeError();
        if(!analysis.isValid()) {
            displayError("Invalid data in analysis, graphs can not be updated");
            return;
        }
      try{
          if(viewers.size() == 0) {
              removeAllViewers();
          }
          chartsPanel.removeAll();
          mainFrame.pack();
          chartsPanel.revalidate();
          chartsPanel.repaint();
          for (int i = 0; i < viewers.size(); i++) { // loop through the arraylist
              viewers.get(i).display(); // update what the viewer displays
              chartsPanel.add(viewers.get(i)); // re-add the viewer to the chartsPanel
          }
      	}
      catch(Exception e){
        	displayError("Cannot update viewer before calculated at first time");
      }
      mainFrame.pack();
      chartsPanel.revalidate();
      chartsPanel.repaint();
    }

    /**
     * This method adds a viewer to the arraylist
     * @param graphViewer the GraphViewer to add
     */
    private static void addViewer(GraphViewer graphViewer) {
        viewers.add(graphViewer); // add it to the list
        updateViewers(); // update the viewers
    }

    /**
     * This method removes all the viewers in the arraylist
     */
    private static void removeAllViewers() {
        chartsPanel.removeAll();
        viewers.clear(); // clear the arraylist
        mainFrame.pack();
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    /**
     * This method returns the map of data for the Forest area (% of land area) dataset from the analysis object
     * @return a map of type <Integer, Object> storing the data for Forest area
     */
    public static Map<Integer, Object> getMapForest() {
        return analysis.getMapForest(); // call getMapForest() and return result
    }

    /**
     * This method returns the map of data for the second (that is the variable) dataset from the analysis object
     * @return a map of type <Integer, Object> storing the data for the second dataset
     */
    public static Map<Integer, Object> getMapVar() {
        return analysis.getMapVar(); // call getMapVar() and return result
    }

    /**
     * This method gets the current country from the analysis object
     * @return the index of the country (based on the country and country code arrays in Util)
     */
    public static int getCountry() {
        return analysis.getCurrentCountry(); // call getCurrentCountry() and return the result
    }
}
