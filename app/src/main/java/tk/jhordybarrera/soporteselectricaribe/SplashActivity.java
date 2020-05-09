package tk.jhordybarrera.soporteselectricaribe;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class SplashActivity extends AppCompatActivity {
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(() -> startLogin(null), 3000);
    }
    public void startLogin(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        handler.removeCallbacksAndMessages(null);
    }
}
