package com.salam.elearning;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.salam.elearning.Models.User;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgetPassword";

    private AppCompatEditText mForgetPasswordEmail;
    private CircularProgressButton mSendEmail;

    private List<User> users;
    private User user;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mForgetPasswordEmail = findViewById(R.id.forget_password_email);
        mSendEmail = findViewById(R.id.forget_password_button);

        mSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mForgetPasswordEmail.getText().toString().trim();

                if(email.isEmpty()){

                    mForgetPasswordEmail.requestFocus();
                    mForgetPasswordEmail.setError("Email cannot be empty");

                }else{
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                        mSendEmail.startAnimation();

                        users = User.findByEmail(email);
                        if(users.size() > 0){
                            user = users.get(0);
                        }

                        new ForgetPassword(email).execute();
                    }else{
                        Utils.showSnackBar(findViewById(R.id.forget_password_screen), "Please enter a valid email.", Snackbar.LENGTH_SHORT);
                    }
                }

            }
        });
    }

    private class ForgetPassword extends AsyncTask<Void, Void, String> {

        String email;

        ForgetPassword(String email) {
            this.email = email;

        }

        @Override
        protected String doInBackground(Void ... voids) {

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("email", email);

            NetworkConnection networkConnection = new NetworkConnection();
            String forgetPasswordApi = getString(R.string.api_forget_password);
            response = networkConnection.performPostCall(forgetPasswordApi, params);

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if(!response.isEmpty()){

                try {

                    Log.d(TAG, response);

                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String error = jsonObject.getString("error");

                    if (status.equalsIgnoreCase("200") && error.isEmpty()) {

                        if(users.size() > 0){
                            user.setPasswordChanged("1");
                            user.update();
                        }else{
                            Gson gson = new Gson();
                            user = gson.fromJson(String.valueOf(jsonObject.getJSONObject("response")), User.class);
                            user.setPasswordChanged("1");
                            user.setServerId(jsonObject.getJSONObject("response").getString("id"));
                            user.save();
                        }

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                mSendEmail.revertAnimation();
                                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        };
                        new Handler().postDelayed(runnable, 500);

                    } else {

                        Utils.showSnackBar(findViewById(R.id.forget_password_screen), error, Snackbar.LENGTH_SHORT);

                    }

                }catch (Exception e){

                    e.printStackTrace();
                    Utils.showSnackBar(findViewById(R.id.forget_password_screen), e.getMessage(), Snackbar.LENGTH_SHORT);

                }

            }else{
                Utils.showSnackBar(findViewById(R.id.forget_password_screen), "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mSendEmail.revertAnimation();
                }
            };

            new Handler().postDelayed(runnable, 500);
        }
    }
}
