package com.example.demointent

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private val pickContact = 101
    private val requestCodePermission = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check Permission + get Contact
        // https://stackoverflow.com/questions/866769/how-to-call-android-contacts-list/867828#867828
        handleEventGetListContact()

        // Open Calendar using Implicit Intent
        openCalendar()

        // How to set the part of the text view is clickable?
        // https://stackoverflow.com/questions/10696986/how-to-set-the-part-of-the-text-view-is-clickable/10697453#10697453
        handleEventClickHightlight()

    }

    private fun handleEventGetListContact() {
        tvGetListContact.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openListContact()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    requestCodePermission
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodePermission) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openListContact()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openListContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, pickContact)
    }


    @SuppressLint("Recycle", "Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            pickContact -> if (resultCode === RESULT_OK) {
                getReadContacts(data)
            }
        }
    }

    @SuppressLint("Range")
    private fun getReadContacts(data: Intent?) {
        val contactData: Uri = data!!.data!!
        val cursor = contentResolver.query(contactData, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            val name =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
            Log.e("Smeb", name)
        }
    }

    private fun handleEventClickHightlight() {
        val ss = SpannableString("Android is a Software stack")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(this@MainActivity, "Navigate Success", Toast.LENGTH_SHORT).show()
            }
        }
        ss.setSpan(clickableSpan, 22, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvSpannable.text = ss
        tvSpannable.movementMethod = LinkMovementMethod.getInstance()
        tvSpannable.highlightColor = Color.TRANSPARENT
    }

    private fun openCalendar() {
        tvCalendar.setOnClickListener {
            val cal: Calendar = Calendar.getInstance()
            val intent = Intent(Intent.ACTION_EDIT)
            intent.type = "vnd.android.cursor.item/event"
            intent.putExtra("beginTime", cal.timeInMillis)
            intent.putExtra("allDay", true)
            intent.putExtra("rrule", "FREQ=YEARLY")
            intent.putExtra("endTime", cal.timeInMillis + 60 * 60 * 1000)
            intent.putExtra("title", "A Test Event from android app")
            startActivity(intent)
        }
    }
}