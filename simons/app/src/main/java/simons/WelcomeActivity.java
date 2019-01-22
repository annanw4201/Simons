package simons;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.Observable;
import java.util.Observer;

import simons.R;

public class WelcomeActivity extends AppCompatActivity implements Observer {
    private Button startGameButton, settingButton;
    private Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("----WelcomeActivity: onCreate");
        setContentView(R.layout.activity_welcome);

        model = Model.getInstance();
        model.addObserver(this);

        // binding widgets
        startGameButton = (Button)findViewById(R.id.startGame);
        settingButton = findViewById(R.id.setting);
        startGameButton.setBackgroundResource(R.drawable.button_3d);
        settingButton.setBackgroundResource(R.drawable.button_3d);

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.milkshake);
        final Context context = this;
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the context (must be final to reference inside anonymous object)
                System.out.println("----WelcomeActivity: goto gameView");
                v.startAnimation(myAnim);
                Intent intent = new Intent(context, gameViewActivity.class);
                // start the activity
                startActivity(intent);
                finish();
                // we're done with this activity ...
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----WelcomeActivity: goto settingView");
                v.startAnimation(myAnim);
                Intent intent = new Intent(context, settingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // add observer to application model
        model.setChangedAndNotify();
    }

    @Override
    protected void onDestroy() {
        model.deleteObserver(this);
        super.onDestroy();
        System.out.println("----WelcomeActivity: OnDestroy");
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("----WelcomeActivity: update");
    }
}
