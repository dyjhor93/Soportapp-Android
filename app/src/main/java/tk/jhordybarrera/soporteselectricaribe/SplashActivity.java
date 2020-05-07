package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        (new Handler()).postDelayed(this::lanzar, 2000);
    }
    public void lanzar(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    };
}
