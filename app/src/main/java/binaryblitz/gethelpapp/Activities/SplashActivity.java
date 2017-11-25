package binaryblitz.gethelpapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import binaryblitz.gethelpapp.R;
import binaryblitz.gethelpapp.Server.GettHelpServerRequest;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_layout);

        try {
            GettHelpServerRequest.with(SplashActivity.this)
                    .authorize();

            Intent intent = new Intent(SplashActivity.this, OrdersActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }
}