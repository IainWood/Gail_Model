package iain.application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

public class GailSurveyPage2 extends AppCompatActivity {

    private final int ERROR_UNANSWERED_QUESTION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gail_survey_page2);
    }

    public void gotoPage1(View view){
        //startActivity(new Intent(GailSurveyPage2.this, GailSurveyPage1.class));
        finish();
    }

    public void gotoResultPage(View view){

        int results = collectData();

        if(results > 0){
            startActivity(new Intent(GailSurveyPage2.this, ResultPage.class));
        } else if(results == ERROR_UNANSWERED_QUESTION) {
            String errMessage = "Please fill in all of the fields";
            Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show();
        }


    }

    //TODO
    /*public void showBiopsyQuestions(){

    }*/

    /**
     * [[---    PAGE 2  ---]]
     * Fetches information from the questions, interprets it, and stores it in CommonUtilities
     * if it is valid.
     * @return positive number for success, negative int(based of type of error) on failure
     */
    public int collectData() {
        //Sets the Spinner variables by finding the id of the object in the xml file
        Spinner q05_spinner = (Spinner) findViewById(R.id.q05_spinner);
        Spinner q06_spinner = (Spinner) findViewById(R.id.q06_spinner);
        Spinner q07_spinner = (Spinner) findViewById(R.id.q07_spinner);
        Spinner q07a_spinner = (Spinner) findViewById(R.id.q07a_spinner);
        Spinner q07b_spinner = (Spinner) findViewById(R.id.q07b_spinner);
        Spinner q08_spinner = (Spinner) findViewById(R.id.q08_spinner);
        Spinner q08a_spinner = (Spinner) findViewById(R.id.q08a_spinner);

        //Collects and assigns the values of the Spinners
        String quest5 = q05_spinner.getSelectedItem().toString();
        String quest6 = q06_spinner.getSelectedItem().toString();
        String quest7 = q07_spinner.getSelectedItem().toString();
        String quest7a = q07a_spinner.getSelectedItem().toString();
        String quest7b = q07b_spinner.getSelectedItem().toString();
        String quest8 = q08_spinner.getSelectedItem().toString();
        String quest8a = q08a_spinner.getSelectedItem().toString();

        if(quest5.equals("Select") || quest6.equals("Select") || quest7.equals("Select") ||
           quest7a.equals("Select") || quest7b.equals("Select") || quest8.equals("Select") || quest8a.equals("Select")){
            return ERROR_UNANSWERED_QUESTION;
        }

        //Get numerical value of each answer
        int firstBirthAge = getFirstBirthAge(quest5);
        int firstDegreeRels = getFirstDegRelatives(quest6);
        int everHadBiopsy = getEverHadBiopsy(quest7);
        int numOfBiopsy = getNumberOfBiopsy(quest7a);
        int hyperPlasia = getHyperPlasia(quest7b);
        double rHyperPlasia = getRHyperPlasia(hyperPlasia);
        int race = getRace(quest8, quest8a);

        CommonUtilities.setFirstBirthAge(firstBirthAge);
        CommonUtilities.setFirstDegreeRels(firstDegreeRels);
        CommonUtilities.setEverHadBiopsy(everHadBiopsy);
        CommonUtilities.setNumOfBiopsy(numOfBiopsy);
        CommonUtilities.setRHyperPlasia(rHyperPlasia);
        CommonUtilities.setRace(race);

        return 1;
    }

    public int getFirstBirthAge(String str){

        switch (str) {
            case "No Births":
                return 0;
            case "< 20":
                return 0;
            case "20 to 24":
                return 1;
            case "25 to 30":
                return 2;
            case ">= 30":
                return 3;
            default:
                return -1;
        }
    }

    public int getFirstDegRelatives(String str){

        switch (str) {
            case "0":
                return 0;
            case "1":
                return 1;
            case ">1":
                return 2;
            case "Unknown":
                return 0;
            default:
                return -1;
        }
    }

    public int getEverHadBiopsy(String str){

        switch (str) {
            case "Yes":
                return 1;
            case "No":
                return 0;
            case "Unknown":
                return 0;
            default:
                return -1;
        }
    }

    public int getNumberOfBiopsy(String str){

        switch (str) {
            case "1":
                return 1;
            case ">1":
                return 2;
            default:
                return -1;
        }
    }

    public int getHyperPlasia(String str){

        switch (str) {
            case "Yes":
                return 1;
            case "No":
                return 0;
            case "Unknown":
                return 99;
            default:
                return -1;
        }
    }

    public double getRHyperPlasia(int hyperPlasia){

        switch(hyperPlasia) {
            case 1:
                return 1.82;
            case 0:
                return 0.93;
            default:
                return 1.0;
        }
    }

    public int getRace(String str, String subRace){

        switch (str) {
            case "White":
                return 1;
            case "African American":
                return 2;
            case "Hispanic":
                return 3;
            case "Asian-American":
                getSubRace(subRace);
                break;
            case "American Indian or Alaskan Native":
                getSubRace(subRace);
                break;
            default:
                return -1;
        }
        return -1;
    }

    public int getSubRace(String str){

        switch (str) {
            case "Chinese":
                return 7;
            case "Japanese":
                return 8;
            case "Filipino":
                return 9;
            case "Hawaiian":
                return 10;
            case "Other Pacific Islander":
                return 11;
            case "Other Asian American":
                return 12;
            default:
                return -1;
        }
    }
}
