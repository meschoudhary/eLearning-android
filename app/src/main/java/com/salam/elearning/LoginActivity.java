package com.salam.elearning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.salam.elearning.Models.User;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.NetworkUtils;
import com.salam.elearning.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Login";
    private EditText mUsername;
    private EditText mPassword;
    private CircularProgressButton  mSignIn;
    private TextView mForgotPassword;
    private TextView mSignUp;

    private String username;
    private String password;

    private Context context;
    private View view;

    private String forgotPasswordApi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        view = (((Activity) context).findViewById(R.id.login_activity));

        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mSignIn = (CircularProgressButton) findViewById(R.id.sign_in_button);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
        mSignUp = (TextView) findViewById(R.id.sign_up);

        mSignIn.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (NetworkUtils.isWifiConnected(context) || NetworkUtils.isMobileConnected(context)) {

            switch (view.getId()) {

                case R.id.sign_in_button:

                    username = mUsername.getText().toString().trim();
                    password = mPassword.getText().toString().trim();

                    if(validation(username, password)){
                        Log.d(TAG,"Validation Passed");
                        mSignIn.startAnimation();
                        new Async(username, password).execute();
                    }

                    break;

                case R.id.forgot_password:

                    Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                    startActivity(i);
                    break;

                case R.id.sign_up:
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    break;
            }
        }else {
            Utils.showSnackBar(view,"You need to have an active internet connection", Snackbar.LENGTH_SHORT);
        }
    }

    private boolean validation(String username, String password){

        if(username.isEmpty()){
            mUsername.requestFocus();
            mUsername.setError("Username cannot be empty");
            return false;
        }

        if(password.isEmpty()){
            mPassword.requestFocus();
            mPassword.setError("Password cannot be empty");
            return false;
        }

        return true;
    }

    private class Async extends AsyncTask<String,String,String> {
        String username;
        String password;

        Async(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return loginUser(username, password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {

            if (!response.isEmpty()) {

                try {

                    Log.d(TAG, response);

                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String error = jsonObject.getString("error");

                    if (status.equalsIgnoreCase("200") && error.isEmpty()) {

                        JSONObject userData = jsonObject.getJSONObject("response");

                        String userID = userData.getString("serverId");
                        String username = userData.getString("username");
                        String email = userData.getString("email");

                        User user;
                        List<User> userAlready = User.findByServerId(userID);

                        if(userAlready.size() > 0){
                            user = userAlready.get(0);
                            user.setLoggedIn("1");
                        }else{
                            user = new User(userID, username, email, "1");

                        }
                        user.save();

                        mSignIn.revertAnimation();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        mSignIn.revertAnimation();
                        Utils.showSnackBar(view, error, Snackbar.LENGTH_SHORT);
                    }
                }catch (Exception e){
                    mSignIn.revertAnimation();
                    e.printStackTrace();
                    Utils.showSnackBar(view, e.getMessage(), Snackbar.LENGTH_SHORT);
                }

            } else {
                mSignIn.revertAnimation();
                Utils.showSnackBar(view, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }
        }
    }

    private String loginUser(String username, String password) throws JSONException {

        HashMap<String, String> params = new HashMap<>();

        String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
        params.put("apiCsrfKey", csrfKey);
        params.put("username", username);
        params.put("password", password);

        NetworkConnection networkConnection = new NetworkConnection();
        String loginApi = getString(R.string.api_login);

        return networkConnection.performPostCall(loginApi, params);
    }

    @Override
    protected void onDestroy() {
        mSignIn.dispose();
        super.onDestroy();
    }


}
