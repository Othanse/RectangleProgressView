package demo.chat.yingshe.com.customrectangleprogressview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private RectangleProgressView2 rpvRectangleProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rpvRectangleProgressView = (RectangleProgressView2) findViewById(R.id.rpv);
        Button start = (Button) findViewById(R.id.start);
        Button pause = (Button) findViewById(R.id.pause);
        Button stop = (Button) findViewById(R.id.stop);


        start.setOnClickListener(onClickListener);
        pause.setOnClickListener(onClickListener);
        stop.setOnClickListener(onClickListener);

        rpvRectangleProgressView.setProgressListener(new RectangleProgressView2.progressListener() {
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
                    System.out.println("  倒计时 到了15了 重新开始计时20秒");
                    rpvRectangleProgressView.stop();
                    rpvRectangleProgressView.start(20);
//                    rpvRectangleProgressView.setColor(Color.RED);
                }
            }
        });


    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start:
                    rpvRectangleProgressView.start(20);
                    break;
                case R.id.pause:
                    rpvRectangleProgressView.pause();
                    break;
                case R.id.stop:
                    rpvRectangleProgressView.stop();
                    break;
            }

        }
    };

}
