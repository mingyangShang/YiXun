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
* ����ͼƬ�Ĺ�����.
* 
*/
public class ImageTools {
   
    /**
     * ͼƬȥɫ,���ػҶ�ͼƬ
     * @param bmpOriginal �����ͼƬ
     * @return ȥɫ���ͼƬ
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
    * ȥɫͬʱ��Բ��
    * @param bmpOriginal ԭͼ
    * @param pixels Բ�ǻ���
    * @return �޸ĺ��ͼƬ
    */
   public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
       return toRoundCorner(toGrayscale(bmpOriginal), pixels);
   }
   
   /**
    * ��ͼƬ���Բ��
    * @param bitmap ��Ҫ�޸ĵ�ͼƬ
    * @param pixels Բ�ǵĻ���
    * @return Բ��ͼƬ
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
    * ʹԲ�ǹ���֧��BitampDrawable
    * @param bitmapDrawable 
    * @param pixels 
    * @return
    */
   public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int pixels) {
     Bitmap bitmap = bitmapDrawable.getBitmap();
     bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
     return bitmapDrawable;
 }
   //ѹ��ͼƬ�Ĵ�СO
   public static Bitmap minBitmap(Context con,int id){
   	BitmapFactory.Options opts = new BitmapFactory.Options();
   	opts.inSampleSize = 4; //�����ֵѹ���ı�����2��������������ֵԽС��ѹ����ԽС��ͼƬԽ����
   	 Bitmap bitmap = null;
   	//����ԭͼ����֮���bitmap����
   	bitmap = BitmapFactory.decodeResource(con.getResources(),id, opts);
   	return bitmap;
   }
 //ѹ��ͼƬ�Ĵ�СO
  /* public static Bitmap minBitmap(Context con,String path){
   	return minBitmap(con,path,4)
   }*/
   public static Bitmap minBitmap(Context con,String path,int scale){
	   	BitmapFactory.Options opts = new BitmapFactory.Options();
	   	opts.inSampleSize = scale; //�����ֵѹ���ı�����2��������������ֵԽС��ѹ����ԽС��ͼƬԽ����
	   	//����ԭͼ����֮���bitmap����
	   	return BitmapFactory.decodeFile(path, opts);
	   }
   
  //������Ļ��С����ͼƬ
  /* public static Bitmap adaptive(Activity context,Bitmap bitmap) {

		WindowManager wm = ((Activity) context).getWindowManager();
		// ��������
		float scalX = wm.getDefaultDisplay().getWidth();// ����
		float scalY = wm.getDefaultDisplay().getHeight();// ����
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();// ��ȡ��Դλͼ�Ŀ�
		int height = bitmap.getHeight();// ��ȡ��Դλͼ�ĸ�
		float w = scalX / 720;
		float h = scalY / 1280;
		matrix.postScale(w, h);// ��ȡ���ű���
		// �������ű�����ȡ�µ�λͼ
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}*/
   
}

