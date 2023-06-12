package com.example.itr.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.itr.R
import com.example.itr.databinding.FragmentLoginBinding
import com.example.itr.util.RegisterValidation
import com.example.itr.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLoginClicked()

        textRegisterClicked()

        lifecycleScope.launch {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.constraintLayout.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.constraintLayout.visibility = View.INVISIBLE
                        binding.progressBar.visibility = View.INVISIBLE
                        findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                        requireActivity().finish()
                    }
                    is Resource.Error -> {
                        binding.constraintLayout.visibility = View.INVISIBLE
                        binding.progressBar.visibility = View.INVISIBLE
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.validation.collect {
                if (it.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.emailEditText.apply {
                            requestFocus()
                            error = it.email.message
                        }
                    }
                }

                if (it.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.passwordEditText.apply {
                            requestFocus()
                            error = it.password.message
                        }
                    }
                }
            }
        }
    }

    private fun textRegisterClicked() {
        binding.registerTextView.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_out)
            binding.registerTextView.startAnimation(animation)
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun btnLoginClicked() {
        binding.apply {
            loginButton.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(loginButton.windowToken, 0)
                viewModel.login(email, password)

            }
        }
    }
}