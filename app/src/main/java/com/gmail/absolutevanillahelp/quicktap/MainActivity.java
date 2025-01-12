package com.gmail.absolutevanillahelp.quicktap;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.widget.*;

import com.gmail.absolutevanillahelp.quicktap.util.TimeConverter;

/**
 * Created by derekzhang on 8/10/15.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Spinner spinner1 = findViewById(R.id.spinner1);
        final Spinner spinner2 = findViewById(R.id.spinner2);
        final Spinner spinner3 = findViewById(R.id.spinner3);
        final Spinner spinner4 = findViewById(R.id.spinner4);

        final EditText player1EditText = findViewById(R.id.player1_editText);
        final EditText player2EditText = findViewById(R.id.player2_editText);

        ArrayAdapter<CharSequence> toFiveAdapter = ArrayAdapter.createFromResource(
                this, R.array.to_five, R.layout.dropdown_item);

        spinner1.setAdapter(toFiveAdapter);
        spinner3.setAdapter(toFiveAdapter);

        ArrayAdapter<CharSequence> toNineAdapter = ArrayAdapter.createFromResource(
                this, R.array.to_nine, R.layout.dropdown_item);

        spinner2.setAdapter(toNineAdapter);
        spinner4.setAdapter(toNineAdapter);

        spinner3.setSelection(3);

        Button goButton = findViewById(R.id.go_button);
        goButton.setOnClickListener(v -> {

            String minutes =
                    spinner1.getSelectedItem().toString() + spinner2.getSelectedItem().toString();
            String seconds =
                    spinner3.getSelectedItem().toString() + spinner4.getSelectedItem().toString();
            long millis = TimeConverter.toMillis(minutes, seconds);

            if (millis == 0) {

                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Time limit cannot be 0!")
                        .setNeutralButton("Ok", null)
                        .setCancelable(false)
                        .show();
                return;
            }
            String player1Name = player1EditText.getText().toString();
            String player2Name = player2EditText.getText().toString();
            if (player1Name.trim().isEmpty() || player2Name.trim().isEmpty()) {

                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Player name cannot be blank!")
                        .setNeutralButton("Ok", null)
                        .setCancelable(false)
                        .show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, QuickTapActivity.class);
            intent.putExtra(QuickTapActivity.TAG_PLAYER_1_NAME, player1Name);
            intent.putExtra(QuickTapActivity.TAG_PLAYER_2_NAME, player2Name);
            intent.putExtra(QuickTapActivity.TAG_TIME_LIMIT, millis);
            MainActivity.this.startActivity(intent);
        });
    }
}