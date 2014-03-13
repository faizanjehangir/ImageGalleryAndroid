package com.galleria.imagegallery;

import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public PictureGallery pictureGallery;
	public RelativeLayout albumRelativeLayout;
	public RelativeLayout thumbnailPreviewRL;
	public RelativeLayout thumbnailInnerLayout;
	public ImageView enlargeImageView;
	public TextView albumName;
	public GridLayout thumbnailGrid;
	public int screenWidth;
	public int screenHeight;
	public int marginSpacesWidth;
	public static GridLayout gl;
	public static RelativeLayout imageWrapper;
	public static RelativeLayout btnPanelLayout;
	public static Button btnPlay;
	public static Button btnPrev;
	public static Button btnNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DisplayMetrics metrics = getApplicationContext().getResources()
				.getDisplayMetrics();
		
		//get buttons
		btnPlay = (Button) findViewById(R.id.btnplay);
		btnPrev = (Button) findViewById(R.id.btnprev);
		btnNext = (Button) findViewById(R.id.btnnext);
		
//		EnableGalleryButtons(false);
			
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		marginSpacesWidth = 150;
		screenWidth -= marginSpacesWidth;
		albumRelativeLayout = (RelativeLayout) findViewById(R.id.albumRelativeLayout);
		gl = (GridLayout) findViewById(R.id.albumMainGrid);
		
		imageWrapper = (RelativeLayout) findViewById(R.id.expandImagebackground);
		
		btnPanelLayout = (RelativeLayout) findViewById(R.id.buttonsPanel);

		thumbnailPreviewRL = (RelativeLayout) findViewById(R.id.thumbnailRelMainLayout);
		thumbnailInnerLayout = (RelativeLayout) findViewById(R.id.thumbnailRelInnerLayout);
		
		albumName = (TextView) findViewById(R.id.albumName);
		thumbnailGrid = (GridLayout) findViewById(R.id.thumbnailGrid);
		
		enlargeImageView = (ImageView) findViewById(R.id.expanded_image);
		
		pictureGallery = new PictureGallery(getApplicationContext(), enlargeImageView,
				albumName, thumbnailPreviewRL, thumbnailGrid);
		
		SetGridLayoutDim(gl, pictureGallery.getAlbumLayoutWidth(),
				pictureGallery.getAlbumLayoutHeight());
		
		SetGridLayoutDim(thumbnailGrid, pictureGallery.getThumbnailWidth(),
				pictureGallery.getThumbnailHeight());
		
		pictureGallery.BuildAlbums();
		
		gl.getChildAt(0).requestFocus();
	}

	public void SetGridLayoutDim(GridLayout gridLayout, int width, int height) {
		int colCount = screenWidth / width; // subtraction from colspan
													// from right/left
		gridLayout.setColumnCount(colCount);
	}
	
	public static void hideOrShowPanelButtons(int visibility){
		btnPlay.setVisibility(visibility);
		btnPrev.setVisibility(visibility);
		btnNext.setVisibility(visibility);
		btnPanelLayout.setVisibility(visibility);
	}

	private int dpToPx(int dp) {
		float density = getApplicationContext().getResources()
				.getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}
	
	public int pxToDp(int px) {
	    DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
	    int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		Log.v("onkeydown pressed", String.valueOf(keyCode));
//		return super.onKeyDown(keyCode, event);
//	}

//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		Log.v("keyup pressed", String.valueOf(keyCode));
//		if (keyCode == 22){
//			if (pictureGallery.inSlideShowMode){
//				btnNext.callOnClick();
//			}
//		}
//		if (keyCode == 21){
//			if (pictureGallery.inSlideShowMode){
//				btnPrev.callOnClick();
//			}
//		}
//		if (keyCode == 66){
//			if (pictureGallery.inSlideShowMode){
//				btnPlay.callOnClick();
//			}
//		}
//		return super.onKeyUp(keyCode, event);
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
