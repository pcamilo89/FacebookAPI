package com.androiducab.facebookapi;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    TextView  txtUsername, txtEmail, txtBirthday,txtFriends, txtGender;
    ProgressDialog mDialog;
    CircleImageView imgAvatar;
    Bitmap imageBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager= CallbackManager.Factory.create();
        
        txtBirthday = findViewById(R.id.profile_birthday);
        txtEmail= findViewById(R.id.profile_email);
        txtFriends= findViewById(R.id.profile_friends);
        imgAvatar = findViewById(R.id.profile_avatar);
        txtUsername = findViewById(R.id.profile_username);
        txtGender = findViewById(R.id.profile_gender);

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_birthday","user_friends", "user_gender"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog= new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Retrieving data..");
                mDialog.show();
                String accesstoken = loginResult.getAccessToken().getToken();
                GraphRequest request= GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                    mDialog.dismiss();
                    Log.d("response",response.toString());
                    getData(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,gender,picture,email,birthday,friends");
                request.setParameters(parameters);
                request.executeAsync();
                }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        //printKeyHash();
        if(AccessToken.getCurrentAccessToken()!=null){

            txtEmail.setText(AccessToken.getCurrentAccessToken().getUserId());

        };
    }

    private void getData(JSONObject object) {
        try{
            String profile_picture= "https://graph.facebook.com/"+object.getString("id")+"/picture?type=large";

            //InputStream in = null;
            //try {
            //    in = (InputStream) profile_picture.getContent();
            //    imageBitmap = BitmapFactory.decodeStream(in);
            //    imgAvatar.setImageBitmap(imageBitmap);
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}


            Picasso.with(this).load(profile_picture).into(imgAvatar);
            txtUsername.setText(object.getString("name"));
            txtEmail.setText("Email: "+object.getString("email"));
            txtBirthday.setText("Birthday: "+object.getString("birthday"));
            txtGender.setText("Gender: "+object.getString("gender"));

            String count = "Friends: "+object.getJSONObject("friends").getJSONObject("summary").getString("total_count");
            txtFriends.setText(count);

        //} catch (MalformedURLException e) {
        //    e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void printKeyHash() {
    try{
        PackageInfo info= getPackageManager().getPackageInfo("com.androiducab.facebookapi", PackageManager.GET_SIGNATURES);
        for (Signature signature:info.signatures){
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));

        }
    } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    }
    }
}
