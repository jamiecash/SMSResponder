package uk.co.cashfamily.smsresponder

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    // Logger tag
    private val TAG = MainActivity::class.java.simpleName

    // Permission request code for callback
    private val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request required permissions if not already granted
        val requiredPermissions = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
        )
        checkAndRequestPermissions(requiredPermissions)

        // Request that user sets app as default SMS handler
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            val roleManager = getSystemService(RoleManager::class.java)
            val roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
            getActivityResult.launch(roleRequestIntent)
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            getActivityResult.launch(intent)
        }

        // Display main app activity
        setContentView(R.layout.activity_main)
    }

    private fun checkAndRequestPermissions(permissions: Array<out String>) {
        /**
         * Checks required permissions, and requests those required but not already granted
         */

        // Create list if required permissions that have not already been granted
        val nonGrantedPermissions = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(applicationContext, permission) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                nonGrantedPermissions.add(permission)
            }
        }

        // Check if we should show rationale for permission requests
        var showRationale = false
        for (permission in nonGrantedPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    permission
                )
            ) {
                showRationale = true
            }
        }

        // Show rationale if required
        if (showRationale) {
            Toast.makeText(applicationContext, R.string.sms_permission_request, Toast.LENGTH_SHORT)
                .show()
        }

        // Request non granted permissions
        if (nonGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                nonGrantedPermissions.toTypedArray(), PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
            /**
            Callback function for permission requests. Displays message to user depending on their
            response to the permission request.
             **/
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {

            // When request is cancelled, the results array are empty. Check if any have not
            // been granted
            if (grantResults.isNotEmpty()) {
                var granted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        granted = false
                    }
                }

                if (granted) {
                    // Permissions are granted
                    Toast.makeText(
                        applicationContext,
                        R.string.sms_permission_granted,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    // Permissions are denied
                    Toast.makeText(
                        applicationContext,
                        R.string.sms_permission_denied,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    // Receiver for default app request activity
    private val getActivityResult =
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                // Default app set
                Toast.makeText(applicationContext, R.string.sms_default_app_set, Toast.LENGTH_SHORT)
            }
            else {
                // Default app not set
                Toast.makeText(applicationContext, R.string.sms_default_app_not_set, Toast.LENGTH_SHORT)
            }
        }

}