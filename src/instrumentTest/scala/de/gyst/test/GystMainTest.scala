package de.gyst.test

import android.test.ActivityInstrumentationTestCase2
import android.widget.TextView
import junit.framework.Assert._
import de.gyst.GystMain
import de.gyst.R

class GystMainTest extends ActivityInstrumentationTestCase2[GystMain](classOf[GystMain]) {
  lazy val activity = getActivity
//  lazy val tv = activity.findViewById(R.id.tv).asInstanceOf[TextView]

  override def setUp(): Unit = {
    super.setUp()
    setActivityInitialTouchMode(false)
    assertNull(activity)
//    assert(tv!=null)
  }

  def testPreConditions() = {
//    assertEquals("Shit", tv.getText.toString)
  }
}
