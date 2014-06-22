package de.gyst

import android.app.Activity
import android.os.Bundle
import android.widget.{Button, TextView, LinearLayout}
import macroid._
import macroid.FullDsl._
import macroid.contrib.ExtraTweaks._
import android.view.{View, Gravity}
import android.view.ViewGroup.LayoutParams._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext


// define our helpers in a mixable trait
trait Styles {
  // sets text, large font size and a long click handler
  def caption(cap: String)(implicit ctx: AppContext): Tweak[TextView] =
    text(cap) + TextSize.large +  On.longClick(
      for {
      _ <- toast("I’m a caption") <~ gravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL) <~ fry
    } yield true)
}


class GystMain extends Activity with Styles with Contexts[Activity]{
  implicit val executionContext = ExecutionContext.fromExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR)
  // prepare a variable to hold our text view
  var cap = slot[TextView]
  var layout = slot[LinearLayout]

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    // this will be a linear layout
    val view = l[LinearLayout](
      w[TextView] <~
        caption("Howdy?") <~
        // assign to cap
        wire(cap),

      // a button
      w[Button] <~
        text("Click me!") <~
        layoutParams[LinearLayout](MATCH_PARENT, WRAP_CONTENT) <~
        On.click {
          // with <@~ we can apply snails like `delay`
          // tweaks coming after them will wait till they finish
          cap <~ text("Button clicked!") <@~ delay(1000) <~ text("Howdy")
        }
    ) <~
      // match layout orientation to screen orientation
      (portrait ? vertical | horizontal) <~~ Transformer {
      // ~~> applies the transformer to all children, grand-children, ...
      // here we set a padding of 4 dp for all inner views
      case x: View => x <~ padding(all = 4 dp)
    } <~ wire(layout)

    setContentView(getUi(view))
    layout <@~ anim(Animations.bumpIn())

  }
}
