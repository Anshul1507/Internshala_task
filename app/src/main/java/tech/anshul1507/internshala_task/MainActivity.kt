package tech.anshul1507.internshala_task

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.anshul1507.internshala_task.databinding.ActivityMainBinding
import tech.anshul1507.internshala_task.ui.HomeFragment
import tech.anshul1507.internshala_task.ui.LoginFragment


open class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
        sharedPrefs = this.getSharedPreferences("notes_shared_prefs", Context.MODE_PRIVATE)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //login fragment launch
        val fragLogin = LoginFragment()
        val fragHome = HomeFragment()
        if(sharedPrefs.getBoolean("is_signed",false)) {
            this.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragHome, "HomeFragment").commit()
        }else {
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
            .setTitle("Logout from ${acct.displayName}'s Notes")
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
}