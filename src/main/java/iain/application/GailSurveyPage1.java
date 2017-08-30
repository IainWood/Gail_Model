package iain.application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class GailSurveyPage1 extends AppCompatActivity {

    private boolean status;
    private final int ERROR_HAD_BC_BEFORE = -1;
    private final int ERROR_HAS_MUTATION = -2;
    private final int ERROR_AGE_FORMAT = -3;
    private final int ERROR_UNANSWERED_QUESTION = -4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gail_survey_page_1);
    }

    public void gotoMainMenu(View view){
        //startActivity(new Intent(GailSurveyPage1.this, MainActivity.class));
        finish();
    }

    public void gotoPage2(View view){

        int results = collectData();

        //Error messages returned to user in form of Toasts
        if(results > 0) {
            startActivity(new Intent(GailSurveyPage1.this, GailSurveyPage2.class));
        } else if(results == ERROR_HAD_BC_BEFORE) {
            String errMessage = "This tool cannot calculate breast cancer risk accurately for women " +
                    "with a medical history of any breast cancer or of DCIS or LCIS.   See \"About the " +
                    "Tool\" section for more information.";
            Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show();
        } else if(results == ERROR_HAS_MUTATION) {
            String errMessage = "Other tools may be more appropriate for women with known mutations " +
                    "in either the BRCA1 or BRCA2 gene, or other hereditary syndromes associated with " +
                    "higher risk of breast cancer. See \"About the Tool\" section for more information.";
            Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show();
        } else if(results == ERROR_AGE_FORMAT){
            String errMessage = "Incorrect age format, please enter a number";
            Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show();
        } else if(results == ERROR_UNANSWERED_QUESTION){
            String errMessage = "Please fill in all of the fields";
            Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * [[---    PAGE 1  ---]]
     * Fetches information from the questions, interprets it, and stores it in CommonUtilities
     * if it is valid.
     * @return positive number for success, negative int(based of type of error) on failure
     */
    public int collectData(){

        //Sets the Spinner variables by finding the id of the object in the xml file
        Switch q01_switch = (Switch) findViewById(R.id.switch1);
        Spinner q02_spinner = (Spinner) findViewById(R.id.q02_spinner);
        EditText q03_editText = (EditText) findViewById(R.id.q03_editText);
        Spinner q04_spinner = (Spinner) findViewById(R.id.q04_spinner);

        //Detects the state of the switch button (question 1)
        //It should not be checked (doesn't work for this model)
        q01_switch.setChecked(false);
        q01_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                status = isChecked;
            }
        });

        //Collects and assigns the values of the Spinners
        String quest2 = q02_spinner.getSelectedItem().toString();
        String quest3 = q03_editText.getText().toString();
        String quest4 = q04_spinner.getSelectedItem().toString();

        //Checks if any of the questions have been
        if(quest2.equals("Select") || quest3.equals("Select") || quest4.equals("Select")){
            return ERROR_UNANSWERED_QUESTION;
        }

        //Returns errors that would not allow the user to continue
        if(status) {
            return ERROR_HAD_BC_BEFORE;
        }

        if(quest2.equals("Yes")){
            return ERROR_HAS_MUTATION;
        }

        if(getAge(quest3) == -1){
            return ERROR_AGE_FORMAT;
        }

        //Gets the proper age values and sets them in Common Utilities
        int age = getAge(quest3);
        int projectionAge = getProjectionAge(age);
        int ageIndicator = getAgeIndicator(age);
        int firstMenPeriodAge = getFirstMenPeriodAge(quest4);

        CommonUtilities.setAge(age);
        CommonUtilities.setProjectionAge(projectionAge);
        CommonUtilities.setAgeIndicator(ageIndicator);
        CommonUtilities.setFirstMenPeriodAge(firstMenPeriodAge);

        //any positive number is a success, errors are negative
        return 1;
    }

    public int getAge(String str){

        int age;
        try{
            age = Integer.parseInt(str);
        } catch (Exception e){
            //Will result in toast being shown and not advancing to next page
            return -1;
        }

        //Test only works for women at or over 35, if age is under 35 set it to 34
        if(age < 35 && age > 0){
            return 34;
        } else if(age >= 35){
            return age;
        } else {
            return -1;
        }
    }

    //projection age is the current age + 5
    public int getProjectionAge(int currentAge){
        return currentAge + 5;
    }

    public int getAgeIndicator(int currentAge){

        if(currentAge >= 50){
            return 1;
        } else {
            return 0;
        }
    }

    public int getFirstMenPeriodAge(String str) {

        switch (str) {
            case "7 to 11":
                return 2;
            case "12 to 13":
                return 1;
            case ">= 14":
                return 0;
            case "Unknown":
                return 0;
            default:
                return -1;
        }
    }

}
