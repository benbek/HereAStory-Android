package com.hereastory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.hereastory.service.api.OutputFileService;
import com.hereastory.service.api.OutputFileServiceFactory;

public class MainActivity extends Activity {
	
	private OutputFileService outputFileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OutputFileServiceFactory.init(this);
        outputFileService = OutputFileServiceFactory.getOutputFileService();
        outputFileService.clearDirectory();

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
