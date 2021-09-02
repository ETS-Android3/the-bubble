package huji.postpc.y2021.tal.yichye.thebubble;

import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;


public class PersonData implements Serializable {

	public String fullName;
	public String userName;
	public String password;
	public Gender gender;
//	public LocalDate dateOfBirth;
	public String aboutMe;
	public String city;
	public String phoneNumber; // TODO: phone number should be private and saved in othe place
	public Image avatar;
	public ArrayList<Image> profilePhotos;
	public int minAgePreference;
	public int maxAgePreference;
	public ArrayList<Gender> genderTendency;
	public ArrayList<String> requests;
	public ArrayList<String> matches;


	public String getId() {
		return userName;
	}

	public String getName() {
		return fullName;
	}

	public Gender getGender() {
		return gender;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
//	public int getAge() {
//		return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
//	}

	@Override
	public String toString() {
		return "PersonData{" +
				", full name='" + fullName + '\'' +
				", user name='" + userName + '\'' +
				", gender=" + gender +
//				", dateOfBirth=" + dateOfBirth +
				", aboutMe='" + aboutMe + '\'' +
				", city='" + city + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", avatar=" + avatar +
				", profilePhotos=" + profilePhotos +
				", minAgePreference=" + minAgePreference +
				", maxAgePreference=" + maxAgePreference +
				", genderTendency=" + genderTendency +
				", requests=" + requests +
				", matches=" + matches +
				'}';
	}

	public enum Gender {
		MALE,
		FEMALE,
		TRANSGENDER_M_F,
		TRANSGENDER_F_M,
		GENDERQUEER
	}
}
