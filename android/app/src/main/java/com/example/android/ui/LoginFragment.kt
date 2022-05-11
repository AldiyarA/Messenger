package com.example.android.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.android.R
import com.example.android.databinding.FragmentLoginBinding
import com.example.android.globals.AppPreferences
import com.example.android.models.Credentials
import com.example.android.models.Profile
import com.example.android.models.UserForm
import com.example.android.view_model.AuthViewModel
import com.example.android.view_model.DBViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val vm: AuthViewModel by viewModel<AuthViewModel>()
    private val db: DBViewModel by viewModel<DBViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        db.get()
        observe()

        Log.e("Button", binding.loginButton.text.toString())

        setOnClickListeners()

        return binding.root
    }

    private fun observe() {
        vm.loginResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val authToken = response.body()
                if (authToken != null) {
                    AppPreferences.accessToken = authToken.access
//                    AppPreferences.refreshToken = authToken.refresh
                    db.insert(authToken.refresh!!)
                }
                findNavController().navigate(R.id.action_login_to_chat)
            }
        }
        vm.signupResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful){
                login()
            }
        }
        db.refreshToken.observe(viewLifecycleOwner) { refreshTokenList ->
            if (refreshTokenList.isEmpty()){
                return@observe
            }
            Log.e("Login fragment", "Found refresh")
            vm.refresh(refreshTokenList.first())
        }
        vm.refreshResponse.observe(viewLifecycleOwner) { response ->
            Log.e("Login fragment", "New Access token")
            Log.e("Login fragment", "Response $response")
            if (response.isSuccessful){
                Log.e("Login fragment", "New Access token success")
                val authToken = response.body()
                if (authToken != null) {
                    AppPreferences.accessToken = authToken.access
                }
                findNavController().navigate(R.id.action_login_to_chat)
            }
            Log.e("Login fragment", "New Access token request end")
        }
    }

    private fun login(){
        val credentials = getCredentials()
        Log.e(LoginFragment::class.java.simpleName, "${credentials.email}, ${credentials.password}")
        vm.login(credentials)
    }

    private fun getCredentials(): Credentials {
        return Credentials(
            email = binding.emailInput.text.toString(),
            password = binding.passwordInput.text.toString(),
        )
    }

    private fun getUserForm(): UserForm{
        val profile = Profile(
            first_name = binding.firstNameInput.text.toString(),
            last_name = binding.lastNameInput.text.toString(),
            bio = binding.bioInput.text.toString(),
            phone = binding.phoneInput.text.toString()
        )
        return UserForm(
            user = getCredentials(),
            profile = profile
        )
    }

    private fun toLogin(){
        binding.loginBanner.visibility = View.VISIBLE
        binding.loginButton.visibility = View.VISIBLE
        binding.toSignupButton.visibility = View.VISIBLE

        binding.profileFields.visibility = View.GONE
        binding.signupBanner.visibility = View.GONE
        binding.signupButton.visibility = View.GONE
        binding.toLoginButton.visibility = View.GONE
    }

    private fun toSignup(){
        binding.profileFields.visibility = View.VISIBLE
        binding.signupBanner.visibility = View.VISIBLE
        binding.signupButton.visibility = View.VISIBLE
        binding.toLoginButton.visibility = View.VISIBLE

        binding.loginBanner.visibility = View.GONE
        binding.loginButton.visibility = View.GONE
        binding.toSignupButton.visibility = View.GONE
    }

    private fun setOnClickListeners(){
        binding.loginButton.setOnClickListener { view: View->
            Log.e("BUTTON CLICKED", "LOGIN")
            login()
        }
        binding.signupButton.setOnClickListener { view: View->
            Log.e("BUTTON CLICKED", "SIGNUP")
            val userForm = getUserForm()
            vm.signup(userForm)
        }
        binding.toLoginButton.setOnClickListener { view: View->
            Log.e("BUTTON CLICKED", "To Login Button")
            toLogin()
        }

        binding.toSignupButton.setOnClickListener { view: View->
            Log.e("BUTTON CLICKED", "To Signup Button")
            toSignup()
        }
    }

}