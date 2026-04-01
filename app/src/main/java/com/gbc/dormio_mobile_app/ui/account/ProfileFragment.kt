package com.gbc.dormio_mobile_app.ui.account

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.profile.ProfileUiState
import com.gbc.dormio_mobile_app.data.model.profile.ProfileUpdateRequest
import com.gbc.dormio_mobile_app.databinding.FragmentProfileBinding
import com.gbc.dormio_mobile_app.viewmodel.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadAvatar(it) }
    }
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.ivAvatar.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Trigger Profile Update
        binding.btnSaveProfile.setOnClickListener {
            val updateRequest = ProfileUpdateRequest(
                firstName = binding.etFirstName.text.toString(),
                lastName = binding.etLastName.text.toString(),
                email = binding.etEmail.text.toString(),
                studentId = binding.etStudentId.text.toString(),
                roomNumber = binding.etRoomNumber.text.toString()
            )
            viewModel.updateProfile(updateRequest)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    private fun handleUiState(state: ProfileUiState) {
        binding.progressBar.isVisible = state.isLoading
        binding.tilRoomNumber.isVisible = state.userRole != "ADMIN"

        state.profile?.let { data ->
            if (!data.avatarUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(data.avatarUrl)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .error(R.drawable.outline_account_circle_24)
                    .circleCrop()
                    .into(binding.ivAvatar)
            }


            if (binding.etFirstName.text.isNullOrEmpty()) {
                binding.etFirstName.setText(data.user.firstName)
                binding.etLastName.setText(data.user.lastName)
                binding.etEmail.setText(data.user.email)
                binding.etStudentId.setText(data.studentId ?: "")
                binding.etRoomNumber.setText(data.roomNumber ?: "")
            }
        }

        if (state.isUpdateSuccessful) {
            Toast.makeText(requireContext(), "Profile Updated!", Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateStatus()
        }

        state.errorMessage?.let { msg ->
            if (!msg.contains("404")) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}