package de.kai_morich.simple_bluetooth_terminal;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {

    Handler handler = null;
    ImageView imageView;
    int pageNumber = 0;
    FragmentManager fragmentManager;
    MediaPlayer mediaPlayer;
    ArrayList<Integer> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        mainViewinit();

        adIntent();

        adpageThread();



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ListFragment listFragment = (ListFragment) fragmentManager.findFragmentById(R.id.all);
        try{
            listFragment.setlist(data.getIntegerArrayListExtra("result"));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void mainViewinit(){
        imageView = findViewById(R.id.bannerImage);

        Fragment fragment = new ListFragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager
                .beginTransaction()
                .replace(R.id.all, fragment)
                .commit();
    }

    public void adIntent(){
        imageView.setOnClickListener(v -> {
            if(pageNumber==0){
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mnd.go.kr/user/boardList.action?boardId=O_46827&siteId=mnd&id=mnd_031200000000"));
                startActivity(i);
            }
            else{
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.blog.naver.com/mnd9090"));
                startActivity(i);
            }
        });
    }

    public void adpageThread(){
        handler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                if(pageNumber==0) {
                    pageNumber = 1;
                    imageView.setImageResource(R.mipmap.banner_1);
                }
                else {
                    pageNumber=0;
                    imageView.setImageResource(R.mipmap.banner_2);
                }
            }
        };
        class TimerThread implements Runnable {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(5000);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        TimerThread timer = new TimerThread();
        final Thread thread1 = new Thread(timer);
        thread1.start();
    }

    public void startmedia(){
        mediaPlayer = MediaPlayer.create(ShowActivity.this,R.raw.sound_button_wrong);
        mediaPlayer.start();
    }

    public void stopmedia(){
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    public void resetmedia(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

}
