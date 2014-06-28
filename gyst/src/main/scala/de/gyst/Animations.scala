package de.gyst

import android.view.animation._
import android.view.animation.Animation.AnimationListener
import scala.util.Random
import android.view.View
import android.widget.TextView
import scala.concurrent.future

object Animations {
  def combineAnimations(animations: List[MarkAnimation]):MarkAnimation = {
    val as = new AnimationSet(false) with MarkAnimation
    animations.foreach(a => as.addAnimation(a))
    as
  }

  def flyEmIn(views:List[View], delay:Int=0){
    views.foreach(v => {
      v.startAnimation(scaleIntoPlace(Random.nextInt(500)+delay))
    })
  }


  def standardFadeIn(delay:Int) = fadeIn(0.2f, 250, delay)
  def standardFadeOut(delay:Int) = fadeOut(250, delay)
  def standardMoveInFromRight(delay:Int) = moveInFromRight(300, 500, delay)
  def standardMoveOutToLeft(delay:Int) = moveOutToLeft(300, 500, delay)
  def standardMoveInFromLeft(delay:Int) = moveInFromRight(-300, 500, delay)
  def standardMoveUp(delay:Int)=moveUp(300, 500, delay)

  def bumpIn(delay:Int=0):MarkAnimation = {
    val a =combineAnimations(List(
      scaleDown(0.4f, 250, delay),
      fadeIn(0.0f, 100, delay)
    ))
    a.setInterpolator(new OvershootInterpolator(1.7f))
    a
  }

  def rotate(degrees:Float, duration:Int, delay:Int=0):MarkAnimation = {
    val a = new RotateAnimation(0, degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f) with MarkAnimation
    a.setFillAfter(true)
    applyBasicAttributes(a, duration, delay)
  }

  def moveDownABitAndFadeOut(delay:Int=0):MarkAnimation = {
    val a = combineAnimations(List(
      fadeOut(350, delay),
      moveDown(200, 400, delay)
    ))
    a.setFillAfter(true)
    a
  }

  def moveDownABitAndFadeIn(delay:Int=0):MarkAnimation = {
    val a = combineAnimations(List(
      fadeIn(0, 350, delay),
      moveDown(200, 400, delay)
    ))
    a.setFillAfter(true)
    a
  }

  def scaleIntoPlace(delay:Int=0):MarkAnimation = {
    combineAnimations(List(
      scaleDown(1.5f, 400, delay),
      fadeIn(0.0f, 250, delay)
    ))
  }

  def moveInFromRightAndFade(delay:Int=0):MarkAnimation = {
    combineAnimations(List(
      standardFadeIn(delay),
      standardMoveInFromRight(delay)
    ))
  }

  def moveOutToLeftAndFade(delay:Int=0):MarkAnimation = {
    combineAnimations(List(
      standardFadeOut(delay),
      standardMoveOutToLeft(delay)
    ))
  }

  def moveInFromLeftAndFade(delay:Int=0):MarkAnimation = {
    combineAnimations(List(
      standardFadeIn(delay),
      standardMoveInFromLeft(delay)
    ))
  }

  def moveUpAndFadeIn(delay:Int=0):MarkAnimation = {
    combineAnimations(List(
      standardFadeIn(delay),
      standardMoveUp(delay)
    ))
  }

  def fadeOut(duration:Int, delay:Int):MarkAnimation ={
    val a=new AlphaAnimation(1.0f, 0.0f) with MarkAnimation
    applyBasicAttributes(a, duration, delay)
  }

  def fadeOutInWithText(startOpacity:Float, duration:Int, delay:Int, text:String, tv:TextView, view:View, viewToFadeOut:View):MarkAnimation = {
    val out = fadeOut(duration, delay)
    out.clbk(_ => {
        tv.setText(text)
        viewToFadeOut.setVisibility(View.VISIBLE)
        view.startAnimation(fadeIn(0, duration, duration))
      }
    )
    out
  }

  def scaleDown(startScale:Float, duration:Int, delay:Int):MarkAnimation = {
    val a = new ScaleAnimation(startScale, 1.0f, startScale, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f) with MarkAnimation
    applyBasicAttributes(a, duration, delay)
  }

  def fadeIn(startOpacity:Float, duration:Int, delay:Int):MarkAnimation = {
    val a = new AlphaAnimation(startOpacity, 1.0f) with MarkAnimation
    applyBasicAttributes(a, duration, delay)
  }

  def moveInFromLeft(howfar:Float, duration:Int, delay:Int):MarkAnimation = {
    val a = new TranslateAnimation(-howfar, 0.0f, 0.0f, 0.0f) with MarkAnimation
    applyBasicAttributes(a, duration, delay)
  }

  def moveInFromRight(howfar:Float, duration:Int, delay:Int):MarkAnimation = {
    val a = new TranslateAnimation(howfar, 0.0f, 0.0f, 0.0f) with MarkAnimation
    applyBasicAttributes(a, duration, delay)
  }

  def moveOutToLeft(howfar:Float, duration:Int, delay:Int):MarkAnimation = {
    val a = new TranslateAnimation(0.0f, -howfar, 0.0f, 0.0f) with MarkAnimation
    applyBasicAttributes(a, duration, delay)
  }

  def moveDown(howfar:Float, duration:Int, delay:Int):MarkAnimation = {
    val a = new TranslateAnimation(0.0f, 0.0f, 0.0f, howfar) with MarkAnimation
    applyBasicAttributes(a, duration, delay)
  }

  def moveUp(howfar:Float, duration:Int, delay:Int):MarkAnimation = {
    val a = new TranslateAnimation(0.0f, 0.0f, howfar, 0.0f) with MarkAnimation
    applyBasicAttributes(a, duration, delay)
  }

  def spinHorizontally(duration:Int, delay:Int=0):MarkAnimation = {
    val a = new ScaleAnimation(1.0f, -1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f) with MarkAnimation
    applyBasicAttributes(a, duration, delay)
    val b = new ScaleAnimation(1.0f, -1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f) with MarkAnimation
    applyBasicAttributes(b, duration, delay+duration)
    combineAnimations(List(a,b))
  }


  def applyBasicAttributes(a:MarkAnimation, duration:Int, delay:Int):MarkAnimation ={
    a.setDuration(duration)
    a.setStartOffset(delay)
    a.setInterpolator(new AccelerateDecelerateInterpolator)
    a.setRepeatCount(0)
    a
  }

  def wiggle(delay:Int):MarkAnimation = {
    val a = new TranslateAnimation(-3.5f, 3.5f, 0.0f, 0.0f) with MarkAnimation
    a.setInterpolator(new MarkWiggleInterpolator)
    a.setDuration(400)
    a.setStartOffset(delay)
    a
  }

  class MarkWiggleInterpolator() extends Interpolator {
    override def getInterpolation(t: Float): Float = {
      1.4f*Math.sin(t*6*Math.PI).asInstanceOf[Float]
    }
  }

  trait MarkAnimation extends Animation{
    val self = this
    def clbk(callback: MarkAnimation => Any):MarkAnimation = {
      this.setAnimationListener(new AnimationListener() {
        def onAnimationEnd(animation: Animation): Unit = {
          callback(self)
        }
        def onAnimationStart(animation: Animation): Unit = {}
        def onAnimationRepeat(animation: Animation): Unit = {}
      })
      this
    }
  }
}

