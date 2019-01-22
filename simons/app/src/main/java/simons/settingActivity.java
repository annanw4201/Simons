package simons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Observer;
import java.util.Observable;

import simons.R;

/**
 * Created by wangtom on 2017-11-22.
 */

public class settingActivity extends AppCompatActivity implements Observer {
    private TextView numberButtonText;
    private SeekBar numberButtonSeekbar;
    private TextView difficultyText;
    private Spinner difficultySpinner;
    private Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settingview);

        // get model and add observer
        model = Model.getInstance();
        model.addObserver(this);
        System.out.println("----Setting OnCreate");

        // binding widgets
        numberButtonText = (TextView) findViewById(R.id.numberButtonTextView);
        numberButtonSeekbar = (SeekBar) findViewById(R.id.numberButtonSeekBar);
        difficultyText = (TextView) findViewById(R.id.difficultyTextView);
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);

        // set number of buttons text
        numberButtonText.setText("Choose Number of Buttons: " + model.getButtons());

        // set and register events for seek bar
        numberButtonSeekbar.setMax(model.getMaxButtonNumber() - 1);
        numberButtonSeekbar.setProgress(model.getButtons() - 1);
        numberButtonSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //seekBar.setProgress(model.getButtons() - 1);
                model.setButtons(progress+1);
                System.out.println("setProgress: "+progress);
                System.out.println("setModel buttons: "+model.getButtons());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // set difficulty spinner
        difficultySpinner.setSelection(model.getDifficultyAsNum());
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model.setDifficultyByNum(position);
                System.out.println("set difficulty: "+model.getDifficulty());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        System.out.println("----Setting menu create");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appmenu, menu);

        // get the menu item to add a listener
        MenuItem item = menu.findItem(R.id.ToWelcome);

        MenuItem item1 = menu.findItem(R.id.toSetting_Game);
        item1.setTitle("Back to Game");
        // get the context (must be final to reference inside anonymous object)
        final Context context = this;

        // create the menu item controller to change views
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // create intent to request the view2 activity to be shown
                Intent intent = new Intent(context, WelcomeActivity.class);
                // start the activity
                startActivity(intent);
                finish();
                // we're done with this activity ...
                return false;
            }
        });

        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(context, gameViewActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        System.out.println("----Setting OnDestroy");
        model.deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("----Setting Update");
        // update number of buttons text
        numberButtonText.setText("Choose Number of Buttons: " + model.getButtons());

        // update seek bar
        numberButtonSeekbar.setProgress(model.getButtons() - 1);

        // update difficulty spinner
        difficultySpinner.setSelection(model.getDifficultyAsNum());
    }

}
