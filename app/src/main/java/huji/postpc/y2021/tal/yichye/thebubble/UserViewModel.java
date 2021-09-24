package huji.postpc.y2021.tal.yichye.thebubble;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.ChatInfo;
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
    protected MutableLiveData<ArrayList<String>> ignoreListLiveData = new MutableLiveData<>(new ArrayList<>());

    protected MutableLiveData<Integer> minAgePreferenceLiveData = new MutableLiveData<Integer>( 26);
    protected MutableLiveData<Integer> maxAgePreferenceLiveData = new MutableLiveData<>(40);


    protected MutableLiveData<ArrayList<PersonData.Gender>> genderTendency = new MutableLiveData<>(new ArrayList<>());

    protected MutableLiveData<String> aboutMeLiveData = new MutableLiveData<>("");

    protected MutableLiveData<ArrayList<Request>> requestsLiveData = new MutableLiveData<>(new ArrayList<>());
    protected MutableLiveData<ArrayList<ChatInfo>> chatsLiveData = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<ArrayList<String>> getIgnoreListLiveData() {
        return ignoreListLiveData;
    }

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

    public MutableLiveData<ArrayList<String>> getPhotosLiveData() {
        return photosLiveData;
    }

    public MutableLiveData<String> getCityLiveData() {
        return cityLiveData;
    }

    public MutableLiveData<String> getAboutMeLiveData() {
        return aboutMeLiveData;
    }

    public MutableLiveData<ArrayList<ChatInfo>> getChatsLiveData() {
        return chatsLiveData;
    }

    public void setGenderTendencyLiveData(ArrayList<PersonData.Gender> newTendencySet, String id) {
        if (id == null) {
            id = userNameLiveData.getValue();
            genderTendency.setValue(newTendencySet);
        }
        usersDB.updateUserField(id, "genderTendency", newTendencySet);
    }

    public void setDateOfBirthLiveData(Long newDate, String id) {
        if (id == null) {
            id = userNameLiveData.getValue();
            dateOfBirthLiveData.setValue(newDate);
        }
        usersDB.updateUserField(id, "dateOfBirth", newDate);
    }

    public void setPhotosLiveData(ArrayList<String> newPhotosArray) {
        photosLiveData.setValue(newPhotosArray);
        usersDB.updateUserField(userNameLiveData.getValue(), "photos", newPhotosArray);
    }

    public void setMinAgePreferenceLiveData(Integer newMinAge, String id) {
        if (id == null) {
            id = userNameLiveData.getValue();
            minAgePreferenceLiveData.setValue(newMinAge);
        }
        usersDB.updateUserField(id, "minAgePreference", newMinAge);
    }

    public void setMaxAgePreferenceLiveData(Integer newMaxAge, String id) {
        if (id == null) {
            id = userNameLiveData.getValue();
            maxAgePreferenceLiveData.setValue(newMaxAge);
        }
        usersDB.updateUserField(id, "maxAgePreference", newMaxAge);
    }


    public void setFullNameLiveData(String newFullName, String id) {
        if (id == null) {
            id = userNameLiveData.getValue();
            fullNameLiveData.setValue(newFullName);
        }
        usersDB.updateUserField(id, "fullName", newFullName);
    }

    public void setAboutMeLiveData(String newDescription, String id) {
        if (id == null) {
            id = userNameLiveData.getValue();
            aboutMeLiveData.setValue(newDescription);
        }
        usersDB.updateUserField(id, "aboutMe", newDescription);
    }

    public void setCityLiveData(String newCity, String id ) {
        if (id == null) {
            id = userNameLiveData.getValue();
            cityLiveData.setValue(newCity);
        }
        usersDB.updateUserField(id, "city", newCity);
    }


    public void setRequestsLiveData(ArrayList<Request> newRequestArray, String id)
    {
        if (id == null){
            id = userNameLiveData.getValue();
            requestsLiveData.setValue(newRequestArray);
        }
        usersDB.updateUserField(id, "requests",newRequestArray);
    }


    public void setChatsLiveData(ArrayList<ChatInfo> chatInfos, String id) {
        if (id == null){
            id = userNameLiveData.getValue();
            chatsLiveData.setValue(chatInfos);
        }
        usersDB.updateUserField(id, "chatInfos", chatInfos);
    }

    public MutableLiveData<PersonData.Gender> getGenderLiveData()
    {
        return myGenderLiveData;
    }

}
