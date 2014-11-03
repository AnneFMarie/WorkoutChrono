package com.annef.workoutchrono.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.annef.workoutchrono.R;

public class PickTypeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timepicker_layout);


        final int time = getIntent().getExtras().getInt(
                "com.annef.workoutchrono.time");

        final NumberPicker mindizaines = (NumberPicker) findViewById(R.id.minPickerDizaine);
        final NumberPicker minunites = (NumberPicker) findViewById(R.id.minPickerUnites);
        final NumberPicker secdizaines = (NumberPicker) findViewById(R.id.secPickerDizaine);
        final NumberPicker secunites = (NumberPicker) findViewById(R.id.secPickerUnites);

        // Bouton Annuler
        Button dismiss = (Button) findViewById(R.id.timepicker_buttonCancel);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent result = new Intent();
                result.putExtra("com.annef.workoutchrono.time", time);
                setResult(RESULT_OK, result);
                finish();

            }
        });
        // Bouton Sauver
        Button save = (Button) findViewById(R.id.timepicker_buttonSave);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int newtime = (mindizaines.getValue()*10 + minunites.getValue())*60 + (secdizaines.getValue()*10 + secunites.getValue());

                Intent result = new Intent();
                setResult(RESULT_OK, result);
                result.putExtra("com.annef.workoutchrono.time", newtime);
                finish();


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pick_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
