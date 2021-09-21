package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class NewUserViewModel extends ViewModel
{

    Long START_DATE = LocalDate.of(2003, 1, 1).toEpochDay();

    protected MutableLiveData<String> fullNameLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> userNameLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> passwordLiveData = new MutableLiveData<>("");

    protected MutableLiveData<String> phoneNumberLiveData = new MutableLiveData<>("");
    protected MutableLiveData<Long> dataOfBirthLiveData = new MutableLiveData<Long>(START_DATE);
    protected MutableLiveData<Integer> myGenderLiveData = new MutableLiveData<Integer>(1);
    protected MutableLiveData<String> cityLiveData = new MutableLiveData<>("");

    protected MutableLiveData<Integer> minAgePreferenceLiveData = new MutableLiveData<Integer>( 26);
    protected MutableLiveData<Integer> maxAgePreferenceLiveData = new MutableLiveData<Integer>( 40);
    protected MutableLiveData<HashSet<Integer>> genderTendencyLiveData = new MutableLiveData<>(new HashSet<>());

    protected MutableLiveData<String> aboutMeLiveData = new MutableLiveData<>("");
    protected MutableLiveData<ArrayList<String>> photosLiveData = new MutableLiveData<ArrayList<String>>(new ArrayList<>());
    protected MutableLiveData<String> profilePhotoLiveData = new MutableLiveData<String>();

    protected MutableLiveData<Integer> progressLiveData = new MutableLiveData<>(0);
    protected MutableLiveData<ArrayList<String>> ignoreListLiveData = new MutableLiveData<>(new ArrayList<>());

}
