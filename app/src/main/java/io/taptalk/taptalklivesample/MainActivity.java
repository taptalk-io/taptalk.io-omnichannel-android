package io.taptalk.taptalklivesample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.taptalk.taptalklive.TapTalkLive;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TapTalkLive.init(getApplicationContext(), R.mipmap.ic_launcher, "TapTalk.live Sample App");
        findViewById(R.id.tv_main).setOnClickListener(v -> TapTalkLive.openChatRoomList(this));
    }
}
