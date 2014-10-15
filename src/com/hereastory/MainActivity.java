package com.hereastory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.hereastory.service.impl.OutputFileServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        setupAddStoryButton();
        setupHearStoryButton();
        new OutputFileServiceImpl(this).clearDirectory();
        
        ParseUser user = new ParseUser();
        user.setUsername("my name");
        user.setPassword("my pass");
        user.setEmail("email@example.com");
        /*try {
			user.signUp();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        try {
			ParseUser.logIn(user.getUsername(), "my pass");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        startActivity(new Intent(this, MapActivity.class));
    }

    private void setupHearStoryButton() {
    	final Button button = (Button) findViewById(R.id.buttonHearStory);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    			Intent intent = new Intent(getApplicationContext(), HearStoryActivity.class);
    			intent.putExtra(IntentConsts.STORY_ID, "m2dCb1QhAC");
    			startActivityForResult(intent, IntentConsts.HEAR_STORY_CODE);            
            }
        });
		
	}

	private void setupAddStoryButton() {
    	final Button button = (Button) findViewById(R.id.buttonCreateStory);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    			Intent intent = new Intent(getApplicationContext(), CreateStoryActivity.class);
    			intent.putExtra(IntentConsts.CURRENT_LAT, 1.2); 
    			intent.putExtra(IntentConsts.CURRENT_LONG, 4.5); 
    			startActivityForResult(intent, IntentConsts.CREATE_STORY_CODE);
            }
        });
	}
	
	protected void onActivityResult(int reqCode, int resCode, Intent data) {
		switch (reqCode) {
		case IntentConsts.CREATE_STORY_CODE:
			if (resCode == RESULT_OK) {
				// TODO:
			}
			break;
		case IntentConsts.HEAR_STORY_CODE:
			if (resCode == RESULT_OK) {
				// TODO:
			}
			break;
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
