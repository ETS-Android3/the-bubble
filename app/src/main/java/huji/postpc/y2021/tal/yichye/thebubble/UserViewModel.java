package huji.postpc.y2021.tal.yichye.thebubble;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;

public class UserViewModel extends ViewModel {

    private UsersDB usersDB = TheBubbleApplication.getInstance().getUsersDB();
    protected MutableLiveData<String> fullNameLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> userNameLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> passwordLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> phoneNumberLiveData = new MutableLiveData<>("");
    protected MutableLiveData<PersonData.Gender> myGenderLiveData = new MutableLiveData<PersonData.Gender>(PersonData.Gender.MALE);
    protected MutableLiveData<String> cityLiveData = new MutableLiveData<>("");
    protected MutableLiveData<Long> dateOfBirthLiveData = new MutableLiveData<>(0L);

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

    public MutableLiveData<String> getFullNameLiveData() {
        return fullNameLiveData;
    }

    public MutableLiveData<String> getUserNameLiveData() {
        return userNameLiveData;
    }

    public MutableLiveData<Integer> getMinAgePreferenceLiveData() {
        return minAgePreferenceLiveData;
    }

    public MutableLiveData<Integer> getMaxAgePreferenceLiveData() {
        return maxAgePreferenceLiveData;
    }

    public MutableLiveData<Long> getDateOfBirthLiveData() {
        return dateOfBirthLiveData;
    }

    public MutableLiveData<ArrayList<PersonData.Gender>> getGenderTendencyLiveData() {
        return genderTendency;
    }

    public MutableLiveData<String> getCityLiveData() {
        return cityLiveData;
    }

    public void setGenderTendencyLiveData(ArrayList<PersonData.Gender> newTendencySet) {
        genderTendency.setValue(newTendencySet);
        usersDB.updateUserField(userNameLiveData.getValue(), "genderTendency", newTendencySet);
    }

    public void setDateOfBirthLiveData(Long newDate) {
        dateOfBirthLiveData.setValue(newDate);
        usersDB.updateUserField(userNameLiveData.getValue(), "dateOfBirth", newDate);
    }


    public void setMinAgePreferenceLiveData(Integer newMinAge) {
        minAgePreferenceLiveData.setValue(newMinAge);
        usersDB.updateUserField(userNameLiveData.getValue(), "minAgePreference", newMinAge);
    }

    public void setMaxAgePreferenceLiveData(Integer newMaxAge) {
        maxAgePreferenceLiveData.setValue(newMaxAge);
        usersDB.updateUserField(userNameLiveData.getValue(), "maxAgePreference", newMaxAge);
    }


    public void setFullNameLiveData(String newFullName) {
        fullNameLiveData.setValue(newFullName);
        usersDB.updateUserField(userNameLiveData.getValue(), "fullName", newFullName);
    }

    public void setAboutMeLiveData(String newDescription) {
        aboutMeLiveData.setValue(newDescription);
        usersDB.updateUserField(userNameLiveData.getValue(), "aboutMe", newDescription);
    }

    public void setCityLiveData(String newCity) {
        aboutMeLiveData.setValue(newCity);
        usersDB.updateUserField(userNameLiveData.getValue(), "city", newCity);
    }




}
