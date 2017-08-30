package iain.application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.text.DecimalFormat;

public class ResultPage extends AppCompatActivity {

    private int age = CommonUtilities.getAge();
    private int projectionAge = CommonUtilities.getProjectionAge();
    private int ageIndicator = CommonUtilities.getAgeIndicator();
    private int firstMensAge = CommonUtilities.getFirstMenPeriodAge();
    private int firstBirthAge = CommonUtilities.getFirstBirthAge();
    private int firstDegreeRels = CommonUtilities.getFirstDegreeRels();
    private int everHadBiopsy = CommonUtilities.getEverHadBiopsy();
    private int numOfBiopsy = CommonUtilities.getNumOfBiopsy();
    private double rHyperPlasia = CommonUtilities.getRHyperPlasia();
    private int race = CommonUtilities.getRace();

    private static DecimalFormat decimalFormat = new DecimalFormat(".####");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        printData();

        TextView textView = (TextView) findViewById(R.id.results_text_view);
        textView.setText(fetchResults());
    }

    private void printData(){
        System.out.print(   "Age:\t" + age + "\n" +
                            "Projection Age:\t" + projectionAge + "\n" +
                            "First Men Period Age:\t" + firstMensAge + "\n" +
                            "First Birth Age:\t" + firstBirthAge + "\n" +
                            "First Degree Rels:\t" + firstDegreeRels + "\n" +
                            "Ever had Biopsy:\t" + everHadBiopsy + "\n" +
                            "Num of Biopsy:\t" + numOfBiopsy + "\n" +
                            "Race:\t" + race + "\n");
    }

    public String fetchResults(){

        RiskCalculator calc = new RiskCalculator();

        //TODO: make this automatically happen through visibility of questions 7a and 7b
        //Necessary to make calculation work
        if(everHadBiopsy == 0){
            numOfBiopsy = 0;
        }

        //1 for absolute, 2 for average
        double absoluteRisk = calc.CalculateRisk(1, age, projectionAge, ageIndicator,
                numOfBiopsy, firstMensAge, firstBirthAge, firstDegreeRels,
                rHyperPlasia, race);

        double averageRisk = calc.CalculateRisk(2, age, projectionAge, ageIndicator,
                numOfBiopsy, firstMensAge, firstBirthAge, firstDegreeRels,
                rHyperPlasia, race);

        //Converts the results to a percentage formatted to 4 decimal places
        String absolute = decimalFormat.format(100 * absoluteRisk).concat("%");
        String average = decimalFormat.format(100 * averageRisk).concat("%");

        return "Absolute Risk: " + absolute +
               "\nAverage Risk: " + average;
    }

}