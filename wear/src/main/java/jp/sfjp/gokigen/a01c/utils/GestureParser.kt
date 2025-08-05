package jp.sfjp.gokigen.a01c.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import jp.sfjp.gokigen.a01c.liveview.glview.ILiveViewRefresher

class GestureParser(context : Context, private val imageView : ILiveViewRefresher) : GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener
{
    private val gestureDetector = GestureDetector(context, this)
    private val scaleGestureDetector = ScaleGestureDetector(context, this)

    // View.OnTouchListener
    fun onTouch(event: MotionEvent): Boolean
    {
        return (gestureDetector.onTouchEvent(event) || scaleGestureDetector.onTouchEvent(event))
    }

    // GestureDetector.OnGestureListener
    override fun onDown(e: MotionEvent): Boolean
    {
        //Log.v(TAG, " Gesture onDown")
        return (false)
    }

    // GestureDetector.OnGestureListener
    override fun onShowPress(e: MotionEvent)
    {
        //Log.v(TAG, " Gesture onShowPress")
    }

    // GestureDetector.OnGestureListener
    override fun onSingleTapUp(e: MotionEvent): Boolean
    {
        //Log.v(TAG, " Gesture onSingleTapUp")
        return (false)
    }

    // GestureDetector.OnGestureListener
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean
    {
        //Log.v(TAG, " Gesture onScroll")
        imageView.moveView(distanceX, distanceY)
        return (false)
    }

    // GestureDetector.OnGestureListener
    override fun onLongPress(e: MotionEvent)
    {
        //Log.v(TAG, " Gesture onLongPress")
        imageView.resetView()
    }

    // GestureDetector.OnGestureListener
    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean
    {
        //Log.v(TAG, " Gesture onFling")
        return (false)
    }

    // ScaleGestureDetector.OnScaleGestureListener
    override fun onScale(detector: ScaleGestureDetector): Boolean
    {
        //Log.v(TAG, " Gesture onScale")
        try
        {
            imageView.setScaleFactor(detector.scaleFactor)
            return (true)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    // ScaleGestureDetector.OnScaleGestureListener
    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean
    {
        //Log.v(TAG, " Gesture onScaleBegin")
        return (true)
    }

    // ScaleGestureDetector.OnScaleGestureListener
    override fun onScaleEnd(detector: ScaleGestureDetector)
    {
        //Log.v(TAG, " Gesture onScaleEnd")
    }
}
