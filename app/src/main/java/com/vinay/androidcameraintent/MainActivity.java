package com.vinay.androidcameraintent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

	private static final int ACTIVITY_START_CAMERA_APP = 0;
	private ImageView mPhotoCapturedImageView;
	private String mImageFileLocation = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);
	}


	public void takePhoto(View view) {
//		Toast.makeText(MainActivity.this, "camera button pressed", Toast.LENGTH_SHORT).show();
		Intent callCameraApplicationIntent = new Intent();
		callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

		File photofile = null;
		try {
			photofile = createImageFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photofile));
		startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {

//			Toast.makeText(MainActivity.this, "Picture Taken Successfully", Toast.LENGTH_SHORT)
// .show();
//			Bundle extras = data.getExtras();
//			Bitmap photoCapturedBitmap = (Bitmap) extras.get("data");
//			mPhotoCapturedImageView.setImageBitmap(photoCapturedBitmap);
//			Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation);
//			mPhotoCapturedImageView.setImageBitmap(photoCapturedBitmap);
			rotateImage(setReduceImageSize());
		}
	}

	File createImageFile() throws IOException {

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "IMAGE_" + timeStamp + "_";
		File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment
				.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);
		mImageFileLocation = image.getAbsolutePath();

		return image;

	}

	private Bitmap setReduceImageSize() {
		int targetImageViewWidth = mPhotoCapturedImageView.getWidth();
		int targetImageViewHeight = mPhotoCapturedImageView.getHeight();

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
		int cameraImageWidth = bmOptions.outWidth;
		int cameraImageHeight = bmOptions.outHeight;

		int scaleFactor = Math.min(cameraImageWidth / targetImageViewWidth, cameraImageHeight /
				targetImageViewHeight);
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inJustDecodeBounds = false;

//		Bitmap photoReduceSizeBitmap = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
//		mPhotoCapturedImageView.setImageBitmap(photoReduceSizeBitmap);

		return BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
	}

	private void rotateImage(Bitmap bitmap) {
		ExifInterface exifInterface = null;

		try {
			exifInterface = new ExifInterface(mImageFileLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_UNDEFINED);
		Matrix matrix = new Matrix();
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.setRotate(90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.setRotate(180);
				break;
			default:
		}
		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap
				.getHeight(), matrix, true);
		mPhotoCapturedImageView.setImageBitmap(rotatedBitmap);
	}
}
