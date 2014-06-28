package de.gyst

import java.net.UnknownHostException
import java.util.Collections
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor}

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.{UserRecoverableAuthIOException, GoogleAuthIOException, GoogleAccountCredential}
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar.Builder
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.{CalendarListEntry, Calendar, CalendarList}
import de.gyst.utils.AndroidConversions

import scala.concurrent._
import scala.util.control.Exception.allCatch
import scala.util.{Failure, Success}
import scala.collection.JavaConverters._
import AndroidConversions._
import TypedResource._
import spray.json._
import DefaultJsonProtocol._

object SetupActivity {
  val REQUEST_ACCOUNT_PICKER = 2
  val REQUEST_AUTHORIZATION = 3
}

class SetupActivity extends Activity with TypedViewHolder {
  implicit lazy val ctx = this
  implicit val ec = ExecutionContext.fromExecutor(new ThreadPoolExecutor(100, 100, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable]))
  val transport = AndroidHttp.newCompatibleTransport()
  val jsonFactory = GsonFactory.getDefaultInstance
  lazy val loadCalendarsButton = findView(TR.button)

  lazy val credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR))
  lazy val client = new Builder(transport, jsonFactory, credential)
    .setApplicationName("Gyst")
    .build()

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    loadCalendarsButton.onClick(loadCalendar())
  }

  override def onResume() = {
    super.onResume()
    if(credential.getSelectedAccountName == null){
      val accountName = GystSharedPreferences.getAccountName()
      accountName.fold(chooseAccount())(credential.setSelectedAccountName(_))
    }
  }

  override def onActivityResult(requestCode: Int, resultCode:Int, data:Intent) = {
    super.onActivityResult(requestCode, resultCode, data)
    Log.e("MOTHER", s"RequestCode. $requestCode, ResultCode, $resultCode")
    requestCode match {
      case SetupActivity.REQUEST_ACCOUNT_PICKER =>
        handleAccountPickerRequest(requestCode, resultCode, data)
      case SetupActivity.REQUEST_AUTHORIZATION =>
        Log.e("MOTHER", s"Data $data")
    }
  }

  def handleAccountPickerRequest(requestCode: Int, resultCode:Int, data:Intent) = {
    val accountName = getAccountNameFromIntent(resultCode, data)
    accountName.fold(chooseAccount())(updateAccountName(_))
  }

  def updateAccountName(an:String) = {
    setAccountNameInCredential(an)
    GystSharedPreferences.setAccountName(an)
  }

  def getAccountNameFromIntent(resultCode:Int, data:Intent):Option[String] = {
    if(resultCode == Activity.RESULT_OK)
      allCatch.opt(
        data.getExtras.getString(AccountManager.KEY_ACCOUNT_NAME)
      )
    else None
  }

  def setAccountNameInCredential(an:String) = {
    credential.setSelectedAccountName(an)
  }

  def loadCalendar():Unit = {
    if(credential.getSelectedAccountName == null)  chooseAccount()
    else {
      val feed = client.calendars().get("gyst-gyst-gyst")
      val itemsFuture = Future {
        feed.execute()
      }
      itemsFuture onComplete {
        case Success(result) =>
//          getGystCalendar(result)
          if(result!=null) {
            Log.e("MOTHER", result.getId().toString)
          }
        case Failure(e:UserRecoverableAuthIOException) =>
          startActivityForResult(e.getIntent, SetupActivity.REQUEST_AUTHORIZATION)
        case Failure(t:GoogleAuthIOException) =>
          Log.e("MOTHER", "AuthIOException " + t.getCause.toString)
        case Failure(t:UnknownHostException) =>
          Log.e("MOTHER", "No internet")
        case Failure(t:GoogleJsonResponseException) => 
          if (t.getDetails.getCode == 404){
            loadCalendar()
          }
        case Failure(t:Exception) =>
          Log.e("MOTHER", "Other Exception " + t.toString)
      }
    }
  }

//  def getGystCalendar(result:Calendar):Calendar = {
//    val calendar = Option(result)
//    val calendars = result.getItems.asScala
//    val gystCalendar = calendars.filter(cal => cal.getId == "gyst-calendar")
//    if(gystCalendar.size > 0){
//      gystCalendar(0)
//    }
//    else{
//      createGystCalendar()
//    }
//  }

//  def createGystCalendar():Calendar = {
//    val gystCalendar = new Calendar()
//    gystCalendar.setDescription("gyst-calendar")
//    val insert = client.calendars().insert(new Calendar)
//  }

  def chooseAccount() = {
    startActivityForResult(credential.newChooseAccountIntent(), SetupActivity.REQUEST_ACCOUNT_PICKER)
  }
}
