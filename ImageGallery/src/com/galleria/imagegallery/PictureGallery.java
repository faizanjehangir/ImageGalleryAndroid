package com.galleria.imagegallery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView.FindListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class PictureGallery implements AnimationListener {

	Context context;
	int layoutWidth;
	int layoutHeight;
	int thumbnailWidth;
	int thumbnailHeight;
	RelativeLayout thumbnailRL;
	TextView albumName;
	GridLayout thumbnailGrid;
	ImageView enlargeImage;

	int enlargedImageId;
	int currentButtonState;
	// Animation
	Animation animFadein;
	Animation animFadeout;
	AnimationSet animation;

	public boolean isPlayPressed = false;

	public static boolean inSlideShowMode = false;

	// Hold a reference to the current animator,
	// so that it can be canceled mid-way.
	private Animator mCurrentAnimator;

	// The system "short" animation time duration, in milliseconds. This
	// duration is ideal for subtle animations or animations that occur
	// very frequently.
	private int mShortAnimationDuration;

	AlphaAnimation anim;

	// hashmap for image info
	LinkedHashMap<Integer, String> albumImageDetails;

	LinkedHashMap<String, LinkedHashMap<Integer, String>> albumInfo;

	public PictureGallery(Context context, ImageView enlargeImage,
			TextView albumName, RelativeLayout thumbnailRL,
			GridLayout thumbnailGrid) {
		this.context = context;
		layoutWidth = 200;
		layoutHeight = 170;

		thumbnailWidth = 200;
		thumbnailHeight = 170;

		// construct a key value pair hashmap
		albumImageDetails = new LinkedHashMap<Integer, String>();
		albumImageDetails.put(1, "movie1");
		albumImageDetails.put(2, "movie2");
		albumImageDetails.put(3, "movie5");
		albumImageDetails.put(4, "movie4");
		albumImageDetails.put(5, "movie1");
		albumImageDetails.put(6, "movie2");
		// construct all albums map
		albumInfo = new LinkedHashMap<String, LinkedHashMap<Integer, String>>();
		albumInfo.put("movies", albumImageDetails);

		anim = new AlphaAnimation(1, 0.2f);
		anim.setDuration(5000);
		// textView.startAnimation (anim);
		// anim.setFillAfter(true);

		// animation = new AnimationSet(false); // change to false
		// animation.addAnimation(animFadein);
		// animation.addAnimation(anim);

		this.thumbnailRL = thumbnailRL;
		this.albumName = albumName;
		this.thumbnailGrid = thumbnailGrid;
		this.enlargeImage = enlargeImage;
	}

	public int getAlbumSize() {
		return 1;
	}

	public int getAlbumLayoutWidth() {
		return layoutWidth;
	}

	public int getAlbumLayoutHeight() {
		return layoutHeight;
	}

	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	public void BuildAlbums() {
		// get all albums in db here
		Iterator<String> albumSetIterator = albumInfo.keySet().iterator();

		inSlideShowMode = false;
		while (albumSetIterator.hasNext()) {
			LinearLayout albumObj = this
					.ConstructAlbumGridLinearLayout(albumSetIterator.next());
			albumObj.setFocusable(true);
			albumObj.setClickable(true);
			MainActivity.gl.addView(albumObj);
		}
	}

	public LinearLayout ConstructAlbumGridLinearLayout(final String sourceName) {
		final HashMap<Integer, String> selectedAlbum = albumInfo
				.get(sourceName);

		LinearLayout albumLayout = new LinearLayout(context);
		RelativeLayout albumDetailsLayout = new RelativeLayout(context);
		TextView albumName = new TextView(context);
		// ImageView albumIcon = new ImageView(context.getApplicationContext());
		TextView albumPicturesNumber = new TextView(context);
		RelativeLayout pictureHolder = new RelativeLayout(context);
		GridLayout.LayoutParams glParams = new GridLayout.LayoutParams();
		glParams.width = layoutWidth;
		glParams.height = layoutHeight;
		glParams.setMargins(7, 7, 0, 0);
		albumLayout.setLayoutParams(new GridLayout.LayoutParams(glParams));

		pictureHolder.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 132));
		albumLayout.setOrientation(LinearLayout.VERTICAL);

		albumDetailsLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		RelativeLayout.LayoutParams rlName = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rlName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		albumName.setLayoutParams(rlName);
		albumName.setText("Family");
		albumName.setTextColor(Color.BLACK);

		RelativeLayout.LayoutParams rlSize = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rlSize.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		albumPicturesNumber.setLayoutParams(rlSize);
		albumPicturesNumber.setText(String.valueOf(selectedAlbum.size()));
		albumPicturesNumber.setTextColor(Color.BLACK);
		Typeface italicTypeface = Typeface.defaultFromStyle(Typeface.ITALIC);
		albumPicturesNumber.setTypeface(italicTypeface);

		Iterator<Integer> keySetIterator = selectedAlbum.keySet().iterator();

		int i = 0;
		while (keySetIterator.hasNext() && (i < 4)) {
			Integer key = keySetIterator.next();
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					92, 66);
			ImageView albumPic = new ImageView(context);
			albumPic.setAdjustViewBounds(true);
			albumPic.setScaleType(ScaleType.CENTER_CROP);
			albumPic.setImageResource(getResourceId(context,
					selectedAlbum.get(key), "drawable"));

			albumPic.setId(key);
			switch (i % 4) {
			case 0:
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				break;
			case 1:
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				break;
			case 2:
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				break;
			case 3:
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				break;
			}
			albumPic.setLayoutParams(layoutParams);
			pictureHolder.addView(albumPic);
			++i;
		}

		albumLayout.setBackgroundResource(R.drawable.album);

		albumDetailsLayout.addView(albumName);
		albumDetailsLayout.addView(albumPicturesNumber);
		albumDetailsLayout.setPadding(0, 5, 0, 0);

		albumLayout.addView(pictureHolder);
		albumLayout.addView(albumDetailsLayout);

		albumLayout.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View album, boolean hasFocus) {
				// TODO Auto-generated method stub
				LinearLayout albumLinear = (LinearLayout) album;
				if (hasFocus)
					albumLinear.setBackgroundResource(R.drawable.albumon);
				else
					albumLinear.setBackgroundResource(R.drawable.album);
			}
		});

		albumLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "clicked" + sourceName,
						Toast.LENGTH_SHORT).show();
				ConstructAlbumImages(sourceName);
			}
		});

		return albumLayout;
	}

	public void ConstructAlbumImages(String sourceName) {
		// get album images based on sourcename from album hashmap
		final HashMap<Integer, String> selectedAlbum = albumInfo
				.get(sourceName);
		Iterator<Integer> keySetIterator = selectedAlbum.keySet().iterator();

		inSlideShowMode = false;
		albumName.setText("Family");
		GridLayout.LayoutParams glParams = new GridLayout.LayoutParams();
		glParams.width = layoutWidth;
		glParams.height = layoutHeight;
		while (keySetIterator.hasNext()) {
			final Integer key = keySetIterator.next();
			LinearLayout thumbNailLayout = new LinearLayout(context);
			thumbNailLayout.setLayoutParams(new GridLayout.LayoutParams(
					glParams));
			thumbNailLayout.setPadding(7, 7, 0, 0);
			thumbNailLayout.setFocusable(true);
			thumbNailLayout.setClickable(true);
			// create image
			ImageView imgThumbNail = new ImageView(context);
			imgThumbNail.setAdjustViewBounds(true);
			imgThumbNail.setScaleType(ScaleType.CENTER_CROP);
			imgThumbNail.setImageResource(getResourceId(context,
					selectedAlbum.get(key), "drawable"));
			imgThumbNail.setLayoutParams(thumbNailLayout.getLayoutParams());
			imgThumbNail.setId(key);
			thumbNailLayout.setBackgroundResource(R.drawable.photo);
			thumbNailLayout.addView(imgThumbNail);
			thumbNailLayout
					.setOnFocusChangeListener(new OnFocusChangeListener() {

						@Override
						public void onFocusChange(View imageLayout,
								boolean hasFocus) {
							// TODO Auto-generated method stub
							SetBackgroundThumbnail(imageLayout);

							LinearLayout layout = (LinearLayout) imageLayout;
							if (hasFocus)
								layout.setBackgroundResource(R.drawable.thumb);
							else
								layout.setBackgroundResource(R.drawable.photo);
						}
					});

			thumbNailLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View imageLayout) {
					// TODO Auto-generated method stub
					inSlideShowMode = true;

					// disable layout
					thumbnailGrid.setVisibility(View.GONE);

					// enlarge image
					int id = getResourceId(context, selectedAlbum.get(key),
							"drawable");
					enlargedImageId = key;
					zoomImageFromThumb(imageLayout, id);
					MainActivity.imageWrapper.setVisibility(View.VISIBLE);
					// MainActivity.EnableGalleryButtons(true);
					MainActivity.btnPanelLayout.startAnimation(anim);
					anim.setFillAfter(true);
					// MainActivity.btnPanelLayout.getChildAt(0).requestFocus();
				}
			});

			thumbnailGrid.addView(thumbNailLayout);
		}

		InitGalleryButtons(sourceName);

		// Retrieve and cache the system's default "short" animation time.
		mShortAnimationDuration = context.getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		thumbnailRL.setVisibility(View.VISIBLE);
		RelativeLayout thumbLayout = (RelativeLayout) thumbnailRL.getChildAt(0);

		GridLayout thumbGrid = (GridLayout) thumbLayout.getChildAt(1);
		thumbGrid.getChildAt(0).requestFocus();
		MainActivity.gl.setVisibility(View.GONE);
	}

	public void SetBackgroundThumbnail(View view) {
		Log.v("on focus", "called");
		LinearLayout imageLinearLayout = (LinearLayout) view;
		ImageView imageThumbnail = (ImageView) imageLinearLayout.getChildAt(0);

		int id = getResourceId(context,
				albumImageDetails.get(imageThumbnail.getId()), "drawable");
		thumbnailRL.setBackgroundResource(id);
		// thumbnailRL.setBackgroundColor(Color.MAGENTA);
	}

	public void InitGalleryButtons(final String sourceName) {

		// get the 3 buttons
		MainActivity.btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// get next sibling of the current layout
				HashMap<Integer, String> selectedAlbum = albumInfo
						.get(sourceName);
				ChangeToNextImage(selectedAlbum);
			}
		});

		MainActivity.btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!isPlayPressed) {
					isPlayPressed = true;
					MainActivity.btnPlay
							.setBackgroundResource(R.drawable.sspause);
					HashMap<Integer, String> selectedAlbum = albumInfo
							.get(sourceName);
					StartSlideShow(selectedAlbum);
				} else {
					isPlayPressed = false;
					MainActivity.btnPlay
							.setBackgroundResource(R.drawable.ssplay);
				}
			}
		});

		MainActivity.btnPrev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// get next sibling of the current layout
				HashMap<Integer, String> selectedAlbum = albumInfo
						.get(sourceName);
				ChangeToPreviousImage(selectedAlbum);

			}
		});

		MainActivity.btnPanelLayout
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if (hasFocus && currentButtonState == 1) {
							MainActivity.btnPanelLayout
									.setVisibility(View.INVISIBLE);
						}
					}
				});
	}

	public static int getResourceId(Context context, String name,
			String resourceType) {
		return context.getResources().getIdentifier(name, resourceType,
				context.getPackageName());
	}

	public void ChangeToNextImage(HashMap<Integer, String> selectedAlbum) {
		Iterator<Integer> keySetIterator = selectedAlbum.keySet().iterator();
		while (keySetIterator.hasNext()) {
			if (keySetIterator.next() == enlargedImageId)
				break;
		}
		if (keySetIterator.hasNext()) {
			enlargedImageId = keySetIterator.next();
			enlargeImage.setImageResource(getResourceId(context,
					selectedAlbum.get(enlargedImageId), "drawable"));
		}
	}

	public void ChangeToPreviousImage(HashMap<Integer, String> selectedAlbum) {
		Iterator<Integer> keySetIterator = selectedAlbum.keySet().iterator();
		int prevImageId = -1;
		if (keySetIterator.hasNext()) {
			prevImageId = keySetIterator.next();
		}
		if (prevImageId != enlargedImageId && (prevImageId != -1)) {
			while (keySetIterator.hasNext()) {
				int currentImageId = keySetIterator.next();
				if (currentImageId == enlargedImageId)
					break;
				prevImageId = currentImageId;
			}
			enlargedImageId = prevImageId;
			enlargeImage.setImageResource(getResourceId(context,
					selectedAlbum.get(enlargedImageId), "drawable"));
		}
	}

	public void StartSlideShow(HashMap<Integer, String> selectedAlbum){
		Iterator<Integer> keySetIterator = selectedAlbum.keySet()
				.iterator();
		ArrayList<Integer> imageIds = new ArrayList<Integer>();
		while (keySetIterator.hasNext()) {
			if (keySetIterator.next() == enlargedImageId){
				imageIds.add(getResourceId(context,
						selectedAlbum.get(enlargedImageId), "drawable"));
				break;
			}			
		}
		while (keySetIterator.hasNext()){
			imageIds.add(getResourceId(context,
					selectedAlbum.get(keySetIterator.next()), "drawable"));
		}
		AlphaAnimation animFadeOut = new AlphaAnimation(1, 0.0f);
		animFadeOut.setDuration(2000);
		MainActivity.btnPanelLayout.startAnimation(animFadeOut);
		animFadeOut.setFillAfter(true);
		animate(enlargeImage, imageIds, 0, false);
		MainActivity.btnPanelLayout.startAnimation(anim);
		anim.setFillAfter(true);
		
		//enlargeImage.setImageResource(imageIds.get(0));
	}
	
	private void animate(final ImageView imageView, final ArrayList<Integer> images, final int imageIndex, final boolean forever) {

		  //imageView <-- The View which displays the images
		  //images[] <-- Holds R references to the images to display
		  //imageIndex <-- index of the first image to show in images[] 
		  //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

		    int fadeInDuration = 500; // Configure time values here
		    int timeBetween = 3000;
		    int fadeOutDuration = 1000;

		    imageView.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
		    imageView.setImageResource(images.get(imageIndex));

		    Animation fadeIn = new AlphaAnimation(0, 1);
		    fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
		    fadeIn.setDuration(fadeInDuration);

		    Animation fadeOut = new AlphaAnimation(1, 0);
		    fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
		    fadeOut.setStartOffset(fadeInDuration + timeBetween);
		    fadeOut.setDuration(fadeOutDuration);

		    AnimationSet animation = new AnimationSet(false); // change to false
		    animation.addAnimation(fadeIn);
		    animation.addAnimation(fadeOut);
		    animation.setRepeatCount(1);
		    imageView.setAnimation(animation);

		    animation.setAnimationListener(new AnimationListener() {
		        public void onAnimationEnd(Animation animation) {
		            if (images.size() - 1 > imageIndex) {
		                animate(imageView, images, imageIndex + 1,forever); //Calls itself until it gets to the end of the array
		            }
		            else {
		                if (forever == true){
		                animate(imageView, images, 0,forever);  //Calls itself to start the animation all over again in a loop if forever = true
		                }
		                enlargeImage.setImageResource(images.get(0));
		            }
		        }
		        public void onAnimationRepeat(Animation animation) {
		            // TODO Auto-generated method stub
		        }
		        public void onAnimationStart(Animation animation) {
		            // TODO Auto-generated method stub
		        }
		    });
		}

	// zoom image
	private void zoomImageFromThumb(final View thumbView, int imageResId) {
		// If there's an animation in progress, cancel it
		// immediately and proceed with this one.
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
		final ImageView expandedImageView = enlargeImage;
		expandedImageView.setImageResource(imageResId);

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the final bounds are the global visible rectangle of the
		// container
		// view. Also set the container view's offset as the origin for the
		// bounds, since that's the origin for the positioning animation
		// properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);
		thumbnailRL.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
				.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins, it will position the zoomed-in view in the place of the
		// thumbnail.
		thumbView.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations
		// to the top-left corner of the zoomed-in view (the default
		// is the center of the view).
		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(
				ObjectAnimator.ofFloat(expandedImageView, View.X,
						startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
						startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
						startScale, 1f))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
						startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		// Upon clicking the zoomed-in image, it should zoom back down
		// to the original bounds and show the thumbnail instead of
		// the expanded image.
		final float startScaleFinal = startScale;
		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their original values.
				AnimatorSet set = new AnimatorSet();
				set.play(
						ObjectAnimator.ofFloat(expandedImageView, View.X,
								startBounds.left))
						.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
								startBounds.top))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_X, startScaleFinal))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub

		if (arg0 == animFadeout) {
			// hide buttons
			currentButtonState = 1;
			// MainActivity.btnPanelLayout.setVisibility(View.INVISIBLE);
			// MainActivity.btnPanelLayout.requestFocus();
		}
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub

	}
}
