package com.gbc.dormio_mobile_app.ui.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.databinding.ActivityAccountSettingsBinding
import com.gbc.dormio_mobile_app.network.TokenManager.clearToken
import com.gbc.dormio_mobile_app.ui.auth.LoginActivity
import com.gbc.dormio_mobile_app.viewmodel.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountSettingsFragment : Fragment(R.layout.activity_account_settings) {

    private val viewModel: ProfileViewModel by viewModels()

    private var _binding: ActivityAccountSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ActivityAccountSettingsBinding.bind(view)

        observeViewModel()

        binding.editProfileIcon.setOnClickListener {
            findNavController().navigate(R.id.action_accountSettings_to_profile)
        }

        binding.accountName.setOnClickListener {
            findNavController().navigate(R.id.action_accountSettings_to_profile)
        }

        setupButtons()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.profile?.let { profile ->
                    val firstName = profile.user.firstName ?: ""
                    val lastName = profile.user.lastName ?: ""

                    val fullName = "$firstName $lastName".trim()

                    binding.accountName.text = fullName
                    binding.accountEmail.text = profile.user.email

                    Glide.with(this@AccountSettingsFragment)
                        .load(profile.avatarUrl)
                        .placeholder(R.drawable.outline_account_circle_24)
                        .error(R.drawable.outline_account_circle_24)
                        .circleCrop()
                        .into(binding.ivAccountAvatar)
                }

                if (state.isLoading) {
                    binding.accountName.text = "Loading..."
                }

                state.errorMessage?.let {
                    Log.d("AccountSettingsFragment", "Error loading profile: $it")
                    binding.accountName.text = "Profile Unavailable"
                }
            }
        }
    }

    private fun setupButtons() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}