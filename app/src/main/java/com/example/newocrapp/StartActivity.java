package com.example.newocrapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.newocrapp.Adapters.SliderAdapter;

public class StartActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private RelativeLayout layout;
    private ImageView help;
    private Button next, back, finish;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_start);

        viewPager = findViewById(R.id.pages);
        layout = findViewById(R.id.dots);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        finish = findViewById(R.id.finish);
        help = findViewById(R.id.help);


        SliderAdapter adapter = new SliderAdapter(this);
        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                for (int i = 0; i <= viewPager.getCurrentItem(); i++) {
                    if (i == 0) {
                        back.setVisibility(View.INVISIBLE);
                        next.setVisibility(View.VISIBLE);
                        finish.setVisibility(View.INVISIBLE);
                    } else if (i != 0 && i != 2) {
                        back.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        finish.setVisibility(View.INVISIBLE);
                    } else {
                        back.setVisibility(View.VISIBLE);
                        next.setVisibility(View.INVISIBLE);
                        finish.setVisibility(View.VISIBLE);
                    }
                }

                handler.postDelayed(this, 300);
            }
        };

        handler.postDelayed(r, 300);
        viewPager.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() != 2) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


//        help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                PopupWindow popupWindow = new PopupWindow(inflater.inflate(R.layout.help_popup, null, false));
//                popupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 15, 20);
//            }
//        });
    }
}