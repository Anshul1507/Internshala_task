package tech.anshul1507.internshala_task.ui

import android.content.Intent
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import tech.anshul1507.internshala_task.MainActivity
import tech.anshul1507.internshala_task.R
import tech.anshul1507.internshala_task.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = MainActivity.mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, MainActivity.signInCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MainActivity.signInCode) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.w("TAG", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun updateUI(acc: GoogleSignInAccount?) {
        val fragHome = HomeFragment()
        val editor: Editor = MainActivity.sharedPrefs.edit()
        editor.putBoolean("is_signed", true)
        editor.apply()
        MainActivity.acct =
            GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragHome, "HomeFragment").commit()
        requireActivity().actionBar?.title = acc?.displayName
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity?)!!.supportActionBar!!.title =
            "Hey ${MainActivity.acct.displayName}"
    }

}