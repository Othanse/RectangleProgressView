package demo.chat.yingshe.com.customrectangleprogressview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RectangleProgressView rpvRectangleProgressView = (RectangleProgressView) findViewById(R.id.rpv);
        Button btnButton = (Button) findViewById(R.id.btn);

        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rpvRectangleProgressView.setProgressListener(new RectangleProgressView.progressListener() {
                    @Override
                    public void over() {
                        System.out.println("    倒计时结束~");
                    }

                    @Override
                    public void start() {
                        System.out.println("    倒计时开始~~");
                    }

                    @Override
                    public void progress(int total, float progress) {
                        System.out.println("  进度：" + progress);

                        if (progress > 15) {
                            rpvRectangleProgressView.setColor(Color.RED);
                        }
                    }
                });

                rpvRectangleProgressView.start(20);
            }
        });


    }
}
