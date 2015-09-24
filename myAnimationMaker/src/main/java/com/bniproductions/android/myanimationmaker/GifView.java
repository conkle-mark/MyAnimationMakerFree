
package com.bniproductions.android.myanimationmaker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GifView extends View {
	
	private String DTAG = "GifView";
	
    public static final int IMAGE_TYPE_UNKNOWN = 0;
    public static final int IMAGE_TYPE_STATIC = 1;
    public static final int IMAGE_TYPE_DYNAMIC = 2;

    public static final int DECODE_STATUS_UNDECODE = 0;
    public static final int DECODE_STATUS_DECODING = 1;
    public static final int DECODE_STATUS_DECODED = 2;
    
    private GifDecoder decoder;
    private Bitmap bitmap;

    public int imageType = IMAGE_TYPE_UNKNOWN;
    public int decodeStatus = DECODE_STATUS_UNDECODE;

    private int width;
    private int height;

    private long time;
    private int index;

    private int resId;
    private String filePath;

    private boolean playFlag = false;

    private LruCache<String, Bitmap> mMemoryCache;
    
    /*
     * constructors
     */
	public GifView(Context context) {

        super(context);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
	}

	public GifView(Context context, AttributeSet attrs) {

        super(context, attrs);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
	}

	public GifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
	}
	
    private InputStream getInputStream() {
        if (filePath != null)
                try {
                        return new FileInputStream(filePath);
                } catch (FileNotFoundException e) {
                }
        if (resId > 0)
                return getContext().getResources().openRawResource(resId);
        return null;
    }
    
    /**
     * set gif file path
     * 
     * @param filePath
     */
    public void setGif(String filePath) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            setDimensions(bitmap);
            setGif(filePath, bitmap);
    }
    
    private void setDimensions(Bitmap bm){
        Log.d(DTAG, "bm.getWidth() "+bm.getWidth());
    	width = bm.getWidth();
    	height = bm.getHeight();
    }
    
    /**
     * set gif file path and cache image
     * 
     * @param filePath
     * @param cacheImage
     */
    public void setGif(String filePath, Bitmap cacheImage) {
            this.resId = 0;
            this.filePath = filePath;
            imageType = IMAGE_TYPE_UNKNOWN;
            decodeStatus = DECODE_STATUS_UNDECODE;
            playFlag = false;
            bitmap = cacheImage;
            //setLayoutParams(new LayoutParams(width, height));
    }
    
    private void decode() {
        release();
        index = 0;
        Log.d(DTAG, "decode");
        decodeStatus = DECODE_STATUS_DECODING;

        new Thread() {
                @Override
                public void run() {
                        decoder = new GifDecoder();
                        decoder.read(getInputStream());
                        if (decoder.width == 0 || decoder.height == 0) {
                                imageType = IMAGE_TYPE_STATIC;
                        } else {
                                imageType = IMAGE_TYPE_DYNAMIC;
                        }
                        postInvalidate();
                        time = System.currentTimeMillis();
                        decodeStatus = DECODE_STATUS_DECODED;
                }
        }.start();
    }
    
    public void release() {
        decoder = null;
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (decodeStatus == DECODE_STATUS_UNDECODE) {
            canvas.drawBitmap(bitmap, 0, 0, null);
            if (playFlag) {
                    decode();
                    invalidate();
            }
		} else if (decodeStatus == DECODE_STATUS_DECODING) {
            canvas.drawBitmap(bitmap, 0, 0, null);
            invalidate();
		} else if (decodeStatus == DECODE_STATUS_DECODED) {
            if (imageType == IMAGE_TYPE_STATIC) {
                    canvas.drawBitmap(bitmap, 0, 0, null);
            } else if (imageType == IMAGE_TYPE_DYNAMIC) {
                    if (playFlag) {
                            long now = System.currentTimeMillis();

                            if (time + decoder.getDelay(index) < now) {
                                    time += decoder.getDelay(index);
                                    incrementFrameIndex();
                            }
                            Bitmap bm = getBitmapFromMemCache(Integer.toString(index));
                            Bitmap bitmap;
                            if(bm == null) {
                                bitmap = decoder.getFrame(index);
                                addBitmapToMemoryCache((Integer.toString(index)),bitmap);
                                Log.d(DTAG, "onDraw decoder.getFrame - index: "+index);
                            }else{
                                bitmap = getBitmapFromMemCache(Integer.toString(index));
                            }

                            if (bitmap != null) {
                                    canvas.drawBitmap(bitmap, 0, 0, null);
                            }
                            invalidate();
                    } else {
                            Bitmap bitmap = decoder.getFrame(index);
                            if(bitmap != null) {
                                canvas.drawBitmap(bitmap, 0, 0, null);
                            }
                    }
            } else {
                    canvas.drawBitmap(bitmap, 0, 0, null);
            }
		}
	}

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            if(bitmap != null) {
                mMemoryCache.put(key, bitmap);
            }
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
	
	private void incrementFrameIndex() {
        index++;
        int frame_count = decoder.getFrameCount();
        if (index >= frame_count) {
                index = 0;
        }
        //Log.d(DTAG, "frame_count: "+frame_count);
	}
	
	private void decrementFrameIndex() {
        index--;
        if (index < 0) {
                index = decoder.getFrameCount() - 1;
        }
	}

	public void play() {
        time = System.currentTimeMillis();
        playFlag = true;
        invalidate();
	}

	public void pause() {
        playFlag = false;
        invalidate();
	}

	public void stop() {
        playFlag = false;
        index = 0;
        invalidate();
	}

	public void nextFrame() {
        if (decodeStatus == DECODE_STATUS_DECODED) {
                incrementFrameIndex();
                invalidate();
        }
	}

	public void prevFrame() {
        if (decodeStatus == DECODE_STATUS_DECODED) {
                decrementFrameIndex();
                invalidate();
        }
	}
	
	public boolean getPlayFlag(){
		return playFlag;
	}
	
	public int getGifWidth(){
		return width;
	}
	
	public int getGifHeight(){
		return height;
	}

}
