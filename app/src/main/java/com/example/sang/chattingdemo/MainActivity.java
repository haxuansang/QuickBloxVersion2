package com.example.sang.chattingdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MainActivity extends AppCompatActivity {
    static final String APP_ID="72018";
    static final String AUTH_KEY="Xp6Y24yq25GO3zc";
    static final String AUTH_SECRET="cT8PxwdYYkROL3S";
    static final String ACCOUNT_KEY="7yc_WUA_shXrP1YqRznJ";
    Button btnLogin,btnRegister;
    EditText edtUsername,edtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtUsername=(EditText)findViewById(R.id.username);
        edtPassword=(EditText)findViewById(R.id.password);
        btnLogin=(Button)findViewById(R.id.login);
        btnRegister=(Button)findViewById(R.id.register);
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user= edtUsername.getText().toString();
                final String pass= edtPassword.getText().toString();
                QBUser qbUser = new QBUser(user,pass);
                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        Bundle bundleUser= new Bundle();
                        bundleUser.putString("username",user);
                        bundleUser.putString("password",pass);
                        Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                        intent.putExtra("PackageUser",bundleUser);
                        MainActivity.this.startActivity(intent);


                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user= edtUsername.getText().toString();
                String pass= edtPassword.getText().toString();
                QBUser qbUser = new QBUser(user,pass);
                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(MainActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(MainActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
