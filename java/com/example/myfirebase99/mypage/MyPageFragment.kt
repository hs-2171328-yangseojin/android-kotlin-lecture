package com.example.myfirebase99.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.myfirebase99.LogInActivity
import com.example.myfirebase99.MainActivity

import com.example.myfirebase99.databinding.FragmentChatlistBinding
import com.example.myfirebase99.databinding.FragmentMypageBinding
import com.example.myfirebase99.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding

        fragmentMypageBinding.signInOutButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                if (auth.currentUser == null) {
                    // todo 로그인
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) {
                            if (it.isSuccessful) {
                                successSignIn()
                            } else {
                                Toast.makeText(
                                    context,
                                    "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }

                    // addOnCompleteListener() 는 파라미터로 Activity를 받는데, 일반적인 액티비티 영역에서는 this로 사용하지만,
                    // 이번에는 Fragment영역이므로 activity로 전달해줘야 함
                    // activity 변수는 nullable이기 때문에 이를 해결하는 방법으로 requireActivity()를 사용하면 되지만,
                    // requireActivity는 nullsafe이므로 만에 하나 null이 들어오면 앱이 죽어버림
                    // 따라서 절대 null이 들어가지 않을 부분에서만 requireActivity를 사용해줘야 함
                    // 가급적이면 그냥 activity?.let{}으로 코드들을 감싸서 사용할 것을 추천


                } else {
                    // todo 로그 아웃
                    auth.signOut()
                    binding.emailEditText.text.clear()
                    binding.emailEditText.isEnabled = true
                    binding.passwordEditText.text.clear()
                    binding.passwordEditText.isEnabled = true

                    binding.signInOutButton.text = "로그인"
                    binding.signInOutButton.isEnabled = false
                    binding.signUpButton.isEnabled = false

                    moveToLogInScreen()
                }
            }
        }

        fragmentMypageBinding.signUpButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(requireActivity()) {
                        if(it.isSuccessful){
                            Toast.makeText(context,"회원가입에 성공했습니다. 로그인 버튼을 눌러주세요", Toast.LENGTH_SHORT).show()
                        }else{

                            Toast.makeText(context,"회원가입에 실패했습니다. 이미 가입한 이메일일 수 있습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }

        fragmentMypageBinding.emailEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }

        }

        fragmentMypageBinding.passwordEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.passwordEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }
        }

    }

    // 앱에서 잠깐 벗어났다가 들어오는 시점에서 로그인이 풀렸을 가능성이 있으므로 반드시 시작시에 로그인 여부를 확인하여
    // 예외처리 해줘야 한다.
    override fun onStart() {
        super.onStart()

        // Start 시점에 로그인이 안되어 있거나 혹은 로그인이 풀린 경우
        if (auth.currentUser == null) {
            binding?.let { binding ->

                binding.emailEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.text.clear()
                binding.passwordEditText.isEnabled = true

                binding.signInOutButton.text = "로그인"
                binding.signInOutButton.isEnabled = false
                binding.signUpButton.isEnabled = false
            }

        } else {
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser!!.email)
                binding.passwordEditText.setText("********")
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false

                binding.signInOutButton.text = "로그 아웃"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled = false
            }

        }

    }

    private fun successSignIn() {
        if (auth.currentUser == null) {
            Toast.makeText(context, "로그인에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpButton?.isEnabled = false
        binding?.signInOutButton?.text = "로그아웃"
    }

    private fun moveToLogInScreen() {
        val intent = Intent(requireContext(), LogInActivity::class.java)
        startActivity(intent)
    }
}