package com.journal.Tobu;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.journal.Tobu.R;

import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements LocationListener, AbsListView.OnScrollListener {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	private Uri fileUri;
	
	int currentFirstVisibleItem = 0;
	int currentVisibleItemCount = 0;
	int totalItemCount = 0;
	int currentScrollState = 0;
	boolean loadingMore = false;
	Long startIndex = 0L;
	Long offset = 10L;
	View footerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DBAdapter mDbHelper = new DBAdapter(getBaseContext());
		ArrayList<ArrayList<String>> displayFields = mDbHelper.selectAll(null);
		display(displayFields);
		
	}
	
	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	    this.currentFirstVisibleItem = firstVisibleItem;
	    this.currentVisibleItemCount = visibleItemCount;
	    this.totalItemCount = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView absListView, int scrollState) {
	    this.currentScrollState = scrollState;
	    this.isScrollCompleted();
	}

	private void isScrollCompleted() {
	    if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE && this.totalItemCount == (currentFirstVisibleItem + currentVisibleItemCount)) {
	        /*** In this way I detect if there's been a scroll which has completed ***/
	        /*** do the work for load more date! ***/
	        if (!loadingMore) {
	            loadingMore = true;
	            loadMoreData();
	        }
	    }
	}

	
	private void loadMoreData() {
		System.out.println("end of scroll");
		DBAdapter mDbHelper = new DBAdapter(getBaseContext());
		ArrayList<ArrayList<String>> displayFields = mDbHelper.selectAll("20");
		display(displayFields);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Called when the user touches the camera button */
	public void onCameraClick(View view) {
		// create Intent to take a picture and return control to the calling
		// application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = SaveMedia.getOutputMediaFileUri(SaveMedia.MEDIA_TYPE_IMAGE,
				getBaseContext()); // create
		// a
		// file
		// to
		// save
		// the
		// image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
															// name

		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private void displayAlert() {
		
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
		dlgAlert.setMessage("Please enter some text");
		dlgAlert.setTitle("Tobu");
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}
	
	private void displayError(Exception e) {
		
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
		dlgAlert.setMessage(e.getMessage());
		dlgAlert.setTitle("Tobu");
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}
	
	/** Called when the user touches the button */
	public void storeFeedEvent(View view) {

		try {
			DBAdapter mDbHelper = new DBAdapter(getBaseContext());
			EditText feedText = (EditText) findViewById(R.id.editText1);
			if (feedText.getText() == null
					|| feedText.getText().toString().trim().equals("")) {
				displayAlert();
				return;
			}
			long id = mDbHelper.insert(feedText.getText().toString(),
					getDate(), FeedTypeConstant.EVENT, getCurrentLocation());
			feedText.getText().clear();
			hideKeyboard();
			ArrayList<ArrayList<String>> displayFields = mDbHelper
					.selectAll(null);
			display(displayFields);
		} catch (Exception e) {
			displayError(e);
		}
	}

	/** Called when the user touches the button */
	public void storeFeedThought(View view) {
		try {
			DBAdapter mDbHelper = new DBAdapter(getBaseContext());

			EditText feedText = (EditText) findViewById(R.id.editText1);
			if (feedText.getText() == null
					|| feedText.getText().toString().trim().equals("")) {
				displayAlert();
				return;
			}
			long id = mDbHelper.insert(feedText.getText().toString(),
					getDate(), FeedTypeConstant.THOUGHT, getCurrentLocation());
			feedText.getText().clear();
			hideKeyboard();
			ArrayList<ArrayList<String>> displayFields = mDbHelper
					.selectAll(null);
			display(displayFields);
		} catch (Exception e) {
			displayError(e);
		}
	}

	/** Called when the user touches the button */
	private void storeFeedImage(String uri) {
		try {
			DBAdapter mDbHelper = new DBAdapter(getBaseContext());

			EditText feedText = (EditText) findViewById(R.id.editText1);
			long id = mDbHelper.insert(uri, getDate(), FeedTypeConstant.IMAGE,
					getCurrentLocation());
			feedText.getText().clear();
			hideKeyboard();
			ArrayList<ArrayList<String>> displayFields = mDbHelper
					.selectAll(null);

			display(displayFields);
		} catch (Exception e) {
			displayError(e);
		}

	}

	private void display(ArrayList<ArrayList<String>> displayFields) {
		LinearLayout layout = (LinearLayout) this
				.findViewById(R.id.linearLayout1);
		layout.removeAllViewsInLayout();
		String lastDate = null;
		for (int i = 0; i < displayFields.size(); i++) {

			if (displayFields.get(i).get(0) != null) {
				lastDate = displayDate(displayFields.get(i).get(0), lastDate, layout);
			}

			if (displayFields.get(i).get(3) != null) {
				if (displayFields.get(i).get(3).equals(FeedTypeConstant.IMAGE)) {
					if (displayFields.get(i).get(1) != null) {
						displayImage(displayFields.get(i).get(1), layout);
					}
				} else {
					TextView viewEntry = new TextView(this);
					String location = "";
					if ((displayFields.get(i).get(3).equals(FeedTypeConstant.EVENT) && (displayFields.get(i).get(2) != null))) {
						location = " at " + displayFields.get(i).get(2);
					}
					viewEntry.setText(displayFields.get(i).get(1) + location);
					viewEntry.setLayoutParams(getEditTextParams());
					viewEntry.setTextSize((float) 20.0);
					viewEntry.setLineSpacing(0, (float) 1.0);
					layout.addView(viewEntry);
				}
			}
		}

	}

	private void displayImage(String path, LinearLayout layout) {
		ImageView imageView = new ImageView(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.bottomMargin = 20;
		lp.topMargin = 20;
		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bounds);

		BitmapFactory.Options opts = new BitmapFactory.Options();
		Bitmap image = BitmapFactory.decodeFile(path, opts);

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
		int orientation = orientString != null ? Integer.parseInt(orientString)
				: ExifInterface.ORIENTATION_NORMAL;

		int rotationAngle = 0;
		if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
			rotationAngle = 90;
		if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
			rotationAngle = 180;
		if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
			rotationAngle = 270;

		Matrix matrix = new Matrix();
		matrix.setRotate(rotationAngle, (float) image.getWidth() / 2,
				(float) image.getHeight() / 2);
		Bitmap rotatedBitmap = Bitmap.createBitmap(image, 0, 0,
				bounds.outWidth, bounds.outHeight, matrix, true);

		Bitmap bMapScaled = scaleImage(rotatedBitmap, 0.6);

		imageView.setImageBitmap(bMapScaled);
		imageView.setBackgroundColor(Color.BLACK);
		imageView.setPadding(10, 10, 10, 10);

		layout.setGravity(Gravity.CENTER);
		layout.addView(imageView, lp);

	}

	private Bitmap scaleImage(Bitmap image, double d) {
		//int srcWidth = image.getWidth();
		//int srcHeight = image.getHeight();
		//int dstWidth = (int) (srcWidth * d);
		//int dstHeight = (int) (srcHeight * d);
		
		int dstWidth = 648;
		int dstHeight = 1152;
		return Bitmap.createScaledBitmap(image, dstWidth, dstHeight, true);
	}

	private LayoutParams getEditTextParams() {
		int left = 20;
		int top = 20;
		int right = 20;
		int bottom = 20;

		TableRow.LayoutParams params = new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(left, top, right, bottom);
		return params;
	}

	private LayoutParams getImageLayoutParams() {
		int top = 20;
		int bottom = 20;

		TableRow.LayoutParams params = new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		params.bottomMargin = bottom;
		params.topMargin = top;
		return params;
	}

	private String displayDate(String newDate, String lastDate,
			LinearLayout layout) {
		System.out.println("Date is " + newDate);
		System.out.println("last Date is " + lastDate);

		if ((lastDate == null) || (!newDate.equals(lastDate))) {
			TextView viewEntry = new TextView(this);
			String tempString = newDate;

			SpannableString spanString = new SpannableString(tempString);

			spanString.setSpan(new StyleSpan(Typeface.BOLD), 0,
					spanString.length(), 0);

			viewEntry.setText(spanString);
			viewEntry.setTextSize((float) 20.0);
			viewEntry.setLineSpacing(0, (float) 2.0);
			// viewEntry.setLayoutParams(new LayoutParams(
			// LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			viewEntry.setLayoutParams(getEditTextParams());
			layout.addView(viewEntry);
			return newDate;
		} else {
			return lastDate;
		}
	}

	private String getDate() {
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		return date;
	}

	private String getCurrentLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if (location == null) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
					0, this);
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(location == null) {
				return null;
			}
		}

		Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());

		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 1);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return extractAddress(addresses.get(0));
	}

	private String extractAddress(Address address) {
		String finalAddress = "";

		if (address.getPremises() != null) {
			finalAddress = finalAddress + address.getPremises() + ", ";
		}

		if (address.getSubLocality() != null) {
			finalAddress = finalAddress + address.getSubLocality() + ", ";
		}

		if (address.getLocality() != null) {
			finalAddress = finalAddress + address.getLocality() + ", ";
		}

		if (address.getAddressLine(0) != null) {
			finalAddress = finalAddress + address.getAddressLine(0) + ", ";
		}

		if (address.getCountryName() != null) {
			finalAddress = finalAddress + address.getCountryName();
		}
		return finalAddress;
	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Image saved to:\n" + fileUri.getPath(),
						Toast.LENGTH_LONG).show();
				storeFeedImage(fileUri.getPath());
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}

		if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Video captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Video saved to:\n" + data.getData(),
						Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the video capture
			} else {
				// Video capture failed, advise user
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("Latitude", "status");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("Latitude", "enable");
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("Latitude", "disable");

	}
	
}
