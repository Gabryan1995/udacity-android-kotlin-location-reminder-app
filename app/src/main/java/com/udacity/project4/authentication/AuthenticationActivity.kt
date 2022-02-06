package com.udacity.project4.authentication

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel>()

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
        this.onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAuthenticationBinding.inflate(layoutInflater)

//      TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//      TODO: If the user was authenticated, send him to RemindersActivity
        binding.loginButton.setOnClickListener {
            launchSignInFlow()
        }

//        onBackPressedDispatcher.addCallback(this) {
//            navController.popBackStack(R.id.mainFragment, false)
//        }

        setContentView(binding.root)

        viewModel.authenticationState.observe(this, { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> startActivity(Intent(this, RemindersActivity::class.java))
                else -> Log.e(
                    TAG,
                    "Authentication state that doesn't require any UI change $authenticationState"
                )
            }
        })

//      TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .setLogo(R.drawable.map)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in user.
            Log.i(
                TAG,
                "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
            )
        } else {
            // Sign in failed. If response is null the user canceled the sign-in flow using
            // the back button. Otherwise check response.getError().getErrorCode() and handle
            // the error.
            Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
        }
    }
}
