package com.gbc.dormio_mobile_app.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.profile.ProfileUiState
import com.gbc.dormio_mobile_app.databinding.FragmentProfileBinding
import com.gbc.dormio_mobile_app.viewmodel.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupObservers()
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

        if (state.userRole == "ADMIN") {
            binding.tilRoomNumber.visibility = View.GONE
        }

        state.profile?.let { data ->
            if (binding.etFirstName.text.isNullOrEmpty()) {
                binding.etFirstName.setText(data.user.firstName)
                binding.etLastName.setText(data.user.lastName)
                binding.etEmail.setText(data.user.email)
                binding.etStudentId.setText(data.studentId ?: "")
                binding.etRoomNumber.setText(data.roomNumber ?: "")
            }
        }

        state.errorMessage?.let { msg ->
            if (msg.contains("404")) {
            } else {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}