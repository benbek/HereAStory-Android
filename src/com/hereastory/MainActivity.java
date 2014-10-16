package com.hereastory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.hereastory.service.api.OutputFileService;
import com.hereastory.service.api.OutputFileServiceFactory;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends Activity {
	
	private OutputFileService outputFileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OutputFileServiceFactory.init(this);
        outputFileService = OutputFileServiceFactory.getOutputFileService();
        outputFileService.clearDirectory();
        
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

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
	protected void onDestroy() {
		outputFileService.clearDirectory();
	    super.onDestroy();
	}

}
