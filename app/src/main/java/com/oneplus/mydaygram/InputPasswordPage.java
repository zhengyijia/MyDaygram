package com.oneplus.mydaygram;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.oneplus.util.MyApplication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InputPasswordPage extends AppCompatActivity {

    private EditText passwordInput;
    private int charNum;
    private List<View> passwordCircles = new ArrayList<>();
    private LinearLayout wrongPasswordAlert;
    private LinearLayout newPasswordAlert;
    private LinearLayout newPasswordAgainAlert;
    private LinearLayout oldPasswordAlert;
    private String tempPassword;
    private boolean inputOld = false;
    private boolean inputFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_input_password_page);

        final MyApplication application = (MyApplication) this.getApplicationContext();
        wrongPasswordAlert = (LinearLayout) findViewById(R.id.wrong_password_alert);
        newPasswordAlert = (LinearLayout) findViewById(R.id.new_password_alert);
        newPasswordAgainAlert = (LinearLayout) findViewById(R.id.new_password_again_alert);
        oldPasswordAlert = (LinearLayout) findViewById(R.id.old_password_alert);
        wrongPasswordAlert.setVisibility(View.GONE);
        if (application.getLockSetting())
            newPasswordAlert.setVisibility(View.GONE);
        newPasswordAgainAlert.setVisibility(View.GONE);
        oldPasswordAlert.setVisibility(View.GONE);


        passwordCircles.add(findViewById(R.id.password_circle1));
        passwordCircles.add(findViewById(R.id.password_circle2));
        passwordCircles.add(findViewById(R.id.password_circle3));
        passwordCircles.add(findViewById(R.id.password_circle4));

        passwordInput = (EditText) findViewById(R.id.password_input);
        if (application.getLockSetting()) {
            if (application.getIsLocked()) {
                passwordInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void afterTextChanged(Editable s) {
                        charNum = passwordInput.getText().toString().length();
                        if (0 <= charNum && charNum <= 3) {
                            int num = 0;
                            for (View v : passwordCircles) {
                                num++;
                                if (num <= charNum)
                                    v.setBackground(ContextCompat.getDrawable(InputPasswordPage.this, R.drawable.password_input_circle));
                                else
                                    v.setBackground(ContextCompat.getDrawable(InputPasswordPage.this, R.drawable.password_uninput_circle));
                            }
                        }
                        if (charNum > 3) {
                            if (application.getPassword().equals(passwordInput.getText().toString())) {
                                application.setIsLocked(false);
                                finish();
                            } else {
                                for (View v : passwordCircles) {
                                    v.setBackground(ContextCompat.getDrawable(InputPasswordPage.this, R.drawable.circle_red));
                                }
                                wrongPasswordAlert.setVisibility(View.VISIBLE);
                                MyTimer timer = new MyTimer();
                                timer.start();//启动线程
                            }

                        }
                    }
                });
            } else {
                oldPasswordAlert.setVisibility(View.VISIBLE);
                passwordInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void afterTextChanged(Editable s) {
                        charNum = passwordInput.getText().toString().length();

                        if (0 <= charNum && charNum <= 3) {
                            int num = 0;
                            for (View v : passwordCircles) {
                                num++;
                                if (num <= charNum)
                                    v.setBackground(ContextCompat.getDrawable(InputPasswordPage.this, R.drawable.password_input_circle));
                                else
                                    v.setBackground(ContextCompat.getDrawable(InputPasswordPage.this, R.drawable.password_uninput_circle));
                            }
                        }
                        if (charNum > 3) {
                            if(!inputOld) {
                                if (application.getPassword().equals(passwordInput.getText().toString())) {
                                    inputOld = true;
                                    oldPasswordAlert.setVisibility(View.GONE);
                                    newPasswordAlert.setVisibility(View.VISIBLE);
                                    passwordInput.setText("");
                                } else {
                                    passwordInput.setText("");
                                }
                            }else{
                                if (!inputFinish) {
                                    tempPassword = passwordInput.getText().toString();
                                    inputFinish = true;
                                    newPasswordAlert.setVisibility(View.GONE);
                                    newPasswordAgainAlert.setVisibility(View.VISIBLE);
                                    passwordInput.setText("");
                                } else {
                                    if (tempPassword.equals(passwordInput.getText().toString())) {
                                        application.setPassword(tempPassword);
                                        application.setLockSetting(true);
                                        finish();
                                    } else {
                                        inputFinish = false;
                                        passwordInput.setText("");
                                        newPasswordAgainAlert.setVisibility(View.GONE);
                                        newPasswordAlert.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        } else {
            passwordInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void afterTextChanged(Editable s) {
                    charNum = passwordInput.getText().toString().length();
                    if (0 <= charNum && charNum <= 3) {
                        int num = 0;
                        for (View v : passwordCircles) {
                            num++;
                            if (num <= charNum)
                                v.setBackground(ContextCompat.getDrawable(InputPasswordPage.this, R.drawable.password_input_circle));
                            else
                                v.setBackground(ContextCompat.getDrawable(InputPasswordPage.this, R.drawable.password_uninput_circle));
                        }
                    }
                    if (charNum > 3) {
                        if (!inputFinish) {
                            tempPassword = passwordInput.getText().toString();
                            inputFinish = true;
                            newPasswordAlert.setVisibility(View.GONE);
                            newPasswordAgainAlert.setVisibility(View.VISIBLE);
                            passwordInput.setText("");
                        } else {
                            if (tempPassword.equals(passwordInput.getText().toString())) {
                                application.setPassword(tempPassword);
                                application.setLockSetting(true);
                                finish();
                            } else {
                                inputFinish = false;
                                passwordInput.setText("");
                                newPasswordAgainAlert.setVisibility(View.GONE);
                                newPasswordAlert.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
        }
    }


    Handler mHandler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    wrongPasswordAlert.setVisibility(View.GONE);
                    passwordInput.setText("");
                    break;
                default:
                    break;
            }
        }
    };

    public class MyTimer extends Thread {
        public MyTimer() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(500);
                mHandler.sendEmptyMessage(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
