# RectangleProgressView
## 矩形倒计时进度框 

这是一个矩形进度框，不过不是正着走的，是倒着走的<br/>
###效果图：
![矩形倒计时框](http://i.imgur.com/Ep52aQV.gif)

###使用：
可以直接在布局中使用，并设置宽高！
<br/>
然后在代码中获取对象后，调用start方法，传入要倒计时的秒数！即可开始倒计时！
<br/>
方法：<br/>

	public void start(int time)	// 开始
	public void stop()			// 停止
	public void pause()			// 暂停-*暂时不可调用
	public int getCurrentSecond()// 获取当前秒数(已经过去的描述)
	public int getTotalSecond()	// 获取总共秒数
	public void setThickness(float thickness)// 设置框厚度	
	public void setStartAngle(int startAngle)// 设置开始角度(默认-90)
	public void setColor(int color)// 设置框颜色(会实时改变)
	public void setProgressListener(progressListener listener)// 设置回调，回调方法start、over、progress(int total,float progress)
