package com.kevin_yang.fuzz_quiz;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class FuzzQuizActivity extends Activity {

        private View mDecorView;
        protected Callbacks mCallBacks;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // no title
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            // full screen
            mDecorView = getWindow().getDecorView();
            hideSystemUI();
            UiChangeListener();
            setWindowFocusChangeListener();

            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_fuzz_quiz);
            FragmentManager fm = getFragmentManager();
            FuzzQuizFragment fuzzQuizFragment = new FuzzQuizFragment();
            fm.beginTransaction().add(R.id.fragment_container, fuzzQuizFragment).commit();

            // Query filter system
            Button allBtn = (Button) findViewById(R.id.button_all);
            allBtn.setOnClickListener(new FilterButtonClickListener());
            Button textBtn = (Button) findViewById(R.id.button_text);
            textBtn.setOnClickListener(new FilterButtonClickListener());
            Button imageBtn = (Button) findViewById(R.id.button_image);
            imageBtn.setOnClickListener(new FilterButtonClickListener());

            mCallBacks = (Callbacks) fuzzQuizFragment;
            // Btn click listener to activate filter on Fragment
            View.OnClickListener filterBtnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int filterMode = 0;
                    switch (v.getId()) {
                        case R.id.button_all:
                            filterMode = FuzzQuizFragment.ALL;
                            break;
                        case R.id.button_text:
                            filterMode = FuzzQuizFragment.TEXT;
                            break;
                        case R.id.button_image:
                            filterMode = FuzzQuizFragment.IMAGE;
                            break;
                    }

                    mCallBacks.onFilterActivated(filterMode);
                }
            };

        }

        //** Establishes callback with Fragment to add content filtering
        public interface Callbacks {
            void onFilterActivated(int filaterMode);
        }

        // hide ui whenever reloaded
        @Override
        public void onResume() {
            super.onResume();
            hideSystemUI();
        }

        // This snippet hides the system bars.
        private void hideSystemUI() {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        public void UiChangeListener() {
            mDecorView.setOnSystemUiVisibilityChangeListener
                    (new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                hideSystemUI();
                            }
                        }
                    });
        }

        public void setWindowFocusChangeListener() {
            mDecorView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    hideSystemUI();
                }
            });
        }

        private class FilterButtonClickListener implements View.OnClickListener{

            @Override
            public void onClick(View v) {

                int filterMode = 0;
                switch (v.getId()) {
                    case R.id.button_all:
                        filterMode = FuzzQuizFragment.ALL;
                        break;
                    case R.id.button_text:
                        filterMode = FuzzQuizFragment.TEXT;
                        break;
                    case R.id.button_image:
                        filterMode = FuzzQuizFragment.IMAGE;
                        break;
                }

                mCallBacks.onFilterActivated(filterMode);

                // Change button color
                int pressedBtnId = v.getId();
                LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.button_container);
                for (int i = 0; i < buttonContainer.getChildCount(); i++) {
                    Button btn = (Button) buttonContainer.getChildAt(i);
                    int id = btn.getId();

                    if (pressedBtnId == id) {
                        btn.setBackgroundColor(getResources().getColor(R.color.buttonBgDown));
                        btn.setTextColor(getResources().getColor(R.color.buttonTxtDown));
                    } else {
                        btn.setBackgroundColor(getResources().getColor(R.color.buttonBgUp));
                        btn.setTextColor(getResources().getColor(R.color.buttonTxtUp));
                    }
                }
            }
        }

}
