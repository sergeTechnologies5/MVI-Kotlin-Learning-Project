package com.mvi.transaction.ui.auth.login.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mvi.transaction.database.api.AbsentLiveData
import com.mvi.transaction.database.entity.UserEntity
import com.mvi.transaction.database.repository.AuthenticationRepository
import com.mvi.transaction.ui.auth.login.state.SigninStateEvent.*
import com.mvi.transaction.utility.DataState

/*
 * THIS IS THE THIRD STEP AFTER CREATING THE VIEW STATE
 */

class SigninViewModel : ViewModel(){

    private val _signinStateEvent: MutableLiveData<SigninStateEvent> = MutableLiveData()
    //Declaring a mutable live data object with out View state which will be private to this class
    private val _signinViewState: MutableLiveData<SigninViewState> = MutableLiveData()

    //this is what will be observed in the sign in fragment
    val signinViewState: LiveData<SigninViewState>
        get() = _signinViewState

    val signinDataState: LiveData<DataState<SigninViewState>> = Transformations.switchMap(_signinStateEvent){ signinStateEvent ->
        //First I check whether the state is empty and if its not I pass it to the handler function which now returns live data
        signinStateEvent?.let { handleSigninStateEvent(signinStateEvent)  }
    }

    //This 'bonoko' handler method is necessity because the when checker need to explicitly know what the state event type is of
    private fun handleSigninStateEvent(stateEvent: SigninStateEvent): LiveData<DataState<SigninViewState>>{
        return when(stateEvent){
            is SigninUserEvent -> {
                println("Debug: Signin Datastate: ${stateEvent.email} . password: ${stateEvent.password}")
                return AuthenticationRepository.getUserSignin2(stateEvent.email, stateEvent.password)
            }

            is None -> { AbsentLiveData.create() }
        }
    }

    fun setSuccessLogin(userEntity: UserEntity){
        val viewstateUpdate = getCurrentViewStateOrNew()
        //We updated SigninViewState with new values
        viewstateUpdate.userEntity = userEntity
        _signinViewState.value = viewstateUpdate
    }

    fun getCurrentViewStateOrNew(): SigninViewState{
        return signinViewState.value?.let { it }?: SigninViewState()
    }

    //This is for triggering the item click state event
    fun setSigninStateEvent(event: SigninStateEvent){
         /*
          * When the menu event is clicked it will trigger the _signinStateEvent
          * so that we can handle the action
          */
        _signinStateEvent.value = event
    }
}