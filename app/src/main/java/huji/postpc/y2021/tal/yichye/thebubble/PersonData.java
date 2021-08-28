package huji.postpc.y2021.tal.yichye.thebubble;

import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;



public class PersonData {

	public UUID id;
	public String name;
	public Gender gender;
	public LocalDate dateOfBirth;
	public String aboutMe;
	public String country;
	public String city;
	public String phoneNumber; // TODO: phone number should be private and saved in othe place
	public Image avatar;
	public ArrayList<Image> profilePhotos;
	public short minAgePreference;
	public short maxAgePreference;
	public ArrayList<Gender> genderTendency;
	public ArrayList<String> requests;
	public ArrayList<String> matches;



	public PersonData(){
		this.id = UUID.randomUUID();
	}

	public String getId() {
		return id.toString();
	}

	public String getName() {
		return name;
	}

	public Gender getGender() {
		return gender;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public int getAge() {
		return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
	}

	@Override
	public String toString() {
		return "PersonData{" +
				"id=" + id +
				", name='" + name + '\'' +
				", gender=" + gender +
				", dateOfBirth=" + dateOfBirth +
				", aboutMe='" + aboutMe + '\'' +
				", country='" + country + '\'' +
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
