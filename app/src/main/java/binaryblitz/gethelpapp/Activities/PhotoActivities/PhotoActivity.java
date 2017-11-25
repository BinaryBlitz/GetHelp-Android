package binaryblitz.gethelpapp.Activities.PhotoActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import binaryblitz.gethelpapp.Custom.ProgressDialog;
import binaryblitz.gethelpapp.Data.Message;
import binaryblitz.gethelpapp.R;
import binaryblitz.gethelpapp.Server.GettHelpServerRequest;
import binaryblitz.gethelpapp.Utils;

public class PhotoActivity extends FragmentActivity {
    public static Uri mImageUri;

    PagerAdapter mPagerAdapter;
    ViewPager mPager;
    public static ArrayList<Message> photos;
    public static int index;

    private void saveImage(Bitmap finalBitmap, ProgressDialog dialog) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);

        mPager = (ViewPager) findViewById(R.id.pager);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(index);
        mPager.setOffscreenPageLimit(5);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.date_text_view)).setText((index + 1) +
                " из " + photos.size());

        findViewById(R.id.drawer_indicatorfdfd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageLoader.getInstance().loadImage(GettHelpServerRequest.baseUrl + photos.get(index).getPhotoUrl(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        ImageLoader.getInstance().loadImage("file:///" + photos.get(index).getPhotoUrl(), new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                String path = MediaStore.Images.Media.insertImage(getContentResolver(), loadedImage, "", null);
                                Uri screenshotUri = Uri.parse(path);
                                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                                intent.setType("image/*");
                                startActivity(Intent.createChooser(intent, "Share image via..."));
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), loadedImage, "", null);
                        Uri screenshotUri = Uri.parse(path);
                        intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        intent.setType("image/*");
                        startActivity(Intent.createChooser(intent, "Share image via..."));
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

            }
        });

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((TextView) findViewById(R.id.date_text_view)).setText((position + 1) +
                        " из " + photos.size());
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PhotoFragment fragment = new PhotoFragment();
            fragment.setLink(photos.get(position).getPhotoUrl());
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public int getCount() {
            return photos.size();
        }
    }
}
