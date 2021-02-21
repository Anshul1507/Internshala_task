package tech.anshul1507.internshala_task

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import tech.anshul1507.internshala_task.databinding.ActivityMainBinding
import tech.anshul1507.internshala_task.ui.HomeFragment


open class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var mGoogleSignInClient: GoogleSignInClient
        var signInCode = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //login fragment launch
        val fragLogin = HomeFragment()
        this.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragLogin, "LoginFragment").commit()

    }

}