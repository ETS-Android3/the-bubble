package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.HashSet;


public class NewUserViewModel extends ViewModel
{

    LocalDate START_DATE = LocalDate.of(2003, 1, 1);

    protected MutableLiveData<String> fullNameLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> userNameLiveData = new MutableLiveData<>("");
    protected MutableLiveData<String> passwordLiveData = new MutableLiveData<>("");

    protected MutableLiveData<String> phoneNumberLiveData = new MutableLiveData<>("");
    protected MutableLiveData<LocalDate> dataOfBirthLiveData = new MutableLiveData<LocalDate>(START_DATE);
    protected MutableLiveData<Integer> myGenderLiveData = new MutableLiveData<Integer>(1);
    protected MutableLiveData<String> cityLiveData = new MutableLiveData<>("");

    protected MutableLiveData<String> photosLiveData = new MutableLiveData<>("");

    protected MutableLiveData<Integer> minAgePreferenceLiveData = new MutableLiveData<Integer>( 26);
    protected MutableLiveData<Integer> maxAgePreferenceLiveData = new MutableLiveData<Integer>( 40);
    protected MutableLiveData<HashSet<Integer>> genderTendencyLiveData = new MutableLiveData<>(new HashSet<>());

    protected MutableLiveData<String> aboutMeLiveData = new MutableLiveData<>("");

    protected MutableLiveData<Integer> progressLiveData = new MutableLiveData<>(0);

}
