class VerticalViewPager : ViewPager {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setPageTransformer(true, VerticalPageTransformer())
        overScrollMode = OVER_SCROLL_NEVER
    }

    private inner class VerticalPageTransformer : PageTransformer {
        override fun transformPage(page: View, position: Float) {

            //now check positions
            if (position < -1) {
                //[-infinite, -1] ; if this page is way off screen to the left
                page.alpha = 0f
            } else if (position <= 0) {
                //[-1,0] ; use the default slide transition when moving to the left
                page.alpha = 1f
                //counteract the default slide transition
                page.translationX = page.width * -position

                //set Y position to swipe in from top
                val yPosition = position * page.height
                page.translationY = yPosition
                page.scaleX = 1f
                page.scaleY = 1f
            } else if (position <= 1) {
                //[0,1] ; counteract the defauly slide transition
                page.translationX = page.width * -position
                //to scalethe page down
                val scale = 0.75f + (1 - 0.75f) * (1 - Math.abs(position))
                page.scaleX = scale
                page.scaleY = scale
            } else {
                //[1, +infinity] ; this page way off screen to the right
                page.alpha = 0f
            }
        }
    }

    private fun swapXYCordinates(event: MotionEvent): MotionEvent {
        //now swap x and y coordinates using this
        val width = width.toFloat()
        val height = height.toFloat()
        val newX = event.y / height * width
        val newY = event.x / width * height
        event.setLocation(newX, newY)
        return event
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val intercepted = super.onInterceptTouchEvent(swapXYCordinates(ev))
        swapXYCordinates(ev)
        return intercepted
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(swapXYCordinates(ev))
    }
}
