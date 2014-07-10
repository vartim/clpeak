package kr.clpeak;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import kr.clpeak.jni_connect;
import libcore.io.*;
 
public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.result_display);

    	populatePlatformSpinner();
    	
    	findViewById(R.id.run_button).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
            	jni_connect clp = new jni_connect(MainActivity.this);
                clp.execute();
            }
    	});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        	case R.id.menu_about:
        		Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
        }
        
        return true;
    }
    
    public void populatePlatformSpinner() {
    	
    	final Spinner spinnerPlatform = (Spinner) findViewById(R.id.spinner_platform_list);
    	
		final List<String> libopenclSoPaths = new ArrayList<String>(Arrays.asList(
    									"libOpenCL.so",
    									"/system/lib/libOpenCL.so",
    									"/system/vendor/lib/libOpenCL.so",
    									"/system/vendor/lib/egl/libGLES_mali.so",
    									"/system/vendor/lib/libPVROCL.so",
    									"/data/data/org.pocl.libs/files/lib/libpocl.so"
    								));
    	
		final List<String> libopenclPlatforms = new ArrayList<String>(Arrays.asList(
										"default",
    									"system lib",
										"system vendor lib",
										"mali",
										"powerVR",
										"pocl"
									));
    	
    	// Don't search for "default" & "pocl"
		for(int i=(libopenclSoPaths.size()-2); i > 0; i--)
    	{
    		if(!(new File(libopenclSoPaths.get(i)).exists()))
    		{
    			libopenclSoPaths.remove(i);
    			libopenclPlatforms.remove(i);
    		}
    	}
    	
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
    										android.R.layout.simple_spinner_item, libopenclPlatforms);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinnerPlatform.setAdapter(dataAdapter);
    	
    	spinnerPlatform.setOnItemSelectedListener(new OnItemSelectedListener()
    	{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				if(libopenclPlatforms.get(arg2).equals("pocl"))
				{
					if(!(new File(libopenclSoPaths.get(arg2)).exists()))
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						
						builder.setMessage("pocl installation not found\ninstall it from playstore?");
						
						builder.setPositiveButton("go", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   Uri uri = Uri.parse("market://details?id=org.pocl.libs");
					        	   Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
					        	   startActivity(myAppLinkToMarket);
					           }
					       });
						
						builder.setNegativeButton("leave it", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id)
					           {}
					       });
						
				    	builder.show();
						spinnerPlatform.setSelection(0);
						return;
					}
				}
				Libcore.os.setenv("LIBOPENCL_SO_PATH", libopenclSoPaths.get(arg2), true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{}
    	}
    	);
    }
}
