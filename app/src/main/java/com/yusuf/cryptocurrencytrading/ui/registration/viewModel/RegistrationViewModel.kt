package com.yusuf.cryptocurrencytrading.ui.registration.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yusuf.cryptocurrencytrading.utils.Utils


class RegistrationViewModel : ViewModel() {

     var loading = MutableLiveData<Boolean>()


     private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
     private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun checkUserIfNotNull(action: () -> Unit){
        val user = auth.currentUser

        if (user != null){
            action()
        }
    }

    fun signIn(email: String, password: String,context: Context,onSuccess: () -> Unit, onFailure: (String) -> Unit){
        if (Utils.emailAndPasswordControl(email,password,context)){
           auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
               onSuccess()
           }.addOnFailureListener {
               onFailure(it.localizedMessage ?: "An unknown error occurred.")
           }
        }
        else {
            onFailure("E-mail or Password can not be empty.")
        }
    }

    fun signUp(email: String, password: String, name: String, context: Context,onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        loading.value = true
        if (Utils.nameControl(name,context) && Utils.emailAndPasswordControl(email,password,context)){
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val user = hashMapOf(
                        "email" to email,
                        "username" to name,
                        "password" to password
                    )

                    firestore.collection("users")
                        .document(authResult.user?.uid ?: "")
                        .set(user)
                        .addOnSuccessListener {
                            onSuccess()
                            loading.value = false
                        }
                        .addOnFailureListener {
                            onFailure(it.localizedMessage ?: "An unknown error occurred.")
                        }

                }.addOnFailureListener {
                    onFailure(it.localizedMessage ?: "An unknown error occurred.")
                }
        }
         else {
            onFailure("E-mail or Password can not be empty.")
        }
    }
}