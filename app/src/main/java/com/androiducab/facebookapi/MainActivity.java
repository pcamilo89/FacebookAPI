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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.androiducab.facebookapi.adapter.RecyclerViewAdapter;
import com.androiducab.facebookapi.adapter.SwipeToDeleteCallback;
import com.androiducab.facebookapi.model.Post;
import com.androiducab.facebookapi.model.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    TextView  txtUsername, txtEmail, txtBirthday,txtFriends, txtGender;
    ProgressDialog mDialog;
    CircleImageView imgAvatar;

    private static final String TAG = "ListActivity";

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private ItemTouchHelper itemTouchHelper;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.myFeed = new ArrayList<Post>();


        callbackManager= CallbackManager.Factory.create();
        
        txtBirthday = findViewById(R.id.profile_birthday);
        txtEmail= findViewById(R.id.profile_email);
        txtFriends= findViewById(R.id.profile_friends);
        imgAvatar = findViewById(R.id.profile_avatar);
        txtUsername = findViewById(R.id.profile_username);
        txtGender = findViewById(R.id.profile_gender);

        initRecyclerView();

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_birthday","user_friends","user_gender","user_posts"));
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

                Bundle params = new Bundle();
                //params.putString("fields", ",privacy,comments.summary(true),likes.summary(true)");
                //params.putString("limit", "100");

                params.putString("fields", "id,created_time,caption,description,from,full_picture,status_type,name,link,message,source,shares,type,attachments,child_attachments,comments.summary(true),likes.summary(true)");
                params.putString("limit", "20");

                /* make the API call */
                new GraphRequest(loginResult.getAccessToken(), "/me/feed", params, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                /* handle the result */
                                //System.out.println("Festival Page response::" + String.valueOf(response.getJSONObject()));

                                try {
                                    //JSONObject jObjResponse = new JSONObject(String.valueOf(response.getJSONObject()));
                                    //System.out.println(jObjResponse);

                                    JSONObject jObjResponse = response.getJSONObject();
                                    JSONArray dataArray =  jObjResponse.getJSONArray("data");

                                    for(int i=0; i<dataArray.length();i++){
                                        JSONObject element = (JSONObject) dataArray.get(i);
                                        System.out.println(String.valueOf(element));

                                        Post tempPost =  new Post();

                                        if(element.has("id")){
                                            Log.e("id: ", element.getString("id"));
                                            tempPost.setId(element.getString("id"));
                                        }

                                        if(element.has("created_time")){
                                            Log.e("created_time: ", element.getString("created_time"));
                                            tempPost.setTime(element.getString("created_time"));
                                        }

                                        if(element.has("from")){
                                            Log.e("from: ", element.getJSONObject("from").getString("name"));
                                            tempPost.setFrom(element.getJSONObject("from").getString("name"));
                                        }

                                        if(element.has("full_picture")){
                                            Log.e("full_picture: ", element.getString("full_picture"));
                                            tempPost.setPicture(element.getString("full_picture"));
                                        }

                                        if(element.has("caption")){
                                            Log.e("caption: ", element.getString("caption"));
                                            tempPost.setCaption(element.getString("caption"));
                                        }

                                        if(element.has("description")){
                                            Log.e("description: ", element.getString("description"));
                                            tempPost.setDescription(element.getString("description"));
                                        }

                                        if(element.has("name")){
                                            Log.e("name: ", element.getString("name"));
                                            tempPost.setName(element.getString("name"));
                                        }

                                        if(element.has("link")){
                                            Log.e("link: ", element.getString("link"));
                                            tempPost.setLink(element.getString("link"));
                                        }

                                        if(element.has("message")){
                                            Log.e("message: ", element.getString("message"));
                                            tempPost.setMessage(element.getString("message"));
                                        }

                                        if(element.has("status_type")){
                                            Log.e("status_type: ", element.getString("status_type"));
                                            tempPost.setStatusType(element.getString("status_type"));
                                        }

                                        if(element.has("type")){
                                            Log.e("type: ", element.getString("type"));
                                            tempPost.setType(element.getString("type"));
                                        }

                                        if(element.has("likes")){
                                            Log.e("likes: ", element.getJSONObject("likes").getJSONObject("summary").getString("total_count"));
                                            tempPost.setLikes(Integer.parseInt(element.getJSONObject("likes").getJSONObject("summary").getString("total_count")));
                                        }

                                        if(element.has("comments")){
                                            Log.e("comments: ", element.getJSONObject("comments").getJSONObject("summary").getString("total_count"));
                                            tempPost.setComments(Integer.parseInt(element.getJSONObject("comments").getJSONObject("summary").getString("total_count")));
                                        }

                                        if(element.has("shares")){
                                            Log.e("shares: ", element.getJSONObject("shares").getString("count"));
                                            tempPost.setShares(Integer.parseInt(element.getJSONObject("shares").getString("count")));
                                        }

                                        Utils.myFeed.add(tempPost);
                                        adapter.notifyDataSetChanged();
                                    }


                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();

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

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recycler_view);

        adapter = new RecyclerViewAdapter(Utils.myFeed, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
