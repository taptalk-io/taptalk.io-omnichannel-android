package io.taptalk.taptalklivesample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.taptalk.taptalklive.Activity.TTLCreateCaseFormActivity;
import io.taptalk.taptalklive.TapTalkLive;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TapTalkLive.init(getApplicationContext(), R.mipmap.ic_launcher, "TapTalk.live Sample App");
        //findViewById(R.id.tv_main).setOnClickListener(v -> TapTalkLive.openChatRoomList(this));

        Intent intent = new Intent(this, TTLCreateCaseFormActivity.class);
        startActivity(intent);
    }
}
