package com.yixun.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.WindowManager;

/**
* 处理图片的工具类.
* 
*/
public class ImageTools {
   
    /**
     * 图片去色,返回灰度图片
     * @param bmpOriginal 传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();    

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
       Canvas c = new Canvas(bmpGrayscale);
       Paint paint = new Paint();
       ColorMatrix cm = new ColorMatrix();
       cm.setSaturation(0);
       ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
       paint.setColorFilter(f);
       c.drawBitmap(bmpOriginal, 0, 0, paint);
       return bmpGrayscale;
   }
   
   
   /**
    * 去色同时加圆角
    * @param bmpOriginal 原图
    * @param pixels 圆角弧度
    * @return 修改后的图片
    */
   public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
       return toRoundCorner(toGrayscale(bmpOriginal), pixels);
   }
   
   /**
    * 把图片变成圆角
    * @param bitmap 需要修改的图片
    * @param pixels 圆角的弧度
    * @return 圆角图片
    */
   public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

       Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
               .getHeight(), Config.ARGB_8888);
       Canvas canvas = new Canvas(output);

       final int color = 0xff424242;
       final Paint paint = new Paint();
       final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
       final RectF rectF = new RectF(rect);
       final float roundPx = pixels;

       paint.setAntiAlias(true);
       canvas.drawARGB(0, 0, 0, 0);
       paint.setColor(color);
       canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

       paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
       canvas.drawBitmap(bitmap, rect, rect, paint);

       return output;
   }

   
   /**
    * 使圆角功能支持BitampDrawable
    * @param bitmapDrawable 
    * @param pixels 
    * @return
    */
   public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int pixels) {
     Bitmap bitmap = bitmapDrawable.getBitmap();
     bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
     return bitmapDrawable;
 }
   //压缩图片的大小O
   public static Bitmap minBitmap(Context con,int id){
   	BitmapFactory.Options opts = new BitmapFactory.Options();
   	opts.inSampleSize = 4; //这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰
   	 Bitmap bitmap = null;
   	//返回原图解码之后的bitmap对象
   	bitmap = BitmapFactory.decodeResource(con.getResources(),id, opts);
   	return bitmap;
   }
 //压缩图片的大小O
  /* public static Bitmap minBitmap(Context con,String path){
   	return minBitmap(con,path,4)
   }*/
   public static Bitmap minBitmap(Context con,String path,int scale){
	   	BitmapFactory.Options opts = new BitmapFactory.Options();
	   	opts.inSampleSize = scale; //这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰
	   	//返回原图解码之后的bitmap对象
	   	return BitmapFactory.decodeFile(path, opts);
	   }
   
  //根据屏幕大小适配图片
  /* public static Bitmap adaptive(Activity context,Bitmap bitmap) {

		WindowManager wm = ((Activity) context).getWindowManager();
		// 背景缩放
		float scalX = wm.getDefaultDisplay().getWidth();// 屏宽
		float scalY = wm.getDefaultDisplay().getHeight();// 屏高
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();// 获取资源位图的宽
		int height = bitmap.getHeight();// 获取资源位图的高
		float w = scalX / 720;
		float h = scalY / 1280;
		matrix.postScale(w, h);// 获取缩放比例
		// 根据缩放比例获取新的位图
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}*/
   
}

