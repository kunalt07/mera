package com.example.meraapp

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import android.text.Editable
import android.text.TextWatcher

class LoginActivity : AppCompatActivity() {
    private lateinit var etPhone: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var btnVerifyProceed: Button
    private lateinit var llPhoneInput: LinearLayout
    private lateinit var llOtpBoxes: LinearLayout
    private lateinit var etOtp1: EditText
    private lateinit var etOtp2: EditText
    private lateinit var etOtp3: EditText
    private lateinit var etOtp4: EditText
    private lateinit var etOtp5: EditText
    private lateinit var etOtp6: EditText
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide status bar and action bar for full screen
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        etPhone = findViewById(R.id.etPhone)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        btnVerifyProceed = findViewById(R.id.btnVerifyProceed)
        llPhoneInput = findViewById(R.id.llPhoneInput)
        llOtpBoxes = findViewById(R.id.llOtpBoxes)
        etOtp1 = findViewById(R.id.etOtp1)
        etOtp2 = findViewById(R.id.etOtp2)
        etOtp3 = findViewById(R.id.etOtp3)
        etOtp4 = findViewById(R.id.etOtp4)
        etOtp5 = findViewById(R.id.etOtp5)
        etOtp6 = findViewById(R.id.etOtp6)
        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Sending OTP...")
        progressDialog.setCancelable(false)

        // OTP auto-advance and backspace logic
        val otpBoxes = arrayOf(etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6)
        for (i in otpBoxes.indices) {
            otpBoxes[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < otpBoxes.size - 1) {
                        otpBoxes[i + 1].requestFocus()
                    } else if (s?.isEmpty() == true && i > 0) {
                        otpBoxes[i - 1].requestFocus()
                    }
                }
            })
        }

        // Step 1: Send OTP
        btnSendOtp.setOnClickListener {
            var phone = etPhone.text.toString().trim()
            phone = phone.replace("[^\\d]".toRegex(), "") // Remove non-digits
            if (phone.length != 10) {
                Toast.makeText(this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
            } else {
                sendOtp(phone)
            }
        }

        // Step 2: Verify OTP
        btnVerifyProceed.setOnClickListener {
            val otp = (etOtp1.text.toString() + etOtp2.text.toString() + etOtp3.text.toString() +
                    etOtp4.text.toString() + etOtp5.text.toString() + etOtp6.text.toString())
            if (otp.length != 6) {
                Toast.makeText(this, "Enter the 6-digit OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyOtp(otp)
            }
        }
    }

    private fun sendOtp(phone: String) {
        btnSendOtp.isEnabled = false
        progressDialog.show()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    progressDialog.dismiss()
                    btnSendOtp.isEnabled = true
                    signInWithCredential(credential)
                }
                override fun onVerificationFailed(e: FirebaseException) {
                    progressDialog.dismiss()
                    btnSendOtp.isEnabled = true
                    Toast.makeText(this@LoginActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    progressDialog.dismiss()
                    btnSendOtp.isEnabled = true
                    this@LoginActivity.verificationId = verificationId
                    Toast.makeText(this@LoginActivity, "OTP sent!", Toast.LENGTH_SHORT).show()
                    // Switch to OTP step
                    llPhoneInput.visibility = View.GONE
                    btnSendOtp.visibility = View.GONE
                    llOtpBoxes.visibility = View.VISIBLE
                    btnVerifyProceed.visibility = View.VISIBLE
                    val tvOtpSubheading = findViewById<TextView>(R.id.tvOtpSubheading)
                    tvOtpSubheading.text = "Please Enter the OTP generated to your Registered Mobile Number"
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp(otp: String) {
        val id = verificationId
        if (id != null) {
            val credential = PhoneAuthProvider.getCredential(id, otp)
            signInWithCredential(credential)
        } else {
            Toast.makeText(this, "Verification ID not found. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Verifying...")
        progressDialog.show()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = android.content.Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}