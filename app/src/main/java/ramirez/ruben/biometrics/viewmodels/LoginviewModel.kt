package ramirez.ruben.biometrics.viewmodels

import androidx.lifecycle.ViewModel
import ramirez.ruben.biometrics.datastore.DataStoreManager

class  LoginviewModel(private val dataStoreManager: DataStoreManager): ViewModel{
    private val emailCredential = "correo@gmail.com"
    private val passwordCredential = "abc123"

    fun login(email: String, password: String){
        if (email.lowercase() == emailCredential && password == passwordCredential){
            
        }
    }
}