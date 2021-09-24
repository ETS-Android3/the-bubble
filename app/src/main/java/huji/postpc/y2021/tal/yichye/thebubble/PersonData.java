package huji.postpc.y2021.tal.yichye.thebubble;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import huji.postpc.y2021.tal.yichye.thebubble.Connections.ChatInfo;
import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;


public class PersonData implements Serializable {

	public String fullName;
	public String userName;
	public String password;
	public Gender gender;
	public long dateOfBirth;
	public String aboutMe;
	public String city;
	public String phoneNumber;

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setDateOfBirth(long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public void setPhotos(ArrayList<String> photos) {
		this.photos = photos;
	}

	public void setMinAgePreference(int minAgePreference) {
		this.minAgePreference = minAgePreference;
	}

	public void setMaxAgePreference(int maxAgePreference) {
		this.maxAgePreference = maxAgePreference;
	}

	public void setGenderTendency(ArrayList<Gender> genderTendency) {
		this.genderTendency = genderTendency;
	}

	public void setRequests(ArrayList<Request> requests) {
		this.requests = requests;
	}

	public void setChatInfos(ArrayList<ChatInfo> chatInfos) {
		this.chatInfos = chatInfos;
	}

	public ArrayList<String> photos;
	public int minAgePreference;
	public int maxAgePreference;
	public ArrayList<Gender> genderTendency;
	public ArrayList<Request> requests;
	public ArrayList<ChatInfo> chatInfos;
	public ArrayList<String> ignoreList;

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

	public ArrayList<Request> getRequests() {
		return requests;
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
				", profilePhotos=" + photos +
				", minAgePreference=" + minAgePreference +
				", maxAgePreference=" + maxAgePreference +
				", genderTendency=" + genderTendency +
				", requests=" + requests +
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
