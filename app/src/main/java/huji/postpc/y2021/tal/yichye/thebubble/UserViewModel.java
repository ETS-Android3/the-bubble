package huji.postpc.y2021.tal.yichye.thebubble;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;

public class UserViewModel extends ViewModel {

    protected MutableLiveData<String> fullNameLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> userNameLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> passwordLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> phoneNumberLiveData = new MutableLiveData<>("");
    protected MutableLiveData<Integer> ageLiveData = new MutableLiveData<Integer>(18);
    protected MutableLiveData<PersonData.Gender> myGenderLiveData = new MutableLiveData<PersonData.Gender>(PersonData.Gender.MALE);
    protected MutableLiveData<String> cityLiveData = new MutableLiveData<>("");

    protected MutableLiveData<String> profilePicture = new MutableLiveData<>("");
    protected MutableLiveData<ArrayList<String>> photosLiveData = new MutableLiveData<>(new ArrayList<>());

    protected MutableLiveData<Integer> minAgePreferenceLiveData = new MutableLiveData<Integer>( 26);
    protected MutableLiveData<Integer> maxAgePreferenceLiveData = new MutableLiveData<>(40);
    protected MutableLiveData<ArrayList<PersonData.Gender>> genderTendency = new MutableLiveData<>(new ArrayList<>());

    protected MutableLiveData<String> aboutMeLiveData = new MutableLiveData<>("");

    protected MutableLiveData<ArrayList<Request>> requestsLiveData = new MutableLiveData<>(new ArrayList<>());


    public MutableLiveData<ArrayList<Request>> getRequestsLiveData() {
        return requestsLiveData;
    }
}
