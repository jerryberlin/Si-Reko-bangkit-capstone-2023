package com.example.itr.ui.register

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
import com.example.itr.databinding.FragmentRegisterBinding
import com.example.itr.models.User
import com.example.itr.util.RegisterValidation
import com.example.itr.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textLoginClicked()

        btnRegisterClicked()

        lifecycleScope.launch {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.constraintLayout.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.constraintLayout.visibility = View.INVISIBLE
                        binding.progressBar.visibility = View.INVISIBLE
                        findNavController().navigate(R.id.action_registerFragment_to_mainActivity)
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
                if (it.name is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.nameInput.apply {
                            requestFocus()
                            error = it.name.message
                        }
                    }
                }

                if (it.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.emailInput.apply {
                            requestFocus()
                            error = it.email.message
                        }
                    }
                }

                if (it.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.passwordInput.apply {
                            requestFocus()
                            error = it.password.message
                        }
                    }
                }

            }
        }
    }

    private fun btnRegisterClicked() {
        binding.apply {
            registerButton.setOnClickListener {
                val name = nameInput.text.toString().trim()
                val email = emailInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(registerButton.windowToken, 0)
                val user = User(
                    name,
                    email
                )
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }
    }

    private fun textLoginClicked() {
        binding.loginLink.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_out)
            binding.loginLink.startAnimation(animation)
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}