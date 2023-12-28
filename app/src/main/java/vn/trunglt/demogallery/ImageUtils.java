package vn.trunglt.demogallery;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

public class ImageUtils {

    public static Bitmap decodeSampledBitmap(Context context, String imagePath, int reqHeight) {
        // Lấy thông tin về mật độ điểm ảnh của thiết bị
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int reqWidth = (int) (reqHeight * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        // Tạo đối tượng BitmapFactory.Options để đọc thông tin về ảnh mà không phải đọc toàn bộ ảnh vào bộ nhớ
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Tính toán tỉ lệ giảm kích thước mẫu
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Thiết lập lại inJustDecodeBounds để đọc toàn bộ ảnh vào bộ nhớ với kích thước giảm
        options.inJustDecodeBounds = false;

        // Đọc ảnh vào Bitmap sử dụng thông số cấu hình đã được thiết lập
        return BitmapFactory.decodeFile(imagePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Chiều rộng và chiều cao của ảnh
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 1;
            final int halfWidth = width / 1;

            // Tính toán inSampleSize theo chiều rộng hoặc chiều cao lớn hơn yêu cầu
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 8;
            }
        }

        return inSampleSize;
    }
}
