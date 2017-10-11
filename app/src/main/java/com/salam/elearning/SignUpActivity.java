package com.salam.elearning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.salam.elearning.Models.Skill;
import com.salam.elearning.Models.User;
import com.salam.elearning.Utils.NetworkConnection;
import com.salam.elearning.Utils.NetworkUtils;
import com.salam.elearning.Utils.Utils;
import com.thomashaertel.widget.MultiSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUp";

    private MultiSpinner spinner;
    private ArrayAdapter<String> adapter;
    private HashMap<String, String> skills = new HashMap<>();
    private ArrayList<String> allSelectedSkills;

    private EditText mUsername;
    private EditText mPassword;
    private EditText mEmail;
    private CircleImageView mImagePicker;
    private CircularProgressButton mSignUp;

    private String username;
    private String password;
    private String email;
    private String image;

    private Bitmap selectedImageBitmap;
    private String picturePath = "";

    private Context context;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        new AllSkills().execute();

        context = this;
        view = (((Activity) context).findViewById(R.id.signup_activity));

        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mEmail = findViewById(R.id.email);
        mImagePicker = findViewById(R.id.profile_image);
        mSignUp = findViewById(R.id.sign_up_button);

        mSignUp.setOnClickListener(this);
        mImagePicker.setOnClickListener(this);

        // create spinner list elements
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        // get spinner and set adapter
        spinner = (MultiSpinner) findViewById(R.id.skill);
        spinner.setAdapter(adapter, false, onSelectedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            picturePath = selectedImage.toString();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                mImagePicker.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            spinner.setSelected(selected);
        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.sign_up_button:
                if (NetworkUtils.isWifiConnected(context) || NetworkUtils.isMobileConnected(context)) {

                    username = mUsername.getText().toString().trim();
                    password = mPassword.getText().toString().trim();
                    email = mEmail.getText().toString().trim();
                    boolean[] selectedSkills = spinner.getSelected();

                    if(validation(username, password, email)){
                        mSignUp.startAnimation();
                        new SignUp(username, password, email, selectedSkills).execute();
                    }
                }else {
                    Utils.showSnackBar(view,"You need to have an active internet connection", Snackbar.LENGTH_SHORT);
                }
                break;

            case R.id.profile_image:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
                break;
        }
    }

    private class AllSkills extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void ... voids) {

            String response = "";
            if (NetworkUtils.isWifiConnected(context) || NetworkUtils.isMobileConnected(context)) {
                String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
                String getAllSkillsApi = getString(R.string.api_get_skills);

                HashMap<String, String> params = new HashMap<>();
                params.put("apiCsrfKey", csrfKey);

                NetworkConnection networkConnection = new NetworkConnection();
                response = networkConnection.performPostCall(getAllSkillsApi, params);
            }else {
                Utils.showSnackBar(view,"You need to have an active internet connection", Snackbar.LENGTH_SHORT);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if(!response.isEmpty()){

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.d(TAG, jsonArray.length() + "");

                    for (int i = 0; i < jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        adapter.add(jsonObject.getString("name"));
                        skills.put(jsonObject.getString("name"), jsonObject.getString("id"));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else{
                Utils.showSnackBar(view, "Error occurred in retrieving skills.", Snackbar.LENGTH_SHORT);
            }
        }
    }

    private boolean validation(String username, String password, String email){

        Log.d(TAG + "_spinner-length", spinner.length() + "");
        if(username.isEmpty()){
            mUsername.requestFocus();
            mUsername.setError("Username cannot be empty");
            return false;
        }

        if(email.isEmpty()){
            mEmail.requestFocus();
            mEmail.setError("Email cannot be empty");
            return false;
        }else if (!email.isEmpty()){
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mEmail.requestFocus();
                mEmail.setError("Please enter a valid email");
                return false;
            }
        }

        if(password.isEmpty()){
            mPassword.requestFocus();
            mPassword.setError("Password cannot be empty");
            return false;
        }else if(password.length() < 6){
            mPassword.requestFocus();
            mPassword.setError("Password must be atleast 6 characters");
            return false;
        }

        if(spinner.length() <= 0){
            spinner.requestFocus();
            spinner.setError("Atleast one skill must be selected");
            return false;
        }

        return  true;

    }

    private class SignUp extends AsyncTask<Void, Void, String> {

        String username;
        String password;
        String email;
        String image;
        boolean[] selectedSkills;

        SignUp(String username, String password, String email, boolean[] selectedSkills) {
            this.username = username;
            this.password = password;
            this.selectedSkills = selectedSkills;
            this.email = email;

        }

        @Override
        protected String doInBackground(Void ... voids) {

            String encoded = "";
            if(!picturePath.isEmpty()) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }

            allSelectedSkills = new ArrayList<>();
            for (int i = 0; i < selectedSkills.length; i++){
                if(selectedSkills[i]){
                    allSelectedSkills.add("'" + skills.get(adapter.getItem(i)) + "'");
                }
            }

            String response = "";

            HashMap<String, String> params = new HashMap<>();

            String csrfKey = "FASMBQWIFJDAJ28915734BBKBK8945CTIRETE354PA67";
            params.put("apiCsrfKey", csrfKey);
            params.put("username", username);
            params.put("password", password);
            params.put("email", email);
            params.put("image", encoded);
            params.put("skills", String.valueOf(new JSONArray(allSelectedSkills)));

            NetworkConnection networkConnection = new NetworkConnection();
            String loginApi = getString(R.string.api_signup);
            response = networkConnection.performPostCall(loginApi, params);

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

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                mSignUp.revertAnimation();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        };
                        new Handler().postDelayed(runnable, 500);

                    } else {

                        Utils.showSnackBar(view, error, Snackbar.LENGTH_SHORT);

                    }

                }catch (Exception e){

                    e.printStackTrace();
                    Utils.showSnackBar(view, e.getMessage(), Snackbar.LENGTH_SHORT);

                }

            }else{
                Utils.showSnackBar(view, "Some error occurred. Please try again.", Snackbar.LENGTH_SHORT);
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mSignUp.revertAnimation();
                }
            };

            new Handler().postDelayed(runnable, 500);
        }
    }
}
