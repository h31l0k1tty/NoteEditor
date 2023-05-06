package com.semenova.practice.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

class BitmapConverter {
    companion object {
        const val REQUEST_TAKE_PHOTO = 0

        fun bitmapToString(bitmap: Bitmap?): String {
            bitmap ?: return ""
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT).replace("\n","")
        }
        
        @Throws(IllegalArgumentException::class)
        fun stringToBitmap(base64Str: String): Bitmap? {
            val decodedBytes: ByteArray = android.util.Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                android.util.Base64.DEFAULT
            )
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }

        fun getQrCodeBitmap(ssid: String): Bitmap {
            val size = 512
            val qrCodeContent = ssid
            val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 }
            val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
            return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
                for (x in 0 until size) {
                    for (y in 0 until size) {
                        it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
            }
        }
    }

}