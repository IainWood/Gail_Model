package iain.application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoGailSurveyPage1(View view){
        startActivity(new Intent(MainActivity.this, GailSurveyPage1.class));
    }

    public void gotoGailInfoPage(View view){
        startActivity(new Intent(MainActivity.this, GailInfoPage.class));
    }
}
