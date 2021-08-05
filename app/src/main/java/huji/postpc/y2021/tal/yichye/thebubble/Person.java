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

enum Gender {
	MALE,
	FEMALE,
	GENDERQUEER,
	TRANSGENDER_M_F,
	TRANSGENDER_F_M
}

public class Person {

	private UUID id;
	private String name;
	private Gender gender;
	private LocalDate dateOfBirth;
	private String aboutMe;
	private String country;
	private String city;
	private int phoneNumber;
	private Image avatar;
	private ArrayList<Image> profilePhotos;
	private short minAgePreference;
	private short maxAgePreference;
	private Gender genderTendency;
	private ArrayList<String> requests;
	private ArrayList<String> matches;



	public Person(){
		this.id = UUID.randomUUID();
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

}
