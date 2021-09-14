package huji.postpc.y2021.tal.yichye.thebubble;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;


public class PersonData implements Serializable {

	public String fullName;
	public String userName;
	public String password;
	public Gender gender;
	public long dateOfBirth;
	public String aboutMe;
	public String city;
	public String phoneNumber; // TODO: phone number should be private and saved in othe place
	public String profilePicture;
	public ArrayList<String> photos;
	public int minAgePreference;
	public int maxAgePreference;
	public ArrayList<Gender> genderTendency;
	public ArrayList<Request> requests;
//	public ArrayList<String> matches;


	public String getId() {
		return userName;
	}

	public String getName() {
		return fullName;
	}

	public String getUserName() {
		return userName;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public Gender getGender() {
		return gender;
	}

	public int getAge()
	{
		return calcAge(dateOfBirth);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public static int calcAge(Long epochDate) {
		LocalDate date = LocalDate.ofEpochDay(epochDate);
		return Period.between(date, LocalDate.now()).getYears();
	}

	@Override
	public String toString() {
		return "PersonData{" +
				", full name='" + fullName + '\'' +
				", user name='" + userName + '\'' +
				", password-'" + password + '\'' +
				", gender=" + gender +
				", dateOfBirth=" + dateOfBirth +
				", aboutMe='" + aboutMe + '\'' +
				", city='" + city + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
//				", avatar=" + profilePicture +
				", profilePhotos=" + photos +
				", minAgePreference=" + minAgePreference +
				", maxAgePreference=" + maxAgePreference +
				", genderTendency=" + genderTendency +
				", requests=" + requests +
//				", matches=" + matches +
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
