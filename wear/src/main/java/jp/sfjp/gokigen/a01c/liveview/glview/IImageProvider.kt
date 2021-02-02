package jp.sfjp.gokigen.a01c.liveview.glview

import android.graphics.Bitmap

interface IImageProvider
{
    fun getImage() : Bitmap?
}
