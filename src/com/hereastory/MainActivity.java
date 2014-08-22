package com.hereastory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.hereastory.service.impl.OutputFileServiceImpl;
import com.hereastory.shared.IntentConsts;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupAddStoryButton();
        setupHearStoryButton();
        new OutputFileServiceImpl().clearDirectory();
    }

    private void setupHearStoryButton() {
    	final Button button = (Button) findViewById(R.id.buttonHearStory);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    			Intent intent = new Intent(getApplicationContext(), HearStoryActivity.class);
    			intent.putExtra(IntentConsts.STORY_ID, 1); // TODO: real id
    			startActivityForResult(intent, IntentConsts.HEAR_STORY_CODE);            
            }
        });
		
	}

	private void setupAddStoryButton() {
    	final Button button = (Button) findViewById(R.id.buttonCreateStory);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    			Intent intent = new Intent(getApplicationContext(), CreateStoryActivity.class);
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
