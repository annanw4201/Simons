package simons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import simons.R;

/**
 * Created by wangtom on 2017-11-22.
 */

public class gameViewActivity extends AppCompatActivity implements Observer {
    private Model model;
    private TextView scoreText, messageText;
    private TableLayout buttonTableView;
    private ArrayList<Button> buttonArray;
    private ToggleButton playButton;
    private Handler handler;
    private Runnable runnable;
    private PopupWindow popupWindow;

    void drawButtons() {
        int rows = model.getButtons() / 3 + 1;
        for (int i = 0; i < rows; ++i) {
            TableRow row = new TableRow(this);
            int maxButtonsRow = 3;
            int totalButtons = model.getButtons();
            for (int j = 0; j < maxButtonsRow && j + 3 * i < totalButtons; ++j) {
                final Button theButton = new Button(this);
                theButton.setId(j + 3 * i);
                theButton.setBackgroundResource(R.drawable.round_button);
                theButton.setEnabled(false);
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.anim_alph);
                theButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Verify: " + v.getId());
                        v.startAnimation(myAnim);
                        model.verifyButton(v.getId());
                    }
                });
                int width = 340;
                TableRow.LayoutParams params = new TableRow.LayoutParams(width - 3*12, width - 3*12);
                params.setMargins(12, 12, 12, 12);
                theButton.setLayoutParams(params);
                row.addView(theButton);
                buttonArray.add(theButton);
            }
            row.setGravity(Gravity.CENTER);
            buttonTableView.addView(row);
        }

        DisplayMetrics m = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(m);
        System.out.println("screen height: " + m.heightPixels / m.density);
        System.out.println("screen width: " + m.widthPixels / m.density);
        float screenHeight = m.heightPixels / m.density;
        float screenWidth = m.widthPixels / m.density;
        buttonTableView.setTranslationY(screenHeight / 2 - buttonTableView.getHeight() / 2);
    }

    void setButtonClickable(boolean val) {
        for (Button b: buttonArray) {
            b.setEnabled(val);
        }
    }

    void setUpPopUpWindow() {
        // Initialize a new instance of popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        final View popupView = inflater.inflate(R.layout.popup_view, null);
        popupWindow = new PopupWindow(
                popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        TextView popUpMssg = popupWindow.getContentView().findViewById(R.id.popupViewText);
        if (model.getState() == Model.State.START || model.getState() == Model.State.COMPUTER) {
            if (popUpMssg != null) popUpMssg.setText("Computer plays, click to continue");
        }
        else if (model.getState() == Model.State.HUMAN) {
            if (popUpMssg != null) popUpMssg.setText("It's your turn, click to continue");
        }
        else if (model.getState() == Model.State.WIN) {
            if (popUpMssg != null) popUpMssg.setText("You " + model.getState() +
                    ", your score is " + model.getScore() + ", click to continue");
        }
        else if (model.getState() == Model.State.LOSE) {
            if (popUpMssg != null) popUpMssg.setText("You " + model.getState() + ", click to restart");
        }


        // show the popup window
        final ConstraintLayout gameView = findViewById(R.id.gamView);
        popupWindow.showAtLocation(gameView, Gravity.CENTER, 0, 0);

        // handle events
        final Animation alphAnim = AnimationUtils.loadAnimation(this, R.anim.anim_alph);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                model.newRound();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (model.getState() == Model.State.COMPUTER) {
                            setButtonClickable(false);
                            Integer button = model.nextButton();
                            Button animateButton = buttonArray.get(button);
                            animateButton.startAnimation(alphAnim);
                            System.out.println("computer handler delay: "+model.getDifficultyAsTime());
                            handler.postDelayed(this, model.getDifficultyAsTime());
                        }
                        else if (model.getState() == Model.State.HUMAN) {
                            setButtonClickable(true);
                            System.out.println("human handler delay: "+model.getDifficultyAsTime());
                            handler.postDelayed(this, model.getDifficultyAsTime());
                        }
                        else if (model.getState() == Model.State.WIN || model.getState() == Model.State.LOSE) {
                            setButtonClickable(false);
                            System.out.println("Win, lose clean handler delay: "+model.getDifficultyAsTime());
                            handler.removeCallbacks(runnable);
                            setUpPopUpWindow();
                        }
                    }
                };
                handler.postDelayed(runnable, model.getDifficultyAsTime());
                return false;
            }
        });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("----gameView Oncreate");
        this.setContentView(R.layout.activity_gameview);

        // set model and add observer
        model = Model.getInstance();
        model.addObserver(this);

        // binding widgets
        scoreText = findViewById(R.id.scoreText);
        messageText = findViewById(R.id.messageText);
        buttonTableView = findViewById(R.id.buttonsTableView);
        playButton = findViewById(R.id.playButton);
        playButton.setBackgroundResource(R.drawable.button_3d);

        // draw the buttons
        buttonArray = new ArrayList<Button>();
        drawButtons();

        handler = new Handler();

        final Animation alphAnim = AnimationUtils.loadAnimation(this, R.anim.anim_alph);
        playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("Computer to play");
                    setUpPopUpWindow();
                }
                if (!isChecked) {
                    System.out.println("Paused");
                    popupWindow.dismiss();
                    handler.removeCallbacks(runnable);
                }
            }
        });

        final Animation milkshakeAnim = AnimationUtils.loadAnimation(this, R.anim.milkshake);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(milkshakeAnim);
            }
        });

        model.setChangedAndNotify();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        System.out.println("----gameView menu Oncreate");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appmenu, menu);

        // get the menu item to add a listener
        MenuItem item = menu.findItem(R.id.ToWelcome);

        MenuItem item1 = menu.findItem(R.id.toSetting_Game);
        item1.setTitle("Setting");

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
                Intent intent = new Intent(context, settingActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        model.deleteObserver(this);
        handler.removeCallbacks(runnable);
        if (popupWindow != null) popupWindow.dismiss();
        super.onDestroy();
        System.out.println("----gameView OnDestroy");
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("----gameView Update");
        scoreText.setText("Score: " + model.getScore());
        messageText.setText(model.getStateAsString());

        //drawButtons();
    }
}
