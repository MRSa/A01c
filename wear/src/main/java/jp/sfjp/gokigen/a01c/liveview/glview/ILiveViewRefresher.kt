package jp.sfjp.gokigen.a01c.liveview.glview

interface ILiveViewRefresher
{
    fun refresh()
    fun resetView()
    fun moveView(x : Float, y : Float)
    fun setScaleFactor(scaleFactor : Float)
    fun setImageProvider(provider: IImageProvider)
}
