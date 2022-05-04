package com.example.android.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.R
import com.example.android.api.AuthService
import com.example.android.api.createAuthService
import com.example.android.databinding.FragmentLoginBinding
import com.example.android.globals.AppPreferences
import com.example.android.models.Credentials
import com.example.android.models.Profile
import com.example.android.models.UserForm
import com.example.android.repository.AuthRepository
import com.example.android.view_model.AuthViewModel
import com.example.android.view_model.AuthViewModelFactory


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var repository: AuthRepository
    private lateinit var viewModel: AuthViewModel
    private lateinit var service: AuthService
    private lateinit var viewModelFactory: AuthViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        configureViewModel()
        observe()

        Log.e("Button", binding.loginButton.text.toString())

        setOnClickListeners()

        return binding.root
    }

    private fun observe() {
        viewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val authToken = response.body()
                if (authToken != null) {
                    AppPreferences.accessToken = authToken.access
                    AppPreferences.refreshToken = authToken.refresh
                }
                findNavController().navigate(R.id.action_login_to_chat)
            }
        }
        viewModel.signupResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful){
                login()
            }
        }
    }

    private fun login(){
        val credentials = getCredentials()
        Log.e(LoginFragment::class.java.simpleName, "${credentials.email}, ${credentials.password}")
        viewModel.login(credentials)
    }

    private fun configureViewModel() {
        service = createAuthService()
        repository = AuthRepository(service)
        viewModelFactory = AuthViewModelFactory(repository = repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
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
            viewModel.signup(userForm)
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