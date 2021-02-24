package tech.anshul1507.internshala_task

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.anshul1507.internshala_task.databinding.ActivityMainBinding
import tech.anshul1507.internshala_task.ui.HomeFragment
import tech.anshul1507.internshala_task.ui.LoginFragment
import java.util.*
import kotlin.concurrent.schedule


open class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE_ASK_PERMISSIONS = 101

    companion object {
        lateinit var mGoogleSignInClient: GoogleSignInClient
        var signInCode = 100
        lateinit var acct: GoogleSignInAccount
        lateinit var sharedPrefs: SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            requestPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        sharedPrefs = this.getSharedPreferences("notes_shared_prefs", Context.MODE_PRIVATE)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //login fragment launch
        val fragLogin = LoginFragment()
        val fragHome = HomeFragment()
        if (sharedPrefs.getBoolean("is_signed", false)) {
            this.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragHome, "HomeFragment").commit()
        } else {
            this.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragLogin, "LoginFragment").commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_log_out) {
            showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout from ${acct.givenName}'s Notes")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Log out") { _, _ ->
                mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this) {
                        val editor: SharedPreferences.Editor = sharedPrefs.edit()
                        editor.putBoolean("is_signed", false)
                        editor.apply()
                        val fragLogin = LoginFragment()
                        this.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragLogin, "LoginFragment").commit()
                    }
            }
            .show()
    }

    private fun requestPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    ),
                    REQUEST_CODE_ASK_PERMISSIONS
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ),
                    REQUEST_CODE_ASK_PERMISSIONS
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Toast.makeText(this@MainActivity, "Enjoy our app", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Permission Denied => Kill App
                Toast.makeText(
                    this@MainActivity,
                    "Grant us Permissions",
                    Toast.LENGTH_SHORT
                )
                    .show()

                //Timer for making above toast visible to user and then kill the app.
                Timer("Permission Denied", false).schedule(
                    500
                ) {
                    if (grantResults.isNotEmpty())
                        finish()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}